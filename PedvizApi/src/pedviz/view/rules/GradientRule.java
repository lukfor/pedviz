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
import java.util.Collections;
import java.util.HashMap;

import pedviz.view.NodeView;

/**
 * This rule allow you to use the nodes color as an indicator for qualitative
 * traits. You can define values like minimal value, maximal value and mean
 * value and assign their to colors. The rule calculates the correct color based
 * on a trait. It works with two or more values.
 * 
 * @author lukas forer
 * 
 */
public class GradientRule extends Rule {
    private HashMap<Double, Color> colors;

    int[] RGBdifference = new int[3];

    int[] beginRGBvalue = new int[3];

    Double fcolors;

    String userdata;

    /**
     * Constructs a GradientRule with the given trait and the list of values.
     * 
     * @param userdata
     * @param colors
     */
    public GradientRule(String userdata, HashMap<Double, Color> colors) {
	this.colors = colors;
	this.userdata = userdata;
	setMode(ONLY_3D);
    }

    /**
     * Returns the trait.
     * 
     * @return the trait.
     */
    public String getUserdata() {
	return userdata;
    }

    /**
     * Sets the trait.
     * 
     * @param userdata
     */
    public void setUserdata(String userdata) {
	this.userdata = userdata;
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.getNode().isDummy()) {
	    if (nodeview.getNode().getUserData(userdata) != null) {
		Double j = 0.0;
		if (nodeview.getNode().getUserData(userdata) instanceof Double) {
		    j = (Double) nodeview.getNode().getUserData(userdata);
		} else {
		    j = Double.parseDouble(nodeview.getNode().getUserData(
			    userdata).toString());
		}

		Double a = findA(j);
		Double b = findB(j);

		beginRGBvalue[0] = colors.get(a).getRed();
		beginRGBvalue[1] = colors.get(a).getGreen();
		beginRGBvalue[2] = colors.get(a).getBlue();

		RGBdifference[0] = colors.get(b).getRed() - beginRGBvalue[0];
		RGBdifference[1] = colors.get(b).getGreen() - beginRGBvalue[1];
		RGBdifference[2] = colors.get(b).getBlue() - beginRGBvalue[2];

		fcolors = b - a;
		j = j - a;
		int R = beginRGBvalue[0];
		int G = beginRGBvalue[1];
		int B = beginRGBvalue[2];
		if (fcolors != 0) {
		    R = beginRGBvalue[0]
			    + (int) (j * RGBdifference[0] / fcolors);
		    G = beginRGBvalue[1]
			    + (int) (j * RGBdifference[1] / fcolors);
		    B = beginRGBvalue[2]
			    + (int) (j * RGBdifference[2] / fcolors);
		}
		nodeview.setColor(new Color(R, G, B));
	    }
	}
    }

    private Double findA(Double j) {
	Double a = Double.MIN_VALUE;
	for (Double key : colors.keySet()) {
	    if (key <= j && key > a)
		a = key;
	}
	if (a == Double.MIN_VALUE) {
	    a = Collections.min(colors.keySet());
	}
	return a;
    }

    private Double findB(Double j) {
	Double b = Double.MAX_VALUE;
	for (Double key : colors.keySet()) {
	    if (key >= j && key < b)
		b = key;
	}
	if (b == Double.MAX_VALUE) {
	    b = Collections.max(colors.keySet());
	}
	return b;
    }

}
