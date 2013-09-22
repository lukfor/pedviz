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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * It's possible to process a Merlin error output file to highlight errors in
 * the haplotype/genotype data.
 * 
 * @author Luki
 * 
 */
public class MerlinErrorChecking {

    private HashMap<Object, MerlinError> errors;

    /**
     * Loads the given Merlin-Error file.
     * 
     * @param filename
     *                Merlin-Error file
     */
    public MerlinErrorChecking(String filename) {
	this(filename, false);
    }

    /**
     * Loads the given Merlin-Error file.
     * 
     * @param filename
     *                Merlin-Error file
     * @param resource
     *                Loads the file from a resource (jar,..).
     */
    public MerlinErrorChecking(String filename, boolean resource) {
	errors = new HashMap<Object, MerlinError>();
	BufferedReader reader = null;
	FileInputStream fis = null;

	try {
	    if (resource) {
		reader = new BufferedReader(new InputStreamReader(getClass()
			.getResourceAsStream(filename)));
	    } else {
		File f = new File(filename);
		fis = new FileInputStream(f);
		reader = new BufferedReader(new InputStreamReader(fis));
	    }
	    String line = reader.readLine();
	    while ((line = reader.readLine()) != null) {
		if (!line.equals("")) {
		    String[] tiles = line.trim().split("\\s+");
		    String famId = tiles[0];
		    String id = tiles[1];
		    String marker = tiles[2];
		    String ratio = tiles[3];
		    errors.put(id, new MerlinError(id, marker));
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

    /**
     * Returns a collection of all errors.
     * 
     * @return a collection of all errors.
     */
    public HashMap<Object, MerlinError> getErrors() {
	return errors;
    }

}
