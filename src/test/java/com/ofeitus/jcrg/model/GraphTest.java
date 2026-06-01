package com.ofeitus.jcrg.model;

import com.ofeitus.jcrg.graph.DuplicateVertexException;
import com.ofeitus.jcrg.graph.NoSuchEdgeException;
import com.ofeitus.jcrg.graph.NoSuchVertexException;
import com.ofeitus.jcrg.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void addVertexTest() {
        Graph<Integer> graph = new Graph<>();
        assertEquals(0, graph.size());
        assertFalse(graph.contains(1));
        graph.addVertex(1);
        assertEquals(1, graph.size());
        assertTrue(graph.contains(1));
        assertTrue(graph.vertices().contains(1));
    }

    @Test
    void duplicateVertexTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        assertThrows(DuplicateVertexException.class, () -> graph.addVertex(1));
    }

    @Test
    void addEdgeIfDoesNotContainVertexTest() {
        Graph<Integer> graph = new Graph<>();
        assertThrows(NoSuchVertexException.class, () -> graph.addEdge(1, 2));

        graph.addVertex(1);
        assertThrows(NoSuchVertexException.class, () -> graph.addEdge(1, 2));

        graph.addVertex(4);
        assertThrows(NoSuchVertexException.class, () -> graph.addEdge(3, 4));
    }

    @Test
    void outgoingEdgesTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        Set<Integer> adjacency = graph.outgoingEdges(1);
        assertTrue(adjacency.contains(2));
        assertTrue(adjacency.contains(3));
    }

    @Test
    void incomingEdgesTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addEdge(2, 1);
        graph.addEdge(3, 1);
        Set<Integer> adjacency = graph.incomingEdges(1);
        assertTrue(adjacency.contains(2));
        assertTrue(adjacency.contains(3));
    }

    @Test
    void outgoingEdgesIfDoesNotContainVertexTest() {
        Graph<Integer> graph = new Graph<>();
        assertThrows(NoSuchVertexException.class, () -> graph.outgoingEdges(1));
    }

    @Test
    void incomingEdgesIfDoesNotContainVertexTest() {
        Graph<Integer> graph = new Graph<>();
        assertThrows(NoSuchVertexException.class, () -> graph.incomingEdges(1));
    }

    @Test
    void removeVertexTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.removeVertex(1);
        assertFalse(graph.contains(1));
    }

    @Test
    void removeVertexIfDoesNotContainTest() {
        Graph<Integer> graph = new Graph<>();
        assertThrows(NoSuchVertexException.class, () -> graph.removeVertex(1));
    }

    @Test
    void removeVertexWithOutgoingEdgesTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addEdge(1, 2);
        graph.removeVertex(1);
        assertTrue(graph.incomingEdges(2).isEmpty());
    }

    @Test
    void removeVertexWithIncomingEdgesTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addEdge(2, 1);
        graph.removeVertex(1);
        assertTrue(graph.outgoingEdges(2).isEmpty());
    }

    @Test
    void removeEdgeTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addEdge(1, 2);
        graph.removeEdge(1, 2);
        assertTrue(graph.outgoingEdges(1).isEmpty());
        assertTrue(graph.incomingEdges(2).isEmpty());
    }

    @Test
    void removeEdgeIfDoesNotContainTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        assertThrows(NoSuchEdgeException.class, () -> graph.removeEdge(1, 2));
    }

    @Test
    void removeEdgeIfDoesNotContainVertexTest() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        assertThrows(NoSuchVertexException.class, () -> graph.removeEdge(1, 2));
    }
}