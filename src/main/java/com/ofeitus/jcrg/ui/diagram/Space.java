package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.ClassCycle;
import com.ofeitus.jcrg.model.Vector2D;
import com.ofeitus.jcrg.ui.component.CyclesList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static com.ofeitus.jcrg.ui.diagram.BodyState.*;
import static com.ofeitus.jcrg.ui.theme.Colors.BACKGROUND_COLOR;

public class Space extends JPanel {

    public static final double TICK = 0.01;
    public static final double COULOMB_CONSTANT = 8.9875517923e9;
    public static final double MIN_MOVEMENT_THRESHOLD = 0.06;
    public static final double AIR_DENSITY = 0.5;

    private final double MAX_ZOOM_FACTOR = 10.0;
    private final double MIN_ZOOM_FACTOR = 0.1;
    private final double ZOOM_SPEED = 1.1;
    private double zoomFactor = 1.0;
    private double offsetX = 0.0;
    private double offsetY = 0.0;

    private final Set<Body> bodies = new HashSet<>();

    public Space(CyclesList cyclesList) {
        super();
        setBackground(BACKGROUND_COLOR);
        addMouseWheelListener(e -> {
            Point mousePoint = e.getPoint();
            double oldZoom = zoomFactor;
            if (e.getWheelRotation() < 0) {
                zoomFactor *= ZOOM_SPEED;
            } else {
                zoomFactor /= ZOOM_SPEED;
            }
            zoomFactor = Math.max(MIN_ZOOM_FACTOR, Math.min(zoomFactor, MAX_ZOOM_FACTOR));
            double factor = zoomFactor / oldZoom;
            offsetX = mousePoint.x - (mousePoint.x - offsetX) * factor;
            offsetY = mousePoint.y - (mousePoint.y - offsetY) * factor;
            revalidate();
            repaint();
        });
        addMouseListener(new MouseAdapter() {
            private Point pressPoint;
            private static final int CLICK_TOLERANCE = 15;

            @Override
            public void mousePressed(MouseEvent e) {
                pressPoint = e.getPoint();
                Point2D logicalPoint = screenToLogicalPoint(e.getPoint());
                Vector2D mousePosition = new Vector2D(logicalPoint.getX(), logicalPoint.getY());
                bodies.stream()
                        .filter(body -> body.contains(mousePosition))
                        .findFirst()
                        .ifPresent(body -> body.setDragged(true));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                bodies.forEach(body -> body.setDragged(false));
                if (pressPoint != null) {
                    double distance = pressPoint.distance(e.getPoint());
                    if (distance <= CLICK_TOLERANCE) {
                        cyclesList.clearSelection();
                        Point2D logicalPoint = screenToLogicalPoint(e.getPoint());
                        Vector2D mousePosition = new Vector2D(logicalPoint.getX(), logicalPoint.getY());
                        bodies.stream()
                                .filter(body -> body.contains(mousePosition))
                                .findFirst()
                                .ifPresentOrElse(
                                        body -> {
                                            bodies.forEach(otherBody -> otherBody.setState(SHADOWED));
                                            bodies.forEach(otherBody -> {
                                                if (otherBody instanceof Edge edge && edge.connectedTo(body)) {
                                                    edge.setState(DEFAULT);
                                                    edge.getFrom().setState(DEFAULT);
                                                    edge.getTo().setState(DEFAULT);
                                                }
                                            });
                                            body.setState(HIGHLIGHTED);
                                        },
                                        () -> bodies.forEach(body -> body.setState(DEFAULT))
                                );
                    }
                }
                pressPoint = null;
            }
        });
        cyclesList.addListSelectionListener(_ -> {
            ClassCycle cycle = cyclesList.getSelectedValue();
            if (cycle != null) {
                bodies.forEach(otherBody -> otherBody.setState(SHADOWED));
                bodies.forEach(body -> {
                    switch (body) {
                        case Vertex vertex -> {
                            if (cycle.containsVertex(vertex.getClassMetadata())) {
                                vertex.setState(HIGHLIGHTED);
                            }
                        }
                        case Edge edge -> {
                            if (cycle.containsEdge(edge.getFrom().getClassMetadata(), edge.getTo().getClassMetadata())) {
                                edge.setState(HIGHLIGHTED);
                            }
                        }
                        default -> {
                        }
                    }
                });
            } else {
                bodies.forEach(body -> body.setState(DEFAULT));
            }
        });

        new Timer((int) (TICK * 1000), _ -> tick()).start();
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    private void tick() {
        Point2D logicalMousePoint = screenToLogicalPoint(getMousePosition());
        Vector2D mousePosition = logicalMousePoint != null ? new Vector2D(logicalMousePoint.getX(), logicalMousePoint.getY()) : null;
        bodies.forEach(body -> body.calculateForce(bodies));
        bodies.forEach(body -> body.move(mousePosition, TICK));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(offsetX, offsetY);
        g2d.scale(zoomFactor, zoomFactor);

        bodies.stream()
                .sorted(Comparator.comparing(Body::getState).thenComparing(body -> -body.getDepth()))
                .forEach(body -> body.draw(g2d));

        g2d.setTransform(originalTransform);
    }

    /**
     * Преобразовать координаты на экране в логические координаты в пространстве с учётом смещения и масштабирования
     */
    public Point2D screenToLogicalPoint(Point screenPoint) {
        if (screenPoint == null) {
            return null;
        }
        AffineTransform tx = new AffineTransform();
        tx.translate(offsetX, offsetY);
        tx.scale(zoomFactor, zoomFactor);
        try {
            return tx.inverseTransform(screenPoint, null);
        } catch (NoninvertibleTransformException e) {
            return screenPoint;
        }
    }
}
