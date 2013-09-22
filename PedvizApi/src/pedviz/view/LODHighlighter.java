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

import pedviz.algorithms.Highlighter;
import pedviz.graph.Node;

/**
 * This class provides methods for highlighting nodes dynamicly.
 * 
 * @author lukas forer
 * 
 */
public class LODHighlighter implements NodeListener {

    /**
     * Maternal lineage of a person.
     */
    public static final int MATERNAL = 2;

    /**
     * Paternal lineage of a person.
     */
    public static final int PATERNAL = 4;

    /**
     * Maternal and paternal lineage of a person.
     */
    public static final int MATERNAL_AND_PATERNAL = 8;

    /**
     * All successors of a person.
     */
    public static final int SUCCESSORS = 16;

    /**
     * All ancestors of a person. (default)
     */
    public static final int ANCESTORS = 32;

    /**
     * All successors and ancestors of a person.
     */
    public static final int SUCCESSORS_AND_ANCESTORS = 64;

    private int mode = Highlighter.ANCESTORS;

    private boolean enabled = true;

    /**
     * Creates a new LODHighlighter object. The default mode is ANCESTORS.
     * 
     */
    public LODHighlighter() {
	setMode(Highlighter.ANCESTORS);
    }

    /**
     * Create a new LODHighlighter object with the given mode.
     * 
     * @param mode
     */
    public LODHighlighter(int mode) {
	setMode(mode);
    }

    public void onNodeEvent(NodeEvent event) {
	if (enabled) {
	    switch (event.getType()) {
	    case NodeEvent.MOUSE_ENTER:
		event.getGraphView().unHighlightAll();
		ArrayList<Node> nodes = Highlighter.findLineOfDescents(event
			.getGraph(), event.getNode(), mode);
		event.getGraphView().highlight(nodes);
		break;
	    case NodeEvent.MOUSE_LEAVE:
		event.getGraphView().unHighlightAll();
		break;
	    }
	}
    }

    /**
     * Returns the mode.
     * 
     * @return the mode.
     */
    public int getMode() {
	return mode;
    }

    /**
     * Sets the mode.
     * 
     * @param mode
     *                the mode.
     */
    public void setMode(int mode) {
	this.mode = mode;
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
