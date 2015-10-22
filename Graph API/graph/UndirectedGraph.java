package graph;

import java.util.HashMap;
import java.util.ArrayList;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may add bodies to abstract methods, modify
 * existing bodies, or override inherited methods.  */

/** An undirected graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Julian Wong
 */
public class UndirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public UndirectedGraph() {
        _vertices = new ArrayList<Vertex>();
        _edges = new ArrayList<Edge>();
        _out = new HashMap<Vertex, ArrayList<Edge>>();
    }

    @Override
    public boolean isDirected() {
        return false;
    }
    @Override
    public int inDegree(Vertex v) {
        return outDegree(v);
    }

    @Override
    public Edge add(Vertex from, Vertex to, ELabel label) {
        Edge edge = super.add(from, to, label);
        ArrayList<Edge> x = (_out.get(to) == null)
            ? new ArrayList<Edge>() : _out.get(to);
        x.add(edge);
        _out.put(to, x);
        return edge;
    }
    @Override
    public void remove(Vertex v) {
        super.remove(v);
        ArrayList<Edge> copy;
        _out.remove(v);
        for (ArrayList<Edge> value : _out.values()) {
            copy = new ArrayList<Edge>(value);
            for (Edge e: copy) {
                if (e.getV0().equals(v) || e.getV1().equals(v)) {
                    value.remove(e);
                }
            }
        }
    }
    @Override
    public void remove(Vertex v1, Vertex v2) {
        super.remove(v1, v2);
        ArrayList<Edge> copy = new ArrayList<Edge>(_out.get(v2));
        for (Edge e: copy) {
            if (e.getV(v2).equals(v1)) {
                _out.get(v2).remove(e);
            }
        }
    }
    @Override
    public Iteration<Vertex> predecessors(Vertex v) {
        return successors(v);
    }
    @Override
    public Iteration<Edge> inEdges(Vertex v) {
        return outEdges(v);
    }
}
