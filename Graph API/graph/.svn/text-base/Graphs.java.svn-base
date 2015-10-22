package graph;

import java.util.List;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.LinkedList;

/** Assorted graph algorithms.
 *  @author Julian Wong
 */
public final class Graphs {

    /* A* Search Algorithms */

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the edge weighter EWEIGHTER.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, uses VWEIGHTER to set the weight of vertex v
     *  to the weight of a minimal path from V0 to v, for each v in
     *  the returned path and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *              < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.  If V1 is
     *  unreachable from V0, returns null and sets the minimum path weights of
     *  all reachable nodes.  The distance to a node unreachable from V0 is
     *  Double.POSITIVE_INFINITY. */
    public static <VLabel, ELabel> List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 final Graph<VLabel, ELabel>.Vertex V1,
                 final Distancer<? super VLabel> h,
                 final Weighter<? super VLabel> vweighter,
                 final Weighting<? super ELabel> eweighter) {
        TreeSet<LinkedList<Graph<VLabel, ELabel>.Edge>> open =
            new TreeSet<LinkedList<Graph<VLabel, ELabel>.Edge>>
            (new Comparator<LinkedList<Graph<VLabel, ELabel>.Edge>>() {
                @Override
                public int compare(LinkedList<Graph<VLabel, ELabel>.Edge> g1,
                                   LinkedList<Graph<VLabel, ELabel>.Edge> g2) {
                    Graph<VLabel, ELabel>.Edge e1, e2; e1 = g1.getLast();
                    e2 = g2.getLast(); Graph<VLabel, ELabel>.Vertex v1, v2;
                    v1 = vweighter.weight(e1.getV0().getLabel())
                        > vweighter.weight(e1.getV1().getLabel()) ? e1.getV1()
                        : e1.getV0();
                    double value1 = vweighter.weight(v1.getLabel())
                        + eweighter.weight(e1.getLabel())
                        + h.dist(e1.getV(v1).getLabel(), V1.getLabel());
                    v2 = vweighter.weight(e2.getV0().getLabel())
                        > vweighter.weight(e2.getV1().getLabel()) ? e2.getV1()
                        : e2.getV0();
                    double value2 = vweighter.weight(v2.getLabel())
                        + eweighter.weight(e2.getLabel())
                        + h.dist(e2.getV(v2).getLabel(), V1.getLabel());
                    return value1 > value2 ? 1 : value2 > value1 ? -1
                        : v1.index() == v2.index() ? 0 : -1;
                }
            });
        LinkedList<Graph<VLabel, ELabel>.Vertex> closed =
            new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        LinkedList<Graph<VLabel, ELabel>.Edge> current =
            new LinkedList<Graph<VLabel, ELabel>.Edge>();
        Graph<VLabel, ELabel>.Vertex V = V0; double weight = 0;
        while (true) {
            if (vweighter.weight(V.getLabel()) == Double.POSITIVE_INFINITY) {
                vweighter.setWeight(V.getLabel(), weight); closed.add(V);
            }
            if (V.equals(V1)) {
                return current;
            }
            for (Graph<VLabel, ELabel>.Edge e: G.outEdges(V)) {
                if (!closed.contains(e.getV(V))) {
                    LinkedList<Graph<VLabel, ELabel>.Edge> copy =
                        new LinkedList<Graph<VLabel, ELabel>.Edge>(current);
                    copy.add(e); open.add(copy);
                }
            }
            if (open.size() > 0) {
                current = open.pollFirst();
                if (vweighter.weight(current.getLast().getV0().getLabel())
                    == Double.POSITIVE_INFINITY) {
                    V = current.getLast().getV1();
                } else {
                    V = current.getLast().getV0();
                }
            } else {
                return null;
            }
            weight = vweighter.weight(V.getLabel())
                + eweighter.weight(current.getLast().getLabel());
            V = current.getLast().getV(V);
        }
    }

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the weights of its edge labels.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, sets the weight of vertex v to the weight of
     *  a minimal path from V0 to v, for each v in the returned path
     *  and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *           < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.
     *
     *  This function has the same effect as the 6-argument version of
     *  shortestPath, but uses the .weight and .setWeight methods of
     *  the edges and vertices themselves to determine and set
     *  weights. If V1 is unreachable from V0, returns null and sets
     *  the minimum path weights of all reachable nodes.  The distance
     *  to a node unreachable from V0 is Double.POSITIVE_INFINITY. */
    public static
    <VLabel extends Weightable, ELabel extends Weighted>
    List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 final Graph<VLabel, ELabel>.Vertex V1,
                 final Distancer<? super VLabel> h) {
        TreeSet<LinkedList<Graph<VLabel, ELabel>.Edge>> open =
            new TreeSet<LinkedList<Graph<VLabel, ELabel>.Edge>>
            (new Comparator<LinkedList<Graph<VLabel, ELabel>.Edge>>() {
                @Override
                public int compare(LinkedList<Graph<VLabel, ELabel>.Edge> g1,
                                   LinkedList<Graph<VLabel, ELabel>.Edge> g2) {
                    Graph<VLabel, ELabel>.Edge e1, e2; e1 = g1.getLast();
                    e2 = g2.getLast(); Graph<VLabel, ELabel>.Vertex v1, v2;
                    v1 = e1.getV0().getLabel().weight()
                        > e1.getV1().getLabel().weight()
                        ? e1.getV1() : e1.getV0();
                    v2 = e2.getV0().getLabel().weight()
                        > e2.getV1().getLabel().weight()
                        ? e2.getV1() : e2.getV0();
                    double value1 = v1.getLabel().weight()
                        + e1.getLabel().weight()
                        + h.dist(e1.getV(v1).getLabel(), V1.getLabel());
                    double value2 = v2.getLabel().weight()
                        + e2.getLabel().weight()
                        + h.dist(e2.getV(v2).getLabel(), V1.getLabel());
                    return value1 > value2 ? 1 : value2 > value1 ? -1
                        : v1.index() == v2.index() ? 0 : -1;
                }
            });
        LinkedList<Graph<VLabel, ELabel>.Vertex> closed =
            new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        LinkedList<Graph<VLabel, ELabel>.Edge> current =
            new LinkedList<Graph<VLabel, ELabel>.Edge>();
        Graph<VLabel, ELabel>.Vertex V = V0; double weight = 0;
        while (true) {
            if (V.getLabel().weight() == Double.POSITIVE_INFINITY) {
                V.getLabel().setWeight(weight); closed.add(V);
            }
            if (V.equals(V1)) {
                return current;
            }
            for (Graph<VLabel, ELabel>.Edge e: G.outEdges(V)) {
                if (!closed.contains(e.getV(V))) {
                    LinkedList<Graph<VLabel, ELabel>.Edge> copy =
                        new LinkedList<Graph<VLabel, ELabel>.Edge>(current);
                    copy.add(e); open.add(copy);
                }
            }
            if (open.size() > 0) {
                current = open.pollFirst();
                if (current.getLast().getV0().getLabel().weight()
                    == Double.POSITIVE_INFINITY) {
                    V = current.getLast().getV1();
                } else {
                    V = current.getLast().getV0();
                }
            } else {
                return null;
            }
            weight = V.getLabel().weight()
                + current.getLast().getLabel().weight();
            V = current.getLast().getV(V);
        }
    }

    /** Returns a distancer whose dist method always returns 0. */
    public static final Distancer<Object> ZERO_DISTANCER =
        new Distancer<Object>() {
            @Override
            public double dist(Object v0, Object v1) {
                return 0.0;
            }
        };
}
