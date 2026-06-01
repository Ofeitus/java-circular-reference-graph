package com.ofeitus.jcrg.resolver;

import com.ofeitus.jcrg.graph.Graph;

import java.util.*;

public class SubgraphExtractor {

    public static <T> List<Graph<T>> getConnectedComponents(Graph<T> graph) {
        Set<T> visited = new HashSet<>();
        List<Graph<T>> subgraphs = new ArrayList<>();

        for (T vertex : graph.vertices()) {
            if (!visited.contains(vertex)) {
                Graph<T> newSubgraph = new Graph<>();
                dfs(vertex, graph, visited, newSubgraph);
                subgraphs.add(newSubgraph);
            }
        }
        return subgraphs;
    }

    private static <T> void dfs(T current, Graph<T> graph, Set<T> visited, Graph<T> newSubgraph) {
        visited.add(current);
        if (!newSubgraph.contains(current)) {
            newSubgraph.addVertex(current);
        }

        for (T adjacent : graph.outgoingEdges(current)) {
            if (!newSubgraph.contains(adjacent)) {
                newSubgraph.addVertex(adjacent);
            }
            newSubgraph.addEdge(current, adjacent);
            if (!visited.contains(adjacent)) {
                dfs(adjacent, graph, visited, newSubgraph);
            }
        }

        for (T adjacent : graph.incomingEdges(current)) {
            if (!newSubgraph.contains(adjacent)) {
                newSubgraph.addVertex(adjacent);
            }
            newSubgraph.addEdge(adjacent, current);
            if (!visited.contains(adjacent)) {
                dfs(adjacent, graph, visited, newSubgraph);
            }
        }
    }
}

