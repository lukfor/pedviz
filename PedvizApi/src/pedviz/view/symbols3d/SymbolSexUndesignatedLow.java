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
import javax.media.j3d.QuadArray;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

/**
 * Draws the symbol for a individual with unknown sex.
 * 
 * @author lukas forer
 * 
 */
public class SymbolSexUndesignatedLow extends Symbol3D {

    public SymbolSexUndesignatedLow() {
	super(0);
    }

    @Override
    public GeometryArray[] createShape(Point3f p, float size, Color fill) {
	GeometryArray[] result = new GeometryArray[5];
	result[0] = buildQuad(p, size, fill);
	int i = 1;
	int c = 1;
	if (colors != null) {
	    for (Color subColor : colors) {
		if (subColor != null) {
		    Point3f p0 = new Point3f(p);
		    p0.y += size * c;
		    result[i] = buildQuad(p0, size, subColor);
		    c++;
		} else {
		    result[i] = null;
		}
		i++;
	    }
	}
	return result;
    }

    private QuadArray buildQuad(Point3f p, float size, Color c) {
	QuadArray quadArray = new QuadArray(8, QuadArray.COORDINATES
		| QuadArray.COLOR_4);
	quadArray.setColor(0, new Color4f(c));
	quadArray.setCoordinate(0, new Point3f(p.x, p.y - size / 2, p.z));

	quadArray.setColor(1, new Color4f(c));
	quadArray.setCoordinate(1, new Point3f(p.x - size / 2, p.y, p.z));

	quadArray.setColor(2, new Color4f(c));
	quadArray.setCoordinate(2, new Point3f(p.x, p.y + size / 2, p.z));
	quadArray.setColor(3, new Color4f(c));
	quadArray.setCoordinate(3, new Point3f(p.x + size / 2, p.y, p.z));
	quadArray.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
	return quadArray;
    }

    @Override
    public int getPriority() {
	return 0;
    }

}
