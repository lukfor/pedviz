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

import javax.swing.event.ChangeListener;

import pedviz.algorithms.Algorithm;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.EdgeView;
import pedviz.view.NodeView;

/**
 * 
 * @author lukas
 * 
 * @param <N>
 *                extended NodeView with additional properties.
 * @param <E>
 *                extended EdgeView with additional properties.
 */
public abstract class Layout<N extends NodeView, E extends EdgeView> implements
	Algorithm {

    protected DefaultNodeView defaultNodeView;

    protected DefaultEdgeView defaultEdgeView;

    protected LayoutedGraph layoutedGraph;

    protected Graph graph;

    /**
     * Creates a new Layout object for the given Graph object width the given
     * settings.
     * 
     * @param graph
     *                Graph object.
     * @param defaultNodeView
     *                node settings.
     * @param defaulEdgeView
     *                edge settings.
     */
    public Layout(Graph graph, DefaultNodeView defaultNodeView,
	    DefaultEdgeView defaulEdgeView) {
	this.graph = graph;
	this.defaultNodeView = defaultNodeView;
	this.defaultEdgeView = defaulEdgeView;
	layoutedGraph = new LayoutedGraph<N, E>(graph, this, defaultNodeView,
		defaultEdgeView);
    }

    /**
     * Returns the layouted graph.
     * 
     * @return
     */
    public LayoutedGraph getLayoutGraph() {
	return layoutedGraph;
    }

    public void addChangeListener(ChangeListener l) {
    }

    public String getMessage() {
	return null;
    }

    public int getPercentComplete() {
	return 0;
    }

    /**
     * Reruns the layoutalgorithm.
     * 
     * @param layoutedGraph
     */
    public void rerun(LayoutedGraph layoutedGraph) {
	this.layoutedGraph = layoutedGraph;
	run();
    }

    public void run() {
    }

    /**
     * Factory method for creating a NodeView object.
     * 
     * @param node
     *                Node object.
     * @param defaultNodeView
     *                node settings.
     * @return new NodeView object.
     */
    public abstract N createNodeView(Node node, DefaultNodeView defaultNodeView);

    /**
     * Factory method for creating a EdgeView object.
     * 
     * @param edge
     *                Edge object.
     * @param defaultEdgeView
     *                edge settings.
     * @return new EdgeView object.
     */
    public abstract E createEdgeView(Edge edge, DefaultEdgeView defaultEdgeView);
}
