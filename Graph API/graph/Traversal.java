package graph;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.ArrayList;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular set of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.  The client can dictate an ordering on
 *  the fringe, determining which item is next removed, by which kind
 *  of traversal is requested.
 *     + A depth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at one end.  It also revisits the node
 *       itself after traversing all successors by calling the
 *       postVisit method on it.
 *     + A breadth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at different ends.  It also revisits the node
 *       itself after traversing all successors as for depth-first
 *       traversals.
 *     + A general traversal treats the fringe as an ordered set, as
 *       determined by a Comparator argument.  There is no postVisit
 *       for this type of traversal.
 *  As vertices are added to the fringe, the traversal calls a
 *  preVisit method on the vertex.
 *
 *  Generally, the client will extend Traversal, overriding the visit,
 *  preVisit, and postVisit methods, as desired (by default, they do nothing).
 *  Any of these methods may throw StopException to halt the traversal
 *  (temporarily, if desired).  The preVisit method may throw a
 *  RejectException to prevent a vertex from being added to the
 *  fringe, and the visit method may throw a RejectException to
 *  prevent its successors from being added to the fringe.
 *  @author Julian Wong
 */
public class Traversal<VLabel, ELabel> {

    /** Perform a traversal of G over all vertices reachable from V.
     *  ORDER determines the ordering in which the fringe of
     *  untraversed vertices is visited.  The effect of specifying an
     *  ORDER whose results change as a result of modifications made during the
     *  traversal is undefined. */
    public void traverse(Graph<VLabel, ELabel> G,
                         Graph<VLabel, ELabel>.Vertex v,
                         final Comparator<VLabel> order) {
        _compare = order;
        traversal = Trav.GEN;
        TreeSet<Graph<VLabel, ELabel>.Vertex> set =
            new TreeSet
            <Graph<VLabel, ELabel>.Vertex>(new Comparator
                                           <Graph<VLabel, ELabel>.Vertex>() {
                    public int compare(Graph<VLabel, ELabel>.Vertex v1,
                                       Graph<VLabel, ELabel>.Vertex v2) {
                        int temp = order.compare(v1.getLabel(), v2.getLabel());
                        return temp != 0 ? temp : -1;
                    }
                });
        set.add(v);
        while (!set.isEmpty()) {
            _finalVertex = set.first();
            try {
                visit(_finalVertex);
                _finalVertex.mark();
            } catch (StopException k) {
                System.out.printf("Error: %s", k);
                System.exit(1);
            }
            if (G.outDegree(_finalVertex) > 0) {
                for (Graph<VLabel, ELabel>.Edge e: G.outEdges(_finalVertex)) {
                    if (!e.getV(_finalVertex).marked()) {
                        try {
                            preVisit(e, _finalVertex);
                            set.add(e.getV(_finalVertex));
                        } catch (StopException k) {
                            System.out.printf("Error: %s", k);
                            System.exit(1);
                        } catch (RejectException k) {
                            e.getV(_finalVertex).mark();
                            continue;
                        }
                    }
                }
            }
            set.remove(_finalVertex);
        }
    }

    /** Performs a depth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it or removed from it at one end in
     *  an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void depthFirstTraverse(Graph<VLabel, ELabel> G,
                                   Graph<VLabel, ELabel>.Vertex v) {
        traversal = Trav.DFS;
        LinkedList<Graph<VLabel, ELabel>.Vertex> list =
            new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> postvisited =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        list.add(v);
        while (!list.isEmpty()) {
            _finalVertex = list.getFirst();
            if (!_finalVertex.marked()) {
                try {
                    visit(_finalVertex);
                    _finalVertex.mark();
                } catch (StopException k) {
                    System.out.printf("Error: %s", k);
                    System.exit(1);
                }
                if (G.outDegree(_finalVertex) > 0) {
                    for (Graph<VLabel, ELabel>.Edge e
                             : G.outEdges(_finalVertex)) {
                        if (!e.getV(_finalVertex).marked()) {
                            try {
                                list.addFirst(e.getV(_finalVertex));
                                preVisit(e, _finalVertex);
                            } catch (StopException k) {
                                System.out.printf("Error: %s", k);
                                System.exit(1);
                            } catch (RejectException k) {
                                e.getV(_finalVertex).mark();
                                continue;
                            }
                        }
                    }
                }
            } else {
                if (!postvisited.contains(_finalVertex)) {
                    try {
                        postVisit(_finalVertex);
                        postvisited.add(_finalVertex);
                    } catch (StopException k) {
                        System.out.printf("Error: %s", k);
                        System.exit(1);
                    }
                }
                list.remove();
            }
        }
    }

    /** Performs a breadth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it at one end and removed from it at the
     *  other in an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void breadthFirstTraverse(Graph<VLabel, ELabel> G,
                                     Graph<VLabel, ELabel>.Vertex v) {
        traversal = Trav.BFS;
        LinkedList<Graph<VLabel, ELabel>.Vertex> list =
            new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> postvisited =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> marked =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        list.add(v);
        while (!list.isEmpty()) {
            _finalVertex = list.getFirst();
            if (!_finalVertex.marked()) {
                try {
                    visit(_finalVertex);
                    _finalVertex.mark();
                } catch (StopException k) {
                    System.out.printf("Error: %s", k);
                }
                if (G.outDegree(_finalVertex) > 0) {
                    for (Graph<VLabel, ELabel>.Edge e
                             :G.outEdges(_finalVertex)) {
                        if (!e.getV(_finalVertex).marked()) {
                            try {
                                preVisit(e, _finalVertex);
                                list.add(e.getV(_finalVertex));
                            } catch (StopException k) {
                                System.out.printf("Error: %s", k);
                                System.exit(1);
                            } catch (RejectException k) {
                                e.getV(_finalVertex).mark();
                                continue;
                            }
                        }
                    }
                }
                list.add(_finalVertex);
            } else {
                if (!postvisited.contains(_finalVertex)) {
                    try {
                        postVisit(_finalVertex);
                        postvisited.add(_finalVertex);
                    } catch (StopException k) {
                        System.out.printf("Error: %s", k);
                        System.exit(1);
                    }
                }
            }
            list.remove(_finalVertex);
        }
    }

    /** Continue the previous traversal starting from V.
     *  Continuing a traversal means that we do not traverse
     *  vertices that have been traversed previously. */
    public void continueTraversing(Graph<VLabel, ELabel>.Vertex v) {
        switch (traversal) {
        case GEN:
            traverse(_graph, v, _compare);
            break;
        case DFS:
            depthFirstTraverse(_graph, v);
            break;
        case BFS:
            breadthFirstTraverse(_graph, v);
            break;
        default:
            break;
        }
    }

    /** If the traversal ends prematurely, returns the Vertex argument to
     *  preVisit, visit, or postVisit that caused a Visit routine to
     *  return false.  Otherwise, returns null. */
    public Graph<VLabel, ELabel>.Vertex finalVertex() {
        return _finalVertex;
    }

    /** If the traversal ends prematurely, returns the Edge argument to
     *  preVisit that caused a Visit routine to return false. If it was not
     *  an edge that caused termination, returns null. */
    public Graph<VLabel, ELabel>.Edge finalEdge() {
        return _finalEdge;
    }

    /** Returns the last graph argument to a traverse routine, or null if none
     *  of these methods have been called. */
    protected Graph<VLabel, ELabel> theGraph() {
        return _graph;
    }

    /** Method to be called when adding the node at the other end of E from V0
     *  to the fringe. If this routine throws a StopException,
     *  the traversal ends.  If it throws a RejectException, the edge
     *  E is not traversed. The default does nothing.
     */
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
                            Graph<VLabel, ELabel>.Vertex v0) {
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** Method to be called immediately after finishing the traversal
     *  of successors of vertex V in pre- and post-order traversals.
     *  If this routine throws a StopException, the traversal ends.
     *  Throwing a RejectException has no effect. The default does nothing.
     */
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** The Vertex (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Vertex _finalVertex;
    /** The Edge (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Edge _finalEdge;
    /** The last graph traversed. */
    protected Graph<VLabel, ELabel> _graph;
    /** Enum. */
    enum Trav {
        /** Enums. */
        GEN, DFS, BFS
    }
    /** The comparator. */
    private Comparator<VLabel> _compare;
    /** The traversal. */
    private Trav traversal;
}
