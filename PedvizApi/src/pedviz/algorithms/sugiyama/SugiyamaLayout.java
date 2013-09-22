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

package pedviz.algorithms.sugiyama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.event.ChangeListener;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Hierarchy;
import pedviz.graph.Layout;
import pedviz.graph.Node;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.NodeView;

/**
 * This class applies the Sugiyama-Algorihm on the specified graph. It works in
 * 3 phases:
 * <ul>
 * <li>Builds a hierachie and inserts dummy nodes.</li>
 * <li>Splits the graph in two walls with the specified Splitter.</li>
 * <li>Improves two successive layers until we get a acceptable result.</li>
 * </ul>
 * The final position for every node will not be calculated in this class. (see
 * RubberBands)
 * 
 * @author Lukas Forer
 * @version 0.1
 */

public class SugiyamaLayout extends Layout<SugiyamaNodeView, SugiyamaEdgeView> {
    private Splitter splitter;

    ChangeListener changeListener;

    int percent = 0;

    /**
     * Creates the SugiyamaLayout with the given Graph object.
     * 
     * @param graph
     *                the Graph object
     */
    public SugiyamaLayout(Graph graph, DefaultNodeView defaultNodeView,
	    DefaultEdgeView defaulEdgeView) {
	super(graph, defaultNodeView, defaulEdgeView);
	splitter = null;
    }

    /**
     * Returns the used WallSplitter.
     * 
     * @return sed WallSplitter
     */
    public Splitter getSplitter() {
	return splitter;
    }

    /**
     * Sets what WallSplitter will be used. By default the algorithm uses the
     * RamdomSplitter.
     * 
     * @param splitter
     */
    public void setSplitter(Splitter splitter) {
	this.splitter = splitter;
    }

    public void run() {
	if (graph.getHierachiesCount() <= 0) {
	    System.out
		    .println("Error: Sugiyama needs a graph with a hierarchie.");
	    return;
	}
	split();
	minCrossing(200);
	graph.setLayouted(true);
    }

    public String getMessage() {
	return "Layout Pedigree...";
    }

    public int getPercentComplete() {
	return percent;
    }

    public void addChangeListener(ChangeListener l) {
	this.changeListener = l;
    }

    private void updateIndex(ArrayList<Node> nodes) {
	for (int i = 0; i < nodes.size(); i++) {
	    NodeView t = layoutedGraph.getNodeView(nodes.get(i).getId());
	    if (t != null)
		t.setPosX(i);
	}
    }

    private boolean improveLayer(int id, int layer1, int layer2) {
	ArrayList<Node> oldOrder = graph.getHierachy(id).getNodes(layer2);

	boolean change = false;
	if (oldOrder != null) {
	    for (Node currentNode : oldOrder) {
		int sum = 0;
		int n = 0;
		if (layer2 > layer1) // Vorgänger benutzen
		{
		    n = currentNode.getInDegree();
		    for (Edge edge : currentNode.getInEdges()) {
				sum += layoutedGraph.getNodeView(
				edge.getStart().getId()).getPosX();
		    }
		} else {
		    n = currentNode.getOutDegree();
		    for (Edge edge : currentNode.getOutEdges()) {
	
			sum += layoutedGraph.getNodeView(edge.getEnd().getId())
				.getPosX();

		    }
		}

		float value = ((SugiyamaNodeView) layoutedGraph
			.getNodeView(currentNode.getId())).getBarycentric();
		if (n > 0)
		    value = (float) sum / (float) n;

		((SugiyamaNodeView) layoutedGraph.getNodeView(currentNode
			.getId())).setBarycentric(value);
		change = true;
	    }

	    if (change) {

		Collections.sort(oldOrder, new Comparator<Node>() {
		    public int compare(Node arg0, Node arg1) {
			return (new Float(((SugiyamaNodeView) layoutedGraph
				.getNodeView(arg0.getId())).getBarycentric()))
				.compareTo(((SugiyamaNodeView) layoutedGraph
					.getNodeView(arg1.getId()))
					.getBarycentric());
		    }
		});

		/*
		 * Collections.sort(oldOrder, new Comparator<Node>() { public
		 * int compare(Node arg0, Node arg1) { SugiyamaNodeView a =
		 * (SugiyamaNodeView)layoutedGraph .getNodeView(arg0.getId());
		 * SugiyamaNodeView b = (SugiyamaNodeView)layoutedGraph
		 * .getNodeView(arg0.getId()); int av = 0; if
		 * (a.getNode().isDummy()){ av = 0; for (Node node:
		 * a.getNode().getNodes()){ if
		 * (node.getUserData("TEST").equals(2)){ av = 2; break; } }
		 * }else{ av = new
		 * Integer(a.getNode().getUserData("TEST").toString()); }
		 * 
		 * int bv = 0; if (b.getNode().isDummy()){ bv = 0; for (Node
		 * node: b.getNode().getNodes()){ if
		 * (node.getUserData("TEST").equals(2)){ bv = 2; break; } }
		 * }else{ bv = new
		 * Integer(b.getNode().getUserData("TEST").toString()); }
		 * 
		 * int temp = new Integer(av).compareTo(bv); if (temp == 0){
		 * temp = new
		 * Float(a.getBarycentric()).compareTo(b.getBarycentric()); }
		 * return temp; } });
		 */

		updateIndex(oldOrder);
	    }
	}
	return change;
    }

    private void minCrossing(int maxIteration) {

	// Initial Order

	for (int i = 0; i < graph.getHierachiesCount(); i++) {
	    Hierarchy hierarchie = graph.getHierachy(i);
	    for (Integer j : hierarchie.getNodes().keySet()) {
		ArrayList<Node> nodes = graph.getHierachy(i).getNodes(j);
		if (nodes != null)
		    updateIndex(nodes);
	    }
	}

	int iteration = 0;

	// Improve Order
	while (iteration < maxIteration) {

	    for (int id = 0; id < graph.getHierachiesCount(); id++) {
		for (int i = graph.getHierachy(id).getLevelSize() - 1; i > 0; i--)
		    improveLayer(id, i, i - 1);
	    }

	    for (int id = 0; id < graph.getHierachiesCount(); id++) {
		for (int i = 0; i < graph.getHierachy(id).getLevelSize() - 1; i++)
		    improveLayer(id, i, i + 1);
	    }

	    fireChangeListener(Math.round((iteration * 100) / maxIteration));

	    iteration++;
	}

	for (int id = 0; id < graph.getHierachiesCount(); id++) {
	    for (int i = graph.getHierachy(id).getLevelSize() - 1; i > 0; i--)
		improveLayer(id, i, i - 1);
	}
    }

    private void split() {
	if (splitter != null) {
	    graph.removeAllHierachies();
	    splitter.beforeSplit(graph);
	    int index = 0;
	    new Hierarchy(graph);
	    new Hierarchy(graph);
	    for (Node node : graph.getNodes()) {
		int wall = splitter.getWall(graph, node, index);
		graph.getHierachy(wall).addNode(node.getLevel(), node);
		index++;
	    }
	    splitter.afterSplit(graph);
	} else {
	    graph.removeAllHierachies();
	    Hierarchy h = new Hierarchy(graph);
	    for (Node node : graph.getNodes()) {
		h.addNode(node.getLevel(), node);
	    }
	}
    }

    private void fireChangeListener(int percent) {
	if (changeListener != null) {
	    this.percent = percent;
	    changeListener.stateChanged(null);
	}
    }

    public SugiyamaNodeView createNodeView(Node node,
	    DefaultNodeView defaultNodeView) {
	return new SugiyamaNodeView(node, defaultNodeView);
    }

    public SugiyamaEdgeView createEdgeView(Edge edge,
	    DefaultEdgeView defaultEdgeView) {
	return new SugiyamaEdgeView(edge, defaultEdgeView);
    }

}
