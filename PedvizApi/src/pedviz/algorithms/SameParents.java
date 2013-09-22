/* Copyright © 2007 by Christian Fuchsberger and Lukas Forer info@pedvizapi.org.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License <http://www.pedvizapi.org/gpl.txt>
 * for more details. 
 */

package pedviz.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeListener;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;

/**
 * This class clusters all nodes with the same parents. The cluster is a dummy
 * node and his edges are alle edges from his sub nodes.This way of clustering
 * has enabled us to get a visual compression (one line from parents to all
 * children).
 * 
 * @author Lukas Forer
 * @version 0.1
 * 
 */
public class SameParents implements Algorithm {
    Graph graph;

    ArrayList<Graph> graphs;

    ChangeListener changeListener;

    int percent = 0;

    /**
     * Creates the SameParents algorithm with the given Graph object.
     * 
     * @param graph
     */
    public SameParents(Graph graph) {
	this.graph = graph;
    }

    /**
     * Creates the SameParents algorithm with the given collection of Graph
     * objects.
     * 
     * @param graph
     */
    public SameParents(ArrayList<Graph> graphs) {
	this.graphs = graphs;
    }

    public String getMessage() {
	return "search nodes with same parents";
    }

    public void run() {
	if (graph != null)
	    clusterNodesWithSameParents(graph);
	if (graphs != null) {
	    for (Graph graph : graphs) {
		clusterNodesWithSameParents(graph);
	    }
	}
    }

    private void clusterNodesWithSameParents(Graph graph) {
	HashMap<Set<Object>, Node> families = new HashMap<Set<Object>, Node>();

	int total = graph.getNodes().size();
	int current = 0;

	// we make a copy of the ArrayList, so we can modified the original.
	ArrayList<Node> nodes = new ArrayList<Node>();
	nodes.addAll(graph.getNodes());

	for (Node node : nodes) {
	    fireChangeListener(Math.round((current * 100) / total));
	    current++;
	    if (node.getInDegree() > 0) {
		Set<Object> currentSet = new HashSet<Object>();
		currentSet.add(node.getIdDad());
		currentSet.add(node.getIdMom());
		if (families.containsKey(currentSet)) {
		    // there exists a node with the same set of parents
		    Node cluster = families.get(currentSet);
		    if (!cluster.isDummy()) {
			// we create a new dummy node and add the in-edges
			Node dummy = new Node("" + graph.getFreeId());
			dummy.setDummy(true);
			dummy.setIdMom(cluster.getIdMom());
			dummy.setIdDad(cluster.getIdDad());
			dummy.setFamId(cluster.getFamId());
			graph.addNode(dummy);

			for (Edge edge : cluster.getInEdges())
			    graph.addEdge(new Edge(edge.getStart(), dummy));

			// add nodes to dummy node
			graph.addNode(dummy, cluster);
			graph.addNode(dummy, node);
			graph.removeNode(node, false);
			graph.removeNode(cluster, false);

			families.put(currentSet, dummy);
		    } else {
			// its just a dummy node
			graph.addNode(cluster, node);
			graph.removeNode(node, false);
		    }
		} else {
		    // its the first node with this set of parents
		    families.put(currentSet, node);
		}
	    }
	}

	nodes.clear();
    }

    public int getPercentComplete() {
	return percent;
    }

    public void addChangeListener(ChangeListener l) {
	this.changeListener = l;
    }

    private void fireChangeListener(int percent) {
	if (changeListener != null) {
	    this.percent = percent;
	    changeListener.stateChanged(null);
	}
    }
}
