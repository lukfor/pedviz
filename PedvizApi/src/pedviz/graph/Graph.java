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
import java.util.Vector;

import pedviz.algorithms.HierarchieBuilder;

/**
 * This class represents the pedigree as a graph. It contains all nodes
 * (individuals) and their edges (relations). It provides also methods that
 * allow you to modify and update the structure of the graph.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class Graph {
    private HashMap<Object, Node> nodes;

    private HashMap<Object, Node> subnodes;

    private ArrayList<Edge> edges;

    private int countId = -2;

    private boolean layouted = false;

    private ArrayList<Hierarchy> hierachies;

    private String name = "noname";

    private String famId = "1";

    private GraphMetaData metaData;

    /**
     * Create a new and empty graph.
     * 
     */
    public Graph() {
	nodes = new HashMap<Object, Node>();
	subnodes = new HashMap<Object, Node>();
	edges = new ArrayList<Edge>();
	hierachies = new ArrayList<Hierarchy>();
    }

    /**
     * Creates a new graph based on the given Cluster object.
     * 
     * @param cluster
     *                Cluster object
     */
    public Graph(Cluster cluster) {
	this();

	for (Node node : cluster.getNodes()) {
	    addNode(node);
	    for (Node subNode : node.getNodes())
		subnodes.put(subNode.getId(), subNode);
	}
	for (Edge edge : cluster.getEdges())
	    addEdge(edge);

	for (Node node : cluster.getNodes()) {
	    if (node.getInDegree() + node.getOutDegree() == 0)
		removeNode(node);
	}

    }

    /**
     * Adds a node to the graph.
     * 
     * @param node
     *                new node
     */
    public void addNode(Node node) {
	nodes.put(node.getId(), node);
	node.setGraph(this);
    }

    /**
     * Returns the node with the given id.
     * 
     * @param id
     * @return if there exists a node with the given id, the reference to the
     *         node, else null.
     */
    public Node getNode(Object id) {
	if (id == null) {
	    return null;
	}
	Node result = nodes.get(id);
	if (result == null)
	    result = subnodes.get(id);
	return result;
    }

    /**
     * Adds a relation between two nodes. It updates automatically the in-edges
     * and out-edges from the two nodes.
     * 
     * @param edge
     *                new edge
     */
    public void addEdge(Edge edge) {
	edges.add(edge);
	edge.getStart().addOutEdge(edge);
	edge.getEnd().addInEdge(edge);
    }

    /**
     * Adds a relation between two nodes. If updateNodes is true, it updates the
     * in-edges and out-edges from the two nodes.
     * 
     * @param edge
     *                new edge
     * @param updateNodes
     *                if updateNodes is true, it updates the in-edges and
     *                out-edges from the two nodes.
     */
    public void addEdge(Edge edge, boolean updateNodes) {
	if (!updateNodes)
	    edges.add(edge);
	else
	    addEdge(edge);
    }

    /**
     * Removes the given node. It updates automatically the in-edges and
     * out-edges from the two nodes.
     * 
     * @param node
     *                node
     */
    public void removeNode(Node node) {
	if (node.getParent() == null) {
	    for (Edge edge : node.getInEdges()) {
		edges.remove(edge);
		edge.getStart().removeOutEdge(edge);
	    }

	    for (Edge edge : node.getOutEdges()) {
		edges.remove(edge);
		edge.getEnd().removeInEdge(edge);
	    }
	    node.setGraph(null);
	    node.getInEdges().clear();
	    node.getOutEdges().clear();
	    subnodes.values().removeAll(node.getNodes());
	    if (node.getHierarchy() != null)
		node.getHierarchy().removeNode(node);
	    nodes.values().remove(node);
	} else {
	    removeNode(node.getParent());
	}
    }

    /**
     * Removes the given node. if updateNodes is true, it updates the in-edges
     * and out-edges from the node.
     * 
     * @param node
     *                node
     * @param updateNodes
     *                if updateNodes is true, it updates the in-edges and
     *                out-edges from the node.
     */
    public void removeNode(Node node, boolean updateNode) {
	if (!updateNode) {
	    for (Edge edge : node.getInEdges()) {
		edges.remove(edge);
		edge.getStart().removeOutEdge(edge);
	    }

	    for (Edge edge : node.getOutEdges()) {
		edges.remove(edge);
		edge.getEnd().removeInEdge(edge);
	    }
	    node.setGraph(null);
	    nodes.values().remove(node);
	} else
	    removeNode(node);
    }

    /**
     * Removes the given edge.
     * 
     * @param edge
     *                edge
     */
    public void removeEdge(Edge edge) {
	edge.getStart().removeOutEdge(edge);
	edge.getEnd().removeInEdge(edge);
	edges.remove(edge);
    }

    /**
     * Returns a collection of all single nodes, dummy nodes and cluster nodes.
     * 
     * @return Collection of nodes.
     */
    public Collection<Node> getNodes() {
	return nodes.values();
    }

    /**
     * Returns a collection of all nodes and subnodes.
     * 
     * @return Collection of nodes and subnodes.
     */
    public Collection<Node> getAllNodes() {
	Collection<Node> result = new Vector<Node>();
	result.addAll(nodes.values());
	result.addAll(subnodes.values());
	return result;
    }

    /**
     * Returns a collection of all edges.
     * 
     * @return Returns the edges.
     */
    public Collection<Edge> getEdges() {
	return edges;
    }

    /**
     * Returns a free id for a node. If you create a dummy node, then you can
     * use this method to get a free not used id.
     * 
     * @return Returns a free id for a node.
     */
    public int getFreeId() {
	return countId--;
    }

    /**
     * Adds a sub node to the given node.
     * 
     * @param parent
     *                cluster node
     * @param node
     *                sub node
     */
    public void addNode(Node parent, Node node) {
	node.setParent(parent);
	parent.addNode(node);
	for (Edge edge : node.getOutEdges()) {
	    Edge newEdge = new Edge(parent, edge.getEnd());
	    addEdge((Edge) newEdge);
	}

	for (Edge edge : node.getInEdges()) {
	    edges.remove(edge);
	    edge.getStart().removeOutEdge(edge);
	}
	if (node.getGraph() != this) {
	    node.setGraph(this);
	}
	if (parent.getGraph() != this) {
	    parent.setGraph(this);
	}
	node.getInEdges().clear();
	subnodes.put(node.getId(), node);
    }

    /**
     * Removes all nodes, edges and sub nodes.
     * 
     */
    public void clear() {
	nodes.clear();
	edges.clear();
	// layers.clear();
	subnodes.clear();
    }

    /**
     * Returns true, if the graph is layouted.
     * 
     * @return true, if the graph is layouted.
     */
    public boolean isLayouted() {
	return layouted;
    }

    /**
     * Sets
     * 
     * @param layouted
     *                true, if the graph is layouted.
     */
    public void setLayouted(boolean layouted) {
	this.layouted = layouted;
    }

    /**
     * Returns the name of the graph.
     * 
     * @return the name of the graph.
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name of the graph.
     * 
     * @param name
     *                the name of the graph.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Removes all hierarchies from this graph.
     * 
     */
    public void removeAllHierachies() {
	hierachies.clear();
    }

    /**
     * Returns the hierarchie with the given id.
     * 
     * @param id
     * @return if the hierarchy exists, returns the Hierachie object, else null.
     */
    public Hierarchy getHierachy(int id) {
	return hierachies.get(id);
    }

    /**
     * Returns the number of hierachies.
     * 
     * @return number of hierachies
     */
    public int getHierachiesCount() {
	return hierachies.size();
    }

    /**
     * Returns the max number of nodes in a hierachy.
     * 
     * @return max number of nodes in a hierachy
     */
    public int getHierachiesDepth() {
	int max = 0;
	for (int i = 0; i < hierachies.size(); i++) {
	    if (max < hierachies.get(i).getNodes().size())
		max = hierachies.get(i).getNodes().size();
	}
	return max;
    }

    protected void addHierarchy(Hierarchy hierachie) {
	hierachies.add(hierachie);
    }

    /**
     * Builds a hierarchy based on the given HierarchieBuilder object.
     * 
     * @param builder
     *                HierarchieBuilder object.
     */
    public void buildHierarchie(HierarchieBuilder builder) {
	builder.buildHirarchie(this);
    }

    @Override
    public String toString() {
	return name;
    }

    public int getSize() {
	int count = 0;
	for (Node node : getAllNodes()) {
	    if (!node.isDummy()) {
		count++;
	    }
	}
	return count;
    }

    public void print() {
	System.out.println("Name: " + getName());
	System.out.println("AllNodes: " + getAllNodes().size());
	System.out.println("Nodes: " + getNodes().size());
	System.out.println("SubNodes: " + subnodes.size());
	for (Edge edge : edges) {
	    System.out.println(edge);
	}
    }

    public String getFamId() {
	return famId;
    }

    public void setFamId(String famId) {
	this.famId = famId;
    }

    public GraphMetaData getMetaData() {
	return metaData;
    }

    public void setMetaData(GraphMetaData metaData) {
	this.metaData = metaData;
    }

}
