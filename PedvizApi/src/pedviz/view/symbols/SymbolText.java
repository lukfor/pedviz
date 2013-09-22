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
import java.awt.geom.Point2D;

import pedviz.view.NodeView;

/**
 * This symbol allow you to display a number or a character in the symbol.
 * 
 * @author lukas forer
 * 
 */

public class SymbolText extends Symbol2D {

    private String text = new String("-");

    private double size = 0.75;

    /**
     * Creates a new symbol that contains the given text.
     * 
     * @param text
     *                Text
     */
    public SymbolText(String text) {
	this.text = text;
    }

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	Font oldfont = g.getFont();
	g.setColor(border);
	Font font = new Font("default", 0, (int) (size * this.size));
	g.setFont(font);
	float width = g.getFontMetrics().stringWidth(text);
	g.drawString(text, (position.x - width / 2f),
		(float) (position.y - (((int) (size * this.size)) / 2.0f)));
	g.setFont(oldfont);

    }

    @Override
    public int getPriority() {
	return 4;
    }

    /**
     * Returns the height of the text.
     * 
     * @return the height of the text.
     */
    public double getSize() {
	return size;
    }

    /**
     * Sets the height of the text.
     * 
     * @param size
     *                the height of the text.
     */
    public void setSize(double size) {
	this.size = size;
    }
}
