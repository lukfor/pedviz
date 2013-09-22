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

package pedviz.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.view.effects.Effect;
import pedviz.view.rules.Rule;

/**
 * This class is the super class for components that visualize a graph. A single
 * Graph can be viewed in multiple ways whitout a lot of changes.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class GraphView {

    protected Vector<NodeListener> nodeListeners;

    protected LayoutedGraph graph;

    protected double translateX = 0, translateY = 0, scale = 1;

    protected boolean edgeVisible = true, selectionEnabled = false,
	    zoomEnabled = true, movingEnabled = true, multiselection = false;

    protected HashMap<Object, NodeView> selectedNodes, highlightedNodes,
	    hiddenNodes;

    protected Vector<Rule> rules;

    protected Color backgroundColor = new Color(255, 255, 255);

    protected boolean autozoom = true;

    protected float alpha = 0.2f;

    protected Effect effect;

    protected DefaultEdgeView defaultEdge;

    protected DefaultNodeView defaultNode;

    /**
     * Creates a new empty Graphview.
     * 
     */

    public GraphView() {
	selectedNodes = new HashMap<Object, NodeView>();
	hiddenNodes = new HashMap<Object, NodeView>();
	highlightedNodes = new HashMap<Object, NodeView>();
	rules = new Vector<Rule>();
	effect = new Effect();
	nodeListeners = new Vector<NodeListener>();
	setTranslate(0, 0);
	setScale(1);
    }

    /**
     * Creates a new Graphview for the given LayoutedGraph object.
     * 
     */

    public GraphView(LayoutedGraph graph) {
	this();
	setGraph(graph);
    }

    /**
     * Sets the LayoutedGraph for this view..
     * 
     * @param graph
     *                Graph object
     */
    public void setGraph(LayoutedGraph graph) {

	deselect();
	unHighlightAll();
	showAll();
	this.graph = graph;
	if (graph != null) {
	    this.defaultEdge = graph.getDefaultEdgeView();
	    this.defaultNode = graph.getDefaultNodeView();

	    if (effect != null) {

		/*
		 * double aspect = getComponent().getBounds().width /
		 * getComponent().getBounds().height; double height =
		 * graph.getBounds().getWidth() / aspect; Rectangle2D b = new
		 * Rectangle2D.Double(graph.getBounds().getX(), - height / 2,
		 * graph.getBounds().getWidth(), height); effect.setBounds(b);
		 */

		effect.setBounds(graph.getBounds());
	    }

	    // Applies all rules
	    for (Object o : graph.getAllNodes().values()) {
		NodeView node = (NodeView) o;
		node.setBounds(node.getPosX(), node.getPosY(), node.getSize());

		// if (!node.getNode().isDummy() || ( node.getNode().isDummy()
		// && !node.isExpand()))
		for (Rule rule : getRules())
		    if (rule.isEnabled())
			rule.applyRule(node);
	    }
	}
	updateGraphView();
    }

    /**
     * Returns the LayoutedGraph of which this is a view.
     * 
     * @return the LayoutedGraph object
     */
    public LayoutedGraph getGraph() {
	return graph;
    }

    /**
     * Deselects the given node if it's selected.
     * 
     * @param node
     *                node
     */
    public void deselect(Node node) {
	NodeView nodeview = graph.getNodeView(node.getId());
	nodeview.setSelected(false);
	selectedNodes.values().remove(nodeview);
    }

    /**
     * Selects a single node.
     * 
     * @param node
     *                node
     */
    public void select(Node node) {
	if (!isMultiselection()) {
	    for (NodeView nodeview : selectedNodes.values())
		nodeview.setSelected(false);

	    selectedNodes.clear();
	}
	NodeView nodeview = graph.getNodeView(node.getId());
	nodeview.setSelected(true);
	selectedNodes.put(node.getId(), nodeview);
    }

    /**
     * Selects a collection of nodes.
     * 
     * @param Nodes
     */
    public void select(ArrayList<Node> Nodes) {
	if (!isMultiselection()) {
	    for (NodeView nodeview : selectedNodes.values())
		nodeview.setSelected(false);

	    selectedNodes.clear();
	}
	for (Node node : Nodes) {
	    NodeView nodeview = graph.getNodeView(node.getId());
	    nodeview.setSelected(true);
	    selectedNodes.put(node.getId(), nodeview);
	}
    }

    /**
     * Returns true if the given node is selected.
     * 
     * @param node
     * @return true if the given node is selected
     */
    public boolean isSelected(Node node) {
	return selectedNodes.containsKey(node.getId());
    }

    /**
     * Deselects all nodes that are currently selected.
     */
    public void deselect() {
	for (NodeView nodeview : selectedNodes.values())
	    nodeview.setSelected(false);

	selectedNodes.clear();
	updateGraphView();
    }

    /**
     * Returns all nodes that are currently selected.
     * 
     * @return a list of all selected nodes
     */
    public ArrayList<Node> getSelection() {
	ArrayList<pedviz.graph.Node> result = new ArrayList<pedviz.graph.Node>();
	for (Object id : selectedNodes.keySet())
	    result.add(graph.getGraph().getNode(id));
	return result;
    }

    /**
     * Returns the number of nodes that are currently selected.
     * 
     * @return nummer of selected nodes
     */
    public int getSelectionCount() {
	return selectedNodes.size();
    }

    /**
     * Enables or disables selection.
     * 
     * @param enabled
     */
    public void setSelectionEnabled(boolean enabled) {
	selectionEnabled = enabled;
    }

    /**
     * Returns true if multiselection is enabled.
     * 
     * @return true, if multiselection is enabled
     */
    public boolean isMultiselection() {
	return multiselection;
    }

    /**
     * Enables or disables multiselection.
     * 
     * @param multiSelection
     */
    public void setMultiselection(boolean multiSelection) {
	this.multiselection = multiSelection;
    }

    /**
     * Returns true if selection is enabled.
     * 
     * @return
     */
    public boolean isSelectionEnabled() {
	return selectionEnabled;
    }

    /**
     * Shows the given node if it's hidden.
     * 
     * @param node
     *                node
     */
    public void show(Node node) {
	NodeView nodeview = graph.getNodeView(node.getId());
	nodeview.setHidden(false);
	hiddenNodes.values().remove(nodeview);
    }

    /**
     * Shows the given nodes.
     * 
     * @param node
     *                node
     */
    public void show(Collection<Node> nodes) {
	for (Node node : nodes) {
	    NodeView nodeview = graph.getNodeView(node.getId());
	    nodeview.setHidden(false);
	    hiddenNodes.values().remove(nodeview);
	}
    }

    /**
     * Shows all nodes that are currently hidden.
     */
    public void showAll() {
	for (NodeView nodeview : hiddenNodes.values()) {
	    nodeview.setHidden(false);
	}

	hiddenNodes.values().clear();
	updateGraphView();
    }

    /**
     * Hides all nodes that are currently shown.
     */
    public void hideAll() {
	for (Object o : graph.getAllNodes().values()) {
	    NodeView nodeview = (NodeView) o;
	    hide(nodeview.getNode());
	}
	updateGraphView();
    }

    /**
     * Hides the given node if it's shown.
     * 
     * @param node
     *                node
     */
    public void hide(Node node) {
	NodeView nodeview = graph.getNodeView(node.getId());
	nodeview.setHidden(true);
	hiddenNodes.put(node.getId(), nodeview);
    }

    /**
     * Hides the given node if it's shown.
     * 
     * @param node
     *                node
     */
    public void hide(Collection<Node> nodes) {
	for (Node node : nodes) {
	    NodeView nodeview = graph.getNodeView(node.getId());
	    nodeview.setHidden(true);
	    hiddenNodes.put(node.getId(), nodeview);
	}
    }

    /**
     * Clears accentuation for all nodes.
     */
    public void unHighlightAll() {
	for (NodeView nodeview : highlightedNodes.values())
	    nodeview.setHighlighted(false);

	highlightedNodes.clear();
	updateGraphView();
    }

    /**
     * Highlights the given node.
     * 
     * @param node
     *                node
     */
    public void highlight(Node node) {
	NodeView nodeview = graph.getNodeView(node.getId());
	nodeview.setHighlighted(true);
	highlightedNodes.put(node.getId(), nodeview);
	updateGraphView();
    }

    /**
     * Highlights a collection of nodes.
     * 
     * @param nodes
     *                list of nodes
     */
    public void highlight(Collection<Node> nodes) {
	for (Node node : nodes) {
	    NodeView nodeview = graph.getNodeView(node.getId());
	    nodeview.setHighlighted(true);
	    highlightedNodes.put(node.getId(), nodeview);
	}
	updateGraphView();
    }

    /**
     * Centers the graph and fits the size on.
     */
    public void centerGraph() {
	autozoom = true;
    }

    /**
     * Fires an update of the GraphView manually.
     */
    public void updateGraphView() {

    }

    /**
     * After you change the LayoutedGraph object manually run this method.
     * 
     */
    public void updateGraphModel() {
	graph.updateModel();
    }

    /**
     * Returns true if edges are visible.
     * 
     * @return true if edges are visible.
     */
    public boolean isEdgeVisible() {
	return edgeVisible;
    }

    /**
     * Displays or hides the edges.
     * 
     * @param flag
     */
    public void setEdgeVisible(boolean flag) {
	edgeVisible = flag;
	updateGraphView();
    }

    /**
     * Exports the GraphView as a jpeg-file.
     * 
     * @param filename
     *                filename
     * @param scale
     *                scale factor
     */
    public void exportJPEG(String filename, float scale, boolean grayscale) {

    }

    /**
     * Returns the SWING/AWT component used by this instance.
     * 
     * @return
     */
    public Component getComponent() {
	return null;
    }

    /**
     * Adds a new Rule.
     * 
     * @param filter
     */
    public void addRule(Rule filter) {
	rules.add(filter);
	if (graph != null)
	    setGraph(graph);
    }

    /**
     * Remove the given rule.
     * 
     * @param filter
     */
    public void removeRule(Rule filter) {
	rules.remove(filter);
    }
    

    /**
     * Returns a vector of all rules.
     * 
     * @return vector of all rules
     */
    public Vector<Rule> getRules() {
	return rules;
    }

    /**
     * Sets the background color of this component.
     * 
     * @param background
     *                color of this component backgound color
     */
    public void setBackgroundColor(Color backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    /**
     * Returns the background color of this component.
     * 
     * @return background color of this component
     */
    public Color getBackgroundColor() {
	return backgroundColor;
    }

    /**
     * Sets the scale factor.
     * 
     * @param scale
     *                scale factor
     */
    public void setScale(double scale) {
	this.scale = Math.max(0.05, scale);
	setTranslateX(getTranslateX());
	setTranslateY(getTranslateY());
    }

    /**
     * Returns the current scale factor.
     * 
     * @return the current scale factor.
     */
    public double getScale() {
	return scale;
    }

    /**
     * Sets the x-translation.
     * 
     * @param translateX
     *                x-translation
     */
    public void setTranslateX(double translateX) {
	this.translateX = translateX;
    }

    /**
     * Sets the y-translation.
     * 
     * @param translateY
     *                y-translation
     */
    public void setTranslateY(double translateY) {
	this.translateY = translateY;
    }

    /**
     * Sets the x-translation and the y-translation.
     * 
     * @param translateX
     *                x-translation
     * @param translateY
     *                y-translation
     */
    public void setTranslate(double translateX, double translateY) {
	setTranslateX(translateX);
	setTranslateY(translateY);
    }

    /**
     * Returns the current x-translation.
     * 
     * @return the current x-translation
     */
    public double getTranslateX() {
	return translateX;
    }

    /**
     * Returns the current y-translation.
     * 
     * @return the current y-translation
     */
    public double getTranslateY() {
	return translateY;
    }

    /**
     * Returns the dimensions of the graph
     * 
     * @return
     */
    /*
     * public Rectangle2D.Float getGraphBounds() { return bounds; }
     */

    /**
     * Returns the alpha value of hidden nodes.
     * 
     * @return alpha value.
     */
    public float getAlpha() {
	return alpha;
    }

    /**
     * To down grade nodes of less interest their alpha value can be lowered.
     * 
     * @param alpha
     *                alpha value.
     */
    public void setAlpha(float alpha) {
	this.alpha = alpha;
    }

    /**
     * Returns the Effect object used by this instance.
     * 
     * @return the current effect.
     */
    public Effect getEffect() {
	return effect;
    }

    /**
     * Sets the given effect.
     * 
     * @param effect
     */
    public void setEffect(Effect effect) {
	this.effect = effect;
	Rectangle2D b = null;
	if (getComponent().getBounds().width > getComponent().getBounds().height) {
	    double aspect = (double) getComponent().getBounds().width
		    / (double) getComponent().getBounds().height;
	    double height = graph.getBounds().getWidth() / aspect;
	    b = new Rectangle2D.Double(graph.getBounds().getX(), -height / 2.0,
		    graph.getBounds().getWidth(), height);
	} else {
	    double aspect = (double) getComponent().getBounds().height
		    / (double) getComponent().getBounds().width;
	    double width = graph.getBounds().getHeight() / aspect;
	    b = new Rectangle2D.Double(-width / 2.0, graph.getBounds().getY(),
		    width, graph.getBounds().getHeight());
	}
	// System.out.println(b);
	effect.setBounds(b);
    }

    /**
     * Returns true if moving is enabled.
     * 
     * @return true if moving is enabled.
     */
    public boolean isMovingEnabled() {
	return movingEnabled;
    }

    /**
     * Enables or disables moving.
     * 
     * @param movingEnabled
     */
    public void setMovingEnabled(boolean movingEnabled) {
	this.movingEnabled = movingEnabled;
    }

    /**
     * Returns true if zooming is enabled.
     * 
     * @return true if zooming is enabled.
     */
    public boolean isZoomEnabled() {
	return zoomEnabled;
    }

    /**
     * Enables or disables zooming.
     * 
     * @param zoomEnabled
     */
    public void setZoomEnabled(boolean zoomEnabled) {
	this.zoomEnabled = zoomEnabled;
    }

    /**
     * Sets a collection of rules.
     * 
     * @param rules
     *                a collection of rules.
     */
    public void setRules(Vector<Rule> rules) {
	this.rules = rules;
    }

    /**
     * Reapplies all rules.
     * 
     */
    public void updateRules() {
	if (getGraph() != null) {
	    for (Object o : getGraph().getAllNodes().values()) {
		NodeView node = (NodeView) o;
		node.reset(graph.getDefaultNodeView());
		// if (node.isExpand())
		for (Rule rule : getRules())
		    if (rule.isEnabled())
			rule.applyRule(node);
	    }
	    updateGraphView();
	}
    }

    /**
     * Redraws the given nodes.
     * 
     * @param nodes
     *                nodes that should be repainted.
     */
    public void updateNodes(Set<pedviz.graph.Node> nodes) {

    }

    /**
     * Sets the cursor of the component.
     * 
     * @param cursor
     *                the cursor of the component.
     */
    public void setCursor(int cursor) {
	getComponent().setCursor(Cursor.getPredefinedCursor(cursor));
    }

    /***************************************************************************
     * Removes the NodeListener object.
     * 
     * @return the NodeListener object.
     */
    public boolean removeNodeListener(NodeListener nodeListener) {
	return nodeListeners.remove(nodeListener);
    }

    /**
     * Registers the NodeListener object.
     * 
     * @param nodeListener
     *                the NodeListener object.
     */
    public boolean addNodeListener(NodeListener nodeListener) {
	return nodeListeners.add(nodeListener);
    }

    /**
     * Fires a NodeEvent to all listeners.
     * 
     * @param event
     */
    public void fireNodeEvent(NodeEvent event) {
	for (NodeListener listener : nodeListeners) {
	    listener.onNodeEvent(event);
	}
    }

}
