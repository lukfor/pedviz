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
package pedviz.event;

import pedviz.view.NodeView;

/**
 * Contains informations about a NodeView Event.
 * 
 * @author Luki
 * 
 */
public class NodeViewEvent {
    private int type;
    private NodeView nodeview;

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_Z = 3;

    /**
     * Creates a new NodeViewEvent with the given parameters.
     * 
     * @param nodeview
     *                NodeView object.
     * @param type
     *                Type.
     */
    public NodeViewEvent(NodeView nodeview, int type) {
	this.nodeview = nodeview;
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
     * Sets the type.
     * 
     * @param type
     *                the type.
     */
    public void setType(int type) {
	this.type = type;
    }

    /**
     * Returns the NodeView object.
     * 
     * @return the NodeView object.
     */
    public NodeView getNodeview() {
	return nodeview;
    }

    /**
     * Sets the NodeView object.
     * 
     * @param nodeview
     *                the NodeView object.
     */
    public void setNodeview(NodeView nodeview) {
	this.nodeview = nodeview;
    }
}
