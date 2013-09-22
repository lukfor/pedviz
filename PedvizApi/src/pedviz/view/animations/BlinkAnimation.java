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

package pedviz.view.animations;

import java.awt.Color;
import java.util.Set;

import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.view.GraphView;
import pedviz.view.NodeView;

/**
 * This animation lets a collection of nodes blink.
 * 
 * @author lukas forer
 * 
 */
public class BlinkAnimation extends Animation {

    private Set<Node> nodes;

    private boolean blink = true;

    private Color background = Color.YELLOW;

    private Color border = Color.YELLOW;

    /**
     * Creates a new BlinkAnimation object for the given GraphView object. All
     * nodes of the given collection change their color to the given color.
     * 
     * @param graphView
     *                GraphView object.
     * @param nodes
     *                a collection of nodes which blinks.
     * @param background
     *                new background color.
     * @param border
     *                new border color.
     */
    public BlinkAnimation(GraphView graphView, Set<Node> nodes,
	    Color background, Color border) {
	this(graphView, nodes);
	this.background = background;
	this.border = border;
    }

    /**
     * Creates a new BlinkAnimation object for the given GraphView object. All
     * nodes of the given collection change their color to the given color.
     * 
     * @param graphView
     * @param nodes
     */
    public BlinkAnimation(GraphView graphView, Set<Node> nodes) {
	super(graphView);
	this.nodes = nodes;
    }

    @Override
    public void pulse(GraphView graphView) {
	LayoutedGraph graph = graphView.getGraph();
	if (blink) {
	    for (Node node : nodes) {
		NodeView mn = graph.getNodeView(node.getId());
		mn.setBorderWidth(0.75f);
		mn.setBorderColor(border);
		mn.setColor(background);
	    }
	} else {
	    for (Node node : nodes) {
		NodeView mn = graph.getNodeView(node.getId());
		mn.setBorderWidth(graph.getDefaultNodeView().getBorderWidth());
		mn.setBorderColor(graph.getDefaultNodeView().getBorderColor());
		mn.setColor(graph.getDefaultNodeView().getColor());
	    }
	}
	blink = !blink;
	graphView.updateNodes(nodes);
    }

    @Override
    public void stop() {
	super.stop();
	LayoutedGraph graph = graphView.getGraph();
	for (Node node : nodes) {
	    NodeView mn = graph.getNodeView(node.getId());
	    mn.setBorderWidth(graph.getDefaultNodeView().getBorderWidth());
	    mn.setBorderColor(graph.getDefaultNodeView().getBorderColor());
	    mn.setColor(graph.getDefaultNodeView().getColor());
	}
	graphView.updateNodes(nodes);
    }
}
