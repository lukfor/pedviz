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
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

/**
 * Draws the symbol for an adopted individual.
 * 
 * @author lukas forer
 * 
 */
public class SymbolAdopted3d extends Symbol3D {

    public SymbolAdopted3d() {
	super(0);
    }

    public SymbolAdopted3d(int style) {
	super(style);
    }

    @Override
    public GeometryArray[] createShape(Point3f position, float size, Color fill) {
	GeometryArray array = ShapeCreator3D.createBox(position.x - size / 2f
		- size / 10f / 2f, position.y, position.z, size / 20f,
		size + 2f, size + 2f);
	GeometryArray array1 = ShapeCreator3D.createBox(position.x + size / 2f
		+ size / 10f / 2f, position.y, position.z, size / 20f,
		size + 2f, size + 2f);
	Color4f color = new Color4f(Color.DARK_GRAY);
	array.setColors(0, ShapeCreator3D.getColorArray(color, array
		.getVertexCount()));
	array1.setColors(0, ShapeCreator3D.getColorArray(color, array1
		.getVertexCount()));

	GeometryArray[] result = new GeometryArray[2];
	result[0] = array;
	result[1] = array1;
	return result;
    }

    @Override
    public int getPriority() {
	return 0;
    }
}
