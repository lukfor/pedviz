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

import pedviz.view.NodeView;
import pedviz.view.Symbol;

/**
 * With this rule you can set a symbol, dependent on the given trait (UserData).
 * You can use it mainly for illustrating the sex and other traits.
 * 
 * @author lukas forer
 * 
 */
public class ShapeRule extends Rule {
    private String userData;

    private Object value;

    private Symbol shape;

    /**
     * Constructs a ShapeRule with given trait id, trait value and the symbol.
     * 
     * @param userData
     *                trait id
     * @param value
     *                trait value
     * @param shape
     *                symbol
     */
    public ShapeRule(String userData, Object value, Symbol shape) {
	this.userData = userData;
	this.value = value;
	this.shape = shape;
    }

    /**
     * Constructs a ShapeRule with the symbol.
     * 
     * @param userData
     *                trait id
     * @param value
     *                trait value
     * @param shape
     *                symbol
     */
    public ShapeRule(Symbol shape) {
	this.userData = null;
	this.value = null;
	this.shape = shape;
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.getNode().isDummy()) {
	    if (userData != null) {
		Object currentValue = nodeview.getNode().getUserData(userData);
		if (currentValue != null) {
		    if (currentValue.equals(value))
			nodeview.getSymbols().add(shape);
		}
	    } else {
		nodeview.getSymbols().add(shape);
	    }
	}
    }

    /**
     * Returns the symbol.
     * 
     * @return the symbol.
     */
    public Symbol getShape() {
	return shape;
    }

    /**
     * Sets the symbol.
     * 
     * @param shape
     *                the symbol.
     */
    public void setShape(Symbol shape) {
	this.shape = shape;
    }

}
