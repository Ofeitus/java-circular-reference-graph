package com.ofeitus.jcrg.graph;

import java.util.Map;
import java.util.Set;
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

    public void printGraph() {
        System.out.println(this);
        adjacencyList.forEach((key, value) -> System.out.println(key + " -> " + value));
    }
}
