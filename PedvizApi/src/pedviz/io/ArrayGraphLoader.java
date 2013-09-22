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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.GraphMetaData;
import pedviz.graph.Node;

/**
 * This Loader class allow you to load a graph from a string array.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class ArrayGraphLoader implements GraphLoader {
    private String[] data;

    private String separator;

    private String format;

    private ArrayList<String> attributes;

    private HashMap<String, Integer> types;

    public static final int INTEGER = 0;

    public static final int DOUBLE = 1;


    private String idColumn = null;

    private String momColumn = null;

    private String dadColumn = null;

    private String famColumn = null;

    private String sexColumn = null;
    
    /**
     * Constructs a CsvGraphLoader
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param separator
     *                character which separates the values
     * @param data
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     */
    public ArrayGraphLoader(String format, String separator, String[] data) {
	this.data = data;
	this.format = format;
	this.separator = separator;
	this.attributes = new ArrayList<String>();
	this.types = new HashMap<String, Integer>();
    }

    /**
     * Sets the type for the given column.
     * 
     * @param column
     *                the name of the column.
     * @param type
     *                Type (STRING(default)/INTEGER/DOUBLE)
     */
    public void setColumnType(String column, int type) {
	types.put(column, type);
    }

    /**
     * Sets the field names, in which the id, mom's id and dad's id are saved. *
     * 
     * @param fam
     *                field name from the fam
     * 
     * @param id
     *                field name from the id
     * @param mom
     *                field name from the mom's id
     * @param dad
     *                field name from the dad's id
     * @param sex
     *                field name from the sex
     */
    public void setSettings(String fam, String id, String mom, String dad,
	    String sex) {
	famColumn = fam;
	idColumn = id;
	momColumn = mom;
	dadColumn = dad;
	sexColumn = sex;
    }

    /**
     * Sets the field names, in which the id, mom's id and dad's id are saved. *
     * 
     * @param fam
     *                field name from the fam
     * 
     * @param id
     *                field name from the id
     * @param mom
     *                field name from the mom's id
     * @param dad
     *                field name from the dad's id
     */
    public void setSettings(String fam, String id, String mom, String dad) {
	setSettings(fam, id, mom, dad, null);
    }

    /**
     * Sets the field names, in which the id, mom's id and dad's id are saved.
     * 
     * @param id
     *                field name from the id
     * @param mom
     *                field name from the mom's id
     * @param dad
     *                field name from the dad's id
     */
    public void setSettings(String id, String mom, String dad) {
	setSettings(null, id, mom, dad);
    }

    
    public void load(Graph graph) {
	int colId = -1;
	int colIdMom = -1;
	int colIdDad = -1;

	ArrayList<Object[]> myEdges = new ArrayList<Object[]>();

	graph.setName("Pedigree");

	String[] colStrings = format.split(separator);

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
	
	GraphMetaData metaData = new GraphMetaData(colStrings, famColumn, idColumn,
		dadColumn, momColumn, sexColumn);
	graph.setMetaData(metaData);


	for (String row : data) {
	    String[] columns = row.split(separator);

	    Object id = Integer.parseInt(columns[colId]);

	    Node node = new Node(id);

	    Object idMom = null;
	    if (!columns[colIdMom].equals(""))
		idMom = Integer.parseInt(columns[colIdMom]);

	    Object idDad = null;
	    if (!columns[colIdDad].equals(""))
		idDad = Integer.parseInt(columns[colIdDad]);

	    node.setIdDad(idDad);
	    node.setIdMom(idMom);
	    myEdges.add(new Object[] { idDad, id });
	    myEdges.add(new Object[] { idMom, id });
	    graph.addNode(node);
	    

	    for (int i = 0; i < columns.length; i++) {
		String value = columns[i];
		if (i == colIdMom) {
		    node.setUserData(colStrings[i], idMom);
		} else if (i == colIdDad) {
		    node.setUserData(colStrings[i], idDad);
		} else {
		    Integer type = types.get(colStrings[i]);
		    if (type != null) {
			switch (type) {
			case INTEGER:
			    node.setUserData(colStrings[i], Integer
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
		}
	    }
	}

	for (Object[] edge : myEdges) {
	    Node start = graph.getNode(edge[0]);
	    Node end = graph.getNode(edge[1]);
	    if (start != null && end != null) {
		Edge edge2 = new Edge(start, end);
		graph.addEdge(edge2);
	    }
	}

    }

    /**
     * Returns a collection of all attributes.
     * 
     * @return a collection of all attributes.
     */
    public ArrayList<String> getAttributes() {
	return attributes;
    }

    public Vector<String> getTraits() {
	Vector<String> colStrings = new Vector<String>();
	String[] tiles = format.split(separator);

	for (String tile : tiles) {
	    colStrings.add(tile);
	}
	return colStrings;
    }

    public String getFamColumn() {
        return famColumn;
    }

    public void setFamColumn(String famColumn) {
        this.famColumn = famColumn;
    }

    public String getSexColumn() {
        return sexColumn;
    }

    public void setSexColumn(String sexColumn) {
        this.sexColumn = sexColumn;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public String getMomColumn() {
        return momColumn;
    }

    public String getDadColumn() {
        return dadColumn;
    }

}
