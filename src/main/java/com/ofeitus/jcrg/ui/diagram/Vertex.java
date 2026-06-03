package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.ClassMetadata;
import com.ofeitus.jcrg.model.Vector2D;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

import static com.ofeitus.jcrg.ui.theme.Colors.*;
import static com.ofeitus.jcrg.model.Vector2D.minMagnitude;
import static com.ofeitus.jcrg.ui.theme.CustomFont.ROBOTO_REGULAR_20;
import static com.ofeitus.jcrg.ui.theme.CustomStroke.BASIC_2;
import static com.ofeitus.jcrg.ui.diagram.Space.*;
import static java.lang.Math.*;

@Getter
@Setter
public class Vertex extends Body {

    public static final double RADIUS = 10;
    private static final int CAPTION_INDENT = 20;

    private final double mass = 1;
    private final double electricCharge = 0.1;

    private Vector2D position;
    private Vector2D velocity = Vector2D.ZERO;
    private Vector2D force = Vector2D.ZERO;

    private final ClassMetadata classMetadata;

    public Vertex(int worldId, int depth, Vector2D position, ClassMetadata classMetadata) {
        super(worldId, depth);
        this.position = position;
        this.classMetadata = classMetadata;
    }

    @Override
    public void calculateForce(Set<Body> otherBodies) {
        force = Vector2D.ZERO;
        otherBodies.forEach(otherBody -> {
            if (worldId != otherBody.getWorldId()) {
                return;
            }
            if (otherBody == this) {
                return;
            }
            switch (otherBody) {
                case Vertex otherVertex -> {
                    Vector2D positionDiff = position.subtract(otherVertex.getPosition());
                    double r = positionDiff.magnitude();
                    double coulombLaw = COULOMB_CONSTANT * abs(electricCharge * otherVertex.getElectricCharge()) / (r * r);
                    force = force.add(positionDiff.normalize().multiply(coulombLaw));
                }
                case Edge edge -> {
                    if (edge.getFrom().equals(this) || edge.getTo().equals(this)) {
                        Vector2D positionDiff = edge.getTo().equals(this) ?
                                edge.getFrom().getPosition().subtract(edge.getTo().getPosition()) :
                                edge.getTo().getPosition().subtract(edge.getFrom().getPosition());
                        double hookeLaw = edge.getRigidity() * (positionDiff.magnitude() - edge.getOriginalLength());
                        force = force.add(positionDiff.normalize().multiply(hookeLaw));
                    }
                }
                case Air air -> {
                    Vector2D airResistance = velocity.normalize().invert().multiply(pow(velocity.magnitude(), 2) * air.getDensity());
                    Vector2D safeAirResistance = minMagnitude(airResistance, velocity.invert().divide(TICK).multiply(mass));
                    force = force.add(safeAirResistance);
                }
                default -> {
                }
            }
        });
    }

    @Override
    public void move(Vector2D mousePosition, double deltaTime) {
        if (dragged && mousePosition != null) {
            position = mousePosition;
            velocity = Vector2D.ZERO;
        } else {
            Vector2D acceleration = force.divide(mass);
            Vector2D movement = velocity.multiply(deltaTime).add(acceleration.multiply(deltaTime * deltaTime / 2));
            if (movement.magnitude() > MIN_MOVEMENT_THRESHOLD) {
                position = position.add(movement);
            }
            velocity = velocity.add(acceleration.multiply(deltaTime));
        }
    }

    @Override
    public boolean contains(Vector2D point) {
        return position.subtract(point).magnitude() < RADIUS;
    }

    @Override
    public void draw(Graphics2D g) {
        if (highlighted) {
            g.setColor(HIGHLIGHT_COLOR);
        } else {
            g.setColor(VERTEX_COLOR);
        }
        g.fillOval((int) (position.x() - RADIUS), (int) (position.y() - RADIUS), (int) RADIUS * 2, (int) RADIUS * 2);
        g.setColor(VERTEX_BORDER_COLOR);
        g.setStroke(BASIC_2);
        g.drawOval((int) (position.x() - RADIUS), (int) (position.y() - RADIUS), (int) RADIUS * 2, (int) RADIUS * 2);
        g.setColor(TEXT_COLOR);
        g.setFont(ROBOTO_REGULAR_20);
        g.drawString(classMetadata.name(), (int) position.x() - g.getFontMetrics().stringWidth(classMetadata.name()) / 2, (int) (position.y() - CAPTION_INDENT));
    }
}
