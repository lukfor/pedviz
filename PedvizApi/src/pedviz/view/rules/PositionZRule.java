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
 * This rule can be used for changing the color, dependent on the position on
 * the Z-axis. It's very helpful, if you want to give each wall a different
 * color in the GraphView3D.
 * 
 * @author lukas forer
 * 
 */
public class PositionZRule extends Rule {
    private float valueZ = 0;

    private Color color;

    /**
     * Constructs the Rule with given position on the Z-axis and color.
     * 
     * @param valueZ
     *                position on the Z-axis
     * @param color
     */
    public PositionZRule(float valueZ, Color color) {
	this.valueZ = valueZ;
	this.color = color;
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.getNode().isDummy()) {
	    if (nodeview.getPosZ() == valueZ) {
		nodeview.setColor(color);
	    }
	}
    }

}
