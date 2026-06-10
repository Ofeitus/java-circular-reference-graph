package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.Vector2D;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;

import static com.ofeitus.jcrg.ui.diagram.BodyState.*;
import static com.ofeitus.jcrg.ui.theme.Colors.*;
import static com.ofeitus.jcrg.ui.theme.CustomStroke.BASIC_1_5;
import static java.lang.Math.*;

@Getter
@Setter
public class Edge extends Body {

    private static final int ARROW_HEAD_SIZE = 15;
    private static final double ARROW_HEAD_ANGLE = PI / 8;
    private static final double ARROW_CURVATURE = 20.0;

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
        } else if (from.state == SHADOWED && to.state == SHADOWED) {
            color = EDGE_SHADOWED_COLOR;
        } else if (from.state == SELECTED) {
            color = HIGHLIGHT_COLOR;
        } else if (to.state == SELECTED) {
            color = HIGHLIGHT_COLOR_2;
        } else {
            color = EDGE_COLOR;
        }
        g.setColor(color);
        g.setStroke(BASIC_1_5);

        Vector2D direction = to.getPosition().subtract(from.getPosition()).normalize();
        Vector2D perpendicular = direction.perpendicular();
        Vector2D fromPoint = from.getPosition().add(direction.add(perpendicular.divide(2)).multiply(Vertex.RADIUS * 1.4));
        Vector2D toPoint = to.getPosition().subtract(direction.subtract(perpendicular.divide(2)).multiply(Vertex.RADIUS * 1.4));
        Vector2D ctrlPoint = Vector2D.avg(fromPoint, toPoint).add(perpendicular.multiply(ARROW_CURVATURE));

        g.draw(new QuadCurve2D.Double(fromPoint.x(), fromPoint.y(), ctrlPoint.x(), ctrlPoint.y(), toPoint.x(), toPoint.y()));

        Path2D arrowHead = getArrowHeadPath(toPoint.subtract(ctrlPoint), toPoint.x(), toPoint.y());
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
