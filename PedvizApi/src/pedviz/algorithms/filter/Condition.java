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
 * Implementations of this interface represent a condition.
 * 
 * @author lukas forer
 * 
 */

public interface Condition {
    /**
     * Check on equal.
     */
    public static final int EQUALS = 0;

    /**
     * Check on not equal.
     */
    public static final int NEQUALS = 1;

    /**
     * Check on greather.
     */
    public static final int GREATER = 2;

    /**
     * Check on lesser.
     */
    public static final int LESSER = 3;

    /**
     * Check on interval.
     */
    public static final int BETWEEN = 4;

    public static final int NBETWEEN = 5;

    /**
     * Checks if the condition for the given node is valid.
     * 
     * @param node
     *                Node object
     * @return true, if the condition for the given node is valid.
     */
    public boolean check(Node node);

}
