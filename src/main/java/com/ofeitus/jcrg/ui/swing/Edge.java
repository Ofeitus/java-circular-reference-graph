package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class Edge extends AbstractBody {

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
        g.drawLine((int) from.getPosition().x(), (int) from.getPosition().y(), (int) to.getPosition().x(), (int) to.getPosition().y());
    }
}
