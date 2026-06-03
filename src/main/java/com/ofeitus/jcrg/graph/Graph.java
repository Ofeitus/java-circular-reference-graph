package com.ofeitus.jcrg.graph;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Graph<T> {

    private final Map<T, Set<T>> adjacencyList = new ConcurrentHashMap<>();
    private final Map<T, Set<T>> inverseAdjacencyList = new ConcurrentHashMap<>();

    public int size() {
        return adjacencyList.size();
    }

    public boolean contains(T vertex) {
        return adjacencyList.containsKey(vertex);
    }

    public Set<T> vertices() {
        return adjacencyList.keySet();
    }

    public Set<T> outgoingEdges(T vertex) {
        if (!contains(vertex)) {
            throw new NoSuchVertexException();
        }
        return adjacencyList.get(vertex);
    }

    public Set<T> incomingEdges(T vertex) {
        if (!contains(vertex)) {
            throw new NoSuchVertexException();
        }
        return inverseAdjacencyList.get(vertex);
    }

    public void addVertex(T vertex) {
        if (contains(vertex)) {
            throw new DuplicateVertexException();
        }
        adjacencyList.put(vertex, ConcurrentHashMap.newKeySet());
        inverseAdjacencyList.put(vertex, ConcurrentHashMap.newKeySet());
    }

    public void removeVertex(T vertex) {
        if (!contains(vertex)) {
            throw new NoSuchVertexException();
        }
        adjacencyList.get(vertex).forEach(adjacent -> inverseAdjacencyList.get(adjacent).remove(vertex));
        inverseAdjacencyList.get(vertex).forEach(adjacent -> adjacencyList.get(adjacent).remove(vertex));

        adjacencyList.remove(vertex);
        inverseAdjacencyList.remove(vertex);
    }

    public void addEdge(T source, T destination) {
        if (!contains(source) || !contains(destination)) {
            throw new NoSuchVertexException();
        }
        adjacencyList.get(source).add(destination);
        inverseAdjacencyList.get(destination).add(source);
    }

    public void removeEdge(T source, T destination) {
        if (!contains(source) || !contains(destination)) {
            throw new NoSuchVertexException();
        }
        if (!adjacencyList.get(source).contains(destination)) {
            throw new NoSuchEdgeException();
        }
        adjacencyList.get(source).remove(destination);
        inverseAdjacencyList.get(destination).remove(source);
    }

    public List<Graph<T>> connectedComponents() {
        Set<T> visited = new HashSet<>();
        List<Graph<T>> subgraphs = new ArrayList<>();

        for (T vertex : vertices()) {
            if (!visited.contains(vertex)) {
                Graph<T> newSubgraph = new Graph<>();
                subgraphDFS(vertex, visited, newSubgraph);
                subgraphs.add(newSubgraph);
            }
        }
        return subgraphs;
    }

    private void subgraphDFS(T current, Set<T> visited, Graph<T> newSubgraph) {
        visited.add(current);
        if (!newSubgraph.contains(current)) {
            newSubgraph.addVertex(current);
        }

        for (T adjacent : outgoingEdges(current)) {
            if (!newSubgraph.contains(adjacent)) {
                newSubgraph.addVertex(adjacent);
            }
            newSubgraph.addEdge(current, adjacent);
            if (!visited.contains(adjacent)) {
                subgraphDFS(adjacent, visited, newSubgraph);
            }
        }

        for (T adjacent : incomingEdges(current)) {
            if (!newSubgraph.contains(adjacent)) {
                newSubgraph.addVertex(adjacent);
            }
            newSubgraph.addEdge(adjacent, current);
            if (!visited.contains(adjacent)) {
                subgraphDFS(adjacent, visited, newSubgraph);
            }
        }
    }

    public List<List<T>> elementaryCycles() {
        List<List<T>> allCycles = new ArrayList<>();
        Set<T> blockedSet = new HashSet<>();
        Map<T, Set<T>> blockedMap = new HashMap<>();
        Deque<T> stack = new ArrayDeque<>();

        for (T vertex : vertices()) {
            blockedMap.put(vertex, new HashSet<>());
        }

        List<T> vertexList = vertices().stream().sorted().toList();
        for (int i = 0; i < vertexList.size(); i++) {
            T startVertex = vertexList.get(i);
            Set<T> subGraph = new HashSet<>();
            for (int j = i; j < vertexList.size(); j++) {
                subGraph.add(vertexList.get(j));
            }
            blockedSet.clear();
            for (T vertex : vertices()) {
                blockedMap.get(vertex).clear();
            }
            findCyclesJohnson(startVertex, startVertex, subGraph, blockedSet, blockedMap, stack, allCycles);
        }

        return allCycles;
    }

    private boolean findCyclesJohnson(
            T startVertex, T currentVertex,
            Set<T> subGraph, Set<T> blockedSet, Map<T, Set<T>> blockedMap,
            Deque<T> stack, List<List<T>> allCycles
    ) {
        boolean foundCycle = false;
        stack.push(currentVertex);
        blockedSet.add(currentVertex);

        for (T neighbor : outgoingEdges(currentVertex)) {
            if (!subGraph.contains(neighbor)) {
                continue;
            }
            if (neighbor.equals(startVertex)) {
                List<T> cycle = new ArrayList<>(stack);
                allCycles.add(cycle);
                foundCycle = true;
            } else if (!blockedSet.contains(neighbor)) {
                if (findCyclesJohnson(startVertex, neighbor, subGraph, blockedSet, blockedMap, stack, allCycles)) {
                    foundCycle = true;
                }
            }
        }

        if (foundCycle) {
            unblock(currentVertex, blockedSet, blockedMap);
        } else {
            for (T neighbor : outgoingEdges(currentVertex)) {
                if (subGraph.contains(neighbor)) {
                    blockedMap.get(neighbor).add(currentVertex);
                }
            }
        }

        stack.pop();
        return foundCycle;
    }

    private void unblock(T vertex, Set<T> blockedSet, Map<T, Set<T>> blockedMap) {
        blockedSet.remove(vertex);
        Set<T> blockedByVertex = blockedMap.get(vertex);

        while (!blockedByVertex.isEmpty()) {
            T w = blockedByVertex.iterator().next();
            blockedByVertex.remove(w);
            if (blockedSet.contains(w)) {
                unblock(w, blockedSet, blockedMap);
            }
        }
    }

    public void print() {
        System.out.println(this);
        adjacencyList.forEach((key, value) -> System.out.println(key + " -> " + value));
    }
}
