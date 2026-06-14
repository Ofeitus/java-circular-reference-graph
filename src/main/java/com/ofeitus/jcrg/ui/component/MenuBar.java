package com.ofeitus.jcrg.ui.component;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.ofeitus.jcrg.ui.action.ChangeThemeAction;
import com.ofeitus.jcrg.ui.action.OpenSourceAction;
import com.ofeitus.jcrg.ui.diagram.Space;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar(ClassTree classTree, CyclesList cyclesList, Space space) {
        JMenu menuFile = new JMenu("File");
        menuFile.add(new JMenuItem(new OpenSourceAction(classTree, cyclesList, space)));
        JMenu menuTheme = new JMenu("Theme");
        menuTheme.add(new JMenuItem(new ChangeThemeAction("Light", FlatMacLightLaf::setup)));
        menuTheme.add(new JMenuItem(new ChangeThemeAction("Dark", FlatMacDarkLaf::setup)));
        add(menuFile);
        add(menuTheme);
    }
}
