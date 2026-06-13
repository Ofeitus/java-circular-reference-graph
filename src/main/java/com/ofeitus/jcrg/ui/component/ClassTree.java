package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.model.ClassMetadata;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class ClassTree extends JPanel {
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode rootNode;

    private final Comparator<DefaultMutableTreeNode> alphabetComparator = Comparator
            .comparing((DefaultMutableTreeNode node) -> !((UserObject) node.getUserObject()).isPackage)
            .thenComparing((DefaultMutableTreeNode node) -> ((UserObject) node.getUserObject()).name.toLowerCase());

    public ClassTree() {
        super(new BorderLayout());
        rootNode = new DefaultMutableTreeNode(new UserObject(true, "Project"));
        treeModel = new DefaultTreeModel(rootNode);

        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new ClassTreeCellRenderer());
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent event) {
            }

            @Override
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent event) {
                collapseChild(event.getPath());
            }
        });

        JToolBar toolBar = new JToolBar();
        toolBar.add(new JLabel("Project"));
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new JButton(new ExpandAllAction()));
        toolBar.add(new JButton(new CollapseAllAction()));

        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    public void addAll(Collection<ClassMetadata> classes) {
        for (ClassMetadata classMetadata : classes) {
            String[] parts = classMetadata.getFullName().split("\\.");
            DefaultMutableTreeNode currentNode = rootNode;

            for (int i = 0; i < parts.length - 1; i++) {
                currentNode = getOrCreateChild(currentNode, new UserObject(true, parts[i]));
            }
            getOrCreateChild(currentNode, new UserObject(false, parts[parts.length - 1]));
        }
        expandAll();
        tree.updateUI();
        expandAll();
    }

    private DefaultMutableTreeNode getOrCreateChild(DefaultMutableTreeNode parent, UserObject userObject) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (child.getUserObject().equals(userObject)) {
                return child;
            }
        }
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(userObject);
        int i = 0;
        while (i < parent.getChildCount() && alphabetComparator.compare(newChild, (DefaultMutableTreeNode) parent.getChildAt(i)) >= 0) {
            i++;
        }
        parent.insert(newChild, i);
        return newChild;
    }

    public void expandAll() {
        expandChild(new TreePath(treeModel.getRoot()));
    }

    private void expandChild(TreePath parent) {
        ((TreeNode) parent.getLastPathComponent()).children().asIterator()
                .forEachRemaining(child -> {
                    TreePath path = parent.pathByAddingChild(child);
                    expandChild(path);
                    tree.expandPath(path);
                });
    }

    public void collapseAll() {
        collapseChild(new TreePath(treeModel.getRoot()));
    }

    private void collapseChild(TreePath parent) {
        ((TreeNode) parent.getLastPathComponent()).children().asIterator()
                .forEachRemaining(child -> {
                    TreePath path = parent.pathByAddingChild(child);
                    collapseChild(path);
                    tree.collapsePath(path);
                });
    }

    private record UserObject(boolean isPackage, String name) {}

    private static class ClassTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            return super.getTreeCellRendererComponent(tree, ((UserObject) ((DefaultMutableTreeNode) value).getUserObject()).name, sel, expanded, leaf, row, hasFocus);
        }
    }

    private class ExpandAllAction extends AbstractAction {

        public ExpandAllAction() {
            super("", FontIcon.of(MaterialDesignA.ARROW_EXPAND, 16, UIManager.getColor("Label.foreground")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            expandAll();
        }
    }

    private class CollapseAllAction extends AbstractAction {

        public CollapseAllAction() {
            super("", FontIcon.of(MaterialDesignA.ARROW_COLLAPSE, 16, UIManager.getColor("Label.foreground")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            collapseAll();
        }
    }
}