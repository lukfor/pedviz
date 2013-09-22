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

package core;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Properties;
import java.util.Vector;

import pedviz.io.db.Database;

/**
 * This class allows the DatabaseGraphLoader to load data from a HSQL database.
 * 
 * @author lukas forer
 */
public class PostgreDB implements Database {
	private Connection connection;

	private boolean connected = false;

	private String user, password, host, driver, label;

	public PostgreDB(String user, String password, String host, String driver) {
		this.user = user;
		this.password = password;
		this.host = host;
		this.driver = driver;
	}

	public PostgreDB(String filename) {
		try {
			Properties properties = new Properties();
			FileInputStream stream = new FileInputStream(filename);
			properties.load(stream);
			user = properties.getProperty("pedvizapi.database.username");
			password = properties.getProperty("pedvizapi.database.password");
			host = properties.getProperty("pedvizapi.database.url");
			driver = properties.getProperty("pedvizapi.database.driver_class");
			label = properties.getProperty("pedvizapi.database.label");
		} catch (Exception e) {
			Application.getInstance().handleException(e);
		}
	}

	public boolean connect(String user, String password, String host) {
		this.user = user;
		this.password = password;
		this.host = host;
		return connect();
	}

	public boolean connect() {
		try {
			connected = false;

			Class driver_class = Class.forName(driver);
			Constructor c = driver_class.getConstructor((Class[]) null);
			DriverManager.registerDriver((Driver) c
					.newInstance((Object[]) null));
			connection = DriverManager.getConnection(host, user, password);
			connection.setAutoCommit(false);
			connected = true;
		} catch (Exception e) {
			Application.getInstance().handleException(e);
		}
		return connected;
	}

	public void close() {
		try {
			connection.close();
			connected = false;
		} catch (Exception e) {
			Application.getInstance().handleException(e);
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
			Application.getInstance().handleException(e);
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
			Application.getInstance().handleException(e);
			return null;
		}

	}

	public Vector<String> getTables() {
		try {
			Vector<String> result = new Vector<String>();
			DatabaseMetaData dbm = connection.getMetaData();
			String types[] = { "TABLE" };
			ResultSet rs = dbm.getTables(null, null, null, types);
			rs.beforeFirst();
			while (rs.next()) {
				String str = rs.getString("TABLE_NAME");
				result.add(str);
			}
			rs.close();
			return result;
		} catch (Exception e) {
			Application.getInstance().handleException(e);
			return null;
		}
	}

	public Vector<String> getColumns(String table) {
		try {
			Vector<String> result = new Vector<String>();
			DatabaseMetaData dbm = connection.getMetaData();
			ResultSet columns = dbm.getColumns(null, "%", table, "%");
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				result.add(columnName);
			}
			Collections.sort(result);
			return result;
		} catch (Exception e) {
			Application.getInstance().handleException(e);
			return null;
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public String getLabel() {
		return label;
	}
}
