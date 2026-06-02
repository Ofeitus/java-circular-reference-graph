package com.ofeitus.jcrg.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Space extends JPanel {

    public static final double TICK = 0.01;

    private final Set<Body> bodies = new HashSet<>();

    public Space(int width, int height) {
        super();
        setPreferredSize(new Dimension(width, height));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Vector2D mousePosition = new Vector2D(e.getX(), e.getY());
                bodies.stream()
                        .filter(body -> body.contains(mousePosition))
                        .findFirst()
                        .ifPresent(body -> body.setDragged(true));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                bodies.forEach(body -> body.setDragged(false));
            }
        });

        new Timer((int) (TICK * 1000), _ -> tick()).start();
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    private void tick() {
        Point mousePoint = getMousePosition();
        Vector2D mousePosition = mousePoint != null ? new Vector2D(mousePoint.x, mousePoint.y) : null;
        bodies.forEach(body -> body.calculateForce(bodies));
        bodies.forEach(body -> body.move(mousePosition, TICK));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        bodies.stream().sorted(Comparator.comparing(Body::getDepth).reversed()).forEach(body -> body.draw(g));
    }
}
