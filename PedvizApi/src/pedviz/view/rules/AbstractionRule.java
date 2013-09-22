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
import pedviz.view.symbols.SymbolFamily;

/**
 * With this Rule it's possible to draw nodes in an abstract way dependent on a
 * given trait.
 * 
 * @author lukas forer
 * 
 */

public class AbstractionRule extends Rule {

    private int symbolmode = 0;

    private Object male = "2";

    private Object female = "1";

    /**
     * Draws all children as a single node. (default)
     */
    public static final int NONE = 0;

    /**
     * Draws all children as a single node and shows their total number.
     */
    public static final int NUMBER = 1;

    /**
     * Draws all children as a single node and shows the number of females and
     * males.
     */
    public static final int NUMBER_2 = 2;

    /**
     * Creates a new AbstractionRule. The mode defines the appearance for
     * abstract nodes.
     * 
     * @param mode
     *                Mode
     *                <ul>
     *                <li>NONE: Draws all children as a single node. (default)</li>
     *                <li>NUMBER: Draws all children as a single node and shows
     *                their total number.</li>
     *                <li>NUMBER_2: Draws all children as a single node and
     *                shows the number of females and males. </li>
     *                </ul>
     */
    public AbstractionRule(int mode, Object male, Object female) {
	super();
	this.male = male;
	this.female = female;
	this.symbolmode = mode;
    }

    public AbstractionRule(int mode) {
	this(mode, "2", "1");
    }

    public AbstractionRule() {
	this(NONE, "2", "1");
    }

    /**
     * Sets the mode.
     * 
     * @param mode
     *                the mode.
     */
    public void setSymbolMode(int symbolmode) {
	this.symbolmode = symbolmode;
    }

    /**
     * Returns the mode.
     * 
     * @return the mode.
     */
    public int getSymbolMode() {
	return symbolmode;
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (nodeview.getNode().isDummy() && !nodeview.isExpand()) {
	    nodeview.getSymbols().clear();
	    nodeview.getSymbols().add(
		    new SymbolFamily(symbolmode, male, female));
	    String hint = "";
	    for (NodeView node : nodeview.getNodes()) {
		hint += node.getHint();
		if (!node.equals(nodeview.getNodes().get(
			nodeview.getNodes().size() - 1))) {
		    hint += "\n{line}\n";
		}
		if (symbolmode == NONE) {
		    node.setSize(node.getSize() / 2f);
		}
	    }
	    if (symbolmode == NONE) {
		nodeview.setSize(nodeview.getSize() / 2f);
		nodeview.setColor(Color.DARK_GRAY);
		nodeview.setBorderColor(Color.DARK_GRAY);
	    } else {
		nodeview.setColor(Color.WHITE);
		nodeview.setSize(nodeview.getSize() * 0.8f);
	    }
	    nodeview.setHintText(hint);
	} else if (!nodeview.isExpand()) {
	    nodeview.getSymbols().clear();
	    nodeview.getSymbols().add(
		    new SymbolFamily(symbolmode, male, female));
	    if (symbolmode == NONE) {
		nodeview.setSize(nodeview.getSize() / 2f);
		nodeview.setColor(Color.DARK_GRAY);
		nodeview.setBorderColor(Color.DARK_GRAY);
	    } else {
		nodeview.setColor(Color.WHITE);
		nodeview.setSize(nodeview.getSize() * 0.8f);
	    }
	}
    }

    public Object getFemale() {
	return female;
    }

    public void setFemale(Object female) {
	this.female = female;
    }

    public Object getMale() {
	return male;
    }

    public void setMale(Object male) {
	this.male = male;
    }

}
