package com.ofeitus.jcrg.ui.action;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeThemeAction extends AbstractAction {

    private final Runnable themeSetup;

    public ChangeThemeAction(String name, Runnable themeSetup) {
        super(name);
        this.themeSetup = themeSetup;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FlatAnimatedLafChange.showSnapshot();
        themeSetup.run();
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }
}
