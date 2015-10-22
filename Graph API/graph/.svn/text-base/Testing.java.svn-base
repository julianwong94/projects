package graph;

import org.junit.Test;
import ucb.junit.textui;
import static org.junit.Assert.*;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your graph package per se (that is, it must be
 * possible to remove them and still have your package work). */

/** Unit tests for the graph package.
 *  @author
 */
public class Testing {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(graph.Testing.class));
    }
    @Test
    public void emptyGraph() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void simpleGraph() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("1");
        Graph<String, String>.Vertex v2 = g.add("2");
        assertEquals("Should have 2 vertices", 2, g.vertexSize());
        g.add(v1, v2, "3");
        assertEquals("Should have 1 edge", 1, g.edgeSize());
        assertEquals("Should be 1", 1, g.outDegree(v1));
        assertEquals("Should be 0", 0, g.outDegree(v2));
        assertEquals("Should not have", 0, g.inDegree(v1));
        assertEquals("1", 1, g.inDegree(v2));
    }
    @Test
    public void removeOperations() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("1");
        Graph<String, String>.Vertex v2 = g.add("2");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "3");
        g.remove(e1);
        assertEquals("Should have none", 0, g.edgeSize());
        assertEquals("Should have no edges", 0, g.outDegree(v1));
        g.remove(v1);
        assertEquals("Should have one", 1, g.vertexSize());
    }

    @Test
    public void undirectedGraph() {
        UndirectedGraph<String, String> g =
            new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("1");
        Graph<String, String>.Vertex v2 = g.add("2");
        assertEquals("Should have 2 vertices", 2, g.vertexSize());
        g.add(v1, v2, "3");
        assertEquals("Should have 1 edge", 1, g.edgeSize());
        assertEquals("Should be 1", 1, g.outDegree(v1));
        assertEquals("Should be 1", 1, g.inDegree(v1));
    }
    @Test
    public void moreUndirected() {
        UndirectedGraph<String, String> g =
            new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("1");
        Graph<String, String>.Vertex v2 = g.add("2");
        g.add(v1, v2, "3");
        g.add(v2, v1, "4");
        assertEquals("Should have 2 edge", 2, g.edgeSize());
        assertEquals("Should be 2", 2, g.outDegree(v1));
        assertEquals("Please be 2", 2, g.inDegree(v1));
    }
    @Test
    public void undirectedRemove() {
        UndirectedGraph<String, String> g =
            new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("1");
        Graph<String, String>.Vertex v2 = g.add("2");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "3");
        g.remove(e1);
        assertEquals("Should have no edge", 0, g.edgeSize());
        assertEquals("Should be 0", 0, g.outDegree(v1));
        assertEquals("Should be not have", 0, g.inDegree(v1));
    }

    @Test
    public void undirectedIterator() {
        UndirectedGraph<String, String> g =
            new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("1");
        Graph<String, String>.Vertex v2 = g.add("2");
        g.add(v1, v2, "3");
        g.add(v2, v1, "4");
        int index = 0;
        for (UndirectedGraph<String, String>.Edge e: g.edges()) {
            index += 1;
        }
        assertEquals("Should have 2", 2, index);
        index = 0;
        for (UndirectedGraph<String, String>.Edge e: g.outEdges(v1)) {
            index += 1;
        }
        assertEquals("Should equal 2", 2, index);
        index = 0;
        for (UndirectedGraph<String, String>.Edge e: g.inEdges(v1)) {
            index += 1;
        }
        assertEquals("Please have 2", 2, index);
    }

}
