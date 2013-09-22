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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import pedviz.view.NodeView;

/**
 * Draws the symbol for a individual with unknown sex.
 * 
 * @author lukas
 * 
 */
public class SymbolSexUndesignated extends Symbol2D {

    GeneralPath path = new GeneralPath();

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	path.reset();
	path.moveTo(position.x - (size / 2f), position.y);
	path.lineTo(position.x, position.y + (size / 2f));
	path.lineTo(position.x + (size / 2f), position.y);
	path.lineTo(position.x, position.y - (size / 2f));
	path.closePath();

	g.setColor(fill);
	g.fill(path);

	if (colors != null) {
	    if (colors.length <= 2) {
		if (colors[0] != null) {
		    g.setColor(colors[0]);
		    path.reset();
		    path.moveTo(position.x - (size / 2f), position.y);
		    path.lineTo(position.x, position.y + (size / 2f));
		    path.lineTo(position.x, position.y - (size / 2f));
		    path.closePath();
		    g.fill(path);
		}
		if (colors.length > 1) {
		    if (colors[1] != null) {
			g.setColor(colors[1]);
			path.reset();
			path.moveTo(position.x + (size / 2f), position.y);
			path.lineTo(position.x, position.y + (size / 2f));
			path.lineTo(position.x, position.y - (size / 2f));
			path.closePath();
			g.fill(path);
		    }
		}
	    }

	    if (colors.length > 2) {
		if (colors[0] != null) {
		    g.setColor(colors[0]);
		    path.reset();
		    path.moveTo(position.x - (size / 2f), position.y);
		    path.lineTo(position.x, position.y - (size / 2f));
		    path.lineTo(position.x, position.y);
		    path.closePath();
		    g.fill(path);
		}
		if (colors[1] != null) {
		    g.setColor(colors[1]);
		    path.reset();
		    path.moveTo(position.x + (size / 2f), position.y);
		    path.lineTo(position.x, position.y - (size / 2f));
		    path.lineTo(position.x, position.y);
		    path.closePath();
		    g.fill(path);
		}
		if (colors[2] != null) {
		    g.setColor(colors[2]);
		    path.reset();
		    path.moveTo(position.x + (size / 2f), position.y);
		    path.lineTo(position.x, position.y + (size / 2f));
		    path.lineTo(position.x, position.y);
		    path.closePath();
		    g.fill(path);
		    if (colors.length > 3) {
			if (colors[3] != null) {
			    g.setColor(colors[3]);
			    path.reset();
			    path.moveTo(position.x - (size / 2f), position.y);
			    path.lineTo(position.x, position.y + (size / 2f));
			    path.lineTo(position.x, position.y);
			    path.closePath();
			    g.fill(path);
			}
		    }
		}
	    }

	}
	// border
	path.reset();
	path.moveTo(position.x - (size / 2f), position.y);
	path.lineTo(position.x, position.y + (size / 2f));
	path.lineTo(position.x + (size / 2f), position.y);
	path.lineTo(position.x, position.y - (size / 2f));
	path.closePath();
	g.setColor(border);
	g.draw(path);

    }

    @Override
    public int getPriority() {
	return 0;
    }
}
