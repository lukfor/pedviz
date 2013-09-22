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
package pedviz.algorithms.magiceye;

import java.util.Vector;

import javax.vecmath.Point3d;

public class CrossedgeDataVector {
    int length;

    Vector<MagicEyeNodeView> targets; // Zielknoten

    Vector<String> strings; // Beschriftungen

    Vector<Point3d> points; // Positionen für Beschriftungen

    Vector<Integer> ids; // IDs

    Point3d dummyPoint = new Point3d();

    Integer dummyID = new Integer(0);

    public CrossedgeDataVector() {
	length = 0;
	targets = new Vector<MagicEyeNodeView>();
	strings = new Vector<String>();
	points = new Vector<Point3d>();
	ids = new Vector<Integer>(0, 1);
    }

    public void add(MagicEyeNodeView n, String s, Point3d p, Integer id) {
	targets.add(n);
	strings.add(s);
	points.add(p);
	ids.add(id);
	length++;
    }

    public MagicEyeNodeView getTarget(int i) {
	return targets.get(i);
    }

    public MagicEyeNodeView getTargetByID(int id) {
	return targets.get(ids.indexOf(new Integer(id)));
    }

    public String getString(int i) {
	return strings.get(i);
    }

    public String getStringByID(int id) {
	return strings.get(ids.indexOf(new Integer(id)));
    }

    public Point3d getPoint(int i) {
	return points.get(i);
    }

    public Point3d getPointByID(int id) {
	return points.get(ids.indexOf(new Integer(id)));
    }

    public int getID(int i) {
	return ((Integer) ids.get(i)).intValue();
    }

    public boolean containsTarget(MagicEyeNodeView n) {
	return targets.contains(n);
    }

    public int getIndex(int id) {
	return ids.indexOf(new Integer(id));
    }

    public void clear() {
	targets.clear();
	strings.clear();
	points.clear();
	ids.clear();
	length = 0;
    }
}