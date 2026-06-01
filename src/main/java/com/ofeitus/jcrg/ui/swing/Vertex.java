package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

import static java.lang.Math.abs;

@Getter
@Setter
public class Vertex extends AbstractBody {

    private static final double COULOMB_CONSTANT = 8.9875517923e9;

    private static final double SIZE = 20;

    private final double mass = 1;
    private final double electricCharge = 0.01;
    private Vector2D position;
    private Vector2D velocity = new Vector2D(0, 0);
    private Vector2D force = new Vector2D(0, 0);

    public Vertex(int worldId, Vector2D position) {
        super(worldId);
        this.position = position;
    }

    @Override
    public void calculateForce(Set<Body> otherBodies) {
        force = new Vector2D(0, 0);
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
                case Air air ->
                        force = force.add(velocity.normalize().invert().multiply(velocity.magnitude() * air.getDensity()));
                default -> {
                }
            }
        });
    }

    @Override
    public void move(double deltaTime) {
        Vector2D acceleration = force.divide(mass);
        position = position.add(velocity.multiply(deltaTime)).add(acceleration.multiply(deltaTime * deltaTime / 2));
        velocity = velocity.add(acceleration.multiply(deltaTime));
    }

    @Override
    public void draw(Graphics g) {
        g.drawOval((int) (position.x() - SIZE / 2), (int) (position.y() - SIZE / 2), (int) SIZE, (int) SIZE);
    }
}
