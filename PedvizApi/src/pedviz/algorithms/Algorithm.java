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

package pedviz.algorithms;

import javax.swing.event.ChangeListener;

/**
 * Implementations of this interface represent a graph algorithm. It can be run
 * as a thread and the registered ChangeListener will be notified about the
 * progress.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public interface Algorithm extends Runnable {

    /**
     * Returns a short description of the algorithm.
     * 
     * @return a short description of the algorithm
     */
    public String getMessage();

    /**
     * Returns the progress in percents from 0 to 100
     * 
     * @return the progress in percents
     */
    public int getPercentComplete();

    /**
     * Registers a ChangeListener. It will be notified about the progress of the
     * algorithm.
     * 
     * @param l
     *                Change
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Starts the algorithm.
     */
    public void run();
}
