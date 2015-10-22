package trip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import graph.Graph;
import graph.Graphs;
import graph.Weightable;
import graph.Weighted;
import graph.Distancer;
import graph.UndirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import java.util.ListIterator;

/** Initial class for the 'trip' program.
 *  @author Julian Wong
 */
public final class Main {

    /** Entry point for the CS61B trip program.  ARGS may contain options
     *  and targets:
     *      [ -m MAP ] [ -o OUT ] [ REQUEST ]
     *  where MAP (default Map) contains the map data, OUT (default standard
     *  output) takes the result, and REQUEST (default standard input) contains
     *  the locations along the requested trip.
     */
    public static void main(String... args) {
        String mapFileName;
        String outFileName;
        String requestFileName;

        mapFileName = "Map";
        outFileName = requestFileName = null;

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-m")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    mapFileName = args[a];
                }
            } else if (args[a].equals("-o")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    outFileName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        if (a == args.length - 1) {
            requestFileName = args[a];
        } else if (a > args.length) {
            usage();
        }

        if (requestFileName != null) {
            try {
                System.setIn(new FileInputStream(requestFileName));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s.%n", requestFileName);
                System.exit(1);
            }
        }

        if (outFileName != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(outFileName),
                                              true));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s for writing.%n",
                                  outFileName);
                System.exit(1);
            }
        }

        trip(mapFileName);
    }

    /** Print a trip for the request on the standard input to the stsndard
     *  output, using the map data in MAPFILENAME.
     */
    private static void trip(String mapFileName) {
        map = new UndirectedGraph<Location, Distance>();
        _locations = new ArrayList<Location>();
        _dis = new HashMap<String, Graph<Location, Distance>.Edge>();
        vertexes = new HashMap<String, Graph<Location, Distance>.Vertex>();
        String temp, from, to, dir, road; double dis, x, y;
        Pattern word = Pattern.compile("\\S+");
        try {
            Scanner in = new Scanner(new FileInputStream(mapFileName));
            while (in.hasNext(word)) {
                temp = in.next(word);
                if (temp.equals("L")) {
                    from = in.next(word); x = Double.parseDouble(in.next(word));
                    y = Double.parseDouble(in.next(word));
                    vertexes.put(from,
                                 map.add(new Location(from, x, y)));
                } else if (temp.equals("R")) {
                    from = in.next(word); road = in.next(word);
                    dis = Double.parseDouble(in.next(word));
                    dir = in.next(word); to = in.next(word);
                    _dis.put(from, map.add(vertexes.get(from), vertexes.get(to),
                                           new Distance(from, road, dis,
                                                        dir, to)));
                }
            }
        } catch (FileNotFoundException e) {
            System.exit(1);
        }
        takeRequest();

    }
    /** Takes request from requestfile. */
    private static void takeRequest() {
        Scanner req = new Scanner(System.in);
        Pattern comma = Pattern.compile("([^\\n,]+)(,)?");
        req.useDelimiter("\\s");
        String temp = null;
        MatchResult word;
        index = 1;
        while (req.hasNext(comma)) {
            req.next(comma);
            word = req.match();
            if (word.end(2) > -1) {
                if (temp != null) {
                    printDirections(temp, word.group(1));
                } else {
                    System.out.printf("From %s:\n\n", word.group(1));
                }
                temp = word.group(1);
            } else {
                printDirections(temp, word.group(1));
                temp = null;
                index = 1;
            }
            for (Graph<Location, Distance>.Vertex v : map.vertices()) {
                v.getLabel().setWeight(Double.POSITIVE_INFINITY);
            }
        }
    }
    /** Prints directions from FROM to TO. */
    private static void printDirections(String from, String to) {
        ListIterator<Graph<Location, Distance>.Edge> directions =
            Graphs.shortestPath(map, vertexes.get(from),
                                vertexes.get(to), HEURISTIC).listIterator();
        Distance temp = directions.next().getLabel();
        String road = temp.getRoad();
        Double distance = temp.weight(); String city = from;
        int x = temp.getFrom().equals(city) ? 1 : 0;
        String direction = parseDirection(temp.getDir().substring(x, x + 1));
        while (directions.hasNext()) {
            city = x == 1 ? temp.getTo() : temp.getFrom();
            temp = directions.next().getLabel();
            x = temp.getFrom().equals(city) ? 1 : 0;
            if (temp.getRoad().equals(road)
                && parseDirection(temp.getDir().substring(x, x + 1))
                    .equals(direction)) {
                distance += temp.weight();
            } else {
                System.out.printf("%d. Take %s %s for %.1f miles.\n",
                                  index, road, direction, distance);
                road = temp.getRoad();
                distance = temp.weight(); index += 1;
                direction = parseDirection(temp.getDir().substring(x, x + 1));
            }
        }
        System.out.printf("%d. Take %s %s for %.1f miles to %s.\n",
                          index, road, direction, distance, to);
        index += 1;
    }

    /** Returns a direction based on input LETTER. */
    private static String parseDirection(String letter) {
        switch (letter) {
        case "S":
            return "south";
        case "N":
            return "north";
        case "E":
            return "east";
        case "W":
            return "west";
        default:
            return null;
        }
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("Error");
        System.exit(1);
    }
    /** Class location. */
    static class Location implements Weightable {
        /** Constructor that stores PLACE, X, and Y. */
        public Location(String place, double x, double y) {
            _place = place;
            _x = x;
            _y = y;
            _weight = Double.POSITIVE_INFINITY;
        }
        /** Returns the place. */
        String getPlace() {
            return _place;
        }
        /** Returns x coordinate. */
        double getX() {
            return _x;
        }
        /** Returns y coordinate. */
        double getY() {
            return _y;
        }
        @Override
        public void setWeight(double w) {
            _weight = w;
        }

        @Override
        public double weight() {
            return _weight;
        }
        @Override
        public String toString() {
            return _place;
        }
        /** Weight. */
        private double _weight;
        /** Place. */
        private String _place;
        /** X coordinate. */
        private double _x;
        /** Y coordinate. */
        private double _y;
    }
    /** Class Distance. */
    static class Distance implements Weighted {
        /** Constructor, stores C1, R, N, D, and C2. */
        public Distance(String c1, String r, double n, String d, String c2) {
            _c1 = c1;
            _r = r;
            _n = n;
            _d = d;
            _c2 = c2;
        }
        /** Returns place coming from. */
        public String getFrom() {
            return _c1;
        }
        /** Returns road name. */
        public String getRoad() {
            return _r;
        }
        @Override
        public double weight() {
            return _n;
        }
        /** Returns direction of travel. */
        public String getDir() {
            return _d;
        }
        /** Returns place going to. */
        public String getTo() {
            return _c2;
        }
        @Override
        public String toString() {
            return _r;
        }
        /** Place from. */
        private String _c1;
        /** Road name. */
        private String _r;
        /** Distance. */
        private double _n;
        /** Direction. */
        private String _d;
        /** Place to. */
        private String _c2;
    }
    /** Distancer based on distance. */
    public static final Distancer<Location> HEURISTIC =
        new Distancer<Location>() {
            @Override
            public double dist(Location v0, Location v1) {
                return Math.sqrt(Math.pow(v0.getY() - v1.getY(), 2)
                                 + Math.pow(v0.getX() - v1.getX(), 2));
            }
        };
    /** The graph. */
    private static Graph<Location, Distance> map;
    /** The locations. */
    private static ArrayList<Location> _locations;
    /** Stores distances. */
    private static HashMap<String, Graph<Location, Distance>.Edge> _dis;
    /** Stores vertexes. */
    private static HashMap<String, Graph<Location, Distance>.Vertex> vertexes;
    /** Index. */
    private static int index;

}
