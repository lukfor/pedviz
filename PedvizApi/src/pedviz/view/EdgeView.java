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
import java.util.List;

import pedviz.graph.Edge;

/**
 * This class contains all informations that are needed for drawing a Edge
 * object in a GraphView.
 * 
 * @author lukas forer
 * 
 */
public class EdgeView extends DefaultEdgeView {
    private NodeView start;

    private List<NodeView> end;

    private Edge edge;

    private Object data;

    /**
     * Creates a EdgeView for the given Edge object with the given
     * DefaultEdgeView as template.
     * 
     * @param edge
     *                Edge
     * @param start
     *                NodeView of the start node.
     * @param end[]
     *                Arry of NodeViews of the end nodes.
     */
    public EdgeView(Edge edge, DefaultEdgeView defaultEdgeView) {
	this.edge = edge;
	setColor(defaultEdgeView.getColor());
	setWidth(defaultEdgeView.getWidth());
	setHighlightedColor(defaultEdgeView.getHighlightedColor());
	setGapBottom(defaultEdgeView.getGapBottom());
	setGapTop(defaultEdgeView.getGapTop());
	setConnectChildren(defaultEdgeView.isConnectChildren());
	setColorForLongLines(defaultEdgeView.getColorForLongLines());
	setHighlightedWidth(defaultEdgeView.getHighlightedWidth());
	setColorForLongLines(defaultEdgeView.getColorForLongLines());
	setAlphaForLongLines(defaultEdgeView.getAlphaForLongLines());
	setAlpha(defaultEdgeView.getAlpha());
	setDeltaForLongLines(defaultEdgeView.getDeltaX(), defaultEdgeView
		.getDeltaY(), defaultEdgeView.getDeltaZ());
    }

    /**
     * Returns true, if the edge is highlighted.
     * 
     * @return true, if the edge is highlighted
     */
    public boolean isHighlighted() {
	boolean s = start.isHighlighted() || start.isMouseOver();
	boolean e = false;

	for (NodeView endNode : end)
	    e = e || endNode.isHighlighted() || endNode.isMouseOver();

	return s && e;
    }

    /**
     * Returns true, if the edge is hidden.
     * 
     * @return true, if the edge is hidden
     */
    public boolean isHidden() {
	boolean s = start.isHidden();
	boolean e = false;

	for (NodeView endNode : end)
	    e = e || !endNode.isHidden();

	return s || !e;
    }

    /**
     * Returns the Edge object.
     * 
     * @return the Edge object
     */
    public Edge getEdge() {
	return edge;
    }

    /**
     * Returns the first end node.
     * 
     * @return the first end node.
     */
    public NodeView getEnd() {
	return end.get(0);
    }

    /**
     * Returns the start node.
     * 
     * @return the start node.
     */
    public NodeView getStart() {
	return start;
    }

    /**
     * Returns a collection of all end nodes.
     * 
     * @return a collection of all end nodes.
     */
    public List<NodeView> getEnds() {
	return end;
    }

    /**
     * Sets the start node.
     * 
     * @param start
     *                start node.
     */
    public void setStart(NodeView start) {
	this.start = start;
    }

    /**
     * Sets all end node.
     * 
     * @param end
     */
    public void setEnds(List<NodeView> end) {
	this.end = end;
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

    @Override
    public Color getColor() {
	if (getColorForLongLines() != null && end != null) {
	    if (start.getNode().isDummy() || end.get(0).getNode().isDummy()) {
		return getColorForLongLines();
	    }

	    if (Math.abs(start.getPosX() - end.get(0).getPosX()) > getDeltaX()
		    || Math.abs(start.getPosY() - end.get(0).getPosY()) > getDeltaY()
		    || Math.abs(start.getPosZ() - end.get(0).getPosZ()) > getDeltaZ())
		return getColorForLongLines();
	}
	return super.getColor();
    }

    @Override
    public float getAlpha() {
	if (!isHighlighted()) {
	    if (start.getNode().isDummy() || end.get(0).getNode().isDummy()) {
		return getAlphaForLongLines();
	    }

	    if ((Math.abs(start.getPosX() - end.get(0).getPosX()) > getDeltaX())
		    || (Math.abs(start.getPosY() - end.get(0).getPosY()) > getDeltaY())
		    || (Math.abs(start.getPosZ() - end.get(0).getPosZ()) > getDeltaZ())) {
		return getAlphaForLongLines();
	    }
	}
	return super.getAlpha();
    }
}
