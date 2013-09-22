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
import java.util.Collections;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Hierarchy;
import pedviz.graph.Node;

/**
 * This algorithm runs through the graph from top to bottom and assigns every
 * node a layer.
 * 
 * 
 */
public class HierarchieUpDown implements HierarchieBuilder {

    /**
     * Creates a new UpDown-Hierarchy.
     * 
     */
    public HierarchieUpDown() {
    }

    public void buildHirarchie(Graph graph) {
	graph.removeAllHierachies();
	Hierarchy hierachie = new Hierarchy(graph);

	for (Node node : graph.getNodes()) {
	    if (node.getInDegree() == 0)
		hierachie.addNode(0, node);
	    else
		hierachie.addNode(-1, node);
	}

	int currentLayer = 0;
	ArrayList<Node> currentNodes = hierachie.getNodes(0);
	while (currentNodes != null) {

	    ArrayList<Integer> newNodesLayer = new ArrayList<Integer>();
	    ArrayList<Node> newNode = new ArrayList<Node>();

	    for (Node node : currentNodes) {
		for (Edge pre : node.getInEdges()) {
		    if (pre.getStart().getLevel() >= currentLayer) {
			newNode.add(node);
			newNodesLayer.add(currentLayer + 1);
		    }
		}

		for (Edge pre : node.getOutEdges()) {
		    Node end = pre.getEnd();
		    if (end.getLevel() <= currentLayer) {
			newNode.add(end);
			newNodesLayer.add(currentLayer + 1);
		    }
		}
	    }

	    for (int i = 0; i < newNode.size(); i++)
		hierachie.addNode(newNodesLayer.get(i), newNode.get(i));

	    currentNodes = hierachie.getNodes(currentLayer);
	    currentLayer++;
	    for (Node node : currentNodes) {

		for (Edge outEdge : node.getOutEdges()) {
		    Node parent = outEdge.getEnd();
		    int maxLayer = -1;

		    for (Edge pre : parent.getInEdges()) {
			Node start = pre.getStart();
			if (start.getLevel() > maxLayer)
			    maxLayer = start.getLevel();
		    }

		    if (parent.getLevel() < 0 && maxLayer < currentLayer)
			hierachie.addNode(currentLayer, parent);
		}

	    }

	    currentNodes = hierachie.getNodes(currentLayer);
	}

	shuffle(graph, 0);

	for (int i = 0; i < graph.getHierachiesCount(); i++)
	    insertDummyNodes(graph, i);
    }

    private void shuffle(Graph graph, int id) {
	for (int i = 0; i < graph.getHierachiesDepth(); i++) {
	    ArrayList<Node> layer = graph.getHierachy(id).getNodes(i);
	    if (layer != null) {
		Collections.shuffle(layer);
	    }
	}
    }

    private void insertDummyNodes(Graph graph, int id) {
	Hierarchy hierarchie = graph.getHierachy(id);

	for (int i = 0; i < graph.getHierachiesDepth() - 1; i++) {
	    ArrayList<Node> currentNodes = hierarchie.getNodes(i);
	    if (currentNodes != null) {

		ArrayList<Node> copyOfCurrentNodes = (ArrayList<Node>) currentNodes
			.clone();

		for (Node node : copyOfCurrentNodes) {

		    ArrayList<Edge> copyOfEdges = (ArrayList<Edge>) node
			    .getOutEdges().clone();

		    for (Edge in : copyOfEdges) {
			Node end = in.getEnd();
			if ((end.getLevel() - i) > 1) {
			    if (node.getInDegree() == 0) {
				hierarchie.addNode(i + 1, node);
			    } else {
				Node dummy = new Node(graph.getFreeId());
				dummy.setDummy(true);
				dummy.setIdMom(in.getEnd().getIdMom());
				dummy.setIdDad(in.getEnd().getIdDad());
				graph.addNode(dummy);
				hierarchie.addNode(i + 1, dummy);
				graph.addEdge(new Edge(node, dummy));
				graph.addEdge(new Edge(dummy, in.getEnd()));
				graph.removeEdge(in);
			    }
			}
		    }
		    copyOfEdges.clear();
		}
		copyOfCurrentNodes.clear();
	    }
	}
    }

}
