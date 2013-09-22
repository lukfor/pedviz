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

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import pedviz.view.j3d.BoxGenerator;
import pedviz.view.j3d.GeometryData;
import pedviz.view.j3d.GeometryGenerator;
import pedviz.view.j3d.SphereGenerator;

/**
 * A very speedy NodeCreator implementation for about 35.000 nodes. Use some
 * fast GeometryGenerators from j3d.org.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class ShapeCreator3D {

    private static int j3d_format = GeometryArray.COORDINATES
	    | GeometryArray.NORMALS | GeometryArray.COLOR_4;

    /**
     * Returns a box.
     * 
     * @param x
     *                position on the x-axis.
     * @param y
     *                position on the y-axis.
     * @param z
     *                position on the z-axis.
     * @param size
     *                radius
     * @return Geometry array for a box.
     */
    public static GeometryArray createBox(float x, float y, float z, float size) {
	return createBox(x, y, z, size, size, size, 0);
    }

    public static GeometryArray createBox(float x, float y, float z,
	    float sizeX, float sizeY, float sizeZ) {
	return createBox(x, y, z, sizeX, sizeY, sizeZ, 0);
    }

    public static GeometryArray createBox(float x, float y, float z,
	    float size, double rot) {
	return createBox(x, y, z, size, size, size, rot);
    }

    public static GeometryArray createBox(float x, float y, float z,
	    float sizeX, float sizeY, float sizeZ, double rotZ) {
	GeometryGenerator generatorBox = new BoxGenerator(sizeX, sizeY, sizeZ);
	GeometryData dataBox = new GeometryData();
	dataBox.geometryType = GeometryData.INDEXED_TRIANGLE_STRIPS;
	dataBox.geometryComponents = GeometryData.NORMAL_DATA;

	generatorBox.generate(dataBox);

	IndexedTriangleStripArray array = new IndexedTriangleStripArray(
		dataBox.vertexCount, j3d_format
			| GeometryArray.TEXTURE_COORDINATE_2,
		dataBox.indexesCount, dataBox.stripCounts);
	if (rotZ == 0)
	    array.setCoordinates(0, translate(dataBox.coordinates, x, y, z));
	else
	    array.setCoordinates(0, dataBox.coordinates);
	array.setCoordinateIndices(0, dataBox.indexes);
	array.setNormals(0, dataBox.normals);
	array.setNormalIndices(0, dataBox.indexes);
	array.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
	if (rotZ != 0) {
	    Transform3D trotZ = new Transform3D();
	    trotZ.rotZ(Math.PI / 4.0f);

	    for (int i = 0; i < array.getVertexCount(); i++) {
		Point3f p = new Point3f();
		array.getCoordinate(i, p);
		trotZ.transform(p);
		p.x += x;
		p.y += y;
		p.z += z;
		array.setCoordinate(i, p);
	    }
	}

	return array;
    }

    /**
     * Creates a color array with the given color and size.
     * 
     * @param color
     *                color
     * @param size
     *                vertex count
     * @return
     */
    public static Color4f[] getColorArray(Color4f color, int size) {
	Color4f[] result = new Color4f[size];
	for (int i = 0; i < size; i++)
	    result[i] = color;
	return result;
    }

    /**
     * Returns a sphere.
     * 
     * @param x
     *                position on the x-axis.
     * @param y
     *                position on the y-axis.
     * @param z
     *                position on the z-axis.
     * @param size
     *                radius
     * @return Geometry array for a sphere.
     */

    public static GeometryArray createSphere(float x, float y, float z,
	    float size) {
	return createSphere(x, y, z, size, false, 0, 8);
    }

    public static GeometryArray createSphere(float x, float y, float z,
	    float size, boolean half, double rotZ, int k) {

	GeometryGenerator generatorSphere = new SphereGenerator(size, k, half);
	GeometryData dataSphere = new GeometryData();
	dataSphere.geometryType = GeometryData.INDEXED_TRIANGLE_STRIPS;
	dataSphere.geometryComponents = GeometryData.NORMAL_DATA;
	generatorSphere.generate(dataSphere);

	IndexedTriangleStripArray array = new IndexedTriangleStripArray(
		dataSphere.vertexCount, j3d_format, dataSphere.indexesCount,
		dataSphere.stripCounts);

	// no rotation => translation
	if (rotZ == 0)
	    array.setCoordinates(0, translate(dataSphere.coordinates, x, y, z));
	else
	    array.setCoordinates(0, dataSphere.coordinates);

	array.setCoordinateIndices(0, dataSphere.indexes);
	array.setNormals(0, dataSphere.normals);
	array.setNormalIndices(0, dataSphere.indexes);
	array.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

	// rotation and translation
	if (rotZ != 0) {
	    Transform3D trotZ = new Transform3D();
	    trotZ.rotZ(rotZ);

	    for (int i = 0; i < array.getVertexCount(); i++) {
		Point3f p = new Point3f();
		array.getCoordinate(i, p);
		trotZ.transform(p);
		p.x += x;
		p.y += y;
		p.z += z;
		array.setCoordinate(i, p);
	    }
	}

	return array;
    }

    private static float[] translate(float[] coord, float x, float y, float z) {
	float[] result = new float[coord.length];
	for (int i = 0; i < coord.length; i += 3) {
	    result[i] = coord[i] + x;
	    result[i + 1] = coord[i + 1] + y;
	    result[i + 2] = coord[i + 2] + z;
	}
	return result;
    }

}
