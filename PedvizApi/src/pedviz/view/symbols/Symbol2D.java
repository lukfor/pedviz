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

import pedviz.view.NodeView;
import pedviz.view.Symbol;

/**
 * This class is the super class for all implemented symbols for the 2d
 * visualization. If you want implement your own Symbol: override the methode
 * drawSymbol. In this method you have all informations you need, so you can
 * draw a lot of funny stuff.
 * 
 * @author lukas forer
 * 
 */
public abstract class Symbol2D extends Symbol {

    /**
     * Draws the symbol.
     * 
     * @param g
     *                Graphic object
     * @param position
     *                position of the node
     * @param size
     *                size of the node
     * @param border
     *                border color
     * @param fill
     *                fill color
     * @param node
     *                Node object
     */
    abstract public void drawSymbol(Graphics2D g, Point2D.Float position,
	    float size, Color border, Color fill, NodeView nodeview);

}
