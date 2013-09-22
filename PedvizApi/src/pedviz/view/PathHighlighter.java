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

import java.util.ArrayList;

import javax.swing.JOptionPane;

import pedviz.algorithms.Highlighter;
import pedviz.graph.Node;

/**
 * This class checks if exists a path between two persons and highlights it.
 * 
 * @author lukas forer
 * 
 */
public class PathHighlighter implements NodeListener {

    private boolean enabled = true;

    /**
     * Creates a new PathHighlighter object.
     * 
     */
    public PathHighlighter() {

    }

    public void onNodeEvent(NodeEvent event) {
	if (enabled) {
	    switch (event.getType()) {
	    case NodeEvent.SELECTED:
		event.getGraphView().setMultiselection(true);

		if (event.getGraphView().getSelectionCount() == 2) {

		    Node nodeA = event.getGraphView().getSelection().get(0);
		    Node nodeB = event.getGraphView().getSelection().get(1);
		    ArrayList<Node> nodes2 = Highlighter.findPath(nodeA, nodeB);
		    if (nodes2 != null) {
			event.getGraphView().unHighlightAll();
			event.getGraphView().highlight(nodes2);
		    } else {
			JOptionPane
				.showMessageDialog(null, "No path between "
					+ nodeA.getId() + " and "
					+ nodeB.getId() + ".");
		    }
		    event.getGraphView().deselect();
		}
		break;
	    case NodeEvent.ALL_DESELECTED:
		event.getGraphView().deselect();
		event.getGraphView().unHighlightAll();
		break;
	    }
	}
    }

    /**
     * Returns true, if the highlighter is enabled.
     * 
     * @return true, if the highlighter is enabled.
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Enables or disables the highlighter.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }
}
