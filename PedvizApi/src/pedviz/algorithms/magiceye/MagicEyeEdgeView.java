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

import pedviz.graph.Edge;
import pedviz.view.DefaultEdgeView;
import pedviz.view.EdgeView;

/**
 * This class extends the EdgeView class by some methods used by the magiceye
 * algorithm. experimental!
 * 
 * @author lukas forer
 * 
 */

public class MagicEyeEdgeView extends EdgeView {

    public MagicEyeEdgeView(Edge edge, DefaultEdgeView defaultEdgeView) {
	super(edge, defaultEdgeView);
    }

}
