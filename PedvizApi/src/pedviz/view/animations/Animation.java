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

package pedviz.view.animations;

import pedviz.view.GraphView;

/**
 * This abstract class is a superclass for animations on a GraphView.
 * 
 * @author Luki
 * 
 */
public abstract class Animation implements Runnable {
    GraphView graphView;

    Thread thread;

    boolean pulsing = false;

    int interval = 500;

    /**
     * Creates a new animation for the given GraphView object.
     * 
     * @param graphView
     */
    public Animation(GraphView graphView) {
	this.graphView = graphView;
    }

    /**
     * This method will be executed when the timer is expired.
     * 
     * @param graphView
     */
    public abstract void pulse(GraphView graphView);

    public void run() {
	while (pulsing) {
	    try {
		Thread.sleep(interval);
	    } catch (InterruptedException e) {
		System.out.println("interrupted");
		pulsing = false;
	    }
	    if (pulsing) {
		pulse(graphView);
	    }
	}
    }

    /**
     * Starts the animation.
     */
    public void start() {
	if (!pulsing) {
	    pulsing = true;
	    thread = new Thread(this);
	    thread.setPriority(Thread.NORM_PRIORITY);
	    thread.start();
	}
    }

    /**
     * Stops the animation.
     */
    public void stop() {
	pulsing = false;
	thread = null;
    }

    /**
     * Returns the interval of the timer.
     * 
     * @return the interval of the timer.
     */
    public int getInterval() {
	return interval;
    }

    /**
     * Sets the interval of the timer.
     * 
     * @param interval
     *                the interval of the timer.
     */
    public void setInterval(int interval) {
	this.interval = interval;
    }
}
