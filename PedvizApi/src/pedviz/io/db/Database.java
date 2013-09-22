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
import java.sql.ResultSet;

/**
 * Implementations of this interface represent a database. progress.
 * 
 * @author lukas forer
 * 
 */
public interface Database {
    /**
     * Connects to the db with the given username, password and host.
     * 
     * @param user
     * @param password
     * @param host
     * @return true, if the connection is ok.
     */
    public boolean connect(String user, String password, String host);

    /**
     * Closes the db connection.
     * 
     */
    public void close();

    /**
     * Returns true, if the db is connected.
     * 
     * @return true, if the db is connected.
     */
    public boolean isConnected();

    /**
     * Returns some informations about the db.
     * 
     * @return some informations about the db.
     */
    public String getInfo();

    /**
     * Executes the given Querie.
     * 
     * @param querie
     * @return result
     */
    public ResultSet executeQuerie(String querie);

    /**
     * Returns the connection.
     * 
     * @return connection
     */
    public Connection getConnection();
}
