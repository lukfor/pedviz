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

import java.util.ArrayList;
import java.util.Vector;

import pedviz.graph.Graph;
import pedviz.graph.Node;

/**
 * This class represents a collection of conditions.
 * 
 * @author lukas forer
 * 
 */
public class Filter {
    private Vector<Condition> conditions;

    /**
     * and operator.
     */
    public static final int AND = 0;

    /**
     * or operator.
     */
    public static final int OR = 1;

    private int operator = OR;

    /**
     * Creates a new Filter object.
     * 
     */
    public Filter() {
	conditions = new Vector<Condition>();
    }

    /**
     * Creates a new Filter object and the conditions are combined with the
     * given operator.
     * 
     * @param operator
     *                operator (AND/OR)
     */
    public Filter(int operator) {
	conditions = new Vector<Condition>();
	this.operator = operator;
    }

    /**
     * Adds a new condition to the collection.
     * 
     * @param condition
     */
    public void addCondition(Condition condition) {
	conditions.add(condition);
    }

    /**
     * Executes all conditions on the given graph object and returns a
     * collection of all valid nodes.
     * 
     * @param graph
     *                Graph object
     * @return a collection of all valid nodes.
     */
    public ArrayList<Node> execute(Graph graph) {
	ArrayList<Node> result = new ArrayList<Node>();
	for (Node node : graph.getAllNodes()) {
	    if (!node.isDummy()) {
		boolean temp = false;
		if (operator == AND)
		    temp = true;
		for (Condition condition : conditions) {
		    if (condition.check(node)) {
			if (operator == AND) {
			    temp = temp & true;
			}
			if (operator == OR) {
			    temp = temp | true;
			}
		    } else {
			if (operator == AND)
			    temp = false;
		    }
		}
		if (temp) {
		    result.add(node);
		}
	    }
	}
	return result;
    }

    /**
     * Returns the operator.
     * 
     * @return the operator.
     */
    public int getOperator() {
	return operator;
    }

    /**
     * Sets the operator.
     * 
     * @param operator
     *                operator.
     */
    public void setOperator(int operator) {
	this.operator = operator;
    }
}
