package com.ofeitus.jcrg.ui.diagram;

import com.ofeitus.jcrg.model.Vector2D;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static com.ofeitus.jcrg.ui.theme.Colors.*;
import static com.ofeitus.jcrg.ui.theme.CustomStroke.BASIC_1_5;
import static java.lang.Math.*;

@Getter
@Setter
public class Edge extends Body {

    private static final int ARROW_HEAD_SIZE = 15;
    private static final double ARROW_HEAD_ANGLE = PI / 8;

    private Color color = EDGE_COLOR;

    private final Vertex from;
    private final Vertex to;
    private final double rigidity = 20;
    private final double originalLength = 1;

    public Edge(int worldId, int depth, Vertex from, Vertex to) {
        super(worldId, depth);
        this.from = from;
        this.to = to;
    }

    @Override
    public void setState(BodyState state) {
        super.setState(state);
        switch (state) {
            case HIGHLIGHTED -> color = HIGHLIGHT_COLOR;
            case DEFAULT -> color = EDGE_COLOR;
            case SHADOWED -> color = EDGE_SHADOWED_COLOR;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(BASIC_1_5);

        Vector2D arrowDirection = to.getPosition().subtract(from.getPosition()).normalize();
        Vector2D fromConnectionPoint = from.getPosition().add(arrowDirection.multiply(Vertex.RADIUS * 1.4));
        Vector2D toConnectionPoint = to.getPosition().subtract(arrowDirection.multiply(Vertex.RADIUS * 1.4));
        Vector2D point3 = to.getPosition().subtract(arrowDirection.multiply(Vertex.RADIUS * 1.4 + (double) ARROW_HEAD_SIZE / 2));
        double x2 = toConnectionPoint.x();
        double y2 = toConnectionPoint.y();
        double x3 = point3.x();
        double y3 = point3.y();
        g.drawLine((int) fromConnectionPoint.x(), (int) fromConnectionPoint.y(), (int) x3, (int) y3);

        double angle = atan2(arrowDirection.y(), arrowDirection.x());

        double xLeft  = x2 - ARROW_HEAD_SIZE * cos(angle - ARROW_HEAD_ANGLE);
        double yLeft  = y2 - ARROW_HEAD_SIZE * sin(angle - ARROW_HEAD_ANGLE);
        double xRight = x2 - ARROW_HEAD_SIZE * cos(angle + ARROW_HEAD_ANGLE);
        double yRight = y2 - ARROW_HEAD_SIZE * sin(angle + ARROW_HEAD_ANGLE);

        g.fillPolygon(new int[]{(int) x2, (int) xLeft, (int) x3, (int) xRight}, new int[]{(int) y2, (int) yLeft, (int) y3, (int) yRight}, 4);
        g.drawPolygon(new int[]{(int) x2, (int) xLeft, (int) x3, (int) xRight}, new int[]{(int) y2, (int) yLeft, (int) y3, (int) yRight}, 4);
    }
}
