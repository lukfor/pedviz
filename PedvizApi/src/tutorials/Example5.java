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

package tutorials;

import javax.swing.JFrame;

import pedviz.algorithms.Sugiyama;
import pedviz.graph.Graph;
import pedviz.io.CsvGraphLoader;
import pedviz.io.GraphIOException;
import pedviz.view.GraphView2D;
import pedviz.view.NodeEvent;
import pedviz.view.NodeListener;

public class Example5 {
    public static void main(String[] args) {

	// Step 1
	Graph graph = new Graph();
	CsvGraphLoader loader = new CsvGraphLoader("data/tutorial_data.csv", ",");
	loader.setSettings("PID", "MOM", "DAD");
	try {
	    loader.load(graph);
	} catch (GraphIOException e) {
	    e.printStackTrace();
	};

	// Step 2
	Sugiyama s = new Sugiyama(graph);
	s.run();

	// Creates a frame
	JFrame frame = new JFrame("Tutorials - Example 1");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(800, 600);

	// Step 3
	GraphView2D view = new GraphView2D(s.getLayoutedGraph());
	frame.add(view.getComponent());

	// Step 4 - NodeListener
	view.setSelectionEnabled(true);
	view.setMultiselection(true);

	view.addNodeListener(new NodeListener() {
	    public void onNodeEvent(NodeEvent event) {
		switch (event.getType()) {
		case NodeEvent.MOUSE_ENTER:
		    System.out.println("Mouse enter: "
			    + event.getNode().getId());
		    break;
		case NodeEvent.MOUSE_LEAVE:
		    System.out.println("Mouse leave: "
			    + event.getNode().getId());
		    break;

		case NodeEvent.DESELECTED:
		    System.out
			    .println("deselected: " + event.getNode().getId());
		    break;

		case NodeEvent.SELECTED:
		    System.out.println("selected: " + event.getNode().getId());
		    break;

		case NodeEvent.ALL_DESELECTED:
		    System.out.println("all deselected: ");
		    break;
		}
	    }
	});
	frame.setVisible(true);
    }
}
