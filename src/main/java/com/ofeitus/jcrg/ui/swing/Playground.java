package com.ofeitus.jcrg.ui.swing;

import javax.swing.*;
import java.util.Random;

public class Playground {

    static void main() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Playground");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            int spaceWidth = 800;
            int spaceHeight = 800;
            frame.setSize(spaceWidth, spaceHeight);

            Random random = new Random();

            Space space = new Space(spaceWidth, spaceHeight);
            int world1 = 1;
            int world2 = 2;

            Vertex vertex1 = new Vertex(world1, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex2 = new Vertex(world1, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex3 = new Vertex(world1, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex4 = new Vertex(world1, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex5 = new Vertex(world1, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex6 = new Vertex(world1, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Edge edge1 = new Edge(world1, vertex1, vertex2);
            Edge edge2 = new Edge(world1, vertex2, vertex3);
            Edge edge3 = new Edge(world1, vertex3, vertex4);
            Edge edge4 = new Edge(world1, vertex4, vertex5);
            Edge edge5 = new Edge(world1, vertex5, vertex6);
            Edge edge6 = new Edge(world1, vertex6, vertex1);

            Vertex vertex7 = new Vertex(world2, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex8 = new Vertex(world2, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Vertex vertex9 = new Vertex(world2, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)));
            Edge edge7 = new Edge(world2, vertex7, vertex8);
            Edge edge8 = new Edge(world2, vertex8, vertex9);
            Edge edge9 = new Edge(world2, vertex9, vertex7);

            space.addBody(new Air(world1));
            space.addBody(new Air(world2));
            space.addBody(vertex1);
            space.addBody(vertex2);
            space.addBody(vertex3);
            space.addBody(vertex4);
            space.addBody(vertex5);
            space.addBody(vertex6);
            space.addBody(vertex7);
            space.addBody(vertex8);
            space.addBody(vertex9);
            space.addBody(edge1);
            space.addBody(edge2);
            space.addBody(edge3);
            space.addBody(edge4);
            space.addBody(edge5);
            space.addBody(edge6);
            space.addBody(edge7);
            space.addBody(edge8);
            space.addBody(edge9);

            JScrollPane scrollPane = new JScrollPane(space);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            frame.add(scrollPane);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
