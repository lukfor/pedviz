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

package pedviz.view.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import pedviz.view.NodeView;

/**
 * Draws the symbol for a female individual.
 * 
 * @author lukas forer
 * 
 */
public class SymbolSexFemale extends Symbol2D {
    private Ellipse2D ellipse = new Ellipse2D.Double();

    private Arc2D arc = new Arc2D.Double();

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {

	g.setColor(fill);
	ellipse.setFrame(position.getX() - (size / 2.0) + 0.5, position.getY()
		- (size / 2.0) + 0.5, size - 1, size - 1);

	g.fill(ellipse);
	if (colors != null) {

	    if (colors.length <= 2) {
		if (colors[0] != null) {
		    g.setColor(colors[0]);
		    arc.setArc(ellipse.getBounds2D(), 90, 180, 1);
		    g.fill(arc);
		}
		if (colors.length > 1) {
		    if (colors[1] != null) {
			g.setColor(colors[1]);
			arc.setArc(ellipse.getBounds2D(), 270, 180, 1);
			g.fill(arc);
		    }
		}
	    }

	    if (colors.length > 2) {
		if (colors[0] != null) {
		    g.setColor(colors[0]);
		    arc.setArc(ellipse.getBounds2D(), 90, 90, 2);
		    g.fill(arc);
		}
		if (colors[1] != null) {
		    g.setColor(colors[1]);
		    arc.setArc(ellipse.getBounds2D(), 0, 90, 2);
		    g.fill(arc);
		}
		if (colors[2] != null) {
		    g.setColor(colors[2]);
		    arc.setArc(ellipse.getBounds2D(), 270, 90, 2);
		    g.fill(arc);
		}
		if (colors.length > 3) {
		    if (colors[3] != null) {
			g.setColor(colors[3]);
			arc.setArc(ellipse.getBounds2D(), 180, 90, 2);
			g.fill(arc);
		    }
		}
	    }
	}
	// Border
	g.setColor(border);
	g.draw(ellipse);
    }

    @Override
    public int getPriority() {
	return 0;
    }
}
