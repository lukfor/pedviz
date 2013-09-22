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

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import pedviz.graph.Cluster;
import pedviz.graph.Graph;
import pedviz.graph.Node;

public class TestAlgo {

    public static Vector<Graph> createGraphs(Graph graph, Vector<Node> nodes,
	    int max) {
	Vector<Graph> result = new Vector<Graph>();

	for (int i = 0; i < nodes.size(); i++) {
	    Node node = nodes.get(i);
	    Set<Node> temp = new HashSet<Node>();
	    Set<Node> parents = getParents(graph, node, 1, max);
	    temp.addAll(parents);
	    for (Node parent : parents) {
		temp.addAll(parent.getChilds());
	    }
	    Cluster cluster = new Cluster(temp);
	    Graph subgraph = new Graph(cluster);

	    int count = 0;
	    for (Node n : nodes) {
		if (subgraph.getNode(n.getId()) != null) {
		    count++;
		}
	    }

	    subgraph.setName("Family_" + i + " (" + count + "/"
		    + subgraph.getSize() + ")");
	    subgraph.buildHierarchie(new HierarchieUpDown());
	    result.add(subgraph);

	}

	return result;
    }

    public static Set<Node> getParents(Graph graph, Node node, int current,
	    int max) {

	Set<Node> result = new HashSet<Node>();

	Node mom = graph.getNode(node.getIdMom());
	Node dad = graph.getNode(node.getIdDad());

	if (mom == null && dad == null) {
	    result.add(node);
	} else {
	    if (current == max) {
		if (mom != null) {
		    result.add(mom);
		}
		if (dad != null) {
		    result.add(dad);
		}
	    } else {
		if (mom != null) {
		    result.addAll(getParents(graph, mom, current + 1, max));
		}
		if (dad != null) {
		    result.addAll(getParents(graph, dad, current + 1, max));
		}
	    }
	}
	return result;

    }

}
