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

package pedviz.rpedviz;

import java.awt.BorderLayout;
import java.awt.Color;

import pedviz.algorithms.Sugiyama;
import pedviz.graph.Graph;
import pedviz.io.ArrayGraphLoader;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView2D;
import pedviz.view.LODHighlighter;
import pedviz.view.rules.Rule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.SymbolAdopted;
import pedviz.view.symbols.SymbolDeceased;
import pedviz.view.symbols.SymbolQualitativeTrait;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;
import pedviz.view.symbols.SymbolSexUndesignated;

/**
 * This class enables a 2d visualization of pedigrees in R.
 * 
 * @author lukas forer
 * 
 */
public class RPedviz2D extends RPedviz {

    /**
     * Constructs a new RPedviz2D object.
     * 
     */
    public RPedviz2D() {
	super("RPedviz 2d");
    }

    @Override
    public void plot(String format, String seperator, String args[],
	    String[] hints, boolean highlighter) {

	graph = new Graph();

	ArrayGraphLoader loader = new ArrayGraphLoader(format, seperator, args);
	loader.setSettings("PID", "MOM", "DAD");
	loader.load(graph);

	DefaultEdgeView e = new DefaultEdgeView();
	e.setColor(new Color(50, 50, 50));
	e.setColorForLongLines(new Color(200, 200, 200));
	e.setGapBottom(5);
	e.setHighlightedColor(Color.red);
	e.setConnectChildren(true);

	DefaultNodeView d = new DefaultNodeView();
	d.setColor(new Color(255, 255, 255));
	d.setBorderColor(new Color(0, 0, 0));

	if (hints != null) {
	    for (String attr : hints) {
		d.addHintAttribute(attr);
	    }
	}

	Sugiyama sugiyama = new Sugiyama(graph, d, e);
	sugiyama.run();
	graphView = new GraphView2D(sugiyama.getLayoutedGraph());
	graphView.setBackgroundColor(new Color(255, 255, 255));
	graphView.addRule(new ShapeRule("sex", "-1",
		new SymbolSexUndesignated()));
	graphView.addRule(new ShapeRule("sex", "1", new SymbolSexMale()));
	graphView.addRule(new ShapeRule("sex", "2", new SymbolSexFemale()));
	graphView.addRule(new ShapeRule("dead", "1", new SymbolDeceased()));
	graphView.addRule(new ShapeRule("adopted", "1", new SymbolAdopted()));

	for (Rule rule : rules) {
	    graphView.addRule(rule);
	}

	graphView.setSelectionEnabled(true);
	lodHighlighter = new LODHighlighter(mode);
	graphView.addNodeListener(lodHighlighter);

	getContentPane().add(graphView.getComponent(), BorderLayout.CENTER);

	toolBar.setVisible(highlighter);
	setVisible(true);

    }

    /**
     * Adds a SymbolRule
     * 
     * @param trait
     *                trait
     * @param value
     *                value
     * @param symbol
     *                one of the following strings: "male", "female",
     *                "undesignated", "deceased", "adopted"
     */
    public void addSymbolRule(String trait, String value, String symbol) {
	if (symbol.toLowerCase().equals("male")) {
	    rules.add(new ShapeRule(trait, value, new SymbolSexMale()));
	}
	if (symbol.toLowerCase().equals("female")) {
	    rules.add(new ShapeRule(trait, value, new SymbolSexFemale()));
	}
	if (symbol.toLowerCase().equals("undesignated")) {
	    rules.add(new ShapeRule(trait, value, new SymbolSexUndesignated()));
	}
	if (symbol.toLowerCase().equals("deceased")) {
	    rules.add(new ShapeRule(trait, value, new SymbolDeceased()));
	}
	if (symbol.toLowerCase().equals("adopted")) {
	    rules.add(new ShapeRule(trait, value, new SymbolAdopted()));
	}
    }

    /**
     * Adds a qualitative trait.
     * 
     * @param trait
     *                trait
     * @param min
     *                minimal value
     * @param max
     *                maximal value
     * @param mean
     *                mean value
     * @param scope
     *                standard deviation
     */
    public void addQualitativeTrait(String trait, double min, double max,
	    double mean, double scope) {
	rules.add(new ShapeRule(new SymbolQualitativeTrait(trait, min, max,
		mean, scope)));
    }

}
