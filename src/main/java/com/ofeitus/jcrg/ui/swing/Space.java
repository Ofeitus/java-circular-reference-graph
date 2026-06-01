package com.ofeitus.jcrg.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Space extends JPanel {
    private static final double TICK = 0.01;

    private final Set<Body> bodies = new HashSet<>();

    public Space(int width, int height) {
        super();
        setPreferredSize(new Dimension(width, height));

        new Timer((int) (TICK * 1000), _ -> tick()).start();
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    private void tick() {
        bodies.forEach(body -> body.calculateForce(bodies));
        bodies.forEach(body -> body.move(TICK));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        bodies.forEach(body -> body.draw(g));
    }
}
