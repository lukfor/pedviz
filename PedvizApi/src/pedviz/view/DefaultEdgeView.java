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

package pedviz.view;

import java.awt.Color;

/**
 * This class contains all informations that are needed to draw a Edge on a
 * GraphView.
 * 
 * @author Luki
 * 
 */
public class DefaultEdgeView {
    private float width = 0.2f;

    private float highlightedWidth = 0.6f;

    private Color color = new Color(80, 80, 80);

    private Color highlightedColor = new Color(255, 255, 0);

    private Color colorForLongLines = new Color(200, 200, 200);

    private int gapBottom = 5;

    private int gapTop = 5;

    private boolean connectChildren = true;

    private float alpha = 1f;

    private float alphaForLongLines = 0.5f;

    private float dx = 10000;

    private float dy = 10000;

    private float dz = 0f;

    /**
     * Returns the color of the line.
     * 
     * @return eges color of the line
     */
    public Color getColor() {
	return color;
    }

    /**
     * Sets the color of the line.
     * 
     * @param color
     *                color of the line
     */
    public void setColor(Color color) {
	this.color = color;
    }

    /**
     * Returns the width of the line.
     * 
     * @return the width of the line
     */
    public float getWidth() {
	return width;
    }

    /**
     * Sets the width of the line.
     * 
     * @param width
     *                the width of the line
     */
    public void setWidth(float width) {
	this.width = width;
    }

    /**
     * Returns the highlighted color of the line.
     * 
     * @return highlighted color of the line
     */
    public Color getHighlightedColor() {
	return highlightedColor;
    }

    /**
     * Sets the highlighted color of the line.
     * 
     * @param highlightedColor
     *                highlighted color of the line.
     */
    public void setHighlightedColor(Color highlightedColor) {
	this.highlightedColor = highlightedColor;
    }

    /**
     * Returns the color for long lines.
     * 
     * @return color for long lines
     */
    public Color getColorForLongLines() {
	return colorForLongLines;
    }

    /**
     * Sets the color for long lines.
     * 
     * @param colorForLongLines
     *                color for long lines
     */
    public void setColorForLongLines(Color colorForLongLines) {
	this.colorForLongLines = colorForLongLines;
    }

    /**
     * Returns true, when childrens will be drawed connected.
     * 
     * @return
     */
    public boolean isConnectChildren() {
	return connectChildren;
    }

    public void setConnectChildren(boolean connectChildren) {
	this.connectChildren = connectChildren;
    }

    /**
     * Returns the gap at the bottom.
     * 
     * @return gap at the bottom
     */
    public int getGapBottom() {
	return gapBottom;
    }

    /**
     * Sets the gap at the bottom.
     * 
     * @param gapBottom
     *                gap at the bottom
     */
    public void setGapBottom(int gapBottom) {
	this.gapBottom = gapBottom;
    }

    /**
     * Returns the gap at the top.
     * 
     * @return gap at the top.
     */
    public int getGapTop() {
	return gapTop;
    }

    /**
     * Sets the gap at the top.
     * 
     * @param gapTop
     *                gap at the top.
     */
    public void setGapTop(int gapTop) {
	this.gapTop = gapTop;
    }

    /**
     * Returns the width for lines that are highlighted.
     * 
     * @return width for lines that are highlighted
     */
    public float getHighlightedWidth() {
	return highlightedWidth;
    }

    /**
     * Sets the width for lines that are highlighted.
     * 
     * @param highlightedWidth
     *                width for lines that are highlighted
     */
    public void setHighlightedWidth(float highlightedWidth) {
	this.highlightedWidth = highlightedWidth;
    }

    /**
     * Sets the alpha value.
     * 
     * @param alpha
     *                the alpha value.
     */
    public void setAlpha(float alpha) {
	this.alpha = alpha;
    }

    /**
     * Returns the alpha value.
     * 
     * @return the alpha value.
     */
    public float getAlpha() {
	return alpha;
    }

    /**
     * Returns the alpha value for long lines.
     * 
     * @return the alpha value for long lines.
     */
    public float getAlphaForLongLines() {
	return alphaForLongLines;
    }

    /**
     * Sets the alpha value for long lines.
     * 
     * @param alphaForLongLines
     *                the alpha value for long lines.
     */
    public void setAlphaForLongLines(float alphaForLongLines) {
	this.alphaForLongLines = alphaForLongLines;
    }

    public void setDeltaForLongLines(float dx, float dy, float dz) {
	this.dx = dx;
	this.dy = dy;
	this.dz = dz;
    }

    public float getDeltaX() {
	return dx;
    }

    public float getDeltaY() {
	return dy;
    }

    public float getDeltaZ() {
	return dz;
    }

}
