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

package view.table;

import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class ListEditor extends DefaultCellEditor {
	public ListEditor(String[] items) {
		super(new JComboBox(items));
	}

	public ListEditor(Vector<String> items) {
		super(new JComboBox(items));
	}
}
