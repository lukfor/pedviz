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
import java.util.HashSet;
import java.util.Set;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;

/**
 * This class provides some generally useful methods for finding parents and
 * children by a given node and checking if exists a path between two nodes.
 * 
 * @author Lukas Forer
 * @version 0.1
 */

public class Highlighter {

    /**
     * Maternal lineage of a person.
     */
    public static final int MATERNAL = 2;

    /**
     * Paternal lineage of a person.
     */
    public static final int PATERNAL = 4;

    /**
     * Maternal and paternal lineage of a person.
     */
    public static final int MATERNAL_AND_PATERNAL = 8;

    /**
     * All successors of a person.
     */
    public static final int SUCCESSORS = 16;

    /**
     * All ancestors of a person. (default)
     */
    public static final int ANCESTORS = 32;

    /**
     * All successors and ancestors of a person.
     */
    public static final int SUCCESSORS_AND_ANCESTORS = 64;

    /**
     * Checks if exists a path between nodeA and nodeB.
     * 
     * @param nodeA
     *                start node
     * @param nodeB
     *                destination node
     * @return all nodes on this path.
     */
    public static ArrayList<Node> findPath(Node nodeA, Node nodeB) {

	ArrayList<Node> result = new ArrayList<Node>();

	if (nodeA != null && nodeB != null) {
	    Set<Node> parentsA = nodeA.getParents();
	    Set<Node> parentsB = nodeB.getParents();

	    if (parentsA.contains(nodeB)) {
		result.add(nodeB);
		result.add(nodeA);

		for (Node node : nodeA.getPathTo(nodeB))
		    result.add(node);
		return result;
	    }

	    if (parentsB.contains(nodeA)) {
		result.add(nodeB);
		result.add(nodeA);

		for (Node node : nodeB.getPathTo(nodeA))
		    result.add(node);
		return result;
	    }

	    Set<Node> parents = new HashSet<Node>();
	    parents.addAll(parentsA);
	    parents.retainAll(parentsB);

	    // They have same parents
	    if (parents.size() > 0) {
		result.add(nodeA);
		result.add(nodeB);

		Node destination = null;
		int level = -1;
		for (Node parent : parents) {
		    if (parent.getLevel() > level && !parent.isDummy()) {
			destination = parent;
			level = parent.getLevel();
		    }
		}

		for (Node parent : parents) {
		    if (parent.getLevel() == level) {
			result.add(parent);

			// it's a dummy
			if (parent.isDummy() == true
				&& parent.getNodeCount() == 0) {
			    Set<Node> dummies = parent.getParents(1);
			    for (Node dummy : dummies) {
				result.add(dummy);
			    }
			}
		    }
		}

		for (Node node : nodeA.getPathTo(destination))
		    result.add(node);

		for (Node node : nodeB.getPathTo(destination))
		    result.add(node);

		return result;
	    }

	}
	return null;
    }

    /**
     * Returns the ancestors and successors for the given node. You can define
     * the number of max generations, that should be considered.
     * 
     * @param node
     *                The node
     * @param max
     *                Number of max generations that should be considered.
     * @return list of ancestors and successors
     */
    public static ArrayList<Node> findParentsAndChilds(Node node, int max) {
	ArrayList<Node> result = new ArrayList<Node>();

	result.add(node);

	for (Node parents : node.getParentsAndChilds(max))
	    result.add(parents);

	return result;
    }

    /**
     * Returns all ancestors and successor for the given node.
     * 
     * @param node
     *                The node
     * @return list of ancestors and successors
     */

    public static ArrayList<Node> findParentsAndChilds(Node node) {
	ArrayList<Node> result = new ArrayList<Node>();

	result.add(node);

	for (Node parents : node.getParentsAndChilds())
	    result.add(parents);

	return result;
    }

    /**
     * <p>
     * Returns a collection of nodes dependent on the given mode.
     * </p>
     * <ul>
     * <li>SUCCESSORS: returns all successors.</li>
     * <li>ANCESTORS: returns all ancestors.</li>
     * <li>SUCCESSORS_AND_ANCESTORS: returns all successors and ancestors.</li>
     * <li>MATERNAL: returns all line of descents maternal.</li>
     * <li>PATERNAL: returns all line of descents paternal.</li>
     * <li>MATERNAL_AND_PATERNAL: returns all line of descents maternal and
     * paternal.</li>
     * </ul>
     * 
     * @param graph
     *                Graph object
     * @param node
     *                strat node
     * @param mode
     *                mode
     * @return a collection of nodes dependent on the given mode.
     */
    public static ArrayList<Node> findLineOfDescents(Graph graph, Node node,
	    int mode) {
	ArrayList<Node> result = new ArrayList<Node>();
	result.add(node);

	if (mode == SUCCESSORS || mode == SUCCESSORS_AND_ANCESTORS) {
	    result.addAll(node.getChilds());

	}
	if (mode == ANCESTORS || mode == SUCCESSORS_AND_ANCESTORS) {
	    result.addAll(node.getParents());
	}

	if (mode == MATERNAL || mode == MATERNAL_AND_PATERNAL) {

	    // mom to mom
	    int level = node.getLevel();
	    Node mom = graph.getNode(node.getIdMom());
	    while (mom != null) {
		result.add(mom);
		// add dummies
		if (mom != null) {
		    if (mom.getLevel() + 1 < level) {
			Node dummie = null;
			for (Edge out : mom.getOutEdges()) {
			    if (out.getEnd().isDummy())
				dummie = out.getEnd();
			}
			while (dummie != null) {
			    result.add(dummie);
			    Node newDummie = null;
			    for (Edge out : dummie.getOutEdges()) {
				if (out.getEnd().isDummy())
				    newDummie = out.getEnd();
			    }
			    dummie = newDummie;
			}
		    }
		}
		mom = graph.getNode(mom.getIdMom());
	    }

	}

	// dad to dad
	if (mode == PATERNAL || mode == MATERNAL_AND_PATERNAL) {
	    int level = node.getLevel();
	    Node dad = graph.getNode(node.getIdDad());
	    while (dad != null) {
		result.add(dad);
		// add dummies
		if (dad != null) {
		    if (dad.getLevel() + 1 < level) {
			Node dummie = null;
			for (Edge out : dad.getOutEdges()) {
			    if (out.getEnd().isDummy())
				dummie = out.getEnd();
			}
			while (dummie != null) {
			    result.add(dummie);
			    Node newDummie = null;
			    for (Edge out : dummie.getOutEdges()) {
				if (out.getEnd().isDummy())
				    newDummie = out.getEnd();
			    }
			    dummie = newDummie;
			}
		    }
		}
		dad = graph.getNode(dad.getIdDad());
	    }
	}
	return result;
    }

}
