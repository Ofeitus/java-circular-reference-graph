package com.ofeitus.jcrg.ui.swing;

import java.awt.*;
import java.util.Set;

public interface Body {

    int getWorldId();

    void calculateForce(Set<Body> otherBodies);
    void move(double deltaTime);
    void draw(Graphics g);
}
