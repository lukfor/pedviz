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

package pedviz.io;

import java.util.Vector;

import pedviz.graph.Graph;

/**
 * This abstract class is a super class for graph loader classes. You can use
 * this guidelines for writing your own Loader class:
 * <ul>
 * <li>Overwrite the method load.
 * <li>Load all Nodes from your source and add them to the Graph object.</li>
 * <li>All informations about a indivual you must add to the Node with the
 * method setUserData().</li>
 * <li>If you have loaded all Nodes, then add all Edges to the Graph.</li>
 * </ul>
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public interface GraphLoader {
 
    /**
     * Loads data in the given Graph object.
     * 
     * @param graph
     *                Gaph object.
     */
    public abstract void load(Graph graph) throws GraphIOException;

    /**
     * Returns a array of all columns.
     * 
     * @return a array of all columns.
     */
    public abstract Vector<String> getTraits() throws GraphIOException;

}
