package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class Spring extends JComponent implements Body {

    private final Body from;
    private final Body to;
    private final double rigidity = 1;
    private final double originalLength = 1;

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public Vector2D getPosition() {
        return null;
    }

    @Override
    public Vector2D getForce() {
        return null;
    }

    @Override
    public void calculateForce(Set<Body> otherBodies) {
    }

    @Override
    public void move(double deltaTime) {
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine((int) from.getPosition().x(), (int) from.getPosition().y(), (int) to.getPosition().x(), (int) to.getPosition().y());
    }
}
