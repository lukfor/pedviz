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

package pedviz.algorithms;

import pedviz.graph.Graph;

/**
 * Implementations of this interface represent an algorithm, which assigns every
 * node a layer.
 * 
 * @author Lukas Forer
 * 
 */
public interface HierarchieBuilder {

    /**
     * Starts the algorithm and assigns every node from the Graph object a
     * layer.
     * 
     * @param graph
     *                Graph Object
     */
    public void buildHirarchie(Graph graph);
}
