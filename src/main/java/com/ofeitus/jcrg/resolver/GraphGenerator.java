package com.ofeitus.jcrg.resolver;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.ofeitus.jcrg.graph.Graph;
import com.ofeitus.jcrg.model.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class GraphGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GraphGenerator.class);

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

    public static Graph<ClassMetadata> generate(File srcDir, boolean removeSelfReferences) throws FileNotFoundException {
        List<File> javaFiles = listFilesRecursive(srcDir).stream()
                .filter(file -> file.getName().endsWith(".java"))
                .toList();

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver(false));
        combinedTypeSolver.add(new JavaParserTypeSolver(srcDir));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        Graph<ClassMetadata> graph = new Graph<>();

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        for (File file : javaFiles) {
            compilationUnits.add(StaticJavaParser.parse(file));
        }

        compilationUnits.forEach(cu -> cu.getTypes().forEach(type -> {
            if (type.getFullyQualifiedName().isPresent()) {
                ClassMetadata classMetadata = new ClassMetadata(type.getNameAsString(), type.getFullyQualifiedName().get());
                graph.addVertex(classMetadata);
            }
        }));

        compilationUnits.forEach(cu -> cu.getTypes().forEach(type -> {
            if (type.getFullyQualifiedName().isPresent()) {
                for (FieldDeclaration field : type.getFields()) {
                    try {
                        ResolvedType resolvedType = field.getElementType().resolve();
                        if (resolvedType.isReferenceType()) {
                            if (resolvedType.asReferenceType().getTypeDeclaration().isPresent()) {
                                ClassMetadata classMetadata = new ClassMetadata(type.getNameAsString(), type.getFullyQualifiedName().get());
                                ResolvedReferenceTypeDeclaration referenceTypeDeclaration = resolvedType.asReferenceType().getTypeDeclaration().get();
                                ClassMetadata fieldClassMetadata = new ClassMetadata(referenceTypeDeclaration.getName(), referenceTypeDeclaration.getQualifiedName());
                                if (graph.contains(fieldClassMetadata)) {
                                    graph.addEdge(classMetadata, fieldClassMetadata);
                                }
                            }
                        }
                    } catch (UnsolvedSymbolException _) {
                        logger.debug("Unresolved class: {}", field.getElementType());
                    }
                }
            }
        }));

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
        logger.debug("Remove unused classes iterations: {}", iterations);

        return graph;
    }
}
