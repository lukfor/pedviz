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
import java.util.HashMap;
import java.util.Vector;

/**
 * Contains haplotypes for each node in a graph.
 * 
 * @author Luki
 * 
 */
public class Haplotypes {
    protected HashMap<Object, Color> founderColors;
    protected HashMap<Object, Vector<HaplotypeItem>> datas;
    protected HashMap<Object, Boolean> founders;
    protected Vector<String> markers;
    protected Vector<String> pos;
    protected Vector<String> chr;
    protected Vector<Integer> visibles;
    protected HashMap<Object, MerlinError> errors;
    private boolean hideAll = false;

    /**
     * Creates a Haplotypes object.
     */
    public Haplotypes() {
	founderColors = new HashMap<Object, Color>();
	datas = new HashMap<Object, Vector<HaplotypeItem>>();
	markers = new Vector<String>();
	pos = new Vector<String>();
	chr = new Vector<String>();
	founders = new HashMap<Object, Boolean>();
	visibles = new Vector<Integer>();
	errors = new HashMap<Object, MerlinError>();
    }

    /**
     * Returns the HaplotypeItem for the node with the given id.
     * 
     * @param id
     * @return Collection of all HaplotypeItem for the node with the given id.
     */
    public Vector<HaplotypeItem> getData(Object id) {
	return datas.get(id);
    }

    /**
     * Returns a collection of all markers in this haplotype.
     * 
     * @return collection of all markers.
     */
    public Vector<String> getMarkers() {
	return markers;
    }

    /**
     * Returns true, if the given marker is visible.
     * 
     * @param marker
     * @return true, if the given marker is visible.
     */
    public boolean isMarkerVisible(int marker) {
	if (visibles.isEmpty() && !hideAll) {
	    return true;
	} else if (hideAll) {
	    return false;
	} else {
	    return visibles.contains(marker);
	}
    }

    /**
     * Returns the number of visible markers.
     * 
     * @return number of visible markers.
     */
    public int getVisibleSize() {
	if (visibles.isEmpty()) {
	    return markers.size();
	} else if (hideAll) {
	    return 0;
	} else {
	    return visibles.size();
	}
    }

    /**
     * Hides all markers.
     */
    public void hideAllMarkers() {
	hideAll = true;
	visibles.clear();
    }

    /**
     * Shows all markers.
     */
    public void showAllMarkers() {
	hideAll = false;
	visibles.clear();
    }

    /**
     * Shows the given marker.
     * 
     * @param marker
     *                marker.
     */
    public void showMarker(int marker) {
	hideAll = false;
	visibles.add(marker);
    }

    /**
     * Returns the number of all markers.
     * 
     * @return number of all markers.
     */
    public int getSize() {
	return markers.size();
    }

    /**
     * Returns a collection of all chrom. positions.
     * 
     * @return a collection of all chrom. positions
     */
    public Vector<String> getPos() {
	return pos;
    }

    /**
     * Returns all errors for the node with the given id.
     * 
     * @return all errors for the node with the given id.
     */
    public HashMap<Object, MerlinError> getErrors() {
	return errors;
    }

    /**
     * Find patterns.
     */
    protected void findPatterns() {
	for (Object id : datas.keySet()) {
	    if (!founders.get(id)) {
		int lastIndex = 0;
		int lastIndexB = 0;
		boolean useDad = false, useMom = false, found = false;
		boolean useDadB = false, useMomB = false, foundB = false;
		for (int i = 0; i < datas.get(id).size(); i++) {
		    HaplotypeItem item = datas.get(id).get(i);

		    HaplotypeItem dad = datas.get(item.getDadId()).get(i);

		    if (!found) {
			if (!dad.getDad().equals(dad.getMom())) {
			    if (item.getDad().equals(dad.getDad())) {
				found = useDad = true;
				useMom = false;
			    }
			    if (item.getDad().equals(dad.getMom())) {
				found = useMom = true;
				useDad = false;
			    }
			}
		    } else {
			if (useDad) {
			    if (!item.getDad().equals(dad.getDad())) {
				for (int j = lastIndex; j < i; j++) {
				    Color color = datas.get(item.getDadId())
					    .get(j).getColorDad();
				    datas.get(id).get(j).setColorDad(color);
				}
				useDad = false;
				useMom = !dad.getDad().equals(dad.getMom());
				found = false;
				lastIndex = i;
			    }
			} else {
			    if (!item.getDad().equals(dad.getMom())) {
				for (int j = lastIndex; j < i; j++) {
				    Color color = datas.get(item.getDadId())
					    .get(j).getColorMom();
				    datas.get(id).get(j).setColorDad(color);
				}
				useDad = !dad.getDad().equals(dad.getMom());
				useMom = false;
				found = false;
				lastIndex = i;
			    }

			}
		    }

		    HaplotypeItem mom = datas.get(item.getMomId()).get(i);

		    if (!foundB) {
			if (!mom.getDad().equals(mom.getMom())) {
			    if (item.getMom().equals(mom.getDad())) {
				foundB = useDadB = true;
				useMomB = false;
			    }
			    if (item.getMom().equals(mom.getMom())) {
				foundB = useMomB = true;
				useDadB = false;
			    }
			}
		    } else {
			if (useDadB) {
			    if (!item.getMom().equals(mom.getDad())) {
				for (int j = lastIndexB; j < i; j++) {
				    Color color = datas.get(item.getMomId())
					    .get(j).getColorDad();
				    datas.get(id).get(j).setColorMom(color);
				}
				useDadB = false;
				useMomB = !mom.getDad().equals(mom.getMom());
				foundB = false;
				lastIndexB = i;
			    }
			} else {
			    if (!item.getMom().equals(mom.getMom())) {
				for (int j = lastIndexB; j < i; j++) {
				    Color color = datas.get(item.getMomId())
					    .get(j).getColorMom();
				    datas.get(id).get(j).setColorMom(color);
				}
				useDadB = !mom.getDad().equals(mom.getMom());
				useMomB = false;
				foundB = false;
				lastIndexB = i;
			    }

			}
		    }

		}

		if (useDad) {
		    for (int j = lastIndex; j < datas.get(id).size(); j++) {
			Color color = datas
				.get(datas.get(id).get(j).getDadId()).get(j)
				.getColorDad();
			datas.get(id).get(j).setColorDad(color);
		    }
		}
		if (useMom) {
		    for (int j = lastIndex; j < datas.get(id).size(); j++) {
			Color color = datas
				.get(datas.get(id).get(j).getDadId()).get(j)
				.getColorMom();
			datas.get(id).get(j).setColorDad(color);

		    }
		}

		if (useDadB) {
		    for (int j = lastIndexB; j < datas.get(id).size(); j++) {
			Color color = datas
				.get(datas.get(id).get(j).getMomId()).get(j)
				.getColorDad();
			datas.get(id).get(j).setColorMom(color);
		    }
		}
		if (useMomB) {
		    for (int j = lastIndexB; j < datas.get(id).size(); j++) {
			Color color = datas
				.get(datas.get(id).get(j).getMomId()).get(j)
				.getColorMom();
			datas.get(id).get(j).setColorMom(color);

		    }
		}
	    }
	}
    }

}
