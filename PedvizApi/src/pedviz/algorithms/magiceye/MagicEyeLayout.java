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

import javax.vecmath.Point3d;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Layout;
import pedviz.graph.Node;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;

/**
 * This class implements the MagicEye layout-algorithm. experimental!
 * 
 * @author lukas forer
 * 
 */
public class MagicEyeLayout extends Layout<MagicEyeNodeView, MagicEyeEdgeView> {

    private int maxDepth;

    private final double siblingSeparation = 0.8;

    private final double subtreeSeparation = 2.0;

    private double xTopAdjustment;

    private MagicEyeNodeView Root;

    private MagicEyeNodeView[] prevNodeList;

    private MagicEyeNodeView[] prevNodeListSav;

    private final double _PI = java.lang.Math.PI;

    private double minx, maxx;

    private double scalex, scaley, start, end, size;

    private boolean hemisphere = true;

    private boolean CURVED_EDGES = false;

    private float radius = 100f;

    // eine Kante zwischen zwei Knoten wird durch LINES_PER_EDGE Segmente
    // approximiert
    public static final int LINES_PER_EDGE = 10;

    // Reziproke von LINES_PER_EDGE
    private double REZ_LINES_PER_EDGE = 1.0 / (double) LINES_PER_EDGE;

    // Anzahl der Zwischenpunkte einer Kante
    public static final int INNER_EDGE_POINTS = LINES_PER_EDGE - 1;

    // minimale und maximale Kantendicke
    public static final float MIN_EDGE_WIDTH = 1.3f;

    public static final float MAX_EDGE_WIDTH = 6.0f;

    // Wert, der bei markierten Kanten zur Breite hinzuaddiert wird
    public static final float MARKED_EDGE_WIDTH_OFFSET = 1.0f;

    // exponentielle Abbildung der Strahlerzahl auf Sättigung, sowie
    // minimale und maximale Sättigung der Kantenfarbe, Hue und Brightness
    public static final double SATURATION_EXP = 0.167;

    public static final float MIN_EDGE_SATURATION = 0.1f;

    public static final float MAX_EDGE_SATURATION = 1.0f;

    public static final float EDGE_H = 0.7f;

    public static final float EDGE_B = 0.75f;

    public static final float MARKED_EDGE_H = 0.35f;

    public static final float MARKED_EDGE_B = 0.6f;

    // Anzahl der Knoten in einem Teilbaum, ab der das automatische Falten
    // greift
    // int FOLDING_TRESHHOLD = 20;

    // Anzahl der Längen- und Breitengrade einer Kugel
    public static final int SPHERE_DETAIL = 20;

    // Schwellwert für den FFEV
    // int FFEV_TRESHHOLD = 5;

    // Antialiasing für Linien
    public static final boolean USE_SMOOTH_LINES = false;

    // Anzeige der Focus-Knotenmenge beim Start
    public static final boolean STARTUP_WITH_FOCUS_NODES = true;

    public static final boolean FOCUS_ANIMATION = true;

    // Schwellwert für autom. Falten
    static int FOLDING_THRESHOLD = 20;

    // Schwellwert für FFEV
    static int FFEV_THRESHOLD = 5;

    // implementierte Layoutalgorithmen
    // original Walker
    private final int LAYOUT_WALKER = 0;

    // Walker modifiziert: Knoten im 1. Level fest
    private final int LAYOUT_WALKER_MOD1 = 1; // Strahlerzahlen als Gewichte

    private final int LAYOUT_WALKER_MOD2 = 2; // Anz. Kinder als Gewichte

    private final int LAYOUT_WALKER_MOD3 = 3; // Anz. Blätter als Gewichte

    private final int LAYOUT_WALKER_MOD4 = 4; // Anz. Knoten als Gewichte

    private final int LAYOUT_WALKER_MOD5 = 5; // keine Wichtung,

    // Gleichaufteilung

    int layouter = LAYOUT_WALKER_MOD1;

    public MagicEyeLayout(Graph graph, DefaultNodeView defaultNodeView,
	    DefaultEdgeView defaulEdgeView) {
	super(graph, defaultNodeView, defaulEdgeView);
    }

    public void run() {
	Node root = graph.getHierachy(0).getNodes(0).get(0);

	MagicEyeTree npTree = new MagicEyeTree(getLayoutGraph(), root);
	layout(layouter, npTree.root);
	layout(layouter, npTree.root);

	for (Object o : npTree.nodes.values()) {
	    MagicEyeNodeView p = (MagicEyeNodeView) o;

	    if (hemisphere) {
		p.setPosX(p.getPosX() * radius);
		float temp = p.getPosY();
		p.setPosY(p.getPosZ() * radius - (radius / 2f));
		p.setPosZ(temp * radius);
	    } else {
		p.setPosX(p.getPosX() * radius * 6);
		p.setPosY(p.getPosY() * radius * 6 - 50);
		p.setPosZ(p.getPosZ() * radius * 6);
	    }
	}

    }

    public MagicEyeNodeView createNodeView(Node node,
	    DefaultNodeView defaultNodeView) {
	return new MagicEyeNodeView(node, defaultNodeView);
    }

    public MagicEyeEdgeView createEdgeView(Edge edge,
	    DefaultEdgeView defaultEdgeView) {
	return new MagicEyeEdgeView(edge, defaultEdgeView);
    }

    public void layout(int layouter, MagicEyeNodeView root) {
	if (root == null)
	    return;
	// Inits
	Root = root;
	init();
	// Walker für den gesamten Baum
	firstWalk(Root, 0);
	xTopAdjustment = Root.x - Root.prelim;
	secondWalk(Root, 0, 0.0);

	//
	// LAYOUT_WALKER_MOD1 bis MOD5
	//
	if (layouter != LAYOUT_WALKER) {
	    // Knoten im 1. Level fest positionieren und jedem Teilbaum
	    // ein Tortenstück bestimmter Grösse zuordnen
	    project2Hemisphere(_PI, 0.0, Root);
	    double weightSum = 0.0;
	    double weight[] = new double[Root.numChilds];
	    switch (layouter) {
	    // Strahlerzahlen als Gewichte
	    case LAYOUT_WALKER_MOD1:
		for (int i = 0; i < Root.numChilds; i++) {
		    weight[i] = (double) Root.getChild(i).strahlerNumber;
		    weightSum += weight[i];
		}
		break;
	    // Anzahl der Sohnknoten als Gewichte
	    case LAYOUT_WALKER_MOD2:
		for (int i = 0; i < Root.numChilds; i++) {
		    weight[i] = (double) Root.getChild(i).numChilds;
		    weightSum += weight[i];
		}
		break;
	    // Anzahl der Blätter als Gewichte
	    case LAYOUT_WALKER_MOD3:
		for (int i = 0; i < Root.numChilds; i++) {
		    weight[i] = (double) Root.getChild(i).leaves;
		    weightSum += weight[i];
		}
		break;
	    // Anzahl der Knoten als Gewichte
	    case LAYOUT_WALKER_MOD4:
		for (int i = 0; i < Root.numChilds; i++) {
		    weight[i] = (double) Root.getChild(i).nodes;
		    weightSum += weight[i];
		}
		break;
	    // Gleichaufteilung, keine Wichtung
	    case LAYOUT_WALKER_MOD5:
	    default:
		for (int i = 0; i < Root.numChilds; i++)
		    weight[i] = 1.0;
		weightSum = (double) Root.numChilds;
		break;
	    }

	    if (layouter != LAYOUT_WALKER_MOD5) {
		// Anpassung der Gewichte, so dass minSize nicht unterschritten
		// wird
		// (näheres siehe Aufzeichnungen)
		double minSize = weightSum / 18; // entspricht 20 Grad
		boolean correction[] = new boolean[Root.numChilds];
		double correctionSum = 0.0;
		double restWeightSum = weightSum;
		for (int i = 0; i < Root.numChilds; i++) {
		    if (weight[i] > minSize)
			correction[i] = true;
		    else {
			correction[i] = false;
			correctionSum += minSize - weight[i];
			restWeightSum -= weight[i];
			weight[i] = minSize;
		    }
		}
		if (restWeightSum > 0.0) {
		    double div = correctionSum / restWeightSum;
		    for (int i = 0; i < Root.numChilds; i++) {
			if (correction[i])
			    weight[i] -= weight[i] * div;
		    }
		}
	    }

	    // Winkelbereiche den einzelnen Teilbäumen zuweisen
	    start = 0.0;
	    scaley = (_PI / 2.0) / (double) Root.depth;
	    for (int i = 0; i < Root.numChilds; i++) {
		MagicEyeNodeView n = Root.getChild(i);
		size = 2.0 * _PI * weight[i] / weightSum;
		end = start + size;
		minx = getMinX(n);
		maxx = getMaxX(n);
		adjustMaxX();
		scalex = (end - start) / (maxx - minx);
		// project2Hemisphere(0.5*(start+end), scaley, n);
		project2Hemisphere((n.x - minx) * scalex + start, n.level
			* scaley, n);
		edgeToRoot(n);
		subtree2Flatmap(n);
		start = end;
	    }
	}
	//
	// LAYOUT_WALKER
	//
	else {
	    // Layout ohne Änderungen übernehmen
	    adjustMaxX();
	    start = 0.0;
	    end = 2.0 * _PI;
	    scalex = (end - start) / (maxx - minx);
	    scaley = (_PI / 2.0) / (double) Root.depth;
	    makeFlatMap(Root);
	}
    }

    private void init() {
	initPrevNodeList();
	maxDepth = Integer.MAX_VALUE;
	minx = Double.MAX_VALUE;
	maxx = Double.MIN_VALUE;
    }

    private void firstWalk(MagicEyeNodeView n, int level) {
	n.leftNeighbor = getPrevNodeAtLevel(level);
	setPrevNodeAtLevel(level, n);
	n.modifier = 0.0;

	if (n.isLeaf() || n.firstChild() == null || level == maxDepth) {
	    if (n.hasLeftSibling())
		n.prelim = n.leftSibling().prelim + siblingSeparation
			+ meanNodeSize(n.leftSibling(), n);
	    else
		n.prelim = 0.0;
	} else {
	    MagicEyeNodeView leftMost;
	    MagicEyeNodeView rightMost;

	    leftMost = rightMost = n.firstChild();
	    firstWalk(leftMost, level + 1);
	    while (rightMost.hasRightSibling()) {
		rightMost = rightMost.rightSibling();
		firstWalk(rightMost, level + 1);
	    }
	    double midPoint = (leftMost.prelim + rightMost.prelim) / 2.0;
	    if (n.hasLeftSibling()) {
		n.prelim = n.leftSibling().prelim + siblingSeparation
			+ meanNodeSize(n.leftSibling(), n);
		n.modifier = n.prelim - midPoint;
		apportion(n, level);
	    } else
		n.prelim = midPoint;
	}
    }

    private void secondWalk(MagicEyeNodeView n, int level, double modSum) {
	if (n.visible) {
	    if (level <= maxDepth) {
		double xTemp = xTopAdjustment + n.prelim + modSum;
		n.x = xTemp;

		if (n.x > maxx)
		    maxx = n.x;
		else if (n.x < minx)
		    minx = n.x;

		for (int i = 0; i < n.numChilds; i++)
		    secondWalk(n.getChild(i), level + 1, modSum + n.modifier);
	    }
	}
    }

    private void apportion(MagicEyeNodeView n, int level) {
	MagicEyeNodeView leftMost = n.firstChild();
	MagicEyeNodeView neighbor = null;
	if (leftMost != null)
	    neighbor = leftMost.leftNeighbor;
	MagicEyeNodeView ancestorLeftMost, ancestorNeighbor, temp;
	int compareDepth = 1;
	int depthToStop = maxDepth - level;
	int leftSiblings;
	double leftModSum, rightModSum, moveDistance, portion;

	while (leftMost != null && neighbor != null
		&& compareDepth <= depthToStop) {
	    leftModSum = rightModSum = 0.0;
	    ancestorLeftMost = leftMost;
	    ancestorNeighbor = neighbor;

	    for (int i = 0; i < compareDepth; i++) {
		ancestorLeftMost = ancestorLeftMost.parent;
		ancestorNeighbor = ancestorNeighbor.parent;
		rightModSum += ancestorLeftMost.modifier;
		leftModSum += ancestorNeighbor.modifier;
	    }

	    moveDistance = neighbor.prelim + leftModSum + subtreeSeparation
		    + meanNodeSize(leftMost, neighbor)
		    - (leftMost.prelim + rightModSum);

	    if (moveDistance > 0.0) {
		temp = n;
		leftSiblings = 0;

		while (temp != null && temp != ancestorNeighbor) {
		    leftSiblings++;
		    temp = temp.leftSibling();
		}
		if (temp != null) {
		    portion = moveDistance / (double) leftSiblings;
		    temp = n;
		    while (temp != ancestorNeighbor) {
			temp.prelim += moveDistance;
			temp.modifier += moveDistance;
			moveDistance -= portion;
			temp = temp.leftSibling();
		    }
		} else
		    break;// return;
	    }

	    compareDepth++;
	    if (leftMost.isLeaf())
		leftMost = getLeftMost(n, 0, compareDepth);
	    else
		leftMost = leftMost.firstChild();
	    // fehlt was? siehe Lars
	    if (leftMost != null)
		neighbor = leftMost.leftNeighbor;

	}
    }

    private MagicEyeNodeView getLeftMost(MagicEyeNodeView n, int level,
	    int depth) {
	MagicEyeNodeView rightMost = null;
	MagicEyeNodeView leftMost = null;

	if (level >= depth)
	    return n;
	else {
	    if (n.isLeaf())
		return null;
	    else {
		rightMost = n.firstChild();
		leftMost = getLeftMost(rightMost, level + 1, depth);
		while (leftMost == null && rightMost.hasRightSibling()) {
		    rightMost = rightMost.rightSibling();
		    leftMost = getLeftMost(rightMost, level + 1, depth);
		}
		return leftMost;
	    }
	}
    }

    private void initPrevNodeList() {
	prevNodeList = new MagicEyeNodeView[Root.depth + 1];
	prevNodeListSav = new MagicEyeNodeView[Root.depth + 1];
	for (int i = 0; i < prevNodeList.length; i++) {
	    prevNodeList[i] = null;
	    prevNodeListSav[i] = null;
	}
    }

    private MagicEyeNodeView getPrevNodeAtLevel(int level) {
	return prevNodeList[level];
    }

    private void setPrevNodeAtLevel(int level, MagicEyeNodeView n) {
	prevNodeListSav[level] = prevNodeList[level];
	prevNodeList[level] = n;
    }

    private double meanNodeSize(MagicEyeNodeView leftNode,
	    MagicEyeNodeView rightNode) {
	return 0.0;
    }

    private void adjustMaxX() {
	maxx += subtreeSeparation / 2;
	minx -= subtreeSeparation / 2;
    }

    // Skalierung der Knotenpositionen auf FlatMap
    private void makeFlatMap(MagicEyeNodeView n) {
	double lambda, phi, lambdaInc, phiInc, a;

	// Knoten auf Halbkugel projizieren
	project2Hemisphere(scalex * (n.x - minx) + start, scaley * n.level, n);

	// Kante auf Halbkugel projizieren
	if (n.parent != null) {
	    // keine geschwungenen Kanten von Root zu den Söhnen
	    if (n.parent == Root) {
		double xInc = (n.getPosX() - Root.getPosX())
			* REZ_LINES_PER_EDGE;
		double yInc = (n.getPosY() - Root.getPosY())
			* REZ_LINES_PER_EDGE;
		double zInc = (n.getPosZ() - Root.getPosZ())
			* REZ_LINES_PER_EDGE;
		double x = Root.getPosX();
		double y = Root.getPosY();
		double z = Root.getPosZ();
		for (int i = 0; i < INNER_EDGE_POINTS; i++) {
		    x += xInc;
		    y += yInc;
		    z += zInc;
		    n.pEdgePoints[i].set(x, y, z);
		}
	    }
	    // alle anderen Kanten werden gegebf. geschwungen dargestellt,
	    // um Überschneidungen zu vermeiden
	    else {
		if (CURVED_EDGES) {
		    // geschwungen
		    lambdaInc = (n.lambda - n.parent.lambda)
			    * REZ_LINES_PER_EDGE;
		    phiInc = (n.phi - n.parent.phi) * REZ_LINES_PER_EDGE;
		    lambda = n.parent.lambda;
		    phi = n.parent.phi;
		    for (int i = 0; i < INNER_EDGE_POINTS; i++) {
			lambda += lambdaInc;
			phi += phiInc;
			project2Hemisphere(lambda, phi, n.pEdgePoints[i]);
		    }
		} else {
		    // gerade
		    MagicEyeNodeView np = n.parent;
		    double xInc = (n.getPosX() - np.getPosX())
			    * REZ_LINES_PER_EDGE;
		    double yInc = (n.getPosY() - np.getPosY())
			    * REZ_LINES_PER_EDGE;
		    double zInc = (n.getPosZ() - np.getPosZ())
			    * REZ_LINES_PER_EDGE;
		    double x = np.getPosX();
		    double y = np.getPosY();
		    double z = np.getPosZ();
		    for (int j = 0; j < INNER_EDGE_POINTS; j++) {
			x += xInc;
			y += yInc;
			z += zInc;
			n.pEdgePoints[j].set(x, y, z);
		    }
		}
	    }
	}

	// rekursiv die Sohnknoten abarbeiten
	if (n.visible) {
	    for (int i = 0; i < n.numChilds; i++)
		makeFlatMap(n.getChild(i));
	}
    }

    private void project2Hemisphere(double lambda, double phi, Point3d p) {
	if (hemisphere) {
	    double a = java.lang.Math.sin(phi);
	    p.set(java.lang.Math.cos(lambda) * a, java.lang.Math.sin(lambda)
		    * a, java.lang.Math.cos(phi));
	} else {
	    double l = phi / _PI;
	    p.set(java.lang.Math.cos(lambda) * l, java.lang.Math.sin(lambda)
		    * l, 0.0);
	}
    }

    private void project2Hemisphere(double lambda, double phi,
	    MagicEyeNodeView n) {
	n.lambda = lambda;
	n.phi = phi;
	if (hemisphere) {
	    double a = java.lang.Math.sin(phi);
	    n.setPosX((float) (Math.cos(lambda) * a));
	    n.setPosY((float) (Math.sin(lambda) * a));
	    n.setPosZ((float) (Math.cos(phi)));
	} else {
	    double l = phi / _PI;
	    n.setPosX((float) (Math.cos(lambda) * l));
	    n.setPosY((float) (Math.sin(lambda) * l));
	    n.setPosZ(0f);
	}
    }

    private double getMinX(MagicEyeNodeView n) {
	MagicEyeNodeView ni;
	double minx = n.x;
	double tmp;

	for (int i = 0; i < n.numChilds; i++) {
	    ni = n.getChild(i);
	    if (ni.visible) {
		tmp = getMinX(ni);
		if (tmp < minx)
		    minx = tmp;
	    }
	}
	return minx;
    }

    private double getMaxX(MagicEyeNodeView n) {
	MagicEyeNodeView ni;
	double maxx = n.x;
	double tmp;

	for (int i = 0; i < n.numChilds; i++) {
	    ni = n.getChild(i);
	    if (ni.visible) {
		tmp = getMaxX(ni);
		if (tmp > maxx)
		    maxx = tmp;
	    }
	}
	return maxx;
    }

    private void edgeToRoot(MagicEyeNodeView n) {
	double xInc = (n.getPosX() - Root.getPosX()) * REZ_LINES_PER_EDGE;
	double yInc = (n.getPosY() - Root.getPosY()) * REZ_LINES_PER_EDGE;
	double zInc = (n.getPosZ() - Root.getPosZ()) * REZ_LINES_PER_EDGE;
	double x = Root.getPosX();
	double y = Root.getPosY();
	double z = Root.getPosZ();
	for (int i = 0; i < INNER_EDGE_POINTS; i++) {
	    x += xInc;
	    y += yInc;
	    z += zInc;
	    n.pEdgePoints[i].set(x, y, z);
	}
    }

    private void subtree2Flatmap(MagicEyeNodeView n) {
	double lambda, phi, lambdaInc, phiInc, a;
	MagicEyeNodeView ni;

	if (n.visible) {
	    for (int i = 0; i < n.numChilds; i++) {
		ni = n.getChild(i);
		// Position auf Halbkugel
		project2Hemisphere((ni.x - minx) * scalex + start, ni.level
			* scaley, ni);
		if (CURVED_EDGES) {
		    // geschwungene Kante
		    lambdaInc = (ni.lambda - ni.parent.lambda)
			    * REZ_LINES_PER_EDGE;
		    phiInc = (ni.phi - ni.parent.phi) * REZ_LINES_PER_EDGE;
		    lambda = ni.parent.lambda;
		    phi = ni.parent.phi;
		    for (int j = 0; j < INNER_EDGE_POINTS; j++) {
			lambda += lambdaInc;
			phi += phiInc;
			project2Hemisphere(lambda, phi, ni.pEdgePoints[j]);
		    }
		} else {
		    // gerade Kante
		    MagicEyeNodeView nip = ni.parent;
		    double xInc = (ni.getPosX() - nip.getPosX())
			    * REZ_LINES_PER_EDGE;
		    double yInc = (ni.getPosY() - nip.getPosY())
			    * REZ_LINES_PER_EDGE;
		    double zInc = (ni.getPosZ() - nip.getPosZ())
			    * REZ_LINES_PER_EDGE;
		    double x = nip.getPosX();
		    double y = nip.getPosY();
		    double z = nip.getPosZ();
		    for (int j = 0; j < INNER_EDGE_POINTS; j++) {
			x += xInc;
			y += yInc;
			z += zInc;
			ni.pEdgePoints[j].set(x, y, z);
		    }
		}
		subtree2Flatmap(ni);
	    }
	}
    }

    public boolean isHemisphere() {
	return hemisphere;
    }

    public void setHemisphere(boolean hemisphere) {
	this.hemisphere = hemisphere;
    }

}
