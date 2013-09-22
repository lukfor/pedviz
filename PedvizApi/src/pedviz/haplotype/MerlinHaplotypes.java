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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

/**
 * The haplotype visualization is based on the Merlin data format . The Merlin
 * error output file ({@link MerlinErrorChecking}) can be used to highlight
 * errors in the haplotype/genotype data.
 * 
 * @author Luki
 * 
 */
public class MerlinHaplotypes extends Haplotypes {

    /**
     * Imports the haplotype information from the given *.chr and *.map file.
     * 
     * @param filename
     *                *.chr file
     * @param map
     *                *.map file
     */
    public MerlinHaplotypes(String filename, String map) {
	this(filename, map, false);
    }

    /**
     * Imports the haplotype information from the given *.chr and *.map file.
     * 
     * @param filename
     *                *.chr file
     * @param map
     *                *.map file
     * @param resource
     *                load files from a resource.
     */
    public MerlinHaplotypes(String filename, String map, boolean resource) {
	super();
	BufferedReader reader = null;
	FileInputStream fis = null;
	Vector<String> ids = new Vector<String>();
	Vector<String> idsMom = new Vector<String>();
	Vector<String> idsDad = new Vector<String>();
	int mark = 0;
	try {
	    if (resource) {
		reader = new BufferedReader(new InputStreamReader(getClass()
			.getResourceAsStream(filename)));
	    } else {
		File f = new File(filename);
		fis = new FileInputStream(f);
		reader = new BufferedReader(new InputStreamReader(fis));
	    }
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		if (!line.equals("")) {
		    if (!line.startsWith(" ")) {
			// Family Name
		    } else {
			// Data

			String[] tiles = line.trim().split("\\s+");

			if (tiles[1].startsWith("(")) {
			    // Header "ID (MOM,DAD)" or "ID (F)"
			    int columns = tiles.length / 2;
			    ids.clear();
			    idsMom.clear();
			    idsDad.clear();
			    for (int i = 0; i < columns; i++) {
				String id = tiles[i * 2];
				if (tiles[i * 2 + 1].equals("(F)")) {
				    founders.put(id, true);
				    founderColors.put(id + "A", new Color(
					    (int) (Math.random() * 255),
					    (int) (Math.random() * 255),
					    (int) (Math.random() * 255)));
				    founderColors.put(id + "B", new Color(
					    (int) (Math.random() * 255),
					    (int) (Math.random() * 255),
					    (int) (Math.random() * 255)));
				    idsDad.add("");
				    idsMom.add("");
				} else {
				    founders.put(id, false);
				    String temp[] = tiles[i * 2 + 1].split(",");
				    idsMom.add(temp[0].replace("(", ""));
				    idsDad.add(temp[1].replace(")", ""));

				}
				ids.add(id);
				Vector<HaplotypeItem> data = new Vector<HaplotypeItem>();
				datas.put(id, data);
			    }
			    mark = 0;
			} else {
			    int columns = tiles.length / 3;
			    for (int i = 0; i < columns; i++) {
				Object id = ids.get(i);
				Vector<HaplotypeItem> data = datas.get(id);
				Color color1 = Color.RED;
				Color color2 = Color.RED;
				if (founders.get(id)) {
				    color1 = founderColors.get(id + "A");
				    color2 = founderColors.get(id + "B");
				}

				String mom = tiles[i * 3].split(",")[0];
				String dad = tiles[i * 3 + 2];
				if (dad.split(",").length > 1) {
				    dad = dad.split(",")[0].substring(1);
				}

				HaplotypeItem temp = new HaplotypeItem(mom,
					dad, color1, color2);
				temp.setDadId(idsDad.get(i));
				temp.setMomId(idsMom.get(i));
				data.add(temp);

			    }
			    if (!markers.contains("Marker_" + (mark + 1))) {
				markers.add("Marker_" + (mark + 1));
			    }
			    mark++;
			}
		    }
		}
	    }

	} catch (IOException e) {
	    System.out.println(e.getMessage());
	} finally {
	    if (reader != null) {
		try {
		    reader.close();
		} catch (IOException ioe) {
		    System.out.println(ioe.getMessage());
		}
	    }
	}

	// load map-file
	if (map != null) {
	    try {
		if (resource) {
		    reader = new BufferedReader(new InputStreamReader(
			    getClass().getResourceAsStream(map)));
		} else {
		    File f = new File(map);
		    fis = new FileInputStream(f);
		    reader = new BufferedReader(new InputStreamReader(fis));
		}
		String line = null;
		int i = 0;
		while ((line = reader.readLine()) != null) {
		    if (!line.equals("") && !line.startsWith("#")) {

			String[] tiles = line.trim().split("\\s+");
			chr.add(tiles[0]);
			pos.add(tiles[2]);
			markers.set(i, tiles[1]);
			i++;
		    }
		}

	    } catch (IOException e) {
		System.out.println(e.getMessage());
	    } finally {
		if (reader != null) {
		    try {
			reader.close();
		    } catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		    }
		}
	    }
	}
	findPatterns();
    }

    /**
     * Sets a collection of errors (MerlinErrorChecking).
     * 
     * @param errors
     *                a collection of errors.
     */
    public void setErrors(HashMap<Object, MerlinError> errors) {
	this.errors = errors;
    }

}
