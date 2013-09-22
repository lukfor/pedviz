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

package pedviz.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;
import pedviz.graph.GraphMetaData;

/**
 * This Loader class allow you to load a graph from a ped-file.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class PedGraphLoader extends FileGraphLoader {

    private String[] data;

    /**
     * Creates a new PedGraphLoader object.
     * 
     * @param filename
     *                filename
     * @param separator
     *                Character which separates the values
     */
    public PedGraphLoader(String filename) {
	this(filename, false);
    }

    public PedGraphLoader(String filename, boolean asResource) {
	super(filename, asResource);
    }

    protected Node parseLine(String line) {

	int colFam = 0;
	int colId = 1;
	int colIdDad = 2;
	int colIdMom = 3;
	int colSex = 4;

	String[] columns = line.trim().split("\\s+");
	if (columns.length != data.length) {
	    return null;
	}
	if (columns.length >= 5) {
	    String id = null;
	    String idMom = null;
	    String idDad = null;
	    id = columns[colId];
	    if (id.equals("")) {
		return null;
	    }

	    if (!columns[colIdMom].equals(""))
		idMom = columns[colIdMom];

	    if (!columns[colIdDad].equals(""))
		idDad = columns[colIdDad];

	    Node node = new Node(id);
	    node.setUserData("PID", id);

	    node.setUserData("FAM", columns[colFam]);
	    node.setFamId(columns[colFam]);

	    node.setUserData("DAD", idDad);
	    node.setIdDad(idDad);

	    node.setUserData("MOM", idMom);
	    node.setIdMom(idMom);

	    if (!columns[colSex].equals("")) {
		node.setUserData("SEX", columns[colSex]);
	    }

	    for (int i = 5; i < columns.length; i++) {
		node.setUserData(data[i], columns[i]);

	    }

	    return node;
	}
	return null;
    }

    @Override
    protected GraphMetaData parseMetaData(InputStream is) throws GraphIOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	try {
	    String line = reader.readLine();
	    reader.close();

	    // check first line
	    String[] columns = line.trim().split("\\s+");
	    int count = columns.length;
	    if (columns.length < 5) {
		throw new GraphIOException("Invalid file-format.");
	    }
	    data = new String[count];
	    data[0] = "FAM";
	    data[1] = "PID";
	    data[2] = "DAD";
	    data[3] = "MOM";
	    data[4] = "SEX";
	    for (int i = 5; i < count; i++) {
		data[5] = "TRAIT_" + (i - 4);
	    }
	    return new GraphMetaData(data, "FAM", "PID", "DAD", "MOM", "SEX", "1","2");
	} catch (Exception e) {
	    throw new GraphIOException(e);
	}
    }

    @Override
    protected Vector<Node> parseNodes(InputStream is) throws GraphIOException {
	Vector<Node> result = new Vector<Node>();
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	int i = 0;
	try {
	    String row = null;
	    while ((row = reader.readLine()) != null) {
		i++;
		Node node = parseLine(row);
		if (node != null) {
		    result.add(node);
		} else {
		    throw new GraphIOException("Line " + i + ": nvalid data.");
		}
	    }
	    reader.close();
	} catch (GraphIOException e) {
	    throw e;
	} catch (Exception e) {
	    throw new GraphIOException(e);
	}
	return result;
    }

}
