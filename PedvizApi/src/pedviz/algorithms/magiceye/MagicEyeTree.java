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
package pedviz.algorithms.magiceye;

import java.util.HashMap;
import java.util.Vector;

import javax.vecmath.Color3f;

import pedviz.graph.Edge;
import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;

/**
 * This class converts a Graph object into a tree data structure. experimental!
 * 
 * @author lukas forer
 * 
 */
public class MagicEyeTree {
    LayoutedGraph graph; // Begriffshierarchie

    // HashMap nodes; // Knotenmenge, Zugriff auf Knoten über ihre IDs
    public HashMap<String, MagicEyeNodeView> nodes; // Knotenmenge, Zugriff

    // auf Knoten über ihre

    // Namen!

    Vector<MagicEyeNodeView> nodesToPrint; // Knoten, deren Bezeichnungen

    // ausgegeben werden

    // sollen

    Vector<MagicEyeNodeView> focusNodes; // Knoten, auf denen der Focus

    // liegt

    Vector<MagicEyeNodeView> nodesWithManyParents;

    public MagicEyeNodeView root; // Wurzelknoten

    int maxSN; // größte Strahlerzahl im Baum

    float edgeWidthFactor; // konst. Faktor für edgeWidth - Berechnung

    float edgeColorFactor; // konst. Faktor für edgeColor - Berechnung

    // Aufbau des Node Position Tree
    // onto - zugrundeliegende Begriffshierarchie
    // c - Concept aus der Ontologie, für dessen Teilbaum ein
    // MagicEyeNodeViewTree
    // gebildet werden soll
    public MagicEyeTree(LayoutedGraph graph, Node c) {
	this.graph = graph;
	nodes = new HashMap<String, MagicEyeNodeView>();
	nodesToPrint = new Vector<MagicEyeNodeView>();
	focusNodes = new Vector<MagicEyeNodeView>();
	nodesWithManyParents = new Vector<MagicEyeNodeView>();
	root = buildMagicEyeNodeViewTree(c, null, 0, 0);
	generateCrossedgeTargets(root);
	addOtherParents(root);
	generateStrahlerNumbers(root);
	maxSN = root.strahlerNumber;
	edgeWidthFactor = (MagicEyeLayout.MAX_EDGE_WIDTH - MagicEyeLayout.MIN_EDGE_WIDTH)
		/ (float) maxSN;
	edgeColorFactor = (MagicEyeLayout.MAX_EDGE_SATURATION - MagicEyeLayout.MIN_EDGE_SATURATION)
		/ (float) Math.pow((double) maxSN,
			MagicEyeLayout.SATURATION_EXP);
	calculateEdgeWidthsAndColors(root);
    }

    // rekursiver Aufbau des Baumes
    private MagicEyeNodeView buildMagicEyeNodeViewTree(Node c,
	    MagicEyeNodeView parent, int level, int num) {
	Node ci; // i-ter Sohnknoten von c
	MagicEyeNodeView ni; // i-ter Sohn des hier erzeugten Knotens
	MagicEyeNodeView ls; // linker Geschwisterknoten von ni
	int maxDepth; // max. Tiefe der Unterbäume
	// MagicEyeNodeView nur dann erzeugen, wenn zum aktuellen Concept noch
	// kein
	// MagicEyeNodeView existiert
	String id = c.getId().toString();
	if (!nodes.containsKey(id)) {
	    // Knoten erzeugen und zur HashMap hinzufügen
	    MagicEyeNodeView n = (MagicEyeNodeView) graph.getNodeView(c);
	    n.init(parent, level, num);
	    nodes.put(id, n);
	    // gehört dieser Knoten zu den Focus Nodes?
	    // wenn ja, dann in focusNodes einfügen
	    focusNodes.add(n);

	    // rekursiv weitermachen
	    int maxi = c.getOutDegree();
	    n.numChilds = maxi;
	    maxDepth = -1;
	    n.leaves = 0;
	    n.nodes = n.numChilds;
	    ls = null;

	    int i = 0;
	    for (Edge e : c.getOutEdges()) {
		ci = (Node) e.getEnd();
		ni = buildMagicEyeNodeViewTree(ci, n, level + 1, i);
		if (ni != null) {
		    ni.leftSibling = ls;
		    if (ls != null)
			ls.rightSibling = ni;
		    ls = ni;
		    n.childs.addElement(ni);
		    if (ni.numChilds > 0)
			n.leaves += ni.leaves;
		    else
			n.leaves++;
		    n.nodes += ni.nodes;
		    if (ni.depth > maxDepth)
			maxDepth = ni.depth;
		} else
		    n.numChilds -= 1;
		i++;
	    }
	    if (ls != null)
		ls.rightSibling = null;
	    n.depth = maxDepth + 1;
	    return n;
	} else
	    return null;
    }

    public MagicEyeNodeView getNode(String name) {
	return (MagicEyeNodeView) nodes.get(name);
    }

    public void removeAllFocusNodes() {
	clearFocusedFlags(root);
	clearAtPathToRootFlags(root);
	focusNodes.clear();
    }

    public void addFocusNode(String name) {
	MagicEyeNodeView n = getNode(name);
	if (n != null)
	    if (!focusNodes.contains(n))
		focusNodes.add(n);
    }

    // bestimmen der Relationen-Zielknoten für jeden Knoten
    void generateCrossedgeTargets(MagicEyeNodeView n) {
	/*
	 * Object[] attribs = n.c.attr.values().toArray(); for (int i=0; i<attribs.length;
	 * i++) { Attribute a = (Attribute)attribs[i]; if (a.isInst) {
	 * n.crossedgeTargets.add(getNode(a.target.getNum()));
	 * n.crossedgeStrings.add(a.id); } }
	 */
	String targetname = null;
	String attrname = null;
	for (Edge e : n.getNode().getOutEdges()) {
	    attrname = e.getEnd().getId().toString();
	    // targetname = onto.getAttrRangeName(attrname);
	    // if (!OntoNode.TypeisAtom(targetname)) {
	    n.crossedgeTargets.add(getNode(attrname));
	    n.crossedgeStrings.add(attrname);
	    // }
	}

	// Relationen-Zielknoten für die neuen Relationen
	MagicEyeNodeView p = n.parent;
	if (p != null) {
	    for (int i = 0; i < n.crossedgeTargets.size(); i++) {
		MagicEyeNodeView target = n.crossedgeTargets.get(i);
		if (!p.crossedgeTargets.contains(target)) {
		    n.crossedgeTargetsNew.add(target);
		    n.crossedgeStringsNew.add(n.crossedgeStrings.get(i));
		}
	    }
	}

	// rekursiv weitermachen
	for (int i = 0; i < n.numChilds; i++)
	    generateCrossedgeTargets(n.getChild(i));
    }

    // zu jedem Knoten im MagicEyeNodeViewTree alle weiteren Väter, die
    // gegebf.
    // durch die zugelassene Mehrfachvererbung in der Ontologie existieren,
    // im Vektor otherParents merken
    void addOtherParents(MagicEyeNodeView n) {
	/*
	 * Concept c = onto.getConceptByNum(n.id.intValue()); for (int i=0; i<c.numParents;
	 * i++) { Concept pi = c.getParent(i); MagicEyeNodeView ni =
	 * getNode(pi.getNum()); if (ni != n.parent) n.otherParents.add(ni); }
	 */
	for (Edge e : n.getNode().getInEdges()) {
	    MagicEyeNodeView ni = getNode(e.getStart().getId().toString());
	    if (ni != n.parent)
		n.otherParents.add(ni);
	}
	if (n.otherParents.size() > 0)
	    nodesWithManyParents.add(n);
	// rekursiv weitermachen
	for (int i = 0; i < n.numChilds; i++)
	    addOtherParents(n.getChild(i));
    }

    // berechnen der Strahlerzahl zu jedem Knoten
    void generateStrahlerNumbers(MagicEyeNodeView n) {
	int maxSN = 0;
	int sn;
	MagicEyeNodeView ni = null;
	boolean equal = true;

	if (n.numChilds == 0) {
	    n.strahlerNumber = 0;
	    return;
	} else {
	    for (int i = 0; i < n.numChilds; i++) {
		ni = n.getChild(i);
		generateStrahlerNumbers(ni);
		sn = ni.strahlerNumber;
		if (sn != maxSN && i > 0)
		    equal = false;
		if (sn > maxSN)
		    maxSN = sn;
	    }

	    if (equal)
		n.strahlerNumber = ni.strahlerNumber + n.numChilds - 1;
	    else
		n.strahlerNumber = maxSN + n.numChilds - 2;
	}
    }

    // berechnen der Kantenbreiten und Farben entsprechend den
    // Strahlerzahlen
    void calculateEdgeWidthsAndColors(MagicEyeNodeView n) {
	float sat;
	int c;

	// Kantenbreite
	n.edgeWidth = (float) n.strahlerNumber * edgeWidthFactor
		+ MagicEyeLayout.MIN_EDGE_WIDTH;

	// Kantenfarbe
	n.edgeColor = new Color3f();
	sat = (float) java.lang.Math.pow((double) n.strahlerNumber,
		MagicEyeLayout.SATURATION_EXP)
		* edgeColorFactor + MagicEyeLayout.MIN_EDGE_SATURATION;
	c = java.awt.Color.HSBtoRGB(MagicEyeLayout.EDGE_H, sat,
		MagicEyeLayout.EDGE_B);
	n.edgeColor.set((float) ((c & 0x00ff0000) >> 16) / 255.0f,
		(float) ((c & 0x0000ff00) >> 8) / 255.0f,
		(float) (c & 0x000000ff) / 255.0f);

	// Farbe für markierte Kante
	n.markedEdgeColor = new Color3f();
	c = java.awt.Color.HSBtoRGB(MagicEyeLayout.MARKED_EDGE_H, sat,
		MagicEyeLayout.MARKED_EDGE_B);
	n.markedEdgeColor.set((float) ((c & 0x00ff0000) >> 16) / 255.0f,
		(float) ((c & 0x0000ff00) >> 8) / 255.0f,
		(float) (c & 0x000000ff) / 255.0f);

	for (int i = 0; i < n.numChilds; i++)
	    calculateEdgeWidthsAndColors(n.getChild(i));
    }

    /*
     * // Es werden alle Knoten ausgeblendet - bis auf folgende: // Focusknoten
     * und deren Teilbäume, Root und alle Knoten im ersten Level public void
     * buildFocusSet() { // vorab automatisches Falten für den gesamten Baum
     * automaticFold(); // Teilbäume der Knoten im ersten Level ausblenden for
     * (int i=0; i<root.numChilds; i++) root.getChild(i).fold(true); // für
     * jeden Focusknoten: Weg zum Rootknoten zeigen und Teilbaum aufklappen for
     * (int i=0; i<focusNodes.length; i++) { MagicEyeNodeView n =
     * focusNodes.getn(i); n.pathToRoot(); //n.fold(false); } }
     */

    public void clearFocusedFlags(MagicEyeNodeView n) {
	if (n.focused) {
	    n.focused = false;
	    for (int i = 0; i < n.numChilds; i++)
		clearFocusedFlags(n.getChild(i));
	}
    }

    public void clearAtPathToRootFlags(MagicEyeNodeView n) {
	for (int i = 0; i < n.numChilds; i++) {
	    MagicEyeNodeView ni = n.getChild(i);
	    if (ni.atPathToRoot) {
		ni.atPathToRoot = false;
		clearAtPathToRootFlags(ni);
	    }
	}
    }

    // Knoten, die im Focus liegen markieren
    // (für alle Focus-Knoten n, werden alle Sohnknoten von n und alle
    // Knoten,
    // die auf dem Weg von n zur
    // Wurzel liegen, inklusive ihrer Sohnknoten, als zur Focus-Menge
    // gehörend
    // markiert.)
    public void buildFocusSet() {
	MagicEyeNodeView n, p, pj;

	// vorab automatisches Falten durchführen
	automaticFold();

	// Für alle Focus-Knoten n, werden alle Knoten, die auf dem Weg von n
	// zur
	// Wurzel liegen, als zur Focus-Menge gehörend, markiert.
	for (int i = 0; i < focusNodes.size(); i++) {
	    n = focusNodes.get(i);
	    n.focused = true;
	    p = n.parent;
	    while (p != null) {
		p.focused = true;
		p = p.parent;
	    }
	    // ein Sohnknoten von n (falls vorhanden) wird auch markiert,
	    // so dass alle Sohnknoten in die Focus-Menge aufgenommen werden
	    if (n.numChilds > 0)
		n.getChild(0).focused = true;
	}
	// alle Knoten ohne Focus ausblenden
	foldNoFocusNodes(root);

	for (int i = 0; i < focusNodes.size(); i++)
	    focusNodes.get(i).pathToRoot();
    }

    void foldNoFocusNodes(MagicEyeNodeView n) {
	MagicEyeNodeView ni;
	boolean foldn = true;

	for (int i = 0; i < n.numChilds; i++) {
	    ni = n.getChild(i);
	    if (ni.focused) {
		foldn = false;
		ni.fold(false);
		foldNoFocusNodes(ni);
	    } else if (ni.numChilds > 0)
		ni.fold(true);
	}
	if (n.numChilds > 0 && foldn)
	    n.fold(true);
    }

    // Implementierung des Filter-Fish-Eye-View (FFEV) zur
    // Knotenbeschriftung
    // Anders als im Paper, ist hier doi(*,x) := d(*,x) + d(r,x), also
    // positiv.
    // Dabei steht 0 für den grössten doi und FFEV_TRESHHOLD für den
    // kleinsten
    // zulässigen doi.
    public void FFEV(MagicEyeNodeView n) {
	nodesToPrint.clear();
	// nodesToPrint.add(n);
	downFFEV(n, n.level);
	upFFEV(n, n.level);
    }

    void downFFEV(MagicEyeNodeView n, int doi) {
	if (MagicEyeLayout.FFEV_THRESHOLD > doi) {
	    nodesToPrint.add(n);
	    if (n.folded == false)
		for (int i = 0; i < n.numChilds; i++)
		    downFFEV(n.getChild(i), doi + 2);
	}
    }

    void upFFEV(MagicEyeNodeView n, int doi) {
	if (doi < MagicEyeLayout.FFEV_THRESHOLD) {
	    nodesToPrint.add(n);
	    if (n.parent != null) {
		MagicEyeNodeView p = n.parent;
		// nodesToPrint.add(p);
		if (doi + 2 < MagicEyeLayout.FFEV_THRESHOLD) {
		    for (int i = 0; i < p.numChilds; i++) {
			MagicEyeNodeView ni = p.getChild(i);
			if (ni != n)
			    downFFEV(ni, doi + 2);
		    }
		}
		upFFEV(p, doi);
	    }
	}
    }

    // automatisches Falten komplexer Teilbäume
    // (siehe Paper von Herman: "Tree Visualization and Navigation Clues")
    public void automaticFold() {
	boxANode(root);
    }

    FoldData boxANode(MagicEyeNodeView n) {
	FoldData retval = new FoldData();
	if (n.numChilds == 0) {
	    // es handelt sich um ein Blatt
	    retval.n = 1;
	    retval.k = 1;
	} else {
	    // kein Blatt, also rekursiver Aufruf für alle Sohnknoten
	    retval.n = 1;
	    retval.k = 0;
	    for (int i = 0; i < n.numChilds; i++) {
		FoldData childData = boxANode(n.getChild(i));
		retval.n += childData.n;
		retval.k += childData.k;
	    }
	    // jetzt wird entschieden, ob gefaltet wird
	    if (n.parent != null && retval.n > MagicEyeLayout.FOLDING_THRESHOLD
		    && isNormal(retval) == false) {
		n.fold(true);
		// gefalteten Knoten als Blatt behandeln
		retval.n = 1;
		retval.k = 1;
	    }
	}
	return retval;
    }

    boolean isNormal(FoldData fd) {
	double n = fd.n;
	double k = fd.k;
	double a = 1.96 * java.lang.Math.sqrt(n / 8);
	double alpha = java.lang.Math.floor(n / 2 - a);
	double beta = java.lang.Math.ceil(n / 2 + a);

	return (alpha <= k && k <= beta);
    }

    private static class FoldData {
	int n; // Anzahl der Knoten

	int k; // Anzahl der Blätter
    }

}