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

package pedviz.view.symbols3d;

import java.awt.Color;

import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3f;

import pedviz.view.Symbol;

/**
 * This class is the super class for all implemented symbols for the 3d
 * visualization.
 * 
 * @author lukas forer
 * 
 */
public abstract class Symbol3D extends Symbol {

    protected int style = 0;

    static final public int STYLE_TURM = 2;

    static final public int STYLE_NORMAL = 0;

    /**
     * Create a new Symbol object with the given style.
     * 
     * @param style
     *                Style
     */
    public Symbol3D(int style) {
	this.style = style;
    }

    /**
     * Creates the GeometryArray of this symbol.
     * 
     * @param position
     *                Position.
     * @param size
     *                Size.
     * @param fill
     *                fill color.
     * @return
     */
    abstract public GeometryArray[] createShape(Point3f position, float size,
	    Color fill);

    /**
     * Sets the style for this symbol.
     * 
     * @param style
     *                the style for this symbol.
     */
    public void setStyle(int style) {
	this.style = style;
    }

    /**
     * Returns the style for this symbol.
     * 
     * @return the style for this symbol.
     */
    public int getStyle() {
	return style;
    }
}