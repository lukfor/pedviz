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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import pedviz.view.NodeView;

/**
 * This symbols provides the possibility to display a specified trait in a bar
 * plot with a nice color gradient. You can also define the minimum value,
 * maximum value, mean value and the standard deviation.
 * 
 * @author lukas forer
 * 
 */
public class SymbolQualitativeTrait extends Symbol2D {

    private Rectangle2D rectangle = new Rectangle2D.Double();

    Line2D.Float line = new Line2D.Float();

    private double min;

    private double max;

    private double mean;

    private double scope;

    private String userData;

    /**
     * Constructs a SymbolQualitativeTrait with the given values.
     * 
     * @param userData
     *                trait identification
     * @param min
     *                minimal value
     * @param max
     *                maximal value
     * @param mean
     *                mean value
     * @param scope
     *                standard deviation
     */
    public SymbolQualitativeTrait(String userData, double min, double max,
	    double mean, double scope) {
	this.min = min;
	this.max = max;
	this.mean = mean;
	this.scope = scope;
	this.userData = userData;
    }

    @Override
    public void drawSymbol(Graphics2D g, Point2D.Float position, float size,
	    Color border, Color fill, NodeView nodeview) {

	if (nodeview.getNode().getUserData(userData) != null) {

	    Double value = 0.0;
	    if (nodeview.getNode().getUserData(userData) instanceof Double) {
		value = (Double) nodeview.getNode().getUserData(userData);
	    } else {
		value = Double.parseDouble(nodeview.getNode().getUserData(
			userData).toString());
	    }

	    Shape oldclip = g.getClip();

	    // scale factor
	    double scale = 4;

	    // expands plot bar
	    float sizef = (float) (size * scale);

	    Double diff = (max - min);
	    float posMean = (float) (((mean - min) * sizef) / diff);
	    float sizeScope = (float) ((scope * sizef) / diff);
	    float posValue = (float) (((value - min) * sizef) / diff);

	    float offsetY = sizef - posValue;

	    float width = (size / 3f);
	    float left = (float) position.getX() + (size / 2.0f);

	    float offsetLine = 0;

	    // if it is near the maximum
	    if (offsetY < (size / 2.0f)) {
		offsetLine = offsetY - (size / 2.0f);
		offsetY = (size / 2.0f);
	    }

	    // if it is near the minimum
	    if (offsetY > sizef - (size / 2.0f)) {
		offsetLine = offsetY - (sizef - (size / 2.0f));
		offsetY = sizef - (size / 2.0f);
	    }

	    float topA = (float) position.getY() - offsetY;

	    rectangle
		    .setFrame(left, position.getY() - (size / 2f), width, size);
	    g.setClip(rectangle);

	    // Gradient red - green
	    GradientPaint gradient = new GradientPaint(left, topA, Color.red,
		    left, topA + (float) (sizef - posMean - sizeScope),
		    Color.green);
	    g.setPaint(gradient);
	    rectangle.setFrame(left, topA, width, sizef - posMean - sizeScope);
	    g.fill(rectangle);

	    // green scope
	    g.setColor(Color.green);
	    rectangle.setFrame(left, topA + sizef - posMean - sizeScope, width,
		    2 * sizeScope);
	    g.fill(rectangle);

	    // Gradient green - blue
	    GradientPaint gradient2 = new GradientPaint(left, topA + sizef
		    - posMean + sizeScope, Color.green, left, topA + sizef,
		    Color.blue);
	    g.setPaint(gradient2);
	    rectangle.setFrame(left, topA + sizef - posMean + (sizeScope / 2f),
		    width, posMean - sizeScope);
	    g.fill(rectangle);

	    g.setClip(oldclip);

	    // value's position
	    g.setColor(Color.WHITE);
	    line.setLine(left, position.getY() + offsetLine, left + width,
		    position.getY() + offsetLine);
	    g.draw(line);

	    // border
	    g.setColor(border);
	    rectangle
		    .setFrame(left, position.getY() - (size / 2f), width, size);
	    g.draw(rectangle);

	}

    }

    @Override
    public int getPriority() {
	return 4;
    }
}
