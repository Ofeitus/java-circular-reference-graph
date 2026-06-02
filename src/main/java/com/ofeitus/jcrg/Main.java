package com.ofeitus.jcrg;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.ofeitus.jcrg.model.ClassMetadata;
import com.ofeitus.jcrg.graph.Graph;
import com.ofeitus.jcrg.model.Vector2D;
import com.ofeitus.jcrg.resolver.GraphGenerator;
import com.ofeitus.jcrg.resolver.SubgraphExtractor;
import com.ofeitus.jcrg.ui.diagram.Air;
import com.ofeitus.jcrg.ui.diagram.Edge;
import com.ofeitus.jcrg.ui.diagram.Space;
import com.ofeitus.jcrg.ui.diagram.Vertex;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        FlatMacLightLaf.setup();
        //List<Graph<ClassMetadata>> subGraphs = SubgraphExtractor.getConnectedComponents(
        //        ClassGraphConGenerator.generate(new File("C:\\Users\\Admin\\IdeaProjects\\smart-resort\\core\\src\\main\\java"), true));
        List<Graph<ClassMetadata>> subGraphs = SubgraphExtractor.getConnectedComponents(
                GraphGenerator.generate(new File("C:\\Users\\Admin\\IdeaProjects\\java-circular-reference-graph\\test"), true));

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Circular references");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            int spaceWidth = 1600;
            int spaceHeight = 900;
            frame.setSize(spaceWidth, spaceHeight);

            Random random = new Random();
            Space space = new Space(spaceWidth, spaceHeight);

            Map<ClassMetadata, Vertex> vertices = new HashMap<>();

            int world = 1;
            int vertexDepth = 1;
            int edgeDepth = 2;

            for (Graph<ClassMetadata> subGraph : subGraphs) {
                space.addBody(new Air(world));
                for (ClassMetadata classMetadata : subGraph.vertices()) {
                    Vertex vertex = new Vertex(world, vertexDepth, new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), classMetadata);
                    space.addBody(vertex);
                    vertices.put(classMetadata, vertex);
                }
                for (ClassMetadata classMetadata : subGraph.vertices()) {
                    for (ClassMetadata adjacent : subGraph.outgoingEdges(classMetadata)) {
                        Edge edge = new Edge(world, edgeDepth, vertices.get(classMetadata), vertices.get(adjacent));
                        space.addBody(edge);
                    }
                }

                world++;
            }

            JScrollPane scrollPane = new JScrollPane(space);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            frame.add(scrollPane);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
