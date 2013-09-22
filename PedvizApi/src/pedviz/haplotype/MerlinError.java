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
package pedviz.haplotype;

/**
 * Contains informations about a Error from a Merlin-Error file.
 * 
 * @author Luki
 * 
 */
public class MerlinError {
    private Object id;
    private String marker;
    private double ratio;

    /**
     * Creates a new MerlinError object with the given id and the given marker.
     * 
     * @param id
     *                id
     * @param marker
     *                marker
     */
    public MerlinError(Object id, String marker) {
	this.id = id;
	this.marker = marker;
    }

    /**
     * Returns the id.
     * 
     * @return the id.
     */
    public Object getId() {
	return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *                the id.
     */
    public void setId(Object id) {
	this.id = id;
    }

    /**
     * Returns the marker.
     * 
     * @return the marker.
     */
    public String getMarker() {
	return marker;
    }

    /**
     * Sets the marker.
     * 
     * @param marker
     *                the marker.
     */
    public void setMarker(String marker) {
	this.marker = marker;
    }

    /**
     * Returns the ratio.
     * 
     * @return the ratio.
     */
    public double getRatio() {
	return ratio;
    }

    /**
     * Sets the ratio.
     * 
     * @param ratio
     *                the ratio.
     */
    public void setRatio(double ratio) {
	this.ratio = ratio;
    }
}
