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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import pedviz.view.NodeView;

/**
 * Draws the symbol for a male individual.
 * 
 * @author lukas forer
 * 
 */
public class SymbolSexMale extends Symbol2D {
    private Rectangle2D rectangle = new Rectangle2D.Double();

    public SymbolSexMale() {
	super();
    }

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	g.setColor(fill);
	rectangle.setFrame(position.getX() - (size / 2f) + 0.5f, position
		.getY()
		- (size / 2f) + 0.5f, size - 1f, size - 1f);

	g.fill(rectangle);

	if (colors != null) {
	    if (colors.length <= 2) {
		if (colors[0] != null) {
		    g.setColor(colors[0]);
		    rectangle.setFrame(position.getX() - (size / 2f) + 0.5f,
			    position.getY() - (size / 2f) + 0.5f,
			    size / 2f - 0.5f, size - 1f);
		    g.fill(rectangle);
		}
		if (colors.length > 1) {
		    if (colors[1] != null) {
			g.setColor(colors[1]);
			rectangle.setFrame(position.getX(), position.getY()
				- (size / 2.0) + 0.5, size / 2.0 - 0.5,
				size - 1);
			g.fill(rectangle);
		    }

		}
	    }

	    if (colors.length > 2) {
		if (colors[0] != null) {
		    g.setColor(colors[0]);
		    rectangle.setFrame(position.getX() - (size / 2.0) + 0.5,
			    position.getY() - (size / 2.0) + 0.5,
			    size / 2.0 - 0.5, size / 2.0 - 0.5);
		    g.fill(rectangle);
		}
		if (colors[1] != null) {
		    g.setColor(colors[1]);
		    rectangle.setFrame(position.getX(), position.getY()
			    - (size / 2.0) + 0.5, size / 2.0 - 0.5,
			    size / 2.0 - 0.5);
		    g.fill(rectangle);
		}
		if (colors[2] != null) {
		    g.setColor(colors[2]);
		    rectangle.setFrame(position.getX(), position.getY(),
			    size / 2.0 - 0.5, size / 2.0 - 0.5);
		    g.fill(rectangle);
		}
		if (colors.length > 3) {
		    if (colors[3] != null) {
			g.setColor(colors[3]);
			rectangle.setFrame(
				position.getX() - (size / 2.0) + 0.5, position
					.getY(), size / 2.0 - 0.5,
				size / 2.0 - 0.5);
			g.fill(rectangle);
		    }

		}
	    }
	}

	// border
	g.setColor(border);
	rectangle.setFrame(position.getX() - (size / 2.0) + 0.5, position
		.getY()
		- (size / 2.0) + 0.5, size - 1, size - 1);
	g.draw(rectangle);
    }

    @Override
    public int getPriority() {
	return 0;
    }
}
