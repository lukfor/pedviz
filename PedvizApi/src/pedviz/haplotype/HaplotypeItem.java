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

import java.awt.Color;

/**
 * Contains informations about a haplotype item.
 * 
 * @author Luki
 * 
 */
public class HaplotypeItem {
    private String mom;
    private String dad;
    private String momId;
    private String dadId;
    private Color colorMom;
    private Color colorDad;

    /**
     * Creates a new HaplotypeItem with the given values.
     * 
     * @param mom
     * @param dad
     * @param colorMom
     * @param colorDad
     */
    public HaplotypeItem(String mom, String dad, Color colorMom, Color colorDad) {
	this.mom = mom;
	this.dad = dad;
	this.colorMom = colorMom;
	this.colorDad = colorDad;
    }

    public String getMom() {
	return mom;
    }

    public void setMom(String mom) {
	this.mom = mom;
    }

    public String getDad() {
	return dad;
    }

    public void setDad(String dad) {
	this.dad = dad;
    }

    public Color getColorMom() {
	return colorMom;
    }

    public void setColorMom(Color color) {
	this.colorMom = color;
    }

    public Color getColorDad() {
	return colorDad;
    }

    public void setColorDad(Color color) {
	this.colorDad = color;
    }

    public String getMomId() {
	return momId;
    }

    public void setMomId(String momId) {
	this.momId = momId;
    }

    public String getDadId() {
	return dadId;
    }

    public void setDadId(String dadId) {
	this.dadId = dadId;
    }
}
