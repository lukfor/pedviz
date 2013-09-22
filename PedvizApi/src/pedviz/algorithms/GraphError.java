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

import java.util.Vector;

import pedviz.graph.Node;

/**
 * Represents an error in a graph Object.
 * 
 * @author Luki
 * 
 */
public class GraphError implements Comparable {

    public static final int INVALID_SEX_MOM = 0;
    public static final int INVALID_SEX_DAD = 1;
    public static final int SINGLETON = 2;
    public static final int MISSING_MOM = 3;
    public static final int MISSING_DAD = 4;
    public static final int MULTI_PEDIGREES = 5;

    public static String[] errorMessages = new String[] {
	    "mom has invalid sex.", "dad has invalid sex.", "singleton.",
	    "mom is missing.", "dad is missing.", "" };

    private int type;
    private boolean multipleNodes = false;
    private Vector<Node> nodes;
    private String message;

    /**
     * Creates a GraphError object for a collection of nodes.
     * 
     * @param type
     *                error-type
     * @param nodes
     *                involved nodes.
     */
    public GraphError(int type, Vector<Node> nodes) {
	this.nodes = nodes;
	this.type = type;
	this.multipleNodes = nodes.size() > 1;
	message = errorMessages[type];
    }

    /**
     * Creates a GraphError object for a single node.
     * 
     * @param type
     *                error-type.
     * @param node
     *                involved node.
     */
    public GraphError(int type, Node node) {
	nodes = new Vector<Node>();
	if (node.getNodeCount() > 0) {
	    nodes.addAll(node.getNodes());
	} else {
	    nodes.add(node);
	}
	this.multipleNodes = false;
	this.type = type;
	message = errorMessages[type];
    }

    /**
     * Creates a GraphError object for a single node with a customized message.
     * 
     * @param type
     *                error-type.
     * @param node
     *                involved node.
     * @param message
     *                message.
     */
    public GraphError(int type, Node node, String message) {
	nodes = new Vector<Node>();
	if (node.getNodeCount() > 0) {
	    nodes.addAll(node.getNodes());
	} else {
	    nodes.add(node);
	}
	this.multipleNodes = false;
	this.type = type;
	this.message = message;
    }

    /**
     * Returns the type.
     * 
     * @return the type.
     */
    public int getType() {
	return type;
    }

    /**
     * Returns true, when more nodes are involved.
     * 
     * @return true, when more nodes are involved.
     */
    public boolean isMultipleNodes() {
	return multipleNodes;
    }

    /**
     * Returns a collection of all involved nodes.
     * 
     * @return a collection of all involved nodes.
     */
    public Vector<Node> getNodes() {
	return nodes;
    }

    @Override
    public String toString() {
	String nodesT = "";
	for (Node node : nodes) {
	    nodesT += node.getId().toString();

	    if (!nodes.lastElement().equals(node)) {
		nodesT += ", ";
	    }
	}
	return "[" + nodesT + "]: " + message;
    }

    public int compareTo(Object arg0) {
	GraphError error = (GraphError) arg0;
	if (error.getNodes().equals(getNodes())) {
	    return 0;
	} else {
	    return new Integer(getNodes().size()).compareTo(error.getNodes()
		    .size());
	}
    }
}
