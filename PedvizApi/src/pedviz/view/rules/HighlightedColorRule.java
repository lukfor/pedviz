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

package pedviz.view.rules;

import java.awt.Color;

import pedviz.view.NodeView;

/**
 * With this Rule you can define the highlighted color for a node, dependent on
 * a given trait.
 * 
 * @author Lukas Forer
 * 
 */
public class HighlightedColorRule extends Rule {
    private String userData;

    private Object value = null;

    private Color color;

    /**
     * Constructs a HighlightedColorRule with the given trait id, trait value
     * and the given color.
     * 
     * @param userData
     *                trait identification
     * @param value
     *                trait value
     * @param color
     *                color
     */
    public HighlightedColorRule(String userData, Object value, Color color) {
	this.userData = userData;
	this.color = color;
	this.value = value;
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.getNode().isDummy()) {
	    Object currentValue = nodeview.getNode().getUserData(userData);
	    if (currentValue != null) {
		if (currentValue.equals(value))
		    nodeview.setHighlightedColor(color);
	    }
	}
    }

}
