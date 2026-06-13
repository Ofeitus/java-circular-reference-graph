package com.ofeitus.jcrg.ui.action;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.ofeitus.jcrg.graph.Graph;
import com.ofeitus.jcrg.model.ClassCycle;
import com.ofeitus.jcrg.model.ClassMetadata;
import com.ofeitus.jcrg.model.Vector2D;
import com.ofeitus.jcrg.ui.component.ClassTree;
import com.ofeitus.jcrg.ui.component.CyclesList;
import com.ofeitus.jcrg.ui.diagram.Edge;
import com.ofeitus.jcrg.ui.diagram.Space;
import com.ofeitus.jcrg.ui.diagram.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class OpenSourceAction extends AbstractAsyncAction {

    private static final Logger logger = LoggerFactory.getLogger(OpenSourceAction.class);
    private static final Random random = new Random();

    private final ClassTree classTree;
    private final CyclesList cyclesList;
    private final Space space;

    public OpenSourceAction(ClassTree classTree, CyclesList cyclesList, Space space) {
        super("Open", "Opening");
        this.classTree = classTree;
        this.cyclesList = cyclesList;
        this.space = space;
    }

    private static List<File> listFilesRecursive(File file) {
        List<File> files = new ArrayList<>();
        if (file.isDirectory()) {
            File[] nextFiles = file.listFiles();
            if (nextFiles != null) {
                for (File nextFile : nextFiles) {
                    files.addAll(listFilesRecursive(nextFile));
                }
            }
        } else {
            files.add(file);
        }
        return files;
    }

    private Graph<ClassMetadata> filterClasses(Graph<ClassMetadata> graph, boolean removeSelfReferences) {
        int iterations = 0;
        AtomicBoolean somethingRemoved = new AtomicBoolean(true);
        while (somethingRemoved.get()) {
            somethingRemoved.set(false);
            graph.vertices().forEach(vertex -> {
                Set<ClassMetadata> outgoingEdges = graph.outgoingEdges(vertex);
                Set<ClassMetadata> incomingEdges = graph.incomingEdges(vertex);
                if (removeSelfReferences && outgoingEdges.contains(vertex)) {
                    graph.removeEdge(vertex, vertex);
                }
                if (outgoingEdges.isEmpty() || incomingEdges.isEmpty()) {
                    graph.removeVertex(vertex);
                    somethingRemoved.set(true);
                }
            });
            iterations++;
        }
        logger.info("Filtered {} classes in {} iterations", graph.size(), iterations);
        return graph;
    }

    @Override
    public void actionPerformedAsync(Consumer<IntermediateResult> resultsCallback) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File selectedFolder;
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFolder = chooser.getSelectedFile();
        } else {
            return;
        }

        List<File> javaFiles = listFilesRecursive(selectedFolder).stream()
                .filter(file -> file.getName().endsWith(".java"))
                .toList();

        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_25);
        config.setSymbolResolver(new JavaSymbolSolver(new JavaParserTypeSolver(selectedFolder, config)));
        JavaParser parser = new JavaParser(config);

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        int filesParsed = 0;
        for (File file : javaFiles) {
            parser.parse(file).ifSuccessful(compilationUnits::add);
            filesParsed ++;
            resultsCallback.accept(new IntermediateResult(filesParsed * 100 / javaFiles.size(),
                    "Parsing files (" + filesParsed + "/" + javaFiles.size() + ")"));
        }

        resultsCallback.accept(new IntermediateResult(100, "Creating graph"));

        Graph<ClassMetadata> graph = new Graph<>();
        List<FieldDeclaration> fields = compilationUnits.stream()
                .flatMap(cu -> cu.getTypes().stream())
                .flatMap(type -> {
                    graph.addVertex(new ClassMetadata(type.getFullyQualifiedName().get()));
                    return type.getFields().stream();
                }).toList();
        logger.info("Parsed {} classes", graph.size());

        AtomicInteger fieldsProcessed = new AtomicInteger();
        fields.forEach(field -> {
            fieldsProcessed.getAndIncrement();
            try {
                ResolvedType fieldType = field.getElementType().resolve();
                if (fieldType.isReferenceType()) {
                    ClassMetadata fieldClassMetadata = new ClassMetadata(fieldType.asReferenceType().getQualifiedName());
                    if (graph.contains(fieldClassMetadata)) {
                        graph.addEdge(new ClassMetadata(((TypeDeclaration<?>) field.getParentNode().get()).getFullyQualifiedName().get()), fieldClassMetadata);
                    }
                }
            } catch (UnsolvedSymbolException _) {
                logger.debug("Unresolved class: {}", field.getElementType());
            }
            resultsCallback.accept(new IntermediateResult(fieldsProcessed.get() * 100 / fields.size(),
                    "Resolving class fields (" + fieldsProcessed.get() + "/" + fields.size() + ")"));
        });

        resultsCallback.accept(new IntermediateResult(100, "Filtering classes"));
        filterClasses(graph, true);

        classTree.clear();
        classTree.addAll(graph.vertices());

        resultsCallback.accept(new IntermediateResult(100, "Searching cycles"));

        List<Graph<ClassMetadata>> subGraphs = graph.connectedComponents();
        List<ClassCycle> cycles = subGraphs.stream()
                .flatMap(subGraph -> subGraph.elementaryCycles().stream())
                .sorted(Comparator.comparing(List::size))
                .map(ClassCycle::new)
                .toList();

        cyclesList.clear();
        cyclesList.addAll(cycles);

        resultsCallback.accept(new IntermediateResult(100, "Creating diagram"));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Map<ClassMetadata, Vertex> vertices = new HashMap<>();

        double maxVertexRadius = 0;
        for (Graph<ClassMetadata> subGraph : subGraphs) {
            for (ClassMetadata classMetadata : subGraph.vertices()) {
                maxVertexRadius = Math.max(subGraph.outgoingEdges(classMetadata).size() + subGraph.incomingEdges(classMetadata).size(), maxVertexRadius);
            }
        }

        space.clear();
        int world = 0;
        for (Graph<ClassMetadata> subGraph : subGraphs) {
            world++;
            for (ClassMetadata classMetadata : subGraph.vertices()) {
                double vertexRadius = subGraph.outgoingEdges(classMetadata).size() + subGraph.incomingEdges(classMetadata).size();
                Vertex vertex = new Vertex(world, 10 + (vertexRadius / maxVertexRadius) * 30, new Vector2D(random.nextInt(screenSize.width), random.nextInt(screenSize.height)), classMetadata);
                space.addBody(vertex);
                vertices.put(classMetadata, vertex);
            }
            for (ClassMetadata classMetadata : subGraph.vertices()) {
                for (ClassMetadata adjacent : subGraph.outgoingEdges(classMetadata)) {
                    Edge edge = new Edge(world, vertices.get(classMetadata), vertices.get(adjacent));
                    space.addBody(edge);
                }
            }
        }
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender);
    }
}
