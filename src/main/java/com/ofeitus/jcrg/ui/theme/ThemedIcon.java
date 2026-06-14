package com.ofeitus.jcrg.ui.theme;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;

public class ThemedIcon extends FontIcon {

    public static ThemedIcon icon(Ikon ikon, int iconSize) {
        ThemedIcon icon = new ThemedIcon();
        icon.setIkon(ikon);
        icon.setIconSize(iconSize);
        icon.setIconColor(UIManager.getColor("Label.foreground"));

        UIManager.addPropertyChangeListener(evt -> {
            if ("lookAndFeel".equals(evt.getPropertyName())) {
                icon.setIconColor(UIManager.getColor("Label.foreground"));
            }
        });
        return icon;
    }
}
