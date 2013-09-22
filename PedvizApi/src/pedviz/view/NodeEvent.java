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

import pedviz.graph.Graph;
import pedviz.graph.Node;

public class NodeEvent {

    /**
     * Mouse enters node.
     */
    public static final int MOUSE_ENTER = 0;

    /**
     * Mouse leaves node.
     */
    public static final int MOUSE_LEAVE = 1;

    /**
     * Node was selected.
     */
    public static final int SELECTED = 2;

    /**
     * Node was deselected.
     */
    public static final int DESELECTED = 3;

    /**
     * All nodes were deselected.
     */
    public static final int ALL_DESELECTED = 4;

    private Node node;

    private GraphView graphView;

    private int type;

    /**
     * Creates a new NodeEvent with the given properties.
     * 
     * @param graphView
     *                Sender object
     * @param node
     *                Node
     * @param type
     *                Type
     */
    public NodeEvent(GraphView graphView, Node node, int type) {
	this.node = node;
	this.graphView = graphView;
	this.type = type;
    }

    /**
     * Returns the type.
     * 
     * @return the type.
     */
    public int getType() {
	return type;
    }

    /**
     * Returns the GraphView object.
     * 
     * @return the GraphView object.
     */
    public GraphView getGraphView() {
	return graphView;
    }

    /**
     * Returns the Node object.
     * 
     * @return the Node object.
     */
    public Node getNode() {
	return node;
    }

    /**
     * Returns the Graph object.
     * 
     * @return the Graph object.
     */
    public Graph getGraph() {
	return graphView.getGraph().getGraph();
    }

    /**
     * Returns the NodeView object.
     * 
     * @return the NodeView object.
     */
    public NodeView getNodeView() {
	return graphView.getGraph().getNodeView(node);
    }

}
