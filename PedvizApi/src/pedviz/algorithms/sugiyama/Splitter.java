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

package pedviz.algorithms.sugiyama;

import pedviz.graph.Graph;
import pedviz.graph.Node;

/**
 * This interface defines methods for an algorithm that can split the given
 * graph in two walls. It can be used as a WallSplitter for the SugiyamaLayout
 * class.
 * 
 * @author Lukas Forer
 * @version 0.1
 */

public interface Splitter {
    /**
     * Returns the id of the wall for the given node. The id is dependent from
     * the used algorithm.
     * 
     * @param graph
     *                the Graph object
     * @param node
     *                the Node object
     * @param index
     *                the index of the Node in the given Graph
     */
    public abstract int getWall(Graph graph, Node node, int index);

    /**
     * This method will be run before the graph will be split.
     * 
     * @param graph
     *                the Graph object
     */
    public abstract void beforeSplit(Graph graph);

    /**
     * This method will be run after the graph was split.
     * 
     * @param graph
     *                the Graph object
     */
    public abstract void afterSplit(Graph graph);
}
