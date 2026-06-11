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

    private static final double HEAD_SIZE = 12;
    private static final double HEAD_ANGLE = PI / 8;

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
        Vector2D fromPoint = from.getPosition().add(direction.multiply(from.getRadius() + HEAD_SIZE / 2)).add(shift);
        Vector2D toPoint = to.getPosition().subtract(direction.multiply(to.getRadius() + HEAD_SIZE / 2)).add(shift);
        Vector2D almostToPoint = to.getPosition().subtract(direction.multiply(to.getRadius() + HEAD_SIZE * 1.5)).add(shift);

        g.draw(new Line2D.Double(fromPoint.x(), fromPoint.y(), almostToPoint.x(), almostToPoint.y()));

        double angle = atan2(direction.y(), direction.x());
        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(toPoint.x(), toPoint.y());
        arrowHead.lineTo(toPoint.x() - HEAD_SIZE * cos(angle - HEAD_ANGLE), toPoint.y() - HEAD_SIZE * sin(angle - HEAD_ANGLE));
        arrowHead.lineTo(toPoint.x() - HEAD_SIZE * cos(angle + HEAD_ANGLE), toPoint.y() - HEAD_SIZE * sin(angle + HEAD_ANGLE));
        arrowHead.closePath();

        g.fill(arrowHead);
    }
}
