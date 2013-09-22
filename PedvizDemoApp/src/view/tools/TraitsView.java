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

package view.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import view.table.ColorEditor;
import view.table.ColorRenderer;
import view.table.ListEditor;
import view.table.ListRenderer;
import view.table.TraitTableModel;
import core.Question;
import core.tools.ToolController;

public class TraitsView extends ToolView implements PropertyChangeListener {

	private JScrollPane scrollPane;

	private TraitTableModel mappingModel;

	private JTable traitsTable;

	private final int height = 150;

	public TraitsView(ToolController events) {
		super("Quantitative Traits", events, true);
	}

	protected void createControls(JPanel content) {
		scrollPane = new JScrollPane();
		traitsTable = new JTable();
		traitsTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
		traitsTable.setDefaultEditor(Color.class, new ColorEditor());
		mappingModel = new TraitTableModel();
		traitsTable.setShowGrid(true);
		traitsTable.setModel(mappingModel);
		traitsTable.addPropertyChangeListener(this);
		scrollPane.setViewportView(traitsTable);
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		content.add(scrollPane, c);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// controller.updatePressed(this);
	}

	@Override
	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		super.setData(traits, questions);

		String empty = "-----";
		Object[][] data = { { new Boolean(false), empty, "0", Color.red },
				{ new Boolean(false), empty, "0", Color.blue },
				{ new Boolean(false), empty, "0", Color.yellow },
				{ new Boolean(false), empty, "0", Color.green } };
		mappingModel.setData(data);
		traitsTable.setRowHeight(20);
		TableColumn col = traitsTable.getColumnModel().getColumn(1);
		col.setWidth(50);
		col.setMinWidth(50);
		Vector<String> values = new Vector<String>();
		values.add(empty);
		for (String trait : traits) {
			Question question = questions.get(trait);
			values.add(question.getTrait());
		}
		col.setCellEditor(new ListEditor(values));
		col.setCellRenderer(new ListRenderer(values));
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		traitsTable.setEnabled(enabled);
	}

	public Object[][] getRows() {
		return mappingModel.getData();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.setSize(150, height);
		return size;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension size = super.getMinimumSize();
		size.setSize(150, height);
		return size;
	}

}
