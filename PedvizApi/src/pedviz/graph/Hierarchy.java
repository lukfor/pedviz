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
import java.util.HashMap;

/**
 * This class represents the hierarchy of a graph. Every graph has one or more
 * hierarchies.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class Hierarchy {
    HashMap<Integer, ArrayList<Node>> nodes;

    /**
     * Creates a hierarchy for the given graph.
     * 
     * @param graph
     */
    public Hierarchy(Graph graph) {
	nodes = new HashMap<Integer, ArrayList<Node>>();
	graph.addHierarchy(this);
    }

    /**
     * Adds the given node at the given level to the hierarchy.
     * 
     * @param level
     *                level
     * @param node
     *                node
     */
    public void addNode(int level, Node node) {
	Hierarchy oldHierachie = node.getHierarchy();
	if (oldHierachie != null)
	    oldHierachie.removeNode(node);

	node.setHierachie(this);
	node.setLevel(level);
	ArrayList<Node> nodesInLevel = nodes.get(level);
	if (nodesInLevel == null) {
	    nodesInLevel = new ArrayList<Node>();
	    nodes.put(level, nodesInLevel);
	}
	nodesInLevel.add(node);
    }

    /**
     * Removes the given node from the hierarchy.
     * 
     * @param node
     *                node
     */
    public void removeNode(Node node) {
	int level = node.getLevel();
	nodes.get(level).remove(node);
    }

    /**
     * Returns a collection of all nodes from the given level.
     * 
     * @param level
     *                level
     * @return a collection of all nodes from the given level.
     */
    public ArrayList<Node> getNodes(int level) {
	return nodes.get(level);
    }

    /**
     * Returns the number of different levels.
     * 
     * @return
     */
    public int getLevelSize() {
	return nodes.size();
    }

    public HashMap<Integer, ArrayList<Node>> getNodes() {
	return nodes;
    }
}
