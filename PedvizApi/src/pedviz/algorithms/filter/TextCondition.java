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
 * This class represents a Condtion for a trait, which contains a string value.
 * 
 * @author lukas forer
 * 
 */
public class TextCondition implements Condition {

    private String data;

    private int mode = EQUALS;

    private String value1;

    private String value2;

    /**
     * Creates a new TextCondition for the given trait with the given mode and
     * values.
     * 
     * @param data
     *                trait
     * @param mode
     *                mode
     * @param value
     *                value
     */
    public TextCondition(String data, int mode, String value) {
	this.data = data;
	this.mode = mode;
	this.value1 = value;
    }

    /**
     * Creates a new TextCondition for the given trait with the given mode and
     * values.
     * 
     * @param data
     *                trait
     * @param mode
     *                mode
     * @param value1
     *                value
     * @param value2
     *                value
     */
    public TextCondition(String data, int mode, String value1, String value2) {
	this.data = data;
	this.mode = mode;
	this.value1 = value1;
	this.value2 = value2;
    }

    public boolean check(Node node) {
	if (node.getUserData(data) != null) {
	    String node_value = node.getUserData(data).toString();
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
	} else {
	    return false;
	}
    }
}
