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

package pedviz.algorithms.sugiyama;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import pedviz.graph.Node;
import pedviz.view.DefaultNodeView;
import pedviz.view.EdgeView;
import pedviz.view.NodeView;

/**
 * This class extends the NodeView class by a barycentric property.
 * 
 * @author Luki
 * 
 */

public class SugiyamaNodeView extends NodeView {

    private float barycentric = Float.MAX_VALUE;

    public SugiyamaNodeView(Node node, DefaultNodeView defaultNodeView) {
	super(node, defaultNodeView);
    }

    /**
     * Returns the barycentric.
     * 
     * @return the barycentric
     */
    public float getBarycentric() {
	return barycentric;
    }

    /**
     * Sets the barycentric.
     * 
     * @param barycentric
     *                barycentric
     */
    public void setBarycentric(float barycentric) {
	this.barycentric = barycentric;
    }

    /**
     * Sorts subnodes by their barycentric.
     */
    public void sortSubNodes() {
	final HashMap<Object, SugiyamaNodeView> n = new HashMap<Object, SugiyamaNodeView>();
	for (NodeView v : getNodes()) {
	    SugiyamaNodeView subNodeView = (SugiyamaNodeView) v;
	    n.put(subNodeView.getNode().getId(), subNodeView);
	    float barycenter = 0.0f;
	    float sum = 0.0f;

	    for (EdgeView edge : subNodeView.getOutEdges()) {
		SugiyamaEdgeView e = (SugiyamaEdgeView) edge;
		sum += e.getEnd().getPosX() - getPosX();
	    }
	    if (subNodeView.getOutEdges().size() > 0)
		barycenter = sum / subNodeView.getOutEdges().size();

	    subNodeView.setBarycentric(barycenter);
	}
	Collections.sort(getNode().getNodes(), new Comparator<Node>() {
	    public int compare(Node arg0, Node arg1) {
		return (new Float(n.get(arg0.getId()).getBarycentric())
			.compareTo(n.get(arg1.getId()).getBarycentric()));
	    }
	});
	n.clear();
    }

    @Override
    public int compareTo(Object arg0) {
	return new Float(getBarycentric()).compareTo(((SugiyamaNodeView) arg0)
		.getBarycentric());
    }

}
