package graph;

import java.util.ArrayList;
import java.util.HashMap;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may add bodies to abstract methods, modify
 * existing bodies, or override inherited methods.  */

/** A directed graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Julian Wong
 */
public class DirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public DirectedGraph() {
        _vertices = new ArrayList<Vertex>();
        _edges = new ArrayList<Edge>();
        _out = new HashMap<Vertex, ArrayList<Edge>>();
        _in = new HashMap<Vertex, ArrayList<Edge>>();
    }

    @Override
    public boolean isDirected() {
        return true;
    }
    @Override
    public Edge add(Vertex from,
                    Vertex to,
                    ELabel label) {
        Edge edge = super.add(from, to, label);
        ArrayList<Edge> x  = (_in.get(to) == null)
            ? new ArrayList<Edge>() : _in.get(to);
        x.add(edge);
        _in.put(to, x);
        return edge;
    }
    @Override
    public void remove(Vertex v) {
        super.remove(v);
        ArrayList<Edge> copy;
        _in.remove(v);
        for (ArrayList<Edge> value : _in.values()) {
            copy = new ArrayList<Edge>(value);
            for (Edge e: copy) {
                if (e.getV0().equals(v) || e.getV1().equals(v)) {
                    value.remove(e);
                }
            }
        }
    }
    @Override
    public void remove(Edge e) {
        super.remove(e);
        _in.get(e.getV1()).remove(e);
        for (ArrayList<Edge> value : _out.values()) {
            value.remove(e);
        }
    }
    @Override
    public void remove(Vertex v1, Vertex v2) {
        super.remove(v1, v2);
        ArrayList<Edge> copy = new ArrayList<Edge>(_in.get(v1));
        for (Edge e: copy) {
            if (e.getV(v1).equals(v2)) {
                _in.get(v1).remove(e);
            }
        }
        copy = new ArrayList<Edge>(_in.get(v2));
        for (Edge e: copy) {
            if (e.getV(v2).equals(v1)) {
                _in.get(v2).remove(e);
            }
        }
    }
}
