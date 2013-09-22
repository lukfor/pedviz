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

import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import pedviz.graph.Node;
import pedviz.view.DefaultNodeView;
import pedviz.view.NodeView;

/**
 * This class extends the NodeView class by some methods used by the magiceye
 * algorithm. experimental!
 * 
 * @author Luki
 * 
 */
public class MagicEyeNodeView extends NodeView {
    public MagicEyeNodeView parent; // Ref auf übergeordneten Knoten

    public MagicEyeNodeView leftSibling, rightSibling; // Ref auf

    // linken/rechten
    // Geschwisterknoten

    public Vector<MagicEyeNodeView> childs; // Feld von Sohnknoten (Refs auf

    // NodePos)

    // Relationenkanten
    public Vector<MagicEyeNodeView> crossedgeTargets; // Zielknoten der

    // Relationen

    public Vector<String> crossedgeStrings; // Bezeichnungen der Relationen

    public Vector<MagicEyeNodeView> crossedgeTargetsNew; // Zielknoten

    // der neuen
    // Relationen

    public Vector<String> crossedgeStringsNew; // Bezeichnungen der neuen

    // Relationen

    public Vector<MagicEyeNodeView> otherParents; // weitere Vaterknoten,

    // die durch
    // Mehrfachvererbung in
    // der Ontologie
    // auftreten können

    public int numChilds; // Anzahl der Unterbäume/Sohnknoten

    public int leaves; // Anzahl der Blätter in diesem Teilbaum

    public int nodes; // Anzahl der Knoten in diesem Teilbaum (Wurzel nicht

    // mitgerechnet)

    public int depth; // Tiefe des (Unter-)Baumes

    public int level; // Hierarchiestufe des Knotens im gesamten Baum

    public int num; // Nummer dieses Knotens in der Knotenliste des Vaters

    public int strahlerNumber; // Strahlerzahl (siehe Paper von Herman: "Tree

    // Visualization and Navigation Clues")

    public float edgeWidth; // Dicke der Kante zum Vaterknoten

    public Color3f edgeColor; // Farbe der Kante zum Vaterknoten

    public Color3f markedEdgeColor; // Farbe der markierten Kante zum

    // Vaterknoten

    // Flags
    public boolean visible; // Sichtbarkeit des Knotens

    public boolean folded; // Ein/Ausblenden des Unterbaumes

    public boolean marked; // true, wenn der Knoten markiert wurde

    public boolean focused; // true, wenn der Knoten zur Focusmenge gehört

    // boolean besidePath; // true, wenn der Knoten an einem PathToRoot
    // liegt // NOT USED
    public boolean atPathToRoot; // true, wenn der Knoten auf einem

    // PathToRoot

    // liegt

    public boolean partiallyFolded;

    // Zwischenergebnisse für Knotenposition
    public double x, xoffs; // x-Position des Wurzelknotens und Verschiebung

    public double y, z; // y, z -Positionen des Wurzelknotens

    // FlatMap Position
    public double lambda, phi;

    // für Walkers Algorithmus benötigte Variablen
    public MagicEyeNodeView leftNeighbor;

    public double modifier;

    public double prelim;

    // Punkte auf der Kante zum Vaterknoten
    public Point3d[] pEdgePoints; // auf der Halbkugel mit Kugelmitte als

    // Projektionszentrum

    public Point3d[] qEdgePoints; // nach Verschiebung des

    // Projektionszentrums

    public MagicEyeNodeView(Node node, DefaultNodeView defaultNodeView) {
	super(node, defaultNodeView);
    }

    public void init(MagicEyeNodeView parent, int level, int num) {
	this.parent = parent;
	this.level = level;
	this.num = num;
	visible = true;
	folded = false;
	partiallyFolded = false;
	marked = false;
	focused = false;
	atPathToRoot = false;
	childs = new Vector<MagicEyeNodeView>();
	x = y = z = xoffs = 0.0f;
	pEdgePoints = new Point3d[MagicEyeLayout.INNER_EDGE_POINTS];
	qEdgePoints = new Point3d[MagicEyeLayout.INNER_EDGE_POINTS];
	for (int i = 0; i < MagicEyeLayout.INNER_EDGE_POINTS; i++) {
	    pEdgePoints[i] = new Point3d();
	    qEdgePoints[i] = new Point3d();
	}
	crossedgeTargets = new Vector<MagicEyeNodeView>();
	crossedgeStrings = new Vector<String>();
	crossedgeTargetsNew = new Vector<MagicEyeNodeView>();
	crossedgeStringsNew = new Vector<String>();
	otherParents = new Vector<MagicEyeNodeView>();
    }

    public MagicEyeNodeView getChild(int i) {
	return (MagicEyeNodeView) childs.get(i);
    }

    public boolean hasChild() {
	return (numChilds > 0);
    }

    public boolean isLeaf() {
	return (numChilds == 0 || folded);
    }

    public boolean hasLeftSibling() {
	return (leftSibling() != null);
    }

    // liefert den nächsten links stehenden und sichtbaren Geschwisterknoten
    public MagicEyeNodeView leftSibling() {
	MagicEyeNodeView nl = leftSibling;
	while (nl != null) {
	    if (nl.visible)
		return nl;
	    nl = nl.leftSibling;
	}
	return null;
    }

    public boolean hasRightSibling() {
	return (rightSibling() != null);
    }

    // liefert den nächsten rechts stehenden und sichtbaren
    // Geschwisterknoten
    public MagicEyeNodeView rightSibling() {
	MagicEyeNodeView nr = rightSibling;
	while (nr != null) {
	    if (nr.visible)
		return nr;
	    nr = nr.rightSibling;
	}
	return null;
    }

    public MagicEyeNodeView firstChild() {
	MagicEyeNodeView fc = (MagicEyeNodeView) childs.get(0);
	if (fc.visible)
	    return fc;
	else
	    return fc.rightSibling();
    }

    public MagicEyeNodeView lastChild() {
	MagicEyeNodeView lc = (MagicEyeNodeView) childs.get(numChilds - 1);
	if (lc.visible)
	    return lc;
	else
	    return lc.leftSibling();
    }

    public void pathToRoot() {
	MagicEyeNodeView ni = null;
	MagicEyeNodeView n = this;
	MagicEyeNodeView p = n.parent;
	n.atPathToRoot = true;
	while (p != null) {
	    if (p.parent != null) {
		for (int i = 0; i < p.numChilds; i++) {
		    ni = p.getChild(i);
		    if (!ni.atPathToRoot)
			ni.visible = false;
		}
		n.visible = true;
		p.visible = true;
		p.atPathToRoot = true;
	    }
	    n = p;
	    p = p.parent;
	}
    }

    // liefert true, wenn 0 < Anzahl der sichtbaren Kinder < numChilds
    public boolean isPartiallyFolded() {
	int visibleChilds = 0;
	for (int i = 0; i < numChilds; i++)
	    if (getChild(i).visible)
		visibleChilds++;
	if (0 < visibleChilds && visibleChilds < numChilds)
	    return true;
	return false;
    }

    /*
     * // alle Knoten, die nicht auf dem Weg von diesem Knoten zur Wurzel
     * liegen, // werden nicht angezeigt public void pathToRoot() { NodePos ni =
     * null; NodePos n = this; NodePos p = n.parent; while (p!=null) { if
     * (p.parent!=null) { // ok, p ist nicht der Root-Knoten for (int i=0; i<p.numChilds;
     * i++) { ni = p.getChild(i); if (ni != n) { ni.visible = false;
     * ni.setVisible(false); } } n.visible = true; p.atPathToRoot = true; } n =
     * p; p = p.parent; } }
     */

    // Unterbaum ein/ausblenden
    public void fold(boolean f) {
	folded = f;
	// setExpand(!f);
    }

    public void unfoldOneLevel() {
	folded = false;
	for (int i = 0; i < numChilds; i++) {
	    MagicEyeNodeView ni = getChild(i);
	    ni.atPathToRoot = false;
	    ni.visible = true;
	}
    }

    public void unfoldComplete() {
	folded = false;
	for (int i = 0; i < numChilds; i++) {
	    MagicEyeNodeView ni = getChild(i);
	    ni.atPathToRoot = false;
	    ni.visible = true;
	    ni.unfoldComplete();
	}
    }

    // Sichtbarkeit für Knoten des Unterbaumes festlegen
    /*
     * public void setExpand(boolean v) { MagicEyeNodeView ni; atPathToRoot =
     * false; for (int i = 0; i < numChilds; i++) { ni = getChild(i); ni.visible =
     * v; if (!ni.folded) ni.setExpand(v); } }
     */

    // Knoten, seinen Unterbaum und Pfad zur Wurzel (un)markieren
    public void mark(boolean f) {
	markSubtree(f);
	markRootPath(f);
    }

    // Setzen des Markierungsflags des Knotens und seines gesamten
    // Unterbaumes
    public void markSubtree(boolean f) {
	marked = f;
	for (int i = 0; i < numChilds; i++)
	    getChild(i).markSubtree(f);
    }

    // Setzen der Markierungsflags aller Knoten auf dem Pfad zur Wurzel
    public void markRootPath(boolean f) {
	MagicEyeNodeView n = this;
	while (n.parent != null) {
	    n = n.parent;
	    n.marked = f;
	}
    }
}
