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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import pedviz.graph.Edge;
import pedviz.graph.Graph;
import pedviz.graph.GraphMetaData;
import pedviz.graph.Node;
import pedviz.io.db.Database;
import pedviz.io.db.HSQLDatabase;
import pedviz.io.db.MySQLDatabase;
import pedviz.io.db.PostgreSQLDatabase;

/**
 * <p>
 * This Loader class allow you to load a graph directly from a database. It's
 * possible to load the whole table or to define your own querie.
 * </p>
 * 
 * <p>
 * Data can be imported from any Java supported relational database. At the
 * moment loader classes for mysql ({@link MySQLDatabase}), hdbsql ({@link HSQLDatabase})
 * and postgresSQL ({@link PostgreSQLDatabase}) are available. Furthermore,
 * it's mandatory to set the mapping for the following fields: id, mom's id and
 * dad's id (Method: setSettings). Additional fields (that maybe are available
 * in the data), such as sex, traits etc. are mapped as Rules.
 * </p>
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class DatabaseGraphLoader implements GraphLoader {
    private String table;

    private String query = "";

    private Vector<String> columns = null;

    private ArrayList<String> attributes;

    private Database database;

    private String idColumn = null;

    private String momColumn = null;

    private String dadColumn = null;

    private String famColumn = null;

    private String sexColumn = null;

    /**
     * Constructs a new DatabaseGraphLoader with the given connection and the
     * table name.
     * 
     * @param connection
     *                Connection object
     * @param table
     *                tablename
     */
    public DatabaseGraphLoader(Database database, String table) {
	this.database = database;
	this.table = table;
	this.attributes = new ArrayList<String>();
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

    /**
     * Returns the field name from the dad's id
     * 
     * @return field name from the dad's id
     */
    public String getDadColumn() {
	return dadColumn;
    }

    /**
     * Returns the field name from the id
     * 
     * @return field name from the id
     */
    public String getIdColumn() {
	return idColumn;
    }

    /**
     * Returns the field name from the mom's id
     * 
     * @return field name from the mom's id
     */
    public String getMomColumn() {
	return momColumn;
    }

    public String getFamColumn() {
	return famColumn;
    }

    public void setFamCol(String famColumn) {
	this.famColumn = famColumn;
    }

    /**
     * Sets a query.
     * 
     * @param query
     */
    public void setQuery(String query) {
	this.query = query;
    }

    public void load(Graph graph) {
	ArrayList<Object[]> myEdges = new ArrayList<Object[]>();

	if (query.equals(""))
	    query = "SELECT * FROM " + table;

	Vector<String> vectorTraits = getTraits();
	String[] traits = new String[vectorTraits.size()];
	for (int i = 0; i < vectorTraits.size(); i++){
	    traits[i] = vectorTraits.get(i);
	}
	GraphMetaData metaData = new GraphMetaData(traits, famColumn, idColumn,
		dadColumn, momColumn, sexColumn);
	graph.setMetaData(metaData);

	try {
	    ResultSet rs = database.executeQuerie(query);

	    rs.beforeFirst();

	    while (rs.next()) {
		// Id
		Object id = rs.getObject(getIdColumn());
		Node node = new Node(id);
		graph.addNode(node);

		// Mom
		Object idMom = rs.getObject(getMomColumn());
		myEdges.add(new Object[] { idMom, id });
		node.setIdMom(idMom);

		// Dad
		Object idDad = rs.getObject(getDadColumn());
		myEdges.add(new Object[] { idDad, id });
		node.setIdDad(idDad);

		// save all attributes in userdata
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
		    node.setUserData(rs.getMetaData().getColumnName(i).toUpperCase(), rs
			    .getObject(i));
		    if (rs.isFirst())
			attributes.add(rs.getMetaData().getColumnName(i).toUpperCase());
		}

		// Fam
		Object idFam = rs.getObject(getFamColumn());
		if (idFam != null) {
		    node.setFamId(idFam.toString());
		} else {
		    node.setUserData(getFamColumn(), 1);
		}

	    }

	    for (Object[] edge : myEdges) {
		Node start = graph.getNode(edge[0]);
		Node end = graph.getNode(edge[1]);

		if (start != null && end != null)
		    graph.addEdge(new Edge(start, end));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Vector<String> getTraits() {
	if (columns == null) {
	    try {
		DatabaseMetaData meta = database.getConnection().getMetaData();
		ResultSet rsColumns = meta.getColumns(null, "%", table, "%");
		columns = new Vector<String>();
		rsColumns.beforeFirst();
		while (rsColumns.next()) {
		    columns.add(rsColumns.getString("COLUMN_NAME").toUpperCase());
		}
	    } catch (Exception e) {
		System.out.println(e);
	    }
	}
	return columns;
    }

    /**
     * Returns a collection of all attributes.
     * 
     * @return a collection of all attributes.
     */
    public ArrayList<String> getAttributes() {
	return attributes;
    }

    public String getSexColumn() {
	return sexColumn;
    }

    public void setSexColumn(String sexColumn) {
	this.sexColumn = sexColumn;
    }
}
