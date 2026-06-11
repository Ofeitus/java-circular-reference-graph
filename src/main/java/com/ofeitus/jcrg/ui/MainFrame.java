package com.ofeitus.jcrg.ui;

import com.ofeitus.jcrg.ui.component.CyclesList;
import com.ofeitus.jcrg.ui.component.MenuBar;
import com.ofeitus.jcrg.ui.diagram.Space;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() throws HeadlessException {
        super("Circular references");

        CyclesList cyclesList = new CyclesList();
        Space space = new Space(cyclesList);

        JScrollPane spaceScrollPane = new JScrollPane(space);
        spaceScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spaceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane cyclesScrollPane = new JScrollPane(cyclesList);

        add(spaceScrollPane, BorderLayout.CENTER);
        add(cyclesScrollPane, BorderLayout.EAST);

        setJMenuBar(new MenuBar(cyclesList, space));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
