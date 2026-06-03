package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.ClassCycle;
import com.ofeitus.jcrg.model.Vector2D;
import com.ofeitus.jcrg.ui.component.CyclesList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static com.ofeitus.jcrg.ui.theme.Colors.BACKGROUND_COLOR;

public class Space extends JPanel {

    public static final double TICK = 0.01;
    public static final double COULOMB_CONSTANT = 8.9875517923e9;
    public static final double MIN_MOVEMENT_THRESHOLD = 0.05;

    private final Set<Body> bodies = new HashSet<>();

    public Space(int width, int height, CyclesList cyclesList) {
        super();
        setBackground(BACKGROUND_COLOR);
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
        cyclesList.addListSelectionListener(e -> {
            bodies.forEach(body -> body.setHighlighted(false));
            ClassCycle cycle = cyclesList.getSelectedValue();
            if (cycle != null) {
                bodies.forEach(body -> {
                    switch (body) {
                        case Vertex vertex -> vertex.setHighlighted(cycle.contains(vertex.getClassMetadata()));
                        case Edge edge -> edge.setHighlighted(
                                cycle.contains(edge.getFrom().getClassMetadata())
                                && cycle.contains(edge.getTo().getClassMetadata())
                        );
                        default -> {
                        }
                    }
                });
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        bodies.stream().sorted(Comparator.comparing(Body::getDepth).reversed()).forEach(body -> body.draw(g2d));
    }
}
