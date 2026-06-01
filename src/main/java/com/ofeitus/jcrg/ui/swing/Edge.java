package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.*;

@Getter
@Setter
public class Edge extends AbstractBody {

    private static final int ARROW_HEAD_SIZE = 10;
    private static final double ARROW_HEAD_ANGLE = PI / 6;

    private final Vertex from;
    private final Vertex to;
    private final double rigidity = 1;
    private final double originalLength = 1;

    public Edge(int worldId, Vertex from, Vertex to) {
        super(worldId);
        this.from = from;
        this.to = to;
    }

    @Override
    public void draw(Graphics g) {
        Vector2D arrowDirection = to.getPosition().subtract(from.getPosition()).normalize();
        Vector2D fromConnectionPoint = from.getPosition().add(arrowDirection.multiply(Vertex.SIZE * 0.7));
        Vector2D toConnectionPoint = to.getPosition().subtract(arrowDirection.multiply(Vertex.SIZE * 0.7));
        double x2 = toConnectionPoint.x();
        double y2 = toConnectionPoint.y();
        g.drawLine((int) fromConnectionPoint.x(), (int) fromConnectionPoint.y(), (int) x2, (int) y2);

        double angle = atan2(arrowDirection.y(), arrowDirection.x());

        double xLeft  = x2 - ARROW_HEAD_SIZE * cos(angle - ARROW_HEAD_ANGLE);
        double yLeft  = y2 - ARROW_HEAD_SIZE * sin(angle - ARROW_HEAD_ANGLE);
        double xRight = x2 - ARROW_HEAD_SIZE * cos(angle + ARROW_HEAD_ANGLE);
        double yRight = y2 - ARROW_HEAD_SIZE * sin(angle + ARROW_HEAD_ANGLE);

        g.drawLine((int) x2, (int) y2, (int) xLeft, (int) yLeft);
        g.drawLine((int) x2, (int) y2, (int) xRight, (int) yRight);
    }
}
