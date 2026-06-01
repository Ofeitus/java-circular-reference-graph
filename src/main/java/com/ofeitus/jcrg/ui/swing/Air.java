package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Set;

@Getter
@Setter
public class Air implements Body {

    private final double density = 0.1;

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
    }
}
