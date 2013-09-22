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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import core.Question;
import core.tools.ToolController;

public class HaplotypesView extends ToolView implements PropertyChangeListener {

	private JScrollPane scrollPane;

	private DefaultTableModel model;

	private JTable traitsList;

	private final int height = 150;

	private JCheckBox showGenotypes;

	private JLabel label;

	private JButton deselectAll;

	private JButton selectAll;

	public HaplotypesView(ToolController events) {
		super("Haplotypes", events);
	}

	protected void createControls(JPanel content) {
		content.setLayout(new BorderLayout());
		showGenotypes = new JCheckBox("Show haplotypes");
		content.add(showGenotypes, BorderLayout.NORTH);
		showGenotypes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				traitsList.setEnabled(showGenotypes.isSelected());
				label.setEnabled(showGenotypes.isSelected());
				deselectAll.setEnabled(showGenotypes.isSelected());
				selectAll.setEnabled(showGenotypes.isSelected());
			}
		});
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(0, 20, 0, 0));
		label = new JLabel("Marker Selection:");
		label.setBorder(new EmptyBorder(10, 0, 5, 5));
		panel.add(label, BorderLayout.NORTH);
		scrollPane = new JScrollPane();
		traitsList = new JTable();
		model = new DefaultTableModel();
		traitsList.setModel(model);
		scrollPane.setViewportView(traitsList);
		traitsList.setEnabled(false);
		label.setEnabled(false);
		panel.add(scrollPane);

		selectAll = new JButton("All");
		selectAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < model.getRowCount(); i++) {
					model.setValueAt(true, i, 0);
				}

			}

		});
		deselectAll = new JButton("None");
		deselectAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < model.getRowCount(); i++) {
					model.setValueAt(false, i, 0);
				}

			}

		});

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		buttonBox.add(selectAll);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(deselectAll);
		panel.add(buttonBox, BorderLayout.SOUTH);
		content.add(panel);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		controller.updatePressed(this);
	}

	@Override
	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		super.setData(traits, questions);

		traitsList.getTableHeader().setVisible(false);
		traitsList.getTableHeader().setPreferredSize(new Dimension(0, 0));
		traitsList.getTableHeader().setMaximumSize(new Dimension(0, 0));
		traitsList.getTableHeader().setMinimumSize(new Dimension(0, 0));
		traitsList.setRowSelectionAllowed(false);
		model = new DefaultTableModel() {
			@Override
			public Class<?> getColumnClass(int arg0) {
				if (arg0 == 0) {
					return Boolean.class;
				} else {
					return super.getColumnClass(arg0);
				}
			}
		};
		model.addColumn("");
		model.addColumn("Trait");
		if (traits != null) {
			for (String trait : traits) {
				Question question = questions.get(trait);
				model.addRow(new Object[] { new Boolean(false),
						question.getTrait() });
			}
		}
		traitsList.setModel(model);
		TableColumn col = traitsList.getColumnModel().getColumn(0);
		col.setWidth(16);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		showGenotypes.setEnabled(enabled);
		traitsList.setEnabled(enabled && showGenotypes.isSelected());
		deselectAll.setEnabled(enabled && showGenotypes.isSelected());
		selectAll.setEnabled(enabled && showGenotypes.isSelected());
	}

	public Vector<String> getSelectedTaits() {
		Vector<String> result = new Vector<String>();
		for (int i = 0; i < model.getRowCount(); i++) {
			Boolean isSelected = (Boolean) model.getValueAt(i, 0);
			if (isSelected) {
				result.add((String) model.getValueAt(i, 1));
			}
		}
		return result;
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

	public boolean showGenotypes() {
		return showGenotypes.isSelected();
	}

}
