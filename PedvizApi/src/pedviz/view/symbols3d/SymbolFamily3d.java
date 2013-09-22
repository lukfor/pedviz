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
import java.awt.geom.Ellipse2D;

import javax.media.j3d.GeometryArray;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

/**
 * Draws the symbol for a family node.
 * 
 * @author lukas forer
 * 
 */
public class SymbolFamily3d extends Symbol3D {
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
    public SymbolFamily3d(int mode, Object male, Object female) {
	super(0);
	this.mode = mode;
	this.male = male;
	this.female = female;
    }

    @Override
    public GeometryArray[] createShape(Point3f p, float size, Color fill) {
	GeometryArray array = ShapeCreator3D.createBox(p.x, p.y, p.z, size);
	Color4f color = new Color4f(fill);
	array.setColors(0, ShapeCreator3D.getColorArray(color, array
		.getVertexCount()));
	GeometryArray[] result = new GeometryArray[5];
	result[0] = array;
	return result;

    }

    @Override
    public int getPriority() {
	return 0;
    }
}
