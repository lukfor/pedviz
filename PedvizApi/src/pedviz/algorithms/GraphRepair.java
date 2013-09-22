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
 */package pedviz.algorithms;

import java.util.ArrayList;
import java.util.Vector;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;
import pedviz.graph.GraphMetaData;

/**
 * The automatic repair method fixes problems by changing the sex or
 * adding virtual individuals (ID starts with ##).
 * 
 * @author Luki
 * 
 */
public class GraphRepair {

    /**
     * Inserts missing parents and change invalid sex.
     * 
     * @param graph
     *                graph.
     * @return list of changes.
     */

    static public Vector<String> insertMissingParents(Graph graph) {
	Vector<Graph> graphs = new Vector<Graph>();
	graphs.add(graph);
	return insertMissingParents(graphs);
    }

    /**
     * Inserts missing parents and change invalid sex.
     * 
     * @param graphs
     *                a collection of graphs.
     * @return list of changes.
     */

    static public Vector<String> insertMissingParents(Vector<Graph> graphs) {
	Vector<String> log = new Vector<String>();
	for (Graph graph : graphs) {
	    GraphMetaData metaData = graph.getMetaData();
	    Object female = metaData.getFemale();
	    Object male = metaData.getMale();
	    ArrayList<Node> nodes = new ArrayList<Node>();
	    nodes.addAll(graph.getNodes());
	    for (Node node : nodes) {
		if (!node.isDummy()
			|| (node.isDummy() && node.getNodes().size() > 0)) {

		    String id = node.getId().toString();
		    if (node.getNodeCount() > 0) {
			id = "";
			for (Node subNode : node.getNodes()) {
			    id += subNode.getId().toString();

			    if (node.getNodes().indexOf(subNode) < node
				    .getNodeCount() - 1) {
				id += ", ";
			    }
			}
		    }

		    // Singleton
		    if (node.getInDegree() == 0 && node.getOutDegree() == 0) {
			graph.removeNode(node);
		    }

		    // Check for missing parents
		    if (node.getInDegree() == 1) {
			if (graph.getNode(node.getIdMom()) == null) {
			    Node mom = new Node("##" + node.getId());
			    mom.setUserData(metaData.get(GraphMetaData.PID), "##" + node.getId());
			    mom.setUserData(metaData.get(GraphMetaData.SEX), female);
			    mom.setUserData("virtual", "1");
			    mom.setFamId(node.getFamId());
			    mom.setUserData(metaData.get(GraphMetaData.FAM), node.getFamId());
			    node.setUserData(metaData.get(GraphMetaData.MOM), "##" + node.getId());
			    for (Node child: node.getNodes()){
				child.setUserData(metaData.get(GraphMetaData.MOM), "##" + node.getId());
			    }
			    graph.addNode(mom);
			    graph.addEdge(new Edge(mom, node));
			    log.add(graph.getName() + ": Add mom for node ["
				    + id + "]");
			}
			if (graph.getNode(node.getIdDad()) == null) {
			    Node dad = new Node("##" + node.getId());
			    dad.setUserData(metaData.get(GraphMetaData.PID), "##" + node.getId());
			    dad.setUserData(metaData.get(GraphMetaData.SEX), male);
			    dad.setUserData("virtual", "1");
			    dad.setFamId(node.getFamId());
			    dad.setUserData(metaData.get(GraphMetaData.FAM), node.getFamId());
			    node.setUserData(metaData.get(GraphMetaData.DAD), "##" + node.getId());
			    for (Node child: node.getNodes()){
				child.setUserData(metaData.get(GraphMetaData.DAD), "##" + node.getId());
			    }
			    graph.addNode(dad);
			    graph.addEdge(new Edge(dad, node));
			    log.add(graph.getName() + ": Add dad for node ["
				    + id + "]");
			}
		    }

		    // Check sex mom
		    Node mom = graph.getNode(node.getIdMom());
		    if (mom != null) {
			if (!mom.getUserData("sex").equals(female)) {
			    mom.setUserData("sex", female);
			    log.add(graph.getName() + ": Change sex of node ["
				    + mom.getId() + "]");
			}
		    }

		    // Check sex dad
		    Node dad = graph.getNode(node.getIdDad());
		    if (dad != null) {
			if (!dad.getUserData("sex").equals(male)) {
			    dad.setUserData("sex", male);
			    log.add(graph.getName() + ": Change sex of node ["
				    + dad.getId() + "]");
			}

		    }
		}
	    }
	    nodes.clear();
	}
	return log;
    }

}
