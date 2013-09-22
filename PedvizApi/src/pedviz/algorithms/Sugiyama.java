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

package pedviz.algorithms;

import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pedviz.algorithms.sugiyama.Splitter;
import pedviz.algorithms.sugiyama.SugiyamaLayout;
import pedviz.graph.Graph;
import pedviz.graph.LayoutedGraph;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;

/**
 * This class combines all steps that are necessary to create a layout for a
 * Graph object. It implements the Algorithm interface, so it's possible to
 * visualize the progress with the DialogProgress class.
 * 
 * @author lukas forer
 * 
 */

public class Sugiyama implements Algorithm, ChangeListener {

    private LayoutedGraph layoutedGraph;

    private RubberBands rubberBands;

    private SameParents sameParents;

    private SugiyamaLayout layout;

    private Vector<Algorithm> algorithms;

    String message = "";

    int i = 0;

    int percent = 0;

    ChangeListener changeListener;

    /**
     * Creates a new Sugyiama object for the given Graph object with default
     * settings.
     * 
     * @param graph
     *                Graph object, which will be layouted.
     */
    public Sugiyama(Graph graph) {
	this(graph, new DefaultNodeView(), new DefaultEdgeView());
    }

    /**
     * Creates a new Sugyiama object for the given Graph object with default
     * settings.
     * 
     * @param graph
     *                Graph object
     * @param splitter
     *                Splitter object
     */
    public Sugiyama(Graph graph, Splitter splitter) {
	this(graph, new DefaultNodeView(), new DefaultEdgeView(), splitter);
    }

    /**
     * Creates a new Sugyiama object for the given Graph object with given
     * settings
     * 
     * @param graph
     *                Graph object, which will be layouted.
     * @param defaultNodeView
     *                default node settings.
     * @param defaultEdgeView
     *                default edge settings.
     */
    public Sugiyama(Graph graph, DefaultNodeView defaultNodeView,
	    DefaultEdgeView defaultEdgeView) {
	this(graph, defaultNodeView, defaultEdgeView, null);
    }

    /**
     * 
     * Creates a new Sugyiama object for the given Graph object with given
     * settings and the given Splitter object.
     * 
     * @param graph
     *                Graph object, which will be layouted.
     * @param defaultNodeView
     *                default node settings.
     * @param defaultEdgeView
     *                default edge settings.
     * @param splitter
     *                Splitter object, which will be used to divide nodes into
     *                walls.
     */
    public Sugiyama(Graph graph, DefaultNodeView defaultNodeView,
	    DefaultEdgeView defaultEdgeView, Splitter splitter) {

	algorithms = new Vector<Algorithm>();

	sameParents = new SameParents(graph);
	sameParents.addChangeListener(this);

	message = "SameParents";
	sameParents.run();

	graph.buildHierarchie(new HierarchieUpDown());

	layout = new SugiyamaLayout(graph, defaultNodeView, defaultEdgeView);
	layout.addChangeListener(this);
	if (splitter != null)
	    layout.setSplitter(splitter);
	algorithms.add(layout);

	layoutedGraph = layout.getLayoutGraph();
	rubberBands = new RubberBands(layoutedGraph);
	rubberBands.addChangeListener(this);
	algorithms.add(rubberBands);
    }

    /**
     * Returns the layouted graph.
     * 
     * @return returns the layouted graph.
     */
    public LayoutedGraph getLayoutedGraph() {
	return layoutedGraph;
    }

    public String getMessage() {
	return message;
    }

    public void run() {
	i = 0;
	message = layout.getMessage();
	layout.run();
	i = 1;
	message = rubberBands.getMessage();
	rubberBands.run();
	System.out.println("tet");
    }

    public void stateChanged(ChangeEvent e) {
	try {
	    fireChangeListener(Math.round((algorithms.get(i)
		    .getPercentComplete())));
	} catch (Exception ex) {
	}
    }

    private void fireChangeListener(int percent) {
	if (changeListener != null) {
	    this.percent = percent;
	    changeListener.stateChanged(null);
	}
    }

    public void addChangeListener(ChangeListener l) {
	this.changeListener = l;
    }

    public int getPercentComplete() {
	return percent;
    }

    /**
     * Returns the SugiyamaLayout object.
     * 
     * @return the SugiyamaLayout object.
     */
    public SugiyamaLayout getLayout() {
	return layout;
    }

    /**
     * Returns the RubberBands object.
     * 
     * @return the RubberBands object.
     */
    public RubberBands getRubberBands() {
	return rubberBands;
    }
}
