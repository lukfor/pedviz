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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Vector;

import pedviz.event.NodeViewEvent;
import pedviz.event.NodeViewListener;
import pedviz.graph.Node;

/**
 * This class contains all informations that are needed for drawing a Node
 * object in a GraphView.It saves also whether the node is selected or
 * highlighted.
 * 
 * @author lukas forer
 * 
 */

public class NodeView extends DefaultNodeView implements Comparable {

    private Node node = null;

    private boolean selected = false;

    private boolean highlighted = false;

    private boolean mouseOver = false;

    private boolean hidden = false;

    private float posX;

    private float posY;

    private float posZ;

    private Object data;

    private Color[] colors;

    ArrayList<NodeView> nodes;

    ArrayList<EdgeView> outEdges;

    ArrayList<EdgeView> inEdges;

    Rectangle2D.Float bounds;

    Vector<NodeViewListener> listeners;

    private boolean momLeft = true;

    /**
     * Constructs a NodeView for the given Node object with the given
     * DefaultNodeView as template.
     * 
     * @param node
     *                node
     * @param defaultNodeView
     *                DefaultNodeView
     */
    public NodeView(Node node, DefaultNodeView defaultNodeView) {
	this.node = node;
	posX = 0;
	posY = 0;
	posZ = 0;
	listeners = new Vector<NodeViewListener>();
	reset(defaultNodeView);
	setExpand(defaultNodeView.isExpand());
	nodes = new ArrayList<NodeView>();
	outEdges = new ArrayList<EdgeView>();
	inEdges = new ArrayList<EdgeView>();

    }

    /**
     * Clones the NodeView.
     */
    public Object clone() {
	NodeView result = new NodeView(node, this);
	result.setColor(getColor());
	result.setSelectedColor(getSelectedColor());
	result.setHighlightedColor(getHighlightedColor());
	result.setSymbols((ArrayList<Symbol>) getSymbols().clone());
	result.setSize(getSize());
	result.setHintAttributes(getHintAttributes());
	result.setBorderColor(getBorderColor());
	result.setBorderWidth(getBorderWidth());
	result.setGap(getGap());
	result.setSelected(isSelected());
	result.setHighlighted(isHighlighted());
	result.setMouseOver(isMouseOver());
	return result;
    }

    /**
     * Returns the Node object for the NodeView.
     * 
     * @return Node object
     */
    public Node getNode() {
	return node;
    }

    /**
     * Checks if the node is selected.
     * 
     * @return true, if the node is selected
     */
    public boolean isSelected() {
	return selected;
    }

    protected void setSelected(boolean selected) {
	this.selected = selected;
    }

    /**
     * Checks if the node is highlighted.
     * 
     * @return true, if the node is highlighted
     */
    public boolean isHighlighted() {
	return highlighted;
    }

    protected void setHighlighted(boolean highlighted) {
	this.highlighted = highlighted;
    }

    /**
     * Checks if the mouse is over the node.
     * 
     * @return
     */
    public boolean isMouseOver() {
	return mouseOver;
    }

    protected void setMouseOver(boolean mouseOver) {
	this.mouseOver = mouseOver;
    }

    /**
     * Return the hint-text.
     * 
     * @return
     */
    public String getHint() {
	if (getHintText() != null)
	    return getHintText();
	else {
	    String hint = "";
	    if (getHintAttributes().size() > 0) {
		for (String hintAttribute : getHintAttributes()) {
		    if (!hint.equals(""))
			hint += "\n";
		    if (getNode().getUserData(hintAttribute) != null) {
			hint += hintAttribute
				+ ": "
				+ getNode().getUserData(hintAttribute)
					.toString();
		    }
		}
		return hint;
	    } else {
		return null;
	    }
	}
    }

    /**
     * Returns the position on the X-axis.
     * 
     * @return position on the X-axis
     */
    public float getPosX() {
	return posX;
    }

    /**
     * Returns the position on the X-axis.
     * 
     * @return position on the Y-axis
     */
    public float getPosY() {
	return posY;
    }

    /**
     * Returns the position on the X-axis.
     * 
     * @return position on the Z-axis
     */
    public float getPosZ() {
	return posZ;
    }

    /**
     * Returns the bounds of the node.
     * 
     * @return bounds of the node
     */
    public Rectangle2D.Float getBounds() {
	return bounds;
    }

    /**
     * Sets the bounds of this node.
     * 
     * @param x
     *                left position
     * @param y
     *                top position
     * @param size
     *                width
     */
    public void setBounds(double x, double y, double size) {
	bounds = new Rectangle2D.Float();
	bounds.setRect(x - size / 2f, y - size / 2f, size, size + getHeight());
    }

    /**
     * Returns the stored Object.
     * 
     * @return stored Object
     */
    public Object getData() {
	return data;
    }

    /**
     * Sets the Object that will be stored.
     * 
     * @param data
     */
    public void setData(Object data) {
	this.data = data;
    }

    /**
     * Sets the Color array.
     * 
     * @param colors
     *                the Color array.
     */
    public void setColors(Color[] colors) {
	this.colors = colors;
    }

    /**
     * Returns the Color array.
     * 
     * @return the Color array.
     */
    public Color[] getColors() {
	return colors;
    }

    /**
     * Returns true, if the node is hidden.
     * 
     * @return true, if the node is hidden.
     */
    public boolean isHidden() {
	return hidden;
    }

    /**
     * 
     * @param hidden
     */
    public void setHidden(boolean hidden) {
	this.hidden = hidden;
    }

    /**
     * Returns a collection of all subnodes.
     * 
     * @return a collection of all subnodes.
     */
    public ArrayList<NodeView> getNodes() {
	return nodes;
    }

    /**
     * Returns a collection of all in-edges.
     * 
     * @return a collection of all in-edges.
     */
    public ArrayList<EdgeView> getInEdges() {
	return inEdges;
    }

    /**
     * Returns a collection of all out-edges.
     * 
     * @return a collection of all out-nedges.
     */
    public ArrayList<EdgeView> getOutEdges() {
	return outEdges;
    }

    /**
     * Sets the position on the X-axis.
     * 
     * @param x
     *                the position on the X-axis.
     */
    public void setPosX(float x) {
	posX = x;
	fireNodeViewEvent(new NodeViewEvent(this, NodeViewEvent.POSITION_X));
    }

    /**
     * Sets the position on the Y-axis.
     * 
     * @param y
     *                the position on the Y-axis.
     */
    public void setPosY(float y) {
	posY = y;
	fireNodeViewEvent(new NodeViewEvent(this, NodeViewEvent.POSITION_Y));
    }

    /**
     * Sets the position on the Z-axis.
     * 
     * @param z
     *                the position on the Z-axis.
     */
    public void setPosZ(float z) {
	posZ = z;
	fireNodeViewEvent(new NodeViewEvent(this, NodeViewEvent.POSITION_Z));
    }

    /**
     * Sorts all subnodes.
     * 
     */
    public void sortSubNodes() {

    }

    /**
     * Compares two nodeviews.
     */
    public int compareTo(Object arg0) {
	return 0;
    }

    /**
     * Resets all settings do the given DefaultNodeView object.
     * 
     * @param defaultNodeView
     */
    public void reset(DefaultNodeView defaultNodeView) {
	setColor(defaultNodeView.getColor());
	setSelectedColor(defaultNodeView.getSelectedColor());
	setHighlightedColor(defaultNodeView.getHighlightedColor());
	setSymbols((ArrayList<Symbol>) defaultNodeView.getSymbols().clone());
	setSize(defaultNodeView.getSize());
	setHintAttributes(defaultNodeView.getHintAttributes());
	setBorderColor(defaultNodeView.getBorderColor());
	setBorderWidth(defaultNodeView.getBorderWidth());
	setGap(defaultNodeView.getGap());
    }

    /**
     * Calcs the node's width.
     * 
     * @param nodeSize
     *                the size of node
     * @param gap
     *                the gap between sub nodes
     * @return the node's width
     */
    public float calcWidth(int nodeSize, int gap) {
	if (isExpand()) {
	    if (node.getNodeCount() == 0) {
		return nodeSize;
	    } else {
		return (node.getNodeCount() * nodeSize)
			+ ((node.getNodeCount() - 1) * gap);
	    }
	} else {
	    return nodeSize;
	}
    }

    public void addNodeViewListener(NodeViewListener listener) {
	listeners.add(listener);
    }

    public void removeNodeViewListener(NodeViewListener listener) {
	listeners.remove(listener);
    }

    private void fireNodeViewEvent(NodeViewEvent event) {
	for (NodeViewListener listener : listeners) {
	    listener.nodeChanged(event);
	}
    }

    public boolean isMomLeft() {
	return momLeft;
    }

    public void setMomLeft(boolean momLeft) {
	this.momLeft = momLeft;
    }

}
