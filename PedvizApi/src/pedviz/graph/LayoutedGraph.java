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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pedviz.event.NodeViewEvent;
import pedviz.event.NodeViewListener;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.EdgeView;
import pedviz.view.NodeView;
import pedviz.view.Symbol;

/**
 * This class contains all layout informations for a Graph object. A single
 * Graph can have one or more layouts.
 * 
 * @author Luki
 * 
 * @param <N>
 * @param <E>
 */
public class LayoutedGraph<N extends NodeView, E extends EdgeView> implements
	NodeViewListener {

    Graph graph;

    HashMap<Object, N> nodes;

    HashMap<Object, N> subNodes;

    ArrayList<E> edges;

    DefaultNodeView defaultNodeView;

    DefaultEdgeView defaultEdgeView;

    Layout layout;

    HashMap<Integer, Float> posHierarchie;

    float border = 14;

    Rectangle.Float bounds;

    /**
     * Creates a new LayoutedGaph object for the given Graph object with the
     * given settings.
     * 
     * @param graph
     *                Graph object.
     * @param layout
     *                Layout object.
     * @param defaultNodeView
     *                node settings.
     * @param defaultEdgeView
     *                edge settings.
     */
    public LayoutedGraph(Graph graph, Layout layout,
	    DefaultNodeView defaultNodeView, DefaultEdgeView defaultEdgeView) {
	this.graph = graph;
	this.defaultNodeView = defaultNodeView;
	this.defaultEdgeView = defaultEdgeView;
	this.layout = layout;
	bounds = new Rectangle.Float();
	posHierarchie = new HashMap<Integer, Float>();
	updateModel();
    }

    /**
     * Returns a collection of all edges.
     * 
     * @return a collection of all edges.
     */
    public ArrayList<E> getEdges() {
	return edges;
    }

    /**
     * Returns the Graph object.
     * 
     * @return the Graph object.
     */
    public Graph getGraph() {
	return graph;
    }

    /**
     * Returns a collection of all nodes.
     * 
     * @return a collection of all nodes.
     */
    public Collection<N> getNodes() {
	return nodes.values();
    }

    /**
     * Returns a collection of all subnodes.
     * 
     * @return a collection of all subnodes.
     */
    public Collection<N> getSubNodes() {
	return subNodes.values();
    }

    /**
     * Returns a collection of all nodes and all subnodes.
     * 
     * @return a collection of all nodes and all subnodes.
     */
    public HashMap<Object, N> getAllNodes() {
	HashMap<Object, N> result = new HashMap<Object, N>();
	result.putAll(nodes);
	result.putAll(subNodes);
	return result;
    }

    /**
     * Returns the NodeView object by the given id.
     * 
     * @param id
     *                the id of a node.
     * @return the NodeView object by the given id.
     */
    public N getNodeView(Object id) {
	N result = nodes.get(id);
	if (result == null) {
	    return subNodes.get(id);
	} else {
	    return result;
	}
    }

    public N getNodeView(Node node) {
	return getNodeView(node.getId());
    }

    /**
     * Returns the edge settings.
     * 
     * @return the edge settings.
     */
    public DefaultEdgeView getDefaultEdgeView() {
	return defaultEdgeView;
    }

    /**
     * Sets the edge settings.
     * 
     * @param defaultEdgeView
     *                the edge settings.
     */
    public void setDefaultEdgeView(DefaultEdgeView defaultEdgeView) {
	this.defaultEdgeView = defaultEdgeView;
    }

    /**
     * Returns the node settings.
     * 
     * @return the node settings.
     */
    public DefaultNodeView getDefaultNodeView() {
	return defaultNodeView;
    }

    /**
     * Sets the node settings.
     * 
     * @param defaultNodeView
     *                the node settings.
     */
    public void setDefaultNodeView(DefaultNodeView defaultNodeView) {
	this.defaultNodeView = defaultNodeView;
    }

    /**
     * Reloads all nodes and edges from the Graph object and updates the
     * existing model.
     * 
     */
    public void updateModel() {
	edges = new ArrayList<E>();
	nodes = new HashMap<Object, N>();
	subNodes = new HashMap<Object, N>();

	for (int i = 0; i < graph.getHierachiesCount(); i++) {
	    Hierarchy hierarchie = graph.getHierachy(i);
	    for (Integer j : hierarchie.getNodes().keySet()) {
		if (hierarchie.getNodes(j) != null) {
		    for (Node node : hierarchie.getNodes(j)) {

			N nodeview = (N) layout.createNodeView(node,
				defaultNodeView);
			nodeview.addNodeViewListener(this);
			nodeview.setSymbols((ArrayList<Symbol>) defaultNodeView
				.getSymbols().clone());
			nodes.put(node.getId(), nodeview);
			// nodeview.setExpand(!node.isDummy());

			for (Node subnode : node.getNodes()) {
			    N nodeview2 = (N) layout.createNodeView(subnode,
				    defaultNodeView);
			    nodeview2.addNodeViewListener(this);
			    nodeview2
				    .setSymbols((ArrayList<Symbol>) defaultNodeView
					    .getSymbols().clone());
			    subNodes.put(subnode.getId(), nodeview2);
			    nodeview.getNodes().add(nodeview2);
			}
		    }
		}
	    }
	}

	ArrayList<Node> t = new ArrayList<Node>();

	for (Edge edge : graph.getEdges()) {
	    E edgeview = (E) layout.createEdgeView(edge, defaultEdgeView);
	    List<NodeView> end = new ArrayList<NodeView>();
	    if (t.contains(edge.getEnd())) {
		edgeview.setConnectChildren(false);
	    } else {
		t.add(edge.getEnd());
	    }
	    if (edge.getEnd().getNodes().size() > 0) {
		for (Node node : edge.getEnd().getNodes()) {
		    end.add(getNodeView(node.getId()));
		    getNodeView(node.getId()).getInEdges().add(edgeview);
		}
	    } else {
		end.add(getNodeView(edge.getEnd().getId()));
		getNodeView(edge.getEnd().getId()).getInEdges().add(edgeview);
	    }

	    NodeView start = null;
	    if (edge.getStart().getNodes().size() > 0) {
		for (Node node : edge.getStart().getNodes()) {
		    if (node.getId().equals(end.get(0).getNode().getIdDad())
			    || node.getId().equals(
				    end.get(0).getNode().getIdMom())) {
			start = getNodeView(node.getId());
			getNodeView(node.getId()).getOutEdges().add(edgeview);
		    }

		}
	    } else {
		start = getNodeView(edge.getStart().getId());
		getNodeView(edge.getStart().getId()).getOutEdges()
			.add(edgeview);
	    }

	    edgeview.setStart(start);
	    edgeview.setEnds(end);
	    edges.add(edgeview);
	}
	t.clear();
    }

    /**
     * Update bounds
     */
    public void nodeChanged(NodeViewEvent event) {
	if (event.getType() == NodeViewEvent.POSITION_X) {
	    if (bounds.x > event.getNodeview().getPosX() - border) {
		float oldX = bounds.x;
		bounds.x = event.getNodeview().getPosX() - border;
		bounds.width += Math.abs(bounds.x - oldX);
	    }

	    if (bounds.width + bounds.x < event.getNodeview().getPosX()
		    + border) {
		bounds.width = event.getNodeview().getPosX() - bounds.x
			+ border;
	    }

	}

	if (event.getType() == NodeViewEvent.POSITION_Y) {
	    if (bounds.y > event.getNodeview().getPosY() - border) {
		float oldY = bounds.y;
		bounds.y = event.getNodeview().getPosY() - border;
		bounds.height += Math.abs(bounds.y - oldY);
	    }

	    if (bounds.y + bounds.height < event.getNodeview().getPosY()
		    + event.getNodeview().getHeight() + border) {
		bounds.height = event.getNodeview().getPosY()
			+ event.getNodeview().getHeight() - bounds.y + border;
	    }

	}

	if (event.getType() == NodeViewEvent.POSITION_Z) {

	}

    }

    public Rectangle.Float getBounds() {
	return bounds;
    }

    public void setBounds(float x, float y, float width, float height) {
	bounds.x = x;
	bounds.y = y;
	bounds.width = width;
	bounds.height = height;
    }

    public Float getPosHierarchie(int i) {
	return posHierarchie.get(i);
    }

    public void setPosHierarchie(int i, float pos) {
	posHierarchie.put(i, pos);
    }
}
