package com.ofeitus.jcrg.ui;

import com.ofeitus.jcrg.ui.component.ClassTree;
import com.ofeitus.jcrg.ui.component.CyclesList;
import com.ofeitus.jcrg.ui.component.MenuBar;
import com.ofeitus.jcrg.ui.diagram.Space;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() throws HeadlessException {
        super("Circular references");

        ClassTree classTree = new ClassTree();
        CyclesList cyclesList = new CyclesList();
        Space space = new Space(cyclesList);

        JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(classTree), new JScrollPane(cyclesList));
        rightPanel.setResizeWeight(0.5);
        JScrollPane spaceScrollPane = new JScrollPane(space);
        spaceScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spaceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rightPanel, spaceScrollPane);
        mainPanel.setResizeWeight(0);

        add(mainPanel, BorderLayout.CENTER);

        setJMenuBar(new MenuBar(classTree, cyclesList, space));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
