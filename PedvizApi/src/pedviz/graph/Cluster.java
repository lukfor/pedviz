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

package pedviz.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

/**
 * This class can contain any nodes you like.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class Cluster {
    private HashMap<Object, Node> nodes;

    private ArrayList<Edge> edges;

    /**
     * Creates a new and empty Cluster.
     * 
     */
    public Cluster() {
	nodes = new HashMap<Object, Node>();
	edges = new ArrayList<Edge>();
    }

    /**
     * Creates a new and empty Cluster.
     * 
     */
    public Cluster(Vector<Node> nodes) {
	this();
	for (Node node : nodes) {
	    addNode(node);
	}
    }

    /**
     * Creates a new and empty Cluster.
     * 
     */
    public Cluster(Set<Node> nodes) {
	this();
	for (Node node : nodes) {
	    addNode(node);
	}
    }

    /**
     * Adds the given node.
     * 
     * @param node
     *                Node object.
     */
    public void addNode(Node node) {
	if (node.getParent() != null) {
	    addNode(node.getParent());
	} else {
	    if (!nodes.containsKey(node.getId())) {
		Node newNode = (Node) node.clone();

		nodes.put(node.getId(), newNode);
		for (Edge edge : node.getInEdges()) {
		    if (nodes.containsKey(edge.getStart().getId())) {
			Edge newEdge = new Edge(nodes.get(edge.getStart()
				.getId()), newNode);
			edges.add(newEdge);
		    }
		}

		for (Edge edge : node.getOutEdges()) {
		    if (nodes.containsKey(edge.getEnd().getId())) {
			Edge newEdge = new Edge(newNode, nodes.get(edge
				.getEnd().getId()));
			edges.add(newEdge);
		    }
		}
	    }
	}
    }

    /**
     * Removes the given node.
     * 
     * @param node
     *                Node object.
     */
    public void removeNode(Node node) {
	nodes.remove(node);
    }

    /**
     * Returns a collection of all nodes.
     * 
     * @return a collection of all nodes.
     */
    public Collection<Node> getNodes() {
	return nodes.values();
    }

    /**
     * Returns a collection of all edges.
     * 
     * @return a collection of all edges.
     */
    public ArrayList<Edge> getEdges() {
	return edges;
    }

}
