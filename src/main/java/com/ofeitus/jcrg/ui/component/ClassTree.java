package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.model.ClassMetadata;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.util.List;

public class ClassTree extends JTree {
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode rootNode;

    public ClassTree() {
        this.rootNode = new DefaultMutableTreeNode("Project");
        this.treeModel = new DefaultTreeModel(rootNode);
        setModel(treeModel);
    }

    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    public void addAll(Collection<ClassMetadata> classes) {
        for (ClassMetadata classMetadata : classes) {
            String[] parts = classMetadata.getFullName().split("\\.");
            DefaultMutableTreeNode currentNode = rootNode;

            for (String part : parts) {
                currentNode = getOrCreateChild(currentNode, part);
            }
        }
        treeModel.reload();
    }

    private DefaultMutableTreeNode getOrCreateChild(DefaultMutableTreeNode parent, String name) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (child.getUserObject().toString().equals(name)) {
                return child;
            }
        }
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(name);
        parent.add(newChild);
        return newChild;
    }

    public void sort() {
        Comparator<DefaultMutableTreeNode> alphabetComparator = Comparator
                .comparing((DefaultMutableTreeNode node) -> node.getChildCount() == 0)
                .thenComparing((DefaultMutableTreeNode node) -> node.getUserObject().toString().toLowerCase());
        sortNode(rootNode, alphabetComparator);
        treeModel.nodeStructureChanged(rootNode);
    }

    private void sortNode(DefaultMutableTreeNode node, Comparator<DefaultMutableTreeNode> comparator) {
        if (node.getChildCount() == 0) {
            return;
        }
        List<DefaultMutableTreeNode> children = new ArrayList<>(node.getChildCount());
        node.children().asIterator().forEachRemaining(child -> {
            children.add((DefaultMutableTreeNode) child);
            sortNode((DefaultMutableTreeNode) child, comparator);
        });
        children.sort(comparator);
        node.removeAllChildren();
        children.forEach(node::add);
    }
}