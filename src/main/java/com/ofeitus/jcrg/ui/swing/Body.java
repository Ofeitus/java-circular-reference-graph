package com.ofeitus.jcrg.ui.swing;

import java.awt.*;
import java.util.Set;

public interface Body {

    double getMass();
    Vector2D getPosition();
    Vector2D getForce();

    void calculateForce(Set<Body> otherBodies);
    void move(double deltaTime);
    void draw(Graphics g);
}
