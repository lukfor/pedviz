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
 * This class is the super class for symbols. If you write your own symbols,
 * don't extend this class. Use Symbol2D and Symbol3D as super classes.
 * 
 * @author lukas forer
 * 
 */
public abstract class Symbol implements Comparable {
    protected Color[] colors;

    /**
     * Returns the priority of the symbol. Symbols like SymbolSexMale and
     * SymbolSexFemale have a low priority (0) however the SymbolDeceased has a
     * higher priority (5). The priorities define the sequence for drawing the
     * symbols.
     * 
     * @return priority of the symbol
     */
    abstract public int getPriority();

    /**
     * 
     * @param colors
     */
    public void setColors(Color[] colors) {
	this.colors = colors;
    };

    /**
     * Compares two symbols.
     */
    public int compareTo(Object o) {
	return new Integer(getPriority()).compareTo(((Symbol) o).getPriority());
    }

    public Float getHeight() {
	return 0f;
    }

}
