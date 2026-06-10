package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.Vector2D;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import static com.ofeitus.jcrg.ui.diagram.BodyState.*;
import static com.ofeitus.jcrg.ui.theme.Colors.*;
import static com.ofeitus.jcrg.ui.theme.CustomStroke.BASIC_2;
import static java.lang.Math.*;

@Getter
@Setter
public class Edge extends Body {

    private static final double ARROW_HEAD_SIZE = 12;
    private static final double ARROW_HEAD_ANGLE = PI / 8;

    private final Vertex from;
    private final Vertex to;
    private final double rigidity = 20;
    private final double originalLength = 1;

    public Edge(int worldId, int depth, Vertex from, Vertex to) {
        super(worldId, depth);
        this.from = from;
        this.to = to;
    }

    public boolean connectedTo(Body body) {
        return from == body || to == body;
    }

    @Override
    public void draw(Graphics2D g) {
        Color color;
        if (state == HIGHLIGHTED) {
            color = HIGHLIGHT_COLOR;
        } else if (from.state == HIGHLIGHTED && to.state == DEFAULT) {
            color = HIGHLIGHT_COLOR;
        } else if (from.state == DEFAULT && to.state == HIGHLIGHTED) {
            color = HIGHLIGHT_COLOR_2;
        } else if (state == DEFAULT) {
            color = EDGE_COLOR;
        } else {
            color = EDGE_SHADOWED_COLOR;
        }
        g.setColor(color);
        g.setStroke(BASIC_2);

        Vector2D direction = to.getPosition().subtract(from.getPosition()).normalize();
        Vector2D shift = direction.perpendicular().multiply(4);
        Vector2D fromPoint = from.getPosition().add(direction.multiply(from.getRadius() + ARROW_HEAD_SIZE / 2)).add(shift);
        Vector2D toPoint = to.getPosition().subtract(direction.multiply(to.getRadius() + ARROW_HEAD_SIZE / 2)).add(shift);

        g.draw(new Line2D.Double(fromPoint.x(), fromPoint.y(), toPoint.x(), toPoint.y()));

        Path2D arrowHead = getArrowHeadPath(direction, toPoint.x(), toPoint.y());
        g.fill(arrowHead);
        g.draw(arrowHead);
    }

    private static Path2D getArrowHeadPath(Vector2D headDirection, double x2, double y2) {
        double angle = atan2(headDirection.y(), headDirection.x());

        double xLeft  = x2 - ARROW_HEAD_SIZE * cos(angle - ARROW_HEAD_ANGLE);
        double yLeft  = y2 - ARROW_HEAD_SIZE * sin(angle - ARROW_HEAD_ANGLE);
        double xRight = x2 - ARROW_HEAD_SIZE * cos(angle + ARROW_HEAD_ANGLE);
        double yRight = y2 - ARROW_HEAD_SIZE * sin(angle + ARROW_HEAD_ANGLE);

        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(x2, y2);
        arrowHead.lineTo(xLeft, yLeft);
        arrowHead.lineTo(xRight, yRight);
        arrowHead.closePath();
        return arrowHead;
    }
}
