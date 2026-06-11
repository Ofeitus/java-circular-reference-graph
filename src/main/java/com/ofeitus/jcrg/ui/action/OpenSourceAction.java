package com.ofeitus.jcrg.ui.action;

import com.github.javaparser.ParserConfiguration;
import com.ofeitus.jcrg.graph.Graph;
import com.ofeitus.jcrg.model.ClassCycle;
import com.ofeitus.jcrg.model.ClassMetadata;
import com.ofeitus.jcrg.model.Vector2D;
import com.ofeitus.jcrg.parser.JavaParser;
import com.ofeitus.jcrg.ui.component.CyclesList;
import com.ofeitus.jcrg.ui.diagram.Edge;
import com.ofeitus.jcrg.ui.diagram.Space;
import com.ofeitus.jcrg.ui.diagram.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class OpenSourceAction extends AbstractAction {

    private static final Random random = new Random();

    private final CyclesList cyclesList;
    private final Space space;

    public OpenSourceAction(CyclesList cyclesList, Space space) {
        super("Open");
        this.cyclesList = cyclesList;
        this.space = space;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cyclesList.getModel().clear();
        space.clear();

        List<Graph<ClassMetadata>> subGraphs;
        try {
            subGraphs = JavaParser.parse(
                            List.of(new File("C:\\Users\\TYUSHEV\\IdeaProjects\\smart-resort\\core\\src\\main\\java"),
                                    new File("C:\\Users\\TYUSHEV\\IdeaProjects\\smart-resort\\reservation\\src\\main\\java")),
                            ParserConfiguration.LanguageLevel.JAVA_8,
                            true)
                    .connectedComponents();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        List<ClassCycle> cycles = subGraphs.stream()
                .flatMap(subGraph -> subGraph.elementaryCycles().stream())
                .sorted(Comparator.comparing(List::size))
                .map(ClassCycle::new)
                .toList();
        cyclesList. getModel().addAll(cycles);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Map<ClassMetadata, Vertex> vertices = new HashMap<>();

        int world = 1;
        int vertexDepth = 1;
        int edgeDepth = 2;

        double maxVertexRadius = 0;
        for (Graph<ClassMetadata> subGraph : subGraphs) {
            for (ClassMetadata classMetadata : subGraph.vertices()) {
                maxVertexRadius = Math.max(subGraph.outgoingEdges(classMetadata).size() + subGraph.incomingEdges(classMetadata).size(), maxVertexRadius);
            }
        }

        for (Graph<ClassMetadata> subGraph : subGraphs) {
            for (ClassMetadata classMetadata : subGraph.vertices()) {
                double vertexRadius = subGraph.outgoingEdges(classMetadata).size() + subGraph.incomingEdges(classMetadata).size();
                Vertex vertex = new Vertex(world, vertexDepth, 10 + (vertexRadius / maxVertexRadius) * 30, new Vector2D(random.nextInt(screenSize.width), random.nextInt(screenSize.height)), classMetadata);
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
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender);
    }
}
