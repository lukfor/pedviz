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
import java.util.HashMap;
import java.util.Vector;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.Node;
import pedviz.graph.GraphMetaData;

/**
 * <p>
 * This Loader class allow you to load a graph from a csv-file.
 * </p>
 * 
 * <p>
 * Are composed of a header and data:
 * </p>
 * <code>Id Fid Mid Sex aff trait
 * 1 1 2 3 2 2 4.4</code>
 * 
 * <p>
 * Id, mom and dad id are mandatory and no special field ordering is needed,
 * because of the required initial field mapping (Method: setSettings).
 * Additional fields (that maybe are available in the data), such as sex, traits
 * etc. are mapped as Rules. Different field separators can be definied. Family
 * id's are not used for the drawing process.
 * </p>
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class CsvGraphLoader extends FileGraphLoader {

    private String separator;

    private int colId = -1;
    private int colIdMom = -1;
    private int colIdDad = -1;

    private String[] colStrings;

    @Override
    protected GraphMetaData parseMetaData(InputStream is) throws GraphIOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	try {
	    String line = reader.readLine();
	    reader.close();
	    if (line != null) {
		colStrings = line.split(separator);

		if (getIdColumn() != null && getMomColumn() != null
			&& getDadColumn() != null) {
		    for (int i = 0; i < colStrings.length; i++) {
			// Id
			String colString = colStrings[i].toUpperCase();
			if (colString.equals(getIdColumn().toUpperCase()))
			    colId = i;

			// Mom
			if (colString.equals(getMomColumn().toUpperCase()))
			    colIdMom = i;

			// Dad
			if (colString.equals(getDadColumn().toUpperCase()))
			    colIdDad = i;
			attributes.add(colString);
		    }

		    if (colId == -1) {
			throw new GraphIOException("Column " + getIdColumn()
				+ " not found.");
		    }
		    if (colIdMom == -1) {
			throw new GraphIOException("Column " + getMomColumn()
				+ " not found.");
		    }
		    if (colIdDad == -1) {
			throw new GraphIOException("Column " + getDadColumn()
				+ " not found.");
		    }
		}

		return new GraphMetaData(colStrings, getFamColumn(), getIdColumn(),
			getDadColumn(), getMomColumn(), getSexColumn());

	    } else {
		throw new GraphIOException("no header found");
	    }
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
	    String row = reader.readLine();
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

    /**
     * Constructs a CsvGraphLoader
     * 
     * @param filename
     *                filename
     * @param separator
     *                Character which separates the values
     */
    public CsvGraphLoader(String filename, String separator) {
	this(filename, separator, false);
    }

    public CsvGraphLoader(String filename, String separator, boolean asResource) {
	super(filename, asResource);
	this.separator = separator;
    }

    private Node parseLine(String line) {

	String[] columns = line.split(separator);
	Object id = null;
	Object idMom = null;
	Object idDad = null;

	id = columns[colId];

	if (id.equals("")) {
	    return null;
	}

	if (!columns[colIdMom].equals("")) {
	    idMom = columns[colIdMom];
	}

	if (!columns[colIdDad].equals("")) {
	    idDad = columns[colIdDad];
	}

	Node node = new Node(id);
	node.setIdDad(idDad);
	node.setIdMom(idMom);

	for (int i = 0; i < columns.length; i++) {
	    String value = columns[i];
	    if (i == colIdMom) {
		node.setUserData(colStrings[i].toUpperCase(), idMom);
	    } else if (i == colIdDad) {
		node.setUserData(colStrings[i].toUpperCase(), idDad);
	    } else {
		Integer type = types.get(colStrings[i]);
		if (type != null) {
		    switch (type) {
		    case INTEGER:
			node
				.setUserData(colStrings[i], Integer
					.parseInt(value));
			break;
		    case DOUBLE:
			node.setUserData(colStrings[i], Double
				.parseDouble(value));
			break;
		    default:
			node.setUserData(colStrings[i], value);
		    }
		} else {
		    node.setUserData(colStrings[i], value);
		}
		if (getFamColumn() != null
			&& colStrings[i].toLowerCase().equals(
				getFamColumn().toLowerCase())) {
		    node.setFamId(value);
		}
	    }
	}
	return node;
    }

}
