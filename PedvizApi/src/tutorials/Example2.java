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
import java.awt.*;

import pedviz.algorithms.Sugiyama;
import pedviz.graph.*;
import pedviz.io.CsvGraphLoader;
import pedviz.io.GraphIOException;
import pedviz.view.GraphView2D;
import pedviz.view.rules.*;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;

public class Example2 {
    public static void main(String[] args) {

	// Step 1
	Graph graph = new Graph();
	CsvGraphLoader loader = new CsvGraphLoader("data/tutorial_data.csv", ",");
	loader.setSettings("PID", "MOM", "DAD");
	try {
	    loader.load(graph);
	} catch (GraphIOException e) {
	    e.printStackTrace();
	}
	
	graph.getMetaData().addTrait("TEST");
	int c = 0;
	for (Node node: graph.getAllNodes()){
	    c++;
	    if (c%5==0){
	    node.setUserData("TEST",2);
	    }else{
		node.setUserData("TEST",0);
	    }
	}

	// Step 2
	Sugiyama s = new Sugiyama(graph);
	s.run();

	// Creates a frame
	JFrame frame = new JFrame("Tutorials - Example 2");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(800, 600);

	// Step 3
	GraphView2D view = new GraphView2D(s.getLayoutedGraph());
	frame.add(view.getComponent());

	// Step 4 - define rules
	view.addRule(new ShapeRule("sex", "1", new SymbolSexMale()));
	view.addRule(new ShapeRule("sex", "2", new SymbolSexFemale()));
	view.addRule(new ColorRule("TEST", 2, Color.BLUE));

	frame.setVisible(true);
    }
}
