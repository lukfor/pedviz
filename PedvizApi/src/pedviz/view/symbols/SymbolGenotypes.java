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
 * Draws the symbol for genotpyes. In the data available genotypes are simply
 * drawn, by adding a genotype symbol with a list of markers.
 * 
 * @author Luki
 * 
 */
public class SymbolGenotypes extends Symbol2D {

    private String[] traits;

    private boolean showId = false;

    /**
     * Creates a new GenotypeSymbol with the given collection of traits.
     * 
     * @param traits
     *                Collection of traits.
     */
    public SymbolGenotypes(String... traits) {
	this(false, traits);
    }

    /**
     * Creates a new GenotypeSymbol with the given collection of traits.
     * 
     * @param showId
     *                Shows the id of the person.
     * @param traits
     *                traits Collection of traits.
     */
    public SymbolGenotypes(boolean showId, String... traits) {
	this.showId = showId;
	this.traits = traits;
    }

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {

	float top = position.y + (size / 2f) + 0.5f;

	Font oldfont = g.getFont();
	g.setColor(border);
	Font font = new Font("default", 0, 3);
	g.setFont(font);
	float height = g.getFontMetrics().getHeight();

	float y = top + height;
	if (showId) {
	    String text = nodeview.getNode().getId().toString();
	    float width = g.getFontMetrics().stringWidth(text);
	    g.drawString(text, (position.x - width / 2f),
		    (float) (y - (((int) (size * 0.5f)) / 2.0f)));
	    y += height;
	}
	for (String trait : traits) {
	    String text = "??";
	    if (nodeview.getNode().getUserData(trait) != null) {
		text = nodeview.getNode().getUserData(trait).toString();
	    }
	    float width = g.getFontMetrics().stringWidth(text);
	    g.drawString(text, (position.x - width / 2f),
		    (float) (y - (((int) (size * 0.5f)) / 2.0f)));
	    y += height;
	}
	g.setFont(oldfont);

    }

    @Override
    public int getPriority() {
	return 1;
    }

    @Override
    public java.lang.Float getHeight() {
	return 0.5f + 4.0f * ((traits.length) + (showId ? 1f : 0f));
    }

    /**
     * Collection of traits with genotype informations.
     * 
     * @param traits
     *                collection of traits.
     */
    public void setTraits(String[] traits) {
	this.traits = traits;
    }
}
