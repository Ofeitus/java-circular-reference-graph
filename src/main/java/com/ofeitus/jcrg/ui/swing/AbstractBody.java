package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class AbstractBody implements Body {

    protected final int worldId;

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
