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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import pedviz.graph.Node;
import pedviz.view.NodeView;

/**
 * Draws the symbol for a family node.
 * 
 * @author lukas forer
 * 
 */
public class SymbolFamily extends Symbol2D {
    private Ellipse2D ellipse = new Ellipse2D.Double();

    private Object male;

    private Object female;

    private double size = 0.6;

    int mode = 0;

    /**
     * Creates a new symbol for a family node.
     * 
     * @param mode
     */
    public SymbolFamily(int mode, Object male, Object female) {
	super();
	this.mode = mode;
	this.male = male;
	this.female = female;
    }

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	if (mode > 0) {

	    int cmale = 0;
	    int cfemale = 0;
	    for (Node child : nodeview.getNode().getNodes()) {
		cfemale += child.getUserData("SEX").equals(female) ? 1 : 0;
		cmale += child.getUserData("SEX").equals(male) ? 1 : 0;
	    }

	    Font oldfont = g.getFont();
	    g.setColor(border);
	    Font font = new Font("default", 0, (int) (size * this.size));
	    g.setFont(font);

	    if (nodeview.getNode().getNodeCount() > 0) {
		if (mode == 2) {
		    g.setColor(fill);
		    ellipse.setFrame(position.getX() - (size / 2.0) - 0.5,
			    position.getY() - (size / 2.0) + 0.5, size + 0.5,
			    size - 1);
		    g.fill(ellipse);
		    font = new Font("default", 0, (int) (size * this.size));
		    g.setFont(font);
		    float width = g.getFontMetrics().stringWidth("" + cfemale);
		    float height = g.getFontMetrics().getHeight();
		    g.setColor(Color.RED);
		    g.drawString("" + cfemale,
			    (position.x - width / 2f + size / 4f), position.y
				    + height / 4.0f);
		    width = g.getFontMetrics().stringWidth("" + cmale);
		    g.setColor(Color.BLUE);
		    g.drawString("" + cmale,
			    (position.x - width / 2f - size / 4f), position.y
				    + height / 4.0f);
		} else {
		    g.setColor(fill);
		    ellipse.setFrame(position.getX() - (size / 2.0) + 0.5,
			    position.getY() - (size / 2.0) + 0.5, size - 1,
			    size - 1);
		    g.fill(ellipse);
		    float width = g.getFontMetrics().stringWidth(
			    nodeview.getNode().getNodeCount() + "");
		    float height = g.getFontMetrics().getHeight();
		    g.setColor(border);
		    g.drawString("" + nodeview.getNode().getNodeCount(),
			    (position.x - width / 2f), position.y + height
				    / 4.0f);

		}

	    } else {
		g.setColor(fill);
		ellipse.setFrame(position.getX() - (size / 2.0) + 0.5, position
			.getY()
			- (size / 2.0) + 0.5, size - 1, size - 1);
		g.fill(ellipse);
		int width = g.getFontMetrics().stringWidth("1");
		int height = g.getFontMetrics().getHeight();
		g.setColor(border);
		g.drawString("1", position.x - width / 2f,
			(float) (position.y + ((float) (height) / 4f)));

	    }
	    g.setColor(border);
	    g.draw(ellipse);
	    g.setFont(oldfont);
	} else {
	    g.setColor(fill);
	    ellipse.setFrame(position.getX() - (size / 2.0) + 0.5, position
		    .getY()
		    - (size / 2.0) + 0.5, size - 1, size - 1);
	    g.setColor(fill);
	    ellipse.setFrame(position.getX() - (size / 2.0) + 0.5, position
		    .getY()
		    - (size / 2.0) + 0.5, size - 1, size - 1);
	    g.fill(ellipse);
	    g.setColor(border);
	    g.draw(ellipse);
	}

    }

    @Override
    public int getPriority() {
	return 0;
    }
}
