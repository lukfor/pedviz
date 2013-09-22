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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeListener;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;

/**
 * This algorithm splits the given Graph object in single families.
 * 
 * @author Lukas Forer
 * @version 0.1
 */

public class FamilySplitter implements Algorithm {
    private HashMap<Integer, Set<Integer>> famSet = new HashMap<Integer, Set<Integer>>();

    private Graph graph;

    private ArrayList<Graph> result;

    private ChangeListener changeListener;

    private int percent = 0;

    /**
     * Creates the FamilySplitter algorithm with the given graph.
     * 
     * @param graph
     *                Graph object
     */
    public FamilySplitter(Graph graph) {
	this.graph = graph;
	result = new ArrayList<Graph>();
    }

    public String getMessage() {
	return "Search Families...";
    }

    public void run() {
	splitInFamilies();
    }

    /**
     * Returns the single families as a collection of Graph objects.
     * 
     * @return single families
     */
    public ArrayList<Graph> getFamilies() {
	return result;
    }

    public int getPercentComplete() {
	return percent;
    }

    public void addChangeListener(ChangeListener l) {
	this.changeListener = l;
    }

    private void addFam(int oldRank, int newRank) {
	Set<Integer> oldSet = famSet.get(newRank);
	Set<Integer> newSet = famSet.get(oldRank);
	if (oldSet != newSet) {
	    for (Integer i : oldSet) {
		newSet.add(i.intValue());
		famSet.put(i.intValue(), newSet);
	    }
	    oldSet.clear();
	}
    }

    private void addFam(int newRank) {
	Set<Integer> set = new HashSet<Integer>();
	set.add(newRank);
	famSet.put(newRank, set);
    }

    private Collection<Graph> createFamList() {

	HashMap<Integer, Graph> families = new HashMap<Integer, Graph>();
	HashMap<Integer, Integer> idfam = new HashMap<Integer, Integer>();

	int c = 0;
	// Creates a HashTable and calcs for every famSet the famId.
	for (Set<Integer> set : famSet.values()) {
	    for (Integer i : set)
		idfam.put(i, c);
	    set.clear();
	    c++;
	}
	famSet.clear();

	for (Node node : graph.getNodes()) {
	    Integer id = idfam.get(node.getFamilieRank());
	    Graph family = families.get(id);
	    if (family == null) {
		family = new Graph();
		family.setMetaData(graph.getMetaData());
		family.setFamId(node.getFamId());
		families.put(id, family);
	    }
	    family.addNode(node);
	    for (Edge out : node.getInEdges())
		family.addEdge(out, false);
	}
	
	for (Graph family: families.values()){
		family.setName("Family_" + family.getFamId() + " (" + family.getSize() + ")");	    
	}
	
	return families.values();
    }

    private void splitInFamilies() {
	int familyRank = 0;
	int total = graph.getNodes().size();
	int current = 0;

	for (Node node : graph.getNodes()) {
	    fireChangeListener(Math.round((current * 100) / total));
	    current++;

	    if (node.getFamilieRank() < 0) {
		addFam(familyRank);
		node.setFamilieRank(familyRank);

		for (Edge edge : node.getOutEdges()) {
		    if (edge.getEnd().getFamilieRank() < 0)
			edge.getEnd().setFamilieRank(familyRank);
		    else {
			Integer oldRank = edge.getEnd().getFamilieRank();
			addFam(oldRank, familyRank);
		    }
		}
	    } else {
		Integer myRank = node.getFamilieRank();
		for (Edge edge : node.getOutEdges()) {
		    if (edge.getEnd().getFamilieRank() < 0)
			edge.getEnd().setFamilieRank(myRank);
		    else {
			Integer oldRank = edge.getEnd().getFamilieRank();
			if (oldRank != myRank)
			    addFam(myRank, oldRank);
		    }
		}
	    }
	    familyRank++;
	}

	result.clear();
	result.addAll(createFamList());
	Collections.sort(result, new Comparator<Graph>() {
	    public int compare(Graph arg0, Graph arg1) {
		return new Integer(arg1.getNodes().size()).compareTo(arg0
			.getNodes().size());
	    }
	});
    }

    private void fireChangeListener(int percent) {
	if (changeListener != null) {
	    this.percent = percent;
	    changeListener.stateChanged(null);
	}
    }

}
