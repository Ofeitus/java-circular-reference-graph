package com.ofeitus.jcrg.ui.theme;

import java.awt.*;

public class ColorUtil {

    public static Color transparent(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
