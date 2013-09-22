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

package view.dialogs;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogDatabase extends Dialog {

	private String defaultTable = "person";

	private DefaultComboBoxModel tableModel;

	private JComboBox tableComboBox;

	private JTextField connectionEdit;

	public DialogDatabase(Frame parent) {
		super(parent, "Import-Settings");
	}

	@Override
	protected void createUI(JPanel container) {
		container.add(new JLabel("Database connection"));
		connectionEdit = new JTextField();
		connectionEdit.setEnabled(false);
		container.add(connectionEdit);

		container.add(new JLabel("Table name"));
		tableComboBox = new JComboBox();
		tableModel = new DefaultComboBoxModel();
		tableComboBox.setModel(tableModel);
		container.add(tableComboBox);
	}

	public void setTablesData(Vector<String> cols) {
		tableModel.removeAllElements();
		if (cols != null) {
			for (String col : cols) {
				tableModel.addElement(col);
			}
			tableModel.setSelectedItem(defaultTable);
		}
	}

	public String getConnection() {
		return connectionEdit.getText();
	}

	public void setConnection(String filename) {
		connectionEdit.setText(filename);
	}

	public String getTable() {
		return (String) tableModel.getSelectedItem();
	}

}
