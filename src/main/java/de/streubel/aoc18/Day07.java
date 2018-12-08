package de.streubel.aoc18;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import de.streubel.AdventOfCodeRunner;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day07 extends AdventOfCodeRunner {

    @Override
    public void run(List<String> input) {

        Graph graph = Graph.parse(input);
        Node firstNode = graph.findFirstNode();

        SortedSet<Node> available = new TreeSet<>(nodeOrdering);
        available.add(firstNode);
        String path = recordPath(available);

        System.out.println("Result Part 1: "+ path);
    }

    private static String recordPath(SortedSet<Node> available) {
        StringBuilder path = new StringBuilder();

        while (!available.isEmpty()) {
            Node node = available.first();
            available.remove(node);

            path.append(node.name);
            node.visited = true;
            for (Edge edge : node.connections) {
                edge.end.counter--;
                if (edge.end.counter <= 0 && !edge.end.visited) {
                    available.add(edge.end);
                }
            }
        }

        return path.toString();
    }



    public static class Node {
        String name;
        Set<Edge> connections;
        int counter;
        boolean visited;

        Node(String name) {
            this.name = name;
            this.connections = new HashSet<>();
            this.counter = 0;
            this.visited = false;
        }

        void addEdgeTo(Node end) {
            connections.add(new Edge(this, end));
        }

        public String toString() {
            return name;
        }
    }

    public static class Edge {
        Node start;
        Node end;

        Edge(Node start, Node end) {
            this.start = start;
            this.end   = end;
            this.end.counter++;
        }

        public String toString() {
            return start.name + "->" + end.name;
        }
    }

    public static class Graph {
        Set<Node> nodes = new HashSet<>();

        Node findFirstNode() {

            //noinspection Guava,ConstantConditions
            Set<Edge> allEdges = FluentIterable.from(nodes)
                    .transformAndConcat(node -> node.connections)
                    .toSet();

            //noinspection Guava,ConstantConditions
            Set<Node> allEndNodes = FluentIterable.from(allEdges)
                    .transform(edge -> edge.end)
                    .toSet();

            Sets.SetView<Node> difference = Sets.difference(nodes, allEndNodes);
            return Iterables.getOnlyElement(difference);
        }

        Node findOrCreateNode(String nodeName) {
            Node node = nodes.stream()
                    .filter(node1 -> nodeName.equals(node1.name))
                    .findFirst()
                    .orElse(null);

            if (node == null) {
                node = new Node(nodeName);
                nodes.add(node);
            }

            return node;
        }

        static Graph parse(List<String> input) {
            Graph graph = new Graph();

            for (String s: input) {
                Pattern pattern = Pattern.compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");

                Matcher m = pattern.matcher(s);
                if (m.matches()) {
                    Node start = graph.findOrCreateNode(m.group(1));
                    Node end   = graph.findOrCreateNode(m.group(2));
                    start.addEdgeTo(end);
                } else {
                    throw new RuntimeException();
                }
            }

            return graph;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static Ordering<Node> nodeOrdering = Ordering.natural()
            .onResultOf((Function<Node, String>) node -> node.name);

}