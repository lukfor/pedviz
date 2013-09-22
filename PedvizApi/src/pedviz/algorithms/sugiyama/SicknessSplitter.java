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
 * This class can be used as a WallSplitter for the SugiyamaLayout class. It
 * splits the specified graph in two walls, dependent on the sickness of a
 * person.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class SicknessSplitter implements Splitter {
    private String[] columns;

    private String[] values;

    /**
     * Creates a SicknessSplitter object with the given array of traits and
     * values.
     * 
     * @param columns
     *                the array contains all columns, which contains sickness
     *                informations.
     * @param values
     *                the array contains the values, which describe when the
     *                person is sick.
     */
    public SicknessSplitter(String[] columns, String[] values) {
	this.columns = columns;
	this.values = values;
    }

    public void beforeSplit(Graph graph) {

    }

    public void afterSplit(Graph graph) {

    }

    public int getWall(Graph graph, Node node, int index) {
	for (int i = 0; i < columns.length; i++) {
	    if (node.getNodes().size() > 0) {
		for (Node child : node.getNodes()) {
		    String data = (String) child.getUserData(columns[i]);
		    if (data != null) {
			if (data.equals(values[i]))
			    return 1;
		    }
		}
	    } else {
		String data = (String) node.getUserData(columns[i]);
		if (data != null) {
		    if (data.equals(values[i]))
			return 1;
		}
	    }
	}
	return 0;
    }
}
