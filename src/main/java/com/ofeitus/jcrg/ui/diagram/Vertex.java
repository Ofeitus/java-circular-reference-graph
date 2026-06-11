package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.ClassMetadata;
import com.ofeitus.jcrg.model.Vector2D;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.util.Set;

import static com.ofeitus.jcrg.ui.diagram.BodyState.*;
import static com.ofeitus.jcrg.ui.theme.Colors.*;
import static com.ofeitus.jcrg.model.Vector2D.minMagnitude;
import static com.ofeitus.jcrg.ui.theme.CustomFont.ROBOTO_REGULAR_20;
import static com.ofeitus.jcrg.ui.theme.CustomStroke.BASIC_2;
import static com.ofeitus.jcrg.ui.diagram.Space.*;
import static java.lang.Math.*;

@Getter
@Setter
public class Vertex extends Body {

    private static final double CAPTION_INDENT = 10;

    private final double mass = 1;
    private final double electricCharge = 0.15;

    private final double radius;

    private Color color = VERTEX_COLOR;
    private Color foregroundColor = FOREGROUND_COLOR;

    private Vector2D position;
    private Vector2D velocity = Vector2D.ZERO;
    private Vector2D force = Vector2D.ZERO;

    private final ClassMetadata classMetadata;

    public Vertex(int worldId, double radius, Vector2D position, ClassMetadata classMetadata) {
        super(worldId, 1);
        this.radius = radius;
        this.position = position;
        this.classMetadata = classMetadata;
    }

    @Override
    public void calculateForce(Set<Body> otherBodies) {
        force = Vector2D.ZERO;
        otherBodies.forEach(otherBody -> {
            if (state == HIGHLIGHTED && otherBody.state != HIGHLIGHTED) {
                return;
            }
            if (worldId != otherBody.worldId) {
                return;
            }
            if (otherBody == this) {
                return;
            }
            switch (otherBody) {
                case Vertex otherVertex -> {
                    Vector2D positionDiff = position.subtract(otherVertex.position);
                    double r = positionDiff.magnitude();
                    double coulombLaw = COULOMB_CONSTANT * abs(electricCharge * otherVertex.electricCharge) / (r * r);
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
                default -> {
                }
            }
        });
        Vector2D airResistance = velocity.normalize().invert().multiply(pow(velocity.magnitude(), 2) * AIR_DENSITY);
        Vector2D safeAirResistance = minMagnitude(airResistance, velocity.invert().divide(TICK).multiply(mass));
        force = force.add(safeAirResistance);
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
        return position.subtract(point).magnitude() < radius;
    }

    @Override
    public void setState(BodyState state) {
        super.setState(state);
        switch (state) {
            case HIGHLIGHTED -> {
                color = HIGHLIGHT_COLOR;
                foregroundColor = FOREGROUND_COLOR;
            }
            case DEFAULT -> {
                color = VERTEX_COLOR;
                foregroundColor = FOREGROUND_COLOR;
            }
            case SHADOWED -> {
                color = VERTEX_SHADEWED_COLOR;
                foregroundColor = FOREGROUND_SHADEWED_COLOR;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Ellipse2D ellipse = new Ellipse2D.Double(position.x() - radius, position.y() - radius, radius * 2, radius * 2);
        g.setColor(color);
        g.fill(ellipse);
        g.setColor(foregroundColor);
        g.setStroke(BASIC_2);
        g.draw(ellipse);

        g.setFont(ROBOTO_REGULAR_20);
        TextLayout layout = new TextLayout(classMetadata.getName(), ROBOTO_REGULAR_20, g.getFontRenderContext());
        double x = position.x() - (double) g.getFontMetrics().stringWidth(classMetadata.getName()) / 2;
        double y = position.y() - radius - CAPTION_INDENT;
        layout.draw(g, (float) x, (float) y);
    }
}
