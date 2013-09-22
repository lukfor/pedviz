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
import pedviz.graph.*;
import pedviz.io.*;
import pedviz.view.DefaultEdgeView;
import pedviz.view.GraphView2D;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.*;
import pedviz.view.*;

import java.awt.geom.*;
import java.awt.*;

class MyHaplotypeSymbol extends Symbol2D {

    private Rectangle2D rectangle = new Rectangle2D.Double();

    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView node) {

	float top = (float) (-position.getY() + (size / 2f) + 0.5);
	float left = (float) (position.getX() - (size / 2f));

	// draws background
	g.setColor(Color.white);
	rectangle.setFrame(left + 1.5, top, size - 3, size * 2);
	g.fill(rectangle);

	// draws the border
	g.setColor(border);
	rectangle.setFrame(left + 1.5, top, size - 3, size * 2);
	g.draw(rectangle);
    }

    @Override
    public int getPriority() {
	return 0;
    }
}

public class Example3 {

    public static void main(String[] args) {

	// Step 1
	Graph graph = new Graph();
	CsvGraphLoader loader = new CsvGraphLoader("tutorial_data.csv", ",");
	loader.setSettings("PID", "MOM", "DAD");
	try {
	    loader.load(graph);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// Step 2 - register symbol
	DefaultNodeView nodeview = new DefaultNodeView();
	nodeview.addSymbol(new MyHaplotypeSymbol());

	// In the current version of the pedvizapi you must adapt the spacing
	// between the nodes by hand.
	DefaultEdgeView edgeview = new DefaultEdgeView();
	edgeview.setGapTop(20);

	Sugiyama s = new Sugiyama(graph, nodeview, edgeview);
	s.getRubberBands().setVerticalSpacing(40);
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
	frame.setVisible(true);
    }
}
