package graph;

import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may make changes that don't affect the API as seen
 * from outside the graph package:
 *   + You may make methods in Graph abstract, if you want different
 *     implementations in DirectedGraph and UndirectedGraph.
 *   + You may add bodies to abstract methods, modify existing bodies,
 *     or override inherited methods.
 *   + You may change parameter names, or add 'final' modifiers to parameters.
 *   + You may private and package private members.
 *   + You may add additional non-public classes to the graph package.
 */

/** Represents a general graph whose vertices are labeled with a type
 *  VLABEL and whose edges are labeled with a type ELABEL. The
 *  vertices are represented by the inner type Vertex and edges by
 *  inner type Edge.  A graph may be directed or undirected.  For
 *  an undirected graph, outgoing and incoming edges are the same.
 *  Graphs may have self edges and may have multiple edges between vertices.
 *
 *  The vertices and edges of the graph, the edges incident on a
 *  vertex, and the neighbors of a vertex are all accessible by
 *  iterators.  Changing the graph's structure by adding or deleting
 *  edges or vertices invalidates these iterators (subsequent use of
 *  them is undefined.)
 *  @author Julian Wong
 */
public abstract class Graph<VLabel, ELabel> {

    /** Represents one of my vertices. */
    public class Vertex {

        /** A new vertex with LABEL as the value of getLabel(). */
        Vertex(VLabel label) {
            _label = label;
            _marked = false;
            _number = vnumber;
            vnumber += 1;
        }

        /** Returns the label on this vertex. */
        public VLabel getLabel() {
            return _label;
        }
        /** Marks vertex. */
        public void mark() {
            _marked = true;
        }
        /** Unmarks vertex. */
        public void unmark() {
            _marked = false;
        }
        /** Returns true if marked. */
        public boolean marked() {
            return _marked;
        }
        /** Returns index. */
        public int index() {
            return _number;
        }
        /** The label on this vertex. */
        private final VLabel _label;
        /** True if marked. */
        private boolean _marked;
        /** Index number. */
        private int _number;

    }

    /** Represents one of my edges. */
    public class Edge {

        /** An edge (V0,V1) with label LABEL.  It is a directed edge (from
         *  V0 to V1) in a directed graph. */
        Edge(Vertex v0, Vertex v1, ELabel label) {
            _label = label;
            _v0 = v0;
            _v1 = v1;
        }

        /** Returns the label on this edge. */
        public ELabel getLabel() {
            return _label;
        }

        /** Return the vertex this edge exits. For an undirected edge, this is
         *  one of the incident vertices. */
        public Vertex getV0() {
            return _v0;
        }

        /** Return the vertex this edge enters. For an undirected edge, this is
         *  the incident vertices other than getV1(). */
        public Vertex getV1() {
            return _v1;
        }

        /** Returns the vertex at the other end of me from V.  */
        public final Vertex getV(Vertex v) {
            if (v == _v0) {
                return _v1;
            } else if (v == _v1) {
                return _v0;
            } else {
                throw new
                    IllegalArgumentException("vertex not incident to edge");
            }
        }

        @Override
        public String toString() {
            return String.format("(%s,%s):%s", _v0, _v1, _label);
        }
        /** Endpoints of this edge.  In directed edges, this edge exits _V0
         *  and enters _V1. */
        private final Vertex _v0, _v1;

        /** The label on this edge. */
        private final ELabel _label;

    }

    /*=====  Methods and variables of Graph =====*/

    /** Returns the number of vertices in me. */
    public int vertexSize() {
        return _vertices.size();
    }

    /** Returns the number of edges in me. */
    public int edgeSize() {
        return _edges.size();
    }

    /** Returns true iff I am a directed graph. */
    public abstract boolean isDirected();

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    public int outDegree(Vertex v) {
        return _out.get(v) != null ? _out.get(v).size() : 0;
    }

    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    public int inDegree(Vertex v) {
        return _in.get(v) != null ? _in.get(v).size() : 0;
    }

    /** Returns outDegree(V). This is simply a synonym, intended for
     *  use in undirected graphs. */
    public final int degree(Vertex v) {
        return outDegree(v);
    }

    /** Returns true iff there is an edge (U, V) in me with any label. */
    public boolean contains(Vertex u, Vertex v) {
        if (_out.get(u) != null) {
            for (Edge e : _out.get(u)) {
                if (e.getV(u).equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    public boolean contains(Vertex u, Vertex v,
                            ELabel label) {
        if (_out.get(u) != null) {
            for (Edge e: _out.get(u)) {
                if (e.getV(u).equals(v) && e.getLabel().equals(label)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns a new vertex labeled LABEL, and adds it to me with no
     *  incident edges. */
    public Vertex add(VLabel label) {
        Vertex vertex = new Vertex(label);
        _vertices.add(vertex);
        return vertex;
    }

    /** Returns an edge incident on FROM and TO, labeled with LABEL
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to,
                    ELabel label) {
        Edge edge = new Edge(from, to, label);
        _edges.add(edge);
        ArrayList<Edge> x = (_out.get(from) == null)
            ? new ArrayList<Edge>() : _out.get(from);
        x.add(edge);
        _out.put(from, x);
        return edge;
    }

    /** Returns an edge incident on FROM and TO with a null label
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to) {
        return add(from, to, null);
    }

    /** Remove V and all adjacent edges, if present. */
    public void remove(Vertex v) {
        ArrayList<Edge> copy;
        if (_vertices.contains(v)) {
            _vertices.remove(v);
            copy = new ArrayList<Edge>(_edges);
            for (Edge e: copy) {
                if (e.getV0().equals(v) || e.getV1().equals(v)) {
                    _edges.remove(e);
                }
            }
            _out.remove(v);
            for (ArrayList<Edge> value: _out.values()) {
                copy = new ArrayList<Edge>(value);
                for (Edge e: copy) {
                    if (e.getV0().equals(v) || e.getV1().equals(v)) {
                        value.remove(e);
                    }
                }
            }
        }
    }

    /** Remove E from me, if present.  E must be between my vertices,
     *  or the result is undefined.  */
    public void remove(Edge e) {
        _edges.remove(e);
        _out.get(e.getV0()).remove(e);
        for (ArrayList<Edge> value : _out.values()) {
            value.remove(e);
        }
    }


    /** Remove all edges from V1 to V2 from me, if present.  The result is
     *  undefined if V1 and V2 are not among my vertices.  */
    public void remove(Vertex v1, Vertex v2) {
        if (_vertices.contains(v1) && _vertices.contains(v2)) {
            ArrayList<Edge> copy = new ArrayList<Edge>(_edges);
            for (Edge e: copy) {
                if (e.getV0().equals(v1) && e.getV1().equals(v2)) {
                    _edges.remove(e);
                }
            }
            copy = new ArrayList<Edge>(_out.get(v1));
            for (Edge e: copy) {
                if (e.getV(v1).equals(v2)) {
                    _out.get(v1).remove(e);
                }
            }
        }
    }
    /** Returns an Iterator over all vertices in arbitrary order. */
    public Iteration<Vertex> vertices() {
        return Iteration.iteration(_vertices.iterator());
    }

    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> ver = new ArrayList<Vertex>();
        if (_out.get(v) != null) {
            for (Edge e: _out.get(v)) {
                ver.add(e.getV(v));
            }
        }
        return Iteration.iteration(ver.iterator());
    }

    /** Returns an iterator over all predecessors of V. */
    public Iteration<Vertex> predecessors(Vertex v) {
        ArrayList<Vertex> ver = new ArrayList<Vertex>();
        if (_in.get(v) != null) {
            for (Edge e: _in.get(v)) {
                ver.add(e.getV(v));
            }
        }
        return Iteration.iteration(ver.iterator());
    }

    /** Returns successors(V).  This is a synonym typically used on
     *  undirected graphs. */
    public final Iteration<Vertex> neighbors(Vertex v) {
        return successors(v);
    }

    /** Returns an iterator over all edges in me. */
    public Iteration<Edge> edges() {
        return Iteration.iteration(_edges.iterator());
    }

    /** Returns iterator over all outgoing edges from V. */
    public Iteration<Edge> outEdges(Vertex v) {
        ArrayList<Edge> edg = new ArrayList<Edge>();
        if (_out.get(v) != null) {
            edg = new ArrayList<Edge>(_out.get(v));
        }
        return Iteration.iteration(edg.iterator());
    }

    /** Returns iterator over all incoming edges to V. */
    public Iteration<Edge> inEdges(Vertex v) {
        ArrayList<Edge> edg = new ArrayList<Edge>();
        if (_in.get(v) != null) {
            edg = new ArrayList<Edge>(_in.get(v));
        }
        return Iteration.iteration(edg.iterator());
    }

    /** Returns outEdges(V). This is a synonym typically used
     *  on undirected graphs. */
    public final Iteration<Edge> edges(Vertex v) {
        return outEdges(v);
    }

    /** Returns the natural ordering on T, as a Comparator.  For
     *  example, if intComp = Graph.<Integer>naturalOrder(), then
     *  intComp.compare(x1, y1) is <0 if x1<y1, ==0 if x1=y1, and >0
     *  otherwise. */
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
    {
        return new Comparator<T>() {
            @Override
            public int compare(T x1, T x2) {
                return x1.compareTo(x2);
            }
        };
    }


    /** Cause subsequent calls to edges() to visit or deliver
     *  edges in sorted order, according to COMPARATOR. Subsequent
     *  addition of edges may cause the edges to be reordered
     *  arbitrarily.  */
    public void orderEdges(final Comparator<ELabel> comparator) {
        Collections.sort(_edges, new Comparator<Edge>() {
                public int compare(Edge v1, Edge v2) {
                    return comparator.compare(v1.getLabel(), v2.getLabel());
                }
            });
    }
    /** Holds the vertices. */
    protected ArrayList<Vertex> _vertices;
    /** Holds the edges. */
    protected ArrayList<Edge> _edges;
    /** Holds outedges. */
    protected HashMap<Vertex, ArrayList<Edge>> _out;
    /** Holds inedges. */
    protected HashMap<Vertex, ArrayList<Edge>> _in;
    /** Index number of vertices. */
    private int vnumber;
}
