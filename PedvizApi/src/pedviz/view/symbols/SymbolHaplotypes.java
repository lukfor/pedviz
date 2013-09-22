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
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import pedviz.haplotype.HaplotypeItem;
import pedviz.haplotype.Haplotypes;
import pedviz.haplotype.MerlinError;
import pedviz.view.NodeView;

/**
 * Draws the symbol for haplotypes.
 * 
 * @author Luki
 * 
 */
public class SymbolHaplotypes extends Symbol2D {

    private Haplotypes haplotypes;

    private boolean showId = false;

    private boolean showMarkers = true;

    /**
     * Creates a haplotype symbol with the given haplotypes.
     * 
     * @param haplotypes
     *                Haplotype object.
     */
    public SymbolHaplotypes(Haplotypes haplotypes) {
	this(haplotypes, false);
    }

    /**
     * Creates a haplotype symbol with the given haplotypes.
     * 
     * @param haplotypes
     *                Haplotype object.
     * @param showId
     *                Shows the id of the person.
     */
    public SymbolHaplotypes(Haplotypes haplotypes, boolean showId) {
	this.haplotypes = haplotypes;
	this.showId = showId;
    }

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {
	float top = (float) (position.getY() + (size / 2f) + 0.5);

	Font oldfont = g.getFont();
	g.setColor(border);
	Font font = new Font("default", 0, 2);
	g.setFont(font);

	float y = top;
	Vector<HaplotypeItem> data = haplotypes.getData(nodeview.getNode()
		.getId());
	if (showId) {
	    String text = nodeview.getNode().getId().toString();
	    float width = g.getFontMetrics().stringWidth(text);
	    g.drawString(text, (position.x - width / 2f), y + 2f);
	    y += 2.5f;
	}
	int fontHeight = 2;

	if (nodeview.isMouseOver() && showMarkers) {
	    Rectangle2D.Float r2 = new Rectangle2D.Float(
		    (position.x - 25f - 1.5f), y, 20f, haplotypes
			    .getVisibleSize()
			    * 2.5f + (showId ? 2.5f : 0f));
	    g.setColor(new Color(230, 230, 230));
	    g.fill(r2);
	}

	for (int i = 0; i < data.size(); i++) {

	    HaplotypeItem haplotype = data.get(i);
	    if (haplotypes.isMarkerVisible(i)) {
		String text = haplotype.getDad();
		String text2 = haplotype.getMom();
		float width = g.getFontMetrics().stringWidth(text);
		float width2 = g.getFontMetrics().stringWidth(text2);

		float posxMom = (position.x + 3f + 0.5f);
		float posxMom2 = position.x + 0.25f;

		float posxDad = (position.x - 3f - 0.5f);
		float posxDad2 = position.x - 0.25f - 2f;

		if (nodeview.isMomLeft()) {
		    posxMom = (position.x - 3f - 0.5f);
		    posxMom2 = position.x - 0.25f - 2f;
		    posxDad = (position.x + 3f + 0.5f);
		    posxDad2 = position.x + 0.25f;
		}

		MerlinError error = haplotypes.getErrors().get(
			nodeview.getNode().getId());

		if (error != null
			&& error.getMarker().equals(
				haplotypes.getMarkers().get(i))) {
		    g.setColor(Color.RED);
		} else {
		    g.setColor(Color.BLACK);

		}
		g.drawString(text, posxDad - width / 2f, y + 2f);

		g.drawString(text2, posxMom - width2 / 2f, y + 2f);

		g.setColor(Color.BLACK);
		if (nodeview.isMouseOver() && showMarkers) {
		    String caption = haplotypes.getMarkers().get(i);
		    if (haplotypes.getPos().size() >= i) {
			String temp = haplotypes.getPos().get(i);
			if (temp.length() > 6) {
			    temp = temp.substring(0, 6);
			}
			caption += "    " + temp;
		    }
		    g.drawString(caption, (position.x - 25f - 0.5f), y + 2f);
		}

		Rectangle2D.Float r = new Rectangle2D.Float(posxDad2, y, 2f,
			2.7f);
		g.setColor(haplotype.getColorDad());
		g.fill(r);

		Rectangle2D.Float r2 = new Rectangle2D.Float(posxMom2, y, 2f,
			2.7f);
		g.setColor(haplotype.getColorMom());
		g.fill(r2);
		y += 2.5f;
	    }
	}
	g.setFont(oldfont);

    }

    @Override
    public int getPriority() {
	return 1;
    }

    @Override
    public java.lang.Float getHeight() {
	return 1f + haplotypes.getVisibleSize() * 2.5f + (showId ? 2.5f : 0f);
    }

    /**
     * 
     * @return
     */
    public boolean isShowMarkers() {
	return showMarkers;
    }

    /**
     * 
     * @param showMarkers
     */
    public void setShowMarkers(boolean showMarkers) {
	this.showMarkers = showMarkers;
    }

}
