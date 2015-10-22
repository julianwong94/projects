package make;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;

import graph.Traversal;
import graph.Weightable;
import graph.Graph;
import graph.NoLabel;
import graph.StopException;
import graph.DirectedGraph;

/** Initial class for the 'make' program.
 *  @author Julian Wong
 */
public final class Main {

    /** Entry point for the CS61B make program.  ARGS may contain options
     *  and targets:
     *      [ -f MAKEFILE ] [ -D FILEINFO ] TARGET1 TARGET2 ...
     */
    public static void main(String... args) {
        String makefileName;
        String fileInfoName;

        if (args.length == 0) {
            usage();
        }

        makefileName = "Makefile";
        fileInfoName = "fileinfo";

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-f")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    makefileName = args[a];
                }
            } else if (args[a].equals("-D")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    fileInfoName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        ArrayList<String> targets = new ArrayList<String>();

        for (; a < args.length; a += 1) {
            targets.add(args[a]);
        }

        make(makefileName, fileInfoName, targets);
    }

    /** Carry out the make procedure using MAKEFILENAME as the makefile,
     *  taking information on the current file-system state from FILEINFONAME,
     *  and building TARGETS, or the first target in the makefile if TARGETS
     *  is empty.
     */
    private static void make(String makefileName, String fileInfoName,
                             List<String> targets) {
        map = new DirectedGraph<Rule, NoLabel>();
        vertices = new HashMap<String, Graph<Rule, NoLabel>.Vertex>();
        _built = new ArrayList<Graph<Rule, NoLabel>.Vertex>();
        startTime = 0.0;
        readFileInfo(fileInfoName);
        readMakeFile(makefileName);
        if (targets.size() == 0) {
            targets.add(first);
        }
        build(targets);
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("Error");
        System.exit(1);
    }

    /** Reads FILEINFONAME. */
    private static void readFileInfo(String fileInfoName) {
        try {
            Scanner inp = new Scanner(new FileInputStream(fileInfoName));
            String[] line;
            String counter;
            Rule holder;
            startTime = Double.parseDouble(inp.nextLine().split("\\s")[0]);
            while (inp.hasNextLine()) {
                line = inp.nextLine().split("\\s+");
                if (line != null) {
                    holder = new Rule(line[0]);
                    holder.setWeight(Double.parseDouble(line[1]));
                    vertices.put(line[0], map.add(holder));
                    _built.add(vertices.get(line[0]));
                }
            }
        } catch (FileNotFoundException e) {
            usage();
        }
    }

    /** Reads MAKEFILENAME. */
    private static void readMakeFile(String makefileName) {
        try {
            Scanner inp = new Scanner(new FileInputStream(makefileName));
            Scanner lin;
            Pattern pattern = Pattern.compile("\\S+:");
            String[] temp = null;
            MatchResult line;
            Rule target = null;
            Rule others;
            while (inp.hasNextLine()) {
                inp.nextLine();
                line = inp.match();
                lin = new Scanner(line.group(0));
                if (lin.hasNext(pattern)) {
                    temp = line.group(0).split("\\s|:\\s*");
                    if (first == null) {
                        first = temp[0];
                    }
                    if (vertices.get(temp[0]) == null) {
                        target = new Rule(temp[0]);
                        target.setWeight(startTime);
                        vertices.put(temp[0], map.add(target));
                    } else {
                        target = vertices.get(temp[0]).getLabel();
                    }
                    for (int i = 1; i < temp.length; i += 1) {
                        if (vertices.get(temp[i]) == null) {
                            others = new Rule(temp[i]);
                            others.setWeight(startTime);
                            vertices.put(temp[i], map.add(others));
                        }
                        map.add(vertices.get(temp[0]),
                                vertices.get(temp[i]), new NoLabel());
                    }
                } else if (lin.hasNext()) {
                    temp = line.group(0).split("\\s");
                    if (temp[0].equals("#")) {
                        continue;
                    }
                    target.add(line.group(0));
                }
            }
        } catch (FileNotFoundException e) {
            usage();
        }
    }

    /** Builds a makefile using TARGETS. */
    private static void build(List<String> targets) {
        creator = new Create();
	_visited = new ArrayList<Graph<Rule, NoLabel>.Vertex>();
	_buildlist = new ArrayList<Graph<Rule, NoLabel>.Vertex>();
	creator.depthFirstTraverse(map, vertices.get(targets.get(0)));
	for (Graph<Rule, NoLabel>.Vertex v: _buildlist) {
	    for (String s : v.getLabel().commands()) {
		System.out.print(s);
	    }
	}
	for (int i = 1; i < targets.size(); i += 1) {
	    _buildlist = new ArrayList<Graph<Rule, NoLabel>.Vertex>();
	    _visited = new ArrayList<Graph<Rule, NoLabel>.Vertex>();
	    creator.continueTraversing(vertices.get(targets.get(i)));
	    for (Graph<Rule, NoLabel>.Vertex v: _buildlist) {
		for (String s : v.getLabel().commands()) {
		    System.out.print(s);
		}
	    }
	}
    }

    /** Stores rules. */
    public static class Rule implements Weightable {
        /** Constructs a target of name NAME. */
        public Rule(String name) {
            _name = name;
            commandlines = new ArrayList<String>();
            _weight = startTime;
        }
        /** Returns Name. */
        public String name() {
            return _name;
        }
        /** Adds String S to commandline. */
        public void add(String s) {
            commandlines.add(s);
        }

        @Override
        public double weight() {
            return _weight;
        }

        @Override
        public void setWeight(double weight) {
            _weight = weight;
        }
        /** Returns the commandlist. */
        public ArrayList<String> commands() {
            return commandlines;
        }

        /** Weight, or changedate. */
        private double _weight;

        /** Target. */
        private String _name;

        /** Commands. */
        private ArrayList<String> commandlines;
    }

    /** Class that extends Traversal that helps build Targets. */
    public static class Create extends Traversal<Rule, NoLabel> {

        @Override
        public void preVisit(Graph<Rule, NoLabel>.Edge e,
                                Graph<Rule, NoLabel>.Vertex v0) {
        }

        @Override
        public void visit(Graph<Rule, NoLabel>.Vertex v) {
            for (Graph<Rule, NoLabel>.Vertex i: map.successors(v)) {
                if (_visited.contains(i) && i.getLabel().weight() <= v.getLabel().weight()) {
                    throw new StopException("Cycle");
                }
            }
            _visited.add(v);
        }

        @Override
        public void postVisit(Graph<Rule, NoLabel>.Vertex v) {
            boolean condition = false;
            if (_built.contains(v)) {
                for (Graph<Rule, NoLabel>.Vertex i: map.predecessors(v)) {
                    if (i.getLabel().weight() >= v.getLabel().weight()) {
                        _buildlist.add(v);
                        v.getLabel().setWeight(startTime);
                        condition = true;
                        break;
                    }
                }
                if (!condition) {
                    for (Graph<Rule, NoLabel>.Vertex i : map.successors(v)) {
                        if (v.getLabel().weight() <= i.getLabel().weight()) {
                            _buildlist.add(v);
                            v.getLabel().setWeight(startTime);
                            break;
                        }
                    }
                }
            } else {
                _built.add(v);
                _buildlist.add(v);
            }
        }
    }

    /** Start time. */
    private static double startTime;

    /** Stores vertexes. */
    private static HashMap<String, Graph<Rule, NoLabel>.Vertex> vertices;

    /** First target in make file. */
    private static String first;

    /** Stores visited vertices. */
    private static ArrayList<Graph<Rule, NoLabel>.Vertex> _visited;

    /** Stores postvisited vertices. */
    private static ArrayList<Graph<Rule, NoLabel>.Vertex> _buildlist;

    /** Creator of a graph by using traversing. */
    private static Create creator;

    /** The graph of all the points and edges. */
    private static DirectedGraph<Rule, NoLabel> map;
    /** Contains list of all newly and previously built vertexes. */
    private static ArrayList<Graph<Rule, NoLabel>.Vertex> _built;

}
