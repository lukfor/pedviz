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

import java.util.Collections;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import pedviz.graph.*;
import pedviz.graph.Node;

/**
 * The integrated method identifies the following basic errors: INVALID_SEX_DAD,
 * INVALID_SEX_MOM, MISSING_DAD, MISSING_MOM, MULTI_PEDIGREES, SINGLETON.
 * 
 * @author Luki
 * 
 */
public class ErrorChecking implements Algorithm {

    private Vector<Graph> graphs;

    private Vector<GraphError> errors;

    /**
     * Inits ErrorChecking.
     * 
     * @param graph
     *                graph
     */
    public ErrorChecking(Graph graph) {
	this.graphs = new Vector<Graph>();
	graphs.add(graph);
	errors = new Vector<GraphError>();
    }

    /**
     * Inits ErrorChecking for a collection of graphs.
     * 
     * @param graphs
     *                collection of graphs.
     */
    public ErrorChecking(Vector<Graph> graphs) {
	this.graphs = graphs;
	errors = new Vector<GraphError>();
    }

    public void addChangeListener(ChangeListener l) {

    }

    public String getMessage() {
	return "Error Checking";
    }

    public int getPercentComplete() {
	return 0;
    }

    /**
     * Starts the error-checking.
     */
    public void run() {
	errors.clear();
	for (Graph graph : graphs) {
	    GraphMetaData metaData = graph.getMetaData();
	    Object male = metaData.getMale();
	    Object female = metaData.getFemale();
	    for (Node node : graph.getNodes()) {
		if (!node.isDummy()
			|| (node.isDummy() && node.getNodes().size() > 0)) {
		    // Singleton
		    if (node.getInDegree() == 0 && node.getOutDegree() == 0) {
			errors.add(new GraphError(GraphError.SINGLETON, node));
		    }

		    // Check for missing parents
		    if (node.getInDegree() == 1) {
			if (graph.getNode(node.getIdDad()) == null) {
			    errors.add(new GraphError(GraphError.MISSING_DAD,
				    node));
			}

			if (graph.getNode(node.getIdMom()) == null) {
			    errors.add(new GraphError(GraphError.MISSING_MOM,
				    node));
			}
		    }

		    // Check sex mom
		    Node mom = graph.getNode(node.getIdMom());
		    if (mom != null) {
			if (!mom.getUserData(metaData.get(GraphMetaData.SEX))
				.equals(female)) {
			    errors.add(new GraphError(
				    GraphError.INVALID_SEX_MOM, node));
			}
		    }

		    // Check sex dad
		    Node dad = graph.getNode(node.getIdDad());
		    if (dad != null) {
			if (!dad.getUserData(metaData.get(GraphMetaData.SEX))
				.equals(male)) {
			    errors.add(new GraphError(
				    GraphError.INVALID_SEX_DAD, node));
			}
		    }
		}
	    }
	}
    }

    /**
     * Returns a list with all errors.
     * 
     * @return a list with all errors.
     */
    public Vector<GraphError> getErrors() {
	Collections.sort(errors);
	return errors;
    }

}
