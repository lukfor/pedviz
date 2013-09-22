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
import java.util.HashMap;

import javax.swing.JPopupMenu;

import pedviz.algorithms.Sugiyama;
import pedviz.algorithms.sugiyama.RandomSplitter;
import pedviz.graph.Graph;
import pedviz.io.ArrayGraphLoader;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView3D;
import pedviz.view.LODHighlighter;
import pedviz.view.rules.GradientRule;
import pedviz.view.rules.Rule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols3d.SymbolAdopted3d;
import pedviz.view.symbols3d.SymbolDeceased3d;
import pedviz.view.symbols3d.SymbolSexFemale3d;
import pedviz.view.symbols3d.SymbolSexMale3d;
import pedviz.view.symbols3d.SymbolSexUndesignated3d;

/**
 * This class enables a 2.5d visualization of pedigrees in R.
 * 
 * @author lukas forer
 * 
 */
public class RPedviz3D extends RPedviz {

    /**
     * Constructs a new RPedviz3D object.
     * 
     */
    public RPedviz3D() {
	super("RPedviz 2,5d");
    }

    @Override
    public void plot(String format, String seperator, String args[],
	    String[] hints, boolean highlighter) {

	graph = new Graph();

	ArrayGraphLoader loader = new ArrayGraphLoader(format, seperator, args);
	loader.setSettings("PID", "MOM", "DAD");
	loader.load(graph);

	DefaultEdgeView e = new DefaultEdgeView();
	e.setColor(new Color(200, 200, 200));
	e.setColorForLongLines(new Color(50, 50, 50));
	e.setGapBottom(5);
	e.setConnectChildren(true);

	DefaultNodeView d = new DefaultNodeView();
	d.setColor(new Color(255, 255, 255));
	d.setBorderColor(new Color(0, 0, 0));

	if (hints != null) {
	    for (String attr : hints) {
		d.addHintAttribute(attr);
	    }
	}
	Sugiyama sugiyama = new Sugiyama(graph, d, e, new RandomSplitter());
	sugiyama.run();

	graphView = new GraphView3D(sugiyama.getLayoutedGraph());
	graphView.setBackgroundColor(new Color(0, 0, 0));
	graphView.addRule(new ShapeRule("sex", "-1",
		new SymbolSexUndesignated3d()));
	graphView.addRule(new ShapeRule("sex", "1", new SymbolSexMale3d()));
	graphView.addRule(new ShapeRule("sex", "2", new SymbolSexFemale3d()));
	graphView.addRule(new ShapeRule("dead", "1", new SymbolDeceased3d()));
	graphView.addRule(new ShapeRule("adopted", "1", new SymbolAdopted3d()));

	for (Rule rule : rules) {
	    graphView.addRule(rule);
	}

	graphView.setSelectionEnabled(true);
	lodHighlighter = new LODHighlighter(mode);
	graphView.addNodeListener(lodHighlighter);

	getContentPane().add(graphView.getComponent(), BorderLayout.CENTER);
	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
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
     *                "undesignated", "dead", "adopted"
     */
    public void addSymbolRule(String trait, String value, String symbol) {
	if (symbol.toLowerCase().equals("male")) {
	    rules.add(new ShapeRule(trait, value, new SymbolSexMale3d()));
	}
	if (symbol.toLowerCase().equals("female")) {
	    rules.add(new ShapeRule(trait, value, new SymbolSexFemale3d()));
	}
	if (symbol.toLowerCase().equals("undesignated")) {
	    rules
		    .add(new ShapeRule(trait, value,
			    new SymbolSexUndesignated3d()));
	}
	if (symbol.toLowerCase().equals("deceased")) {
	    rules.add(new ShapeRule(trait, value, new SymbolDeceased3d()));
	}
	if (symbol.toLowerCase().equals("adopted")) {
	    rules.add(new ShapeRule(trait, value, new SymbolAdopted3d()));
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
	HashMap<Double, Color> colors = new HashMap<Double, Color>();
	colors.put(min, Color.blue);
	colors.put(mean - scope, Color.green);
	colors.put(mean + scope, Color.green);
	colors.put(max, Color.red);
	rules.add(new GradientRule(trait, colors));
    }

}
