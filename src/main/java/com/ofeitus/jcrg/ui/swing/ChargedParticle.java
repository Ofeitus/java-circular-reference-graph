package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

import static java.lang.Math.abs;

@Getter
@Setter
public class ChargedParticle extends JComponent implements Body, ElectricallyCharged {

    private static final double SIZE = 20;

    private final double mass = 1;
    private final double electricCharge = 0.01;
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D force;

    public ChargedParticle(Vector2D position, Vector2D velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    @Override
    public void calculateForce(Set<Body> otherBodies) {
        force = new Vector2D(0, 0);
        otherBodies.forEach(otherBody -> {
            if (otherBody == this) {
                return;
            }
            if (otherBody instanceof ElectricallyCharged charged) {
                Vector2D positionDiff = position.subtract(otherBody.getPosition());
                double r = positionDiff.magnitude();
                Vector2D forceDirection = positionDiff.normalize();
                double coulombLaw = COULOMB_CONSTANT * abs(electricCharge * charged.getElectricCharge()) / (r * r);
                force = force.add(forceDirection.multiply(coulombLaw));
            } else if (otherBody instanceof Spring spring ) {
                if (spring.getFrom().equals(this)) {
                    Vector2D positionDiff = spring.getTo().getPosition().subtract(spring.getFrom().getPosition());
                    double r = positionDiff.magnitude();
                    Vector2D forceDirection = positionDiff.normalize();
                    double hookeLaw = spring.getRigidity() * (r - spring.getOriginalLength());
                    force = force.add(forceDirection.multiply(hookeLaw));
                } else if (spring.getTo().equals(this)) {
                    Vector2D positionDiff = spring.getFrom().getPosition().subtract(spring.getTo().getPosition());
                    double r = positionDiff.magnitude();
                    Vector2D forceDirection = positionDiff.normalize();
                    double hookeLaw = spring.getRigidity() * (r - spring.getOriginalLength());
                    force = force.add(forceDirection.multiply(hookeLaw));
                }
            } else if (otherBody instanceof Air air) {
                double v = velocity.magnitude();
                Vector2D forceDirection = velocity.normalize().invert();
                force = force.add(forceDirection.multiply(v * air.getDensity()));
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
