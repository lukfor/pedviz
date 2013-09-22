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

/**
 * This class represents a connection between two nodes. The start node is the
 * parent and the end node is the child.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class Edge {
    private Node start;

    private Node end;

    private boolean dummy = false;

    /**
     * Creates a new Edge object with the given start and end node.
     * 
     * @param start
     * @param end
     */
    public Edge(Node start, Node end) {
	this.start = start;
	this.end = end;
    }

    /**
     * Returns the end node
     * 
     * @return end node (child)
     */
    public Node getEnd() {
	return end;
    }

    /**
     * Sets the end node.
     * 
     * @param end
     *                end node (child) Sets the end node.
     */
    public void setEnd(Node end) {
	this.end = end;
    }

    /**
     * Returns the start node.
     * 
     * @return start node (parent)
     */
    public Node getStart() {
	return start;
    }

    /**
     * Sets the start node.
     * 
     * @param start
     *                node (parent) Sets the start node.
     */
    public void setStart(Node start) {
	this.start = start;
    }

    /**
     * Returns true if the edge is a dummy.
     * 
     * @return true if the edge is a dummy.
     */
    public boolean isDummy() {
	return dummy;
    }

    /**
     * True, if the edge is a dummy.
     * 
     * @param dummy
     *                True, if the edge is a dummy.
     */
    public void setDummy(boolean dummy) {
	this.dummy = dummy;
    }

    @Override
    public String toString() {
	return start + "(" + start.getLevel() + ")  ----> (" + end.getLevel()
		+ ")" + end;
    }

}
