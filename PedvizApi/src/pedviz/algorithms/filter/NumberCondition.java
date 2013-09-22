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

package pedviz.algorithms.filter;

import pedviz.graph.Node;

/**
 * This class represents a Condtion for a trait, which contains an integer or a
 * double value.
 * 
 * @author lukas forer
 * 
 */
public class NumberCondition implements Condition {

    private String data;

    private int mode = EQUALS;

    private Double value1;

    private Double value2;

    /**
     * Creates a new NumberCondition for the given trait with the given mode and
     * values.
     * 
     * @param data
     *                trait
     * @param mode
     *                mode
     * @param value
     *                value
     */
    public NumberCondition(String data, int mode, Double value) {
	this.data = data;
	this.mode = mode;
	this.value1 = value;
    }

    /**
     * Creates a new NumberCondition for the given trait with the given mode and
     * values.
     * 
     * @param data
     *                trait
     * @param mode
     *                mode
     * @param value1
     *                value1
     * @param value2
     *                value2
     */
    public NumberCondition(String data, int mode, Double value1, Double value2) {
	this.data = data;
	this.mode = mode;
	this.value1 = value1;
	this.value2 = value2;
    }

    public boolean check(Node node) {
	Double node_value = 0.0;
	if (node.getUserData(data) != null) {
	    if (node.getUserData(data) instanceof Double) {
		node_value = (Double) node.getUserData(data);
	    } else {
		node_value = Double.parseDouble(node.getUserData(data)
			.toString());
	    }
	    switch (mode) {
	    case EQUALS:
		return value1.compareTo(node_value) == 0;
	    case NEQUALS:
		return value1.compareTo(node_value) != 0;
	    case BETWEEN:
		return value1.compareTo(node_value) <= 0
			&& value2.compareTo(node_value) >= 0;
	    case NBETWEEN:
		return !(value1.compareTo(node_value) <= 0 && value2
			.compareTo(node_value) >= 0);
	    case GREATER:
		return value1.compareTo(node_value) < 0;
	    case LESSER:
		return value1.compareTo(node_value) > 0;
	    default:
		return false;
	    }
	}
	return false;
    }
}
