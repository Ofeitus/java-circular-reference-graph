package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Body {

    protected final int worldId;
    private final int depth;
    protected boolean dragged;

    public void calculateForce(Set<Body> otherBodies) {
    }

    public void move(Vector2D mousePosition, double deltaTime) {
    }

    public boolean contains(Vector2D point) {
        return false;
    }

    public void draw(Graphics g) {
    }
}
