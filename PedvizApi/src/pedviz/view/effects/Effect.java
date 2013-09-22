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
 * This class is the super class for effects like FishEyeEffect.
 * 
 * @author Luki
 * 
 */
public class Effect {

    boolean autoUpdateOnMove = false;

    boolean autoUpdateOnDrag = false;

    private Point2D focus = new Point2D.Float(0, 0);

    private Rectangle2D bounds;

    private float speed = 3;

    /**
     * Returns the speed.
     * 
     * @return the speed.
     */
    public float getSpeed() {
	return speed;
    }

    /**
     * Sets the speed.
     * 
     * @param speed
     *                the speed.
     */
    public void setSpeed(float speed) {
	this.speed = speed;
    }

    public Effect() {

    }

    /**
     * Transforms the given points.
     * 
     * @param x
     * @param y
     * @return
     */
    public Point2D.Float transformPoint(float x, float y) {
	return new Point2D.Float(x, y);
    }

    /**
     * Transforms the given size. x,y are untransformed and newX, newY are the
     * transformed coordinates.
     * 
     * @param size
     *                size
     * @param x
     *                untransformed x coord.
     * @param y
     *                untransformed y coord.
     * @param newX
     *                transformed x coord.
     * @param newY
     *                transformed y coord.
     * @return
     */
    public float transformSize(float size, float x, float y, float newX,
	    float newY) {
	return size;
    }

    /**
     * Returns the bounds.
     * 
     * @return the bounds.
     */
    public Rectangle2D getBounds() {
	return bounds;
    }

    /**
     * Sets the bounds.
     * 
     * @param bounds
     *                the bounds.
     */
    public void setBounds(Rectangle2D bounds) {
	this.bounds = bounds;
    }

    /**
     * Returns true, when the focus point will be updated automatically on mouse
     * move.
     * 
     * @return true, when the focus point will be updated automatically on mouse
     *         move.
     */
    public boolean isAutoUpdateOnMove() {
	return autoUpdateOnMove;
    }

    public void setAutoUpdateOnMove(boolean autoUpdate) {
	this.autoUpdateOnMove = autoUpdate;
    }

    /**
     * Returns true, when the focus point will be updated automatically on mouse
     * drag.
     * 
     * @return true, when the focus point will be updated automatically on mouse
     *         drag.
     */
    public boolean isAutoUpdateOnDrag() {
	return autoUpdateOnDrag;
    }

    public void setAutoUpdateOnDrag(boolean autoUpdate) {
	this.autoUpdateOnDrag = autoUpdate;
    }

    /**
     * Returns the focus point.
     * 
     * @return the focus point.
     */
    public Point2D getFocus() {
	return focus;
    }

    /**
     * Sets the focus point.
     * 
     * @param x
     *                x-coordinate
     * @param y
     *                y-coordinate
     */
    public void setFocus(double x, double y) {
	if (getBounds() != null) {
	    if (x < getBounds().getMinX())
		x = getBounds().getMinX();

	    if (x > getBounds().getMaxX())
		x = getBounds().getMaxX();

	    if (y < getBounds().getMinY())
		y = getBounds().getMinY();

	    if (y > getBounds().getMaxY())
		y = getBounds().getMaxY();

	    focus = new Point2D.Double(x, y);
	}
    }

    /**
     * Moves the focus point by the given values.
     * 
     * @param dx
     *                delta x
     * @param dy
     *                delta y
     */
    public void moveFocus(double dx, double dy) {
	setFocus(focus.getX() + dx, focus.getY() + dy);
    }

}
