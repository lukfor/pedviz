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

package pedviz.view.effects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class implements the FishEyeEffect.
 * 
 * @author Luki
 * 
 */
public class FisheyeEffect extends Effect {

    private float dx = 5f;

    private float dy = 5f;

    private float sz = 2f;

    private boolean m_distortX = false;

    private boolean m_distortY = false;

    private float minSize = 0f;

    /**
     * Creates a new FishEyeEffect with the given values.
     * 
     * @param dx
     * @param dy
     * @param sz
     */
    public FisheyeEffect(float dx, float dy, float sz) {
	this.dx = dx;
	this.dy = dy;
	this.sz = sz;
	m_distortX = dx > 0;
	m_distortY = dy > 0;
    }

    public Point2D.Float transformPoint(float x, float y) {
	if (getFocus() == null | getBounds() == null) {
	    return new Point2D.Float(x, y);
	}
	float newX = (float) distortX(x, getFocus(), getBounds());
	float newY = (float) distortY(y, getFocus(), getBounds());
	return new Point2D.Float(newX, newY);
    }

    public float transformSize(float size, float x, float y, float newX,
	    float newY) {
	if (getFocus() == null | getBounds() == null) {
	    return size;
	}
	Rectangle2D box = new Rectangle2D.Double(x - size / 2f, y + size / 2f,
		size, size);
	return Math
		.max(minSize, size
			* (float) distortSize(box, newX, newY, getFocus(),
				getBounds()));
    }

    private double fisheye(double x, double a, double d, double min, double max) {
	if (d != 0) {
	    boolean left = x < a;
	    double v, m = (left ? a - min : max - a);
	    if (m == 0)
		m = max - min;
	    v = Math.abs(x - a) / m;
	    v = (d + 1) / (d + (1 / v));
	    return (left ? -1 : 1) * m * v + a;
	} else {
	    return x;
	}
    }

    protected double distortSize(Rectangle2D bbox, double x, double y,
	    Point2D anchor, Rectangle2D bounds) {
	if (!m_distortX && !m_distortY)
	    return 1.;
	double fx = 1, fy = 1;
	if (m_distortX) {
	    double ax = anchor.getX();
	    double minX = bbox.getMinX();
	    double maxX = bbox.getMaxX();
	    double xx = (Math.abs(minX - ax) > Math.abs(maxX - ax) ? minX
		    : maxX);
	    if (xx < bounds.getMinX() || xx > bounds.getMaxX())
		xx = (xx == minX ? maxX : minX);
	    fx = fisheye(xx, ax, dx, bounds.getMinX(), bounds.getMaxX());
	    fx = Math.abs(x - fx) / bbox.getWidth();
	}

	if (m_distortY) {
	    double ay = anchor.getY();
	    double minY = bbox.getMinY();
	    double maxY = bbox.getMaxY();
	    double yy = (Math.abs(minY - ay) > Math.abs(maxY - ay) ? minY
		    : maxY);
	    if (yy < bounds.getMinY() || yy > bounds.getMaxY())
		yy = (yy == minY ? maxY : minY);
	    fy = fisheye(yy, ay, dy, bounds.getMinY(), bounds.getMaxY());
	    fy = Math.abs(y - fy) / bbox.getHeight();
	}

	double sf = (!m_distortY ? fx : (!m_distortX ? fy : Math.min(fx, fy)));
	if (Double.isInfinite(sf) || Double.isNaN(sf)) {
	    return 1.;
	} else {
	    return sz * sf;
	}
    }

    protected double distortX(double x, Point2D anchor, Rectangle2D bounds) {
	return fisheye(x, anchor.getX(), dx, bounds.getMinX(), bounds.getMaxX());
    }

    protected double distortY(double y, Point2D anchor, Rectangle2D bounds) {
	return fisheye(y, anchor.getY(), dy, bounds.getMinY(), bounds.getMaxY());
    }

    /**
     * Returns the minimum size of a node.
     * 
     * @return the minimum size of a node.
     */
    public float getMinSize() {
	return minSize;
    }

    /**
     * Sets the minimum size of a node.
     * 
     * @param minSize
     *                the minimum size of a node.
     */
    public void setMinSize(float minSize) {
	this.minSize = minSize;
    }

    
    public void setDX(float dx){
	this.dx = dx;
    }
    
    public void setDY(float dy){
	this.dy = dy;
    }
    
    public void setSZ(float sz){
	this.sz = sz;
    }
    
}
