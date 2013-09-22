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

package pedviz.io.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class allows the DatabaseGraphLoader to load data from a HSQL database.
 * 
 * @author lukas forer
 */
public class HSQLDatabase implements Database {
    private Connection connection;

    private boolean connected = false;

    public boolean connect(String user, String password, String host) {
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	    // DriverManager.registerDriver(new org.hsqldb.jdbcDriver());
	    connection = DriverManager.getConnection(host, user, password);
	    connection.setAutoCommit(false);
	    connected = true;
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
	return connected;
    }

    public void close() {
	try {
	    connection.close();
	    connected = false;
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }

    public boolean isConnected() {
	return connected;
    }

    public String getInfo() {
	String result = "";
	try {
	    DatabaseMetaData dma = connection.getMetaData();
	    result += "Connected to " + dma.getURL() + "\n";
	    result += "Driver: " + dma.getDriverName() + "\n";
	    result += "Version: " + dma.getDriverVersion() + "\n";
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
	return result;
    }

    public ResultSet executeQuerie(String querie) {
	try {
	    Statement stmt = connection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE,
		    ResultSet.CONCUR_READ_ONLY);

	    return stmt.executeQuery(querie);
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	    return null;
	}
    }

    public Connection getConnection() {
	return connection;
    }
}
