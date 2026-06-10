package com.ofeitus.jcrg;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.github.javaparser.ParserConfiguration;
import com.ofeitus.jcrg.model.ClassCycle;
import com.ofeitus.jcrg.model.ClassMetadata;
import com.ofeitus.jcrg.graph.Graph;
import com.ofeitus.jcrg.model.Vector2D;
import com.ofeitus.jcrg.parser.JavaParser;
import com.ofeitus.jcrg.ui.component.CyclesList;
import com.ofeitus.jcrg.ui.diagram.Edge;
import com.ofeitus.jcrg.ui.diagram.Space;
import com.ofeitus.jcrg.ui.diagram.Vertex;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {

    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        System.setProperty("awt.dnd.drag.threshold", "10");
        FlatMacLightLaf.setGlobalExtraDefaults(Map.of(
            "@accentColor", "#76ABAE",
            "defaultFont", "16 Roboto"
        ));
        FlatMacDarkLaf.setup();

        List<Graph<ClassMetadata>> subGraphs = JavaParser.parse(
                List.of(new File("C:\\Users\\TYUSHEV\\IdeaProjects\\smart-resort\\core\\src\\main\\java"),
                        new File("C:\\Users\\TYUSHEV\\IdeaProjects\\smart-resort\\reservation\\src\\main\\java")),
                        ParserConfiguration.LanguageLevel.JAVA_8,
                        true)
                .connectedComponents();

        List<ClassCycle> cycles = subGraphs.stream()
                .flatMap(subGraph -> subGraph.elementaryCycles().stream())
                .sorted(Comparator.comparing(List::size))
                .map(ClassCycle::new)
                .toList();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Circular references");
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            CyclesList cyclesList = new CyclesList(cycles);

            Space space = new Space(cyclesList);

            Map<ClassMetadata, Vertex> vertices = new HashMap<>();

            int world = 1;
            int vertexDepth = 1;
            int edgeDepth = 2;

            for (Graph<ClassMetadata> subGraph : subGraphs) {
                for (ClassMetadata classMetadata : subGraph.vertices()) {
                    Vertex vertex = new Vertex(world, vertexDepth, new Vector2D(random.nextInt(screenSize.width), random.nextInt(screenSize.height)), classMetadata);
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

            JScrollPane spaceScrollPane = new JScrollPane(space);
            spaceScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            spaceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            frame.add(spaceScrollPane, BorderLayout.CENTER);

            JScrollPane cyclesScrollPane = new JScrollPane(cyclesList);
            frame.add(cyclesScrollPane, BorderLayout.EAST);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}
