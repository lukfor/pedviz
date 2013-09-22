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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import pedviz.algorithms.Sugiyama;
import pedviz.graph.Graph;
import pedviz.haplotype.Haplotypes;
import pedviz.haplotype.MerlinHaplotypes;
import pedviz.io.CsvGraphLoader;
import pedviz.io.GraphIOException;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView2D;
import pedviz.view.rules.ColorRule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.SymbolHaplotypes;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;

public class Example32 {

    private static Haplotypes haplotypes;

    private static JList list;

    private static Sugiyama s;

    private static GraphView2D view;

    public static void main(String[] args) {

	// Step 1
	Graph graph = new Graph();
	CsvGraphLoader loader = new CsvGraphLoader("data/pedin.22", " ");
	loader.setSettings("Id", "MOM", "DAD");
	try {
	    loader.load(graph);
	} catch (GraphIOException e) {
	    e.printStackTrace();
	}

	// Step 2 - register symbol
	DefaultNodeView nodeview = new DefaultNodeView();
	haplotypes = new MerlinHaplotypes("data/merlin.chr", "data/map.22");
	nodeview.addSymbol(new SymbolHaplotypes(haplotypes));

	nodeview.addHintAttribute("Id");
	nodeview.setColor(Color.WHITE);

	// In the current version of the pedvizapi you must adapt the spacing
	// between the nodes by hand.
	DefaultEdgeView edgeview = new DefaultEdgeView();
	// edgeview.setGapTop(20);

	s = new Sugiyama(graph, nodeview, edgeview);
	s.getRubberBands().setHorizontalGap(10);
	s.run();

	// Creates a frame
	JFrame frame = new JFrame("Tutorials - Example 3");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(new BorderLayout());
	frame.setSize(800, 600);

	// Step 3
	view = new GraphView2D(s.getLayoutedGraph());
	// view.setLegend(new LegendHaplotype(haplotypes));
	frame.getContentPane().add(view.getComponent());

	list = new JList();
	ListModel model = new DefaultComboBoxModel(haplotypes.getMarkers());
	list.setModel(model);

	list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	frame.getContentPane().add(new JScrollPane(list), BorderLayout.EAST);

	JButton button = new JButton("Update");
	button.addActionListener(new ActionListener() {

	    // @Override
	    public void actionPerformed(ActionEvent arg0) {
		haplotypes.hideAllMarkers();
		for (Integer i : list.getSelectedIndices()) {
		    haplotypes.showMarker(i);
		}
		s.getRubberBands().updateYPosition();
		view.centerGraph();
		view.updateGraphView();
	    }

	});

	frame.getContentPane().add(button, BorderLayout.SOUTH);

	// Step 4
	view.addRule(new ShapeRule("sex", "1", new SymbolSexMale()));
	view.addRule(new ShapeRule("sex", "2", new SymbolSexFemale()));
	view.addRule(new ColorRule("aff", "2", Color.BLACK));
	frame.setVisible(true);
    }
}
