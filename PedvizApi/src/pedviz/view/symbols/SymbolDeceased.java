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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import pedviz.view.NodeView;

/**
 * Draws the symbol for a deceased individual.
 * 
 * @author lukas forer
 * 
 */
public class SymbolDeceased extends Symbol2D {
    Line2D.Float line = new Line2D.Float();

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	g.setColor(border);
	line.setLine(position.x + (size / 2f), position.y - (size / 2f),
		position.x - (size / 2f), position.y + (size / 2f));
	g.draw(line);
    }

    @Override
    public int getPriority() {
	return 5;
    }
}
