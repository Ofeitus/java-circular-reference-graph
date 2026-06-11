package com.ofeitus.jcrg;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.ofeitus.jcrg.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        FlatMacDarkLaf.setGlobalExtraDefaults(Map.of(
            "@accentColor", "#76ABAE",
            "defaultFont", "16 Roboto"
        ));
        FlatMacDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
