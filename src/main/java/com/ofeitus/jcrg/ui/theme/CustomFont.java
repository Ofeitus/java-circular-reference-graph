package com.ofeitus.jcrg.ui.theme;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class CustomFont {

    public static final Font ROBOTO_REGULAR_20;

    static {
        try (InputStream is = CustomFont.class.getResourceAsStream("/fonts/Roboto-Regular.ttf")) {
            if (is == null) {
                throw new IOException("Font file not found in resources!");
            }
            ROBOTO_REGULAR_20 = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
