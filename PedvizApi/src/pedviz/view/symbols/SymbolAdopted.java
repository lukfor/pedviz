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
 * Draws the symbol for an adopted individual.
 * 
 * @author lukas forer
 * 
 */

public class SymbolAdopted extends Symbol2D {

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	g.setColor(border);

	GeneralPath path = new GeneralPath();
	path.moveTo(position.x - (size / 2f) + 0.4f, position.y + (size / 2f)
		+ 0.6f);
	path.lineTo(position.x - (size / 2f) - 0.4f, position.y + (size / 2f)
		- 0.3f);
	path.lineTo(position.x - (size / 2f) - 0.4f, position.y - (size / 2f)
		+ 0.3f);
	path.lineTo(position.x - (size / 2f) + 0.4f, position.y - (size / 2f)
		- 0.6f);
	g.draw(path);

	path.moveTo(position.x + (size / 2f) - 0.4f, position.y + (size / 2f)
		+ 0.6f);
	path.lineTo(position.x + (size / 2f) + 0.4f, position.y + (size / 2f)
		- 0.3f);
	path.lineTo(position.x + (size / 2f) + 0.4f, position.y - (size / 2f)
		+ 0.3f);
	path.lineTo(position.x + (size / 2f) - 0.4f, position.y - (size / 2f)
		- 0.6f);
	g.draw(path);
    }

    @Override
    public int getPriority() {
	return 4;
    }
}
