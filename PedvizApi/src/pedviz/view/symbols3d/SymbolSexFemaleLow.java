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
import javax.media.j3d.LineArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.TriangleFanArray;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

/**
 * Draws the symbol for a female individual.
 * 
 * @author lukas forer
 * 
 */
public class SymbolSexFemaleLow extends Symbol3D {

    public SymbolSexFemaleLow() {
	super(0);
    }

    @Override
    public GeometryArray[] createShape(Point3f p, float size, Color fill) {
	GeometryArray[] result = new GeometryArray[5];
	result[0] = buildCircle(p, size, fill);
	int i = 1;
	int c = 1;
	if (colors != null) {
	    for (Color subColor : colors) {
		if (subColor != null) {
		    Point3f p0 = new Point3f(p);
		    p0.y += size * c;
		    result[i] = buildCircle(p0, size, subColor);
		    c++;
		} else {
		    result[i] = null;
		}
		i++;
	    }
	}
	return result;
    }

    @Override
    public int getPriority() {
	return 0;
    }

    private GeometryArray buildCircle(Point3f p, float size, Color color) {
	int segm = 8;
	float radius = size / 2f;
	Point3f[] points = new Point3f[segm];
	Color4f[] colors = new Color4f[segm];
	int step = 360 / (segm - 1);
	for (int deg = 0; deg < 360; deg += step) {
	    double angle = Math.toRadians(deg);
	    colors[deg / 45] = new Color4f(color);
	    points[deg / 45] = new Point3f();
	    points[deg / 45].x = radius * (float) Math.sin(angle) + p.x;
	    points[deg / 45].y = radius * (float) Math.cos(angle) + p.y;
	    points[deg / 45].z += p.z;
	}
	points[segm - 1].set(points[0]);
	int[] vertex = new int[] { segm };
	TriangleFanArray tfa = new TriangleFanArray(segm, LineArray.COORDINATES
		| QuadArray.COLOR_4, vertex);
	tfa.setCoordinates(0, points);
	tfa.setColors(0, colors);
	tfa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
	return tfa;
    }

}
