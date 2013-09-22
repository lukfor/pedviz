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

import java.awt.Color;

import javax.swing.JFrame;

import pedviz.algorithms.Sugiyama;
import pedviz.graph.Graph;
import pedviz.io.CsvGraphLoader;
import pedviz.io.GraphIOException;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView2D;
import pedviz.view.rules.ColorRule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.SymbolGenotypes;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;

public class Example33 {

    public static void main(String[] args) {

	// Step 1
	Graph graph = new Graph();
	CsvGraphLoader loader = new CsvGraphLoader("data/10081.pre", " ");
	loader.setSettings("Id", "Fid", "Mid");
	try {
	    loader.load(graph);
	} catch (GraphIOException e) {
	    e.printStackTrace();
	}
	// Step 2 - register symbol
	DefaultNodeView nodeview = new DefaultNodeView();
	nodeview.addSymbol(new SymbolGenotypes("GABRB1", "D4S1645"));
	nodeview.addHintAttribute("Id");
	nodeview.setColor(Color.WHITE);

	// In the current version of the pedvizapi you must adapt the spacing
	// between the nodes by hand.
	DefaultEdgeView edgeview = new DefaultEdgeView();
	// edgeview.setGapTop(20);

	Sugiyama s = new Sugiyama(graph, nodeview, edgeview);
	// s.getRubberBands().setVerticalSpacing(40);
	s.run();

	// Creates a frame
	JFrame frame = new JFrame("Tutorials - Example 3");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(800, 600);

	// Step 3
	GraphView2D view = new GraphView2D(s.getLayoutedGraph());
	frame.add(view.getComponent());

	// Step 4
	view.addRule(new ShapeRule("sex", "1", new SymbolSexMale()));
	view.addRule(new ShapeRule("sex", "2", new SymbolSexFemale()));
	view.addRule(new ColorRule("aff", "2", Color.BLACK));
	frame.setVisible(true);
    }
}
