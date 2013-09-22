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

import java.util.ArrayList;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;

/**
 * This class can be used as a Splitter for the SugiyamaLayout class. It splits
 * the specified graph in two at random walls.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class RandomSplitter implements Splitter {

    /**
     * Creates a new RandomSplitter object.
     * 
     */
    public RandomSplitter() {

    }

    public void beforeSplit(Graph graph) {

    }

    public void afterSplit(Graph graph) {
	ArrayList<Node> list = new ArrayList<Node>();

	for (Node node : graph.getNodes()) {
	    if (node.getInDegree() == 0) {
		boolean t = true;
		for (Edge e : node.getOutEdges()) {
		    if (e.getEnd().getHierarchy() == node.getHierarchy())
			t = false;
		}
		if (t)
		    list.add(node);
	    }

	    if (node.getOutDegree() == 0) {
		boolean t = true;
		for (Edge e : node.getInEdges()) {
		    if (e.getStart().getHierarchy() == node.getHierarchy())
			t = false;
		}
		if (t)
		    list.add(node);
	    }
	}

	for (int j = 0; j < list.size(); j++) {
	    if (list.get(j).getHierarchy() == graph.getHierachy(0))
		graph.getHierachy(1).addNode(list.get(j).getLevel(),
			list.get(j));
	    else if (list.get(j).getHierarchy() == graph.getHierachy(1))
		graph.getHierachy(0).addNode(list.get(j).getLevel(),
			list.get(j));
	}
    }

    public int getWall(Graph graph, Node node, int index) {
	return index % 2;
    }
}
