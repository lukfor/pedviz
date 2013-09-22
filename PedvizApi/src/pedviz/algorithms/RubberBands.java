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

import javax.swing.event.ChangeListener;

import pedviz.graph.Edge;
import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.view.NodeView;

/**
 * This class implements the RubberBands algorithm. It provides methods for
 * setting up the spacing between nodes and their size.
 * 
 * @author Lukas Forer
 * @version 0.1
 */

public class RubberBands implements Algorithm {
    private LayoutedGraph graph;

    private float horizontalSpacing = 20f;

    private float depth = 100f;

    private int nodeSize = 7;

    private float verticalSpacing = 25f;

    private int horizontalGap = 1;

    private int maxIterations = 200;

    private ChangeListener changeListener;

    private int percent = 0;

    /**
     * Creates the RubberBands algorithm for the given Graph.
     * 
     * @param graph
     *                the Graph object
     */
    public RubberBands(LayoutedGraph graph) {
	this.graph = graph;
    }

    public void run() {
	if (graph.getGraph() == null)
	    System.out.println("oje");
	for (int i = 0; i < graph.getGraph().getHierachiesCount(); i++)
	    init(i);

	for (int i = 0; i < graph.getGraph().getHierachiesCount(); i++) {
	    for (int j = 0; j < maxIterations; j++) {
		fireChangeListener(Math.round(j / 2f));
		rubberBands(i);
	    }
	}
	calcPosForSubnodes();
	centerGraph();

    }

    public String getMessage() {
	return "Calc Positions...";
    }

    public int getPercentComplete() {
	return percent;
    }

    public void addChangeListener(ChangeListener changeListener) {
	this.changeListener = changeListener;
    }

    /**
     * Returns the spacing between two walls.
     * 
     * @return spacing between two walls
     */
    public float getDepth() {
	return depth;
    }

    /**
     * Sets the spacing between two walls.
     * 
     * @param depth
     *                spacing between walls.
     */
    public void setDepth(float depth) {
	this.depth = depth;
    }

    /**
     * Return the gap between two nodes, if they have the same parents.
     * 
     * @return gap between two nodes
     */
    public float getHorizontalSpacing() {
	return horizontalSpacing;
    }

    /**
     * Sets the gap between two nodes, if they have the same parents.
     * 
     * @param horizontalGap
     *                gap between two nodes
     */
    public void setHorizontalSpacing(float minimumX) {
	this.horizontalSpacing = minimumX;
    }

    /**
     * Returns the size of a single node.
     * 
     * @return size of a single node
     */
    public int getNodeSize() {
	return nodeSize;
    }

    /**
     * Sets the size of a single node.
     * 
     * @param nodeSize
     *                size of a single node.
     */
    public void setNodeSize(int nodeSize) {
	this.nodeSize = nodeSize;
    }

    /**
     * Returns the spacing between two generations.
     * 
     * @return pacing between two generations
     */
    public float getVerticalSpacing() {
	return verticalSpacing;
    }

    /**
     * Sets the spacing between two generations.
     * 
     * @param verticalSpacing
     */
    public void setVerticalSpacing(float height) {
	this.verticalSpacing = height;
    }

    /**
     * Return the gap between two nodes, if they have the same parents.
     * 
     * @return gap between two nodes
     */
    public int getHorizontalGap() {
	return horizontalGap;
    }

    /**
     * Sets the gap between two nodes, if they have the same parents.
     * 
     * @param horizontalGap
     *                gap between two nodes
     */
    public void setHorizontalGap(int gap) {
	this.horizontalGap = gap;
    }

    /**
     * Returns the maximal number of iterations.
     * 
     * @return maximal number of iterations
     */
    public int getMaxIterations() {
	return maxIterations;
    }

    /**
     * Sets the maximal number of iterations.
     * 
     * @param maxIterations
     *                maximal number of iterations
     */
    public void setMaxIterations(int maxIterations) {
	this.maxIterations = maxIterations;
    }

    private void init(int id) {

	for (Integer i : graph.getGraph().getHierachy(id).getNodes().keySet()) {

	    ArrayList<Node> layer = graph.getGraph().getHierachy(id)
		    .getNodes(i);
	    if (layer != null) {
		float currentX = 0;
		for (int j = 0; j < layer.size(); j++) {

		    Node currentNode = layer.get(j);
		    NodeView currentNodeView = graph.getNodeView(currentNode
			    .getId());
		    currentNodeView.setPosX(currentX);
		    currentNodeView.setPosY((verticalSpacing + currentNodeView
			    .getHeight())
			    * i);
		    currentNodeView.setPosZ(id * depth);
		    if (graph.getNodeView(currentNode.getIdMom()) != null
			    && graph.getNodeView(currentNode.getIdDad()) != null) {
			currentNodeView.setMomLeft(graph.getNodeView(
				currentNode.getIdMom()).getPosY() < graph
				.getNodeView(currentNode.getIdDad()).getPosY());
		    }
		    graph
			    .setPosHierarchie(i,
				    (verticalSpacing + currentNodeView
					    .getHeight())
					    * i);

		    if (j + 1 < layer.size()) {
			Node nextNode = layer.get(j + 1);
			NodeView nextNodeView = graph.getNodeView(nextNode
				.getId());
			float currentWidth = currentNodeView.calcWidth(
				nodeSize, horizontalGap);
			float nextWidth = nextNodeView.calcWidth(nodeSize,
				horizontalGap);
			currentX += horizontalSpacing + (currentWidth / 2f)
				+ (nextWidth / 2f);
		    }
		}
	    }
	}

    }

    private void rubberBands(int id) {
	for (Integer j : graph.getGraph().getHierachy(id).getNodes().keySet()) {
	    ArrayList<Node> layer = graph.getGraph().getHierachy(id)
		    .getNodes(j);
	    if (layer != null) {
		for (int k = 0; k < layer.size(); k++) {

		    Node node = layer.get(k);
		    NodeView nodeView = graph.getNodeView(node);

		    float xPosNode = nodeView.getPosX();
		    double fRub = fRub(node);

		    if (fRub < 0) {
			// we move the node to left
			Node leftNode = null;
			float xPosLeft = -Float.MAX_VALUE;
			if (k - 1 >= 0) {
			    leftNode = layer.get(k - 1);
			    NodeView leftNodeView = graph.getNodeView(leftNode);
			    xPosLeft = leftNodeView.getPosX();
			    float diffX = (float) Math.min(Math.abs(fRub),
				    xPosNode - xPosLeft
					    - minDistance(leftNode, node));
			    nodeView.setPosX(xPosNode - diffX);
			} else
			    nodeView.setPosX(xPosNode - (float) Math.abs(fRub));
		    } else {
			// we move the node to right
			Node rightNode = null;
			float xPosRight = Float.MAX_VALUE;
			if (k + 1 < layer.size()) {
			    rightNode = layer.get(k + 1);
			    NodeView rightNodeView = graph
				    .getNodeView(rightNode.getId());
			    xPosRight = rightNodeView.getPosX();
			    float diffX = (float) Math.min(Math.abs(fRub),
				    xPosRight - xPosNode
					    - minDistance(node, rightNode));
			    nodeView.setPosX(xPosNode + diffX);
			} else
			    nodeView.setPosX(xPosNode + (float) Math.abs(fRub));
		    }
		}
	    }
	}
    }

    private void calcPosForSubnodes() {
	for (Object o : graph.getGraph().getNodes()) {
	    Node node = (Node) o;
	    NodeView nodeview = graph.getNodeView(node.getId());
	    float width = nodeview.calcWidth(nodeSize, horizontalGap);
	    NodeView nodeView = graph.getNodeView(node.getId());
	    float newPosX = nodeView.getPosX() - (width / 2f) + (nodeSize / 2f);
	    for (Node subNode : node.getNodes()) {
		NodeView subNodeView = graph.getNodeView(subNode.getId());
		subNodeView.setPosX(newPosX);
		subNodeView.setPosY(nodeView.getPosY());
		subNodeView.setPosZ(nodeView.getPosZ());
		newPosX += horizontalGap + nodeSize;
	    }
	}

	for (Object o : graph.getNodes()) {
	    NodeView nodeview = (NodeView) o;
	    nodeview.sortSubNodes();
	}

	for (Object o : graph.getGraph().getNodes()) {
	    Node node = (Node) o;
	    NodeView nodeview = graph.getNodeView(node.getId());
	    nodeview.sortSubNodes();
	    float width = nodeview.calcWidth(nodeSize, horizontalGap);
	    float newPosX = nodeview.getPosX() - (width / 2f) + (nodeSize / 2f);
	    for (Node subNode : node.getNodes()) {
		NodeView subNodeView = graph.getNodeView(subNode.getId());
		subNodeView.setPosX(newPosX);
		subNodeView.setPosY(nodeview.getPosY());
		subNodeView.setPosZ(nodeview.getPosZ());
		newPosX += horizontalGap + nodeSize;
	    }
	}

    }

    private void centerGraph() {

	float diffx = graph.getBounds().width / 2f + graph.getBounds().x;
	float diffy = graph.getBounds().height / 2f + graph.getBounds().y;
	graph.setBounds(0, 0, 0, 0);
	for (Object nodeView2 : graph.getAllNodes().values()) {
	    NodeView nodeView = (NodeView) nodeView2;
	    nodeView.setPosX(nodeView.getPosX() - diffx);
	    nodeView.setPosY(nodeView.getPosY() - diffy);
	    nodeView.setPosZ(nodeView.getPosZ() - depth / 2f);
	}

    }

    private void fireChangeListener(int percent) {
	if (changeListener != null) {
	    this.percent = percent;
	    changeListener.stateChanged(null);
	}
    }

    private double fRub(Node node) {
	float sum = 0;
	NodeView nodeView = graph.getNodeView(node.getId());

	for (Edge edge : node.getInEdges()) {
	    Node start = edge.getStart();
	    NodeView startNodeView = graph.getNodeView(start.getId());
	    sum += startNodeView.getPosX() - nodeView.getPosX();
	}

	for (Edge edge : node.getOutEdges()) {
	    Node end = edge.getEnd();
	    NodeView endNodeView = graph.getNodeView(end.getId());
	    sum += endNodeView.getPosX() - nodeView.getPosX();
	}

	if (node.getDegree() == 0)
	    return 0;
	else
	    return sum / node.getDegree();
    }

    private float minDistance(Node left, Node right) {
	if (left == null || right == null)
	    return 0;

	NodeView leftNodeView = graph.getNodeView(left.getId());
	NodeView rightNodeView = graph.getNodeView(right.getId());

	float leftWidth = leftNodeView.calcWidth(nodeSize, horizontalGap);
	float rightWidth = rightNodeView.calcWidth(nodeSize, horizontalGap);

	return horizontalSpacing + (leftWidth / 2f) + (rightWidth / 2f);
    }

    public void updateYPosition() {
	graph.setBounds(graph.getBounds().x, 0, graph.getBounds().width, 0);
	for (int id = 0; id < graph.getGraph().getHierachiesCount(); id++) {
	    for (Integer i : graph.getGraph().getHierachy(id).getNodes()
		    .keySet()) {

		ArrayList<Node> layer = graph.getGraph().getHierachy(id)
			.getNodes(i);
		if (layer != null) {
		    for (int j = 0; j < layer.size(); j++) {

			Node node = layer.get(j);
			NodeView currentNodeView = graph.getNodeView(node);
			currentNodeView
				.setPosY((verticalSpacing + currentNodeView
					.getHeight())
					* i);

			graph.setPosHierarchie(i,
				(verticalSpacing + currentNodeView.getHeight())
					* i);

			for (Node subNode : node.getNodes()) {
			    NodeView subNodeView = graph.getNodeView(subNode
				    .getId());
			    subNodeView.setPosY(currentNodeView.getPosY());
			}

		    }
		}
	    }
	}
	centerGraph();
    }
}
