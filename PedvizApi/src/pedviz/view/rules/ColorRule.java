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
import java.util.Vector;

import pedviz.view.NodeView;

/**
 * With this Rule you can define the color for a node, dependent on a given
 * trait. It provides also the possibility to using up to 4 colors in the same
 * symbol, how it's usual in pedigree drawing.
 * 
 * @author Lukas Forer
 * 
 */
public class ColorRule extends Rule {
    private Vector<String> userData;

    private Vector<Object> value = null;

    private Vector<Color> color;

    private Vector<Boolean> enabled;

    /**
     * Constructs a new ColorRule with the given trait id, trait value and the
     * given color.
     * 
     * @param userData
     *                trait identification
     * @param value
     *                trait value
     * @param color
     *                color
     */
    public ColorRule(String userData, Object value, Color color) {
	this.userData = new Vector<String>();
	this.userData.add(userData);
	this.color = new Vector<Color>();
	this.color.add(color);
	this.value = new Vector<Object>();
	this.value.add(value);
	this.enabled = new Vector<Boolean>();
	this.enabled.add(true);
    }

    /**
     * Adds a new trait to this rule. If you have two rules, the symbol will be
     * divided. At 3 and 4 rules, the symbol will be quartered.
     * 
     * @param userData
     *                trait identification
     * @param value
     *                trait value
     * @param color
     *                color
     */
    public void addRule(String userData, Object value, Color color) {
	this.userData.add(userData);
	this.color.add(color);
	this.value.add(value);
	this.enabled.add(true);
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.getNode().isDummy()) {

	    if (userData.size() == 1) {
		Object currentValue = nodeview.getNode().getUserData(
			userData.get(0));
		if (currentValue != null && currentValue.equals(value.get(0))
			&& enabled.get(0)) {
		    nodeview.setColor(color.get(0));
		}
	    } else {
		Color[] colors = new Color[userData.size()];
		for (int i = 0; i < userData.size(); i++) {
		    Object currentValue = nodeview.getNode().getUserData(
			    userData.get(i));
		    if (currentValue != null && enabled.get(i)) {
			if (currentValue.equals(value.get(i))) {
			    colors[i] = color.get(i);
			} else {
			    colors[i] = null;
			}
		    }
		}
		nodeview.setColors(colors);
	    }
	}
    }

    /**
     * Enables or disables the rule with the given index.
     * 
     * @param index
     *                Index
     * @param enabled
     */
    public void setEnabled(int index, boolean enabled) {
	this.enabled.set(index, enabled);
    }

    /**
     * Sets the condition for the rule width the given index.
     * 
     * @param index
     *                Index
     * @param trait
     *                Trait
     * @param value
     *                Value
     */
    public void setCondition(int index, String trait, Object value) {
	this.value.set(index, value);
	this.userData.set(index, trait);
    }

    /**
     * Sets the color for the rule with the given index.
     * 
     * @param index
     *                Index
     * @param color
     *                Color
     */
    public void setColor(int index, Color color) {
	this.color.set(index, color);
    }

    /**
     * Returns true, if the rule with the given index is enabled.
     * 
     * @param index
     *                Index
     * @return true, if the rule with the given index is enabled.
     */
    public boolean isEnabled(int index) {
	return this.enabled.get(index);
    }

}
