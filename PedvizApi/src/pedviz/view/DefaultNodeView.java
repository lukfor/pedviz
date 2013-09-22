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
import java.util.ArrayList;
import java.util.Vector;

/**
 * This class contains all informations that are needed to draw a Node on a
 * GraphView.
 * 
 * @author Luki
 * 
 */
public class DefaultNodeView {

    private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

    private float size = 7f;

    private int gap = 0;

    protected Color color = new Color(255, 0, 0);

    protected Color borderColor = new Color(0, 0, 0);

    private Color selectedColor = new Color(0, 255, 0);

    private Color highlightedColor = new Color(255, 255, 0);

    private Color mouseOverColor = new Color(255, 255, 0);

    private float borderWidth = 0.2f;

    private Vector<String> hintAttributes = new Vector<String>();

    private String hint = null;

    boolean expand = true;

    public DefaultNodeView() {

    }

    /**
     * Returns the color of the node.
     * 
     * @return color of the node
     */
    public Color getColor() {
	return color;
    }

    /**
     * Sets the color of the node.
     * 
     * @param color
     *                color of the node
     */
    public void setColor(Color color) {
	this.color = color;
    }

    /**
     * Returns a collection of all symbols for this node.
     * 
     * @return a collection of all symbols for this node.
     */
    public ArrayList<Symbol> getSymbols() {
	return symbols;
    }

    /**
     * Sets all symbols of the node.
     * 
     * @param symbols
     *                a collection of all symbols for this node.
     */
    public void setSymbols(ArrayList<Symbol> symbols) {
	this.symbols = symbols;
    }

    /**
     * Returns the selected color of the node.
     * 
     * @return the selected color of the node
     */
    public Color getSelectedColor() {
	return selectedColor;
    }

    /**
     * Sets the selected color of the node.
     * 
     * @param selectedColor
     */
    public void setSelectedColor(Color selectedColor) {
	this.selectedColor = selectedColor;
    }

    /**
     * Returns the size of the node.
     * 
     * @return size of the node
     */
    public float getSize() {
	return size;
    }

    /**
     * Sets the size of the node.
     * 
     * @param size
     *                size of the node
     */
    public void setSize(float size) {
	this.size = size;
    }

    /**
     * Returns the highlighted color of the node.
     * 
     * @return the highlighted color of the node
     */
    public Color getHighlightedColor() {
	return highlightedColor;
    }

    /**
     * Sets the highlighted color of the node.
     * 
     * @param highlightedColor
     *                highlighted color of the node.
     */
    public void setHighlightedColor(Color highlightedColor) {
	this.highlightedColor = highlightedColor;
    }

    /**
     * Sets the "mouse over" color of the node.
     * 
     * @return "mouse over" color of the node
     */
    public Color getMouseOverColor() {
	return mouseOverColor;
    }

    /**
     * Returns the "mouse over" color of the node.
     * 
     * @param mouseOverColor
     *                "mouse over" color of the node
     */
    public void setMouseOverColor(Color mouseOverColor) {
	this.mouseOverColor = mouseOverColor;
    }

    protected Vector<String> getHintAttributes() {
	return hintAttributes;
    }

    protected void setHintAttributes(Vector<String> hintAttributes) {
	this.hintAttributes = hintAttributes;
    }

    /**
     * Adds a trait that will be displayed in the hint.
     * 
     * @param hintAttribute
     *                trait that will be displayed in the hint
     */
    public void addHintAttribute(String hintAttribute) {
	hintAttributes.add(hintAttribute);
    }

    /**
     * Sets a trait that will be displayed in the hint.
     * 
     * @param hintAttribute
     *                trait that will be displayed in the hint
     */
    public void setHintAttribute(String hintAttribute) {
	hintAttributes.clear();
	hintAttributes.add(hintAttribute);
    }

    /**
     * Returns the color of the node's border.
     * 
     * @return color of the node's border
     */
    public Color getBorderColor() {
	return borderColor;
    }

    /**
     * Sets the color of the node's border.
     * 
     * @param borderColor
     *                color of the node's borde
     */
    public void setBorderColor(Color borderColor) {
	this.borderColor = borderColor;
    }

    /**
     * Returns the width of the node's border.
     * 
     * @return width of the node's border
     */
    public float getBorderWidth() {
	return borderWidth;
    }

    /**
     * Sets the width of the node's border.
     * 
     * @param borderWidth
     *                width of the node's border
     */
    public void setBorderWidth(float borderWidth) {
	this.borderWidth = borderWidth;
    }

    /**
     * Returns the gap between two children.
     * 
     * @return gap between two children
     */
    public int getGap() {
	return gap;
    }

    /**
     * Sets the gap between two children.
     * 
     * @param gap
     *                between two children
     */
    public void setGap(int gap) {
	this.gap = gap;
    }

    /**
     * Sets the hint text.
     * 
     * @param hint
     *                hint text.
     */
    public void setHintText(String hint) {
	this.hint = hint;
    }

    protected String getHintText() {
	return hint;
    }

    /**
     * Adds a symbol to the collection of symbols.
     * 
     * @param symbol
     *                Sybol object.
     */
    public void addSymbol(Symbol symbol) {
	symbols.add(symbol);
    }

    /**
     * Returns true, if the node is visible.
     * 
     * @return true, if the node is expanded.
     */
    public boolean isExpand() {
	return expand;
    }

    /**
     * Enables and disables the visibility of the node.
     * 
     * @param expand
     *                visibility of the node
     */
    public void setExpand(boolean expand) {
	this.expand = expand;
    }

    public Float getHeight() {
	float height = 0;
	for (Symbol symbol : symbols) {
	    if (symbol.getHeight() > height) {
		height = symbol.getHeight();
	    }
	}
	return height;
    }

}
