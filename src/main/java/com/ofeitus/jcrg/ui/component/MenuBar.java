package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.ui.action.OpenSourceAction;
import com.ofeitus.jcrg.ui.diagram.Space;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar(CyclesList cyclesList, Space space) {
        JMenu menuFile = new JMenu("File");
        menuFile.add(new JMenuItem(new OpenSourceAction(cyclesList, space)));
        add(menuFile);
    }
}
