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

import java.util.Set;

import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.view.GraphView;
import pedviz.view.NodeView;

/**
 * This animation lets a collection of nodes change their size.
 * 
 * @author Luki
 * 
 */
public class SizeAnimation extends Animation {

    private Set<Node> nodes;

    private float f = 1.0f;

    private boolean g = true;

    /**
     * Creates a new SizeAnimation for the given GraphView object. All nodes of
     * the given collection change their size.
     * 
     * @param graphView
     * @param nodes
     */
    public SizeAnimation(GraphView graphView, Set<Node> nodes) {
	super(graphView);
	this.nodes = nodes;
    }

    @Override
    public void pulse(GraphView graphView) {
	LayoutedGraph graph = graphView.getGraph();
	if (g) {
	    f += 0.1f;
	} else {
	    f -= 0.1f;
	}

	if (f <= 1.0f) {
	    g = true;
	}

	if (f >= 1.5f) {
	    g = false;
	}

	for (Node node : nodes) {
	    NodeView mn = graph.getNodeView(node.getId());
	    mn.setSize(graph.getDefaultNodeView().getSize() * f);
	}
    }
}
