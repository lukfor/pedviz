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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Question;
import core.tools.ToolController;

public class LayoutView extends ToolView implements ItemListener {

	private String[] hierarchies = { "UpDown", "DownUp" };

	private JLabel labelSplitter, labelValue;

	private JComboBox valueField;

	private JComboBox traitsList;

	private DefaultComboBoxModel traitModel;

	public LayoutView(ToolController events) {
		super("Layout Algorithm", events, true);
	}

	protected void createControls(JPanel content) {
		content.setLayout(new GridBagLayout());
		// Label
		GridBagConstraints constLabel = new GridBagConstraints();
		constLabel.gridx = 0;
		constLabel.gridy = 0;
		constLabel.weightx = 0;
		constLabel.weighty = 0;

		GridBagConstraints text1GBC = new GridBagConstraints();
		text1GBC.gridx = 1;
		text1GBC.gridy = 0;
		text1GBC.weightx = 1;
		text1GBC.weighty = 0;
		text1GBC.fill = GridBagConstraints.HORIZONTAL;
		hierarchies = new String[] { "UpDown", "DownUp" };
		new JComboBox(hierarchies);

		traitModel = new DefaultComboBoxModel();
		traitsList = new JComboBox();
		traitsList.addItemListener(this);
		traitsList.setModel(traitModel);
		labelSplitter = new JLabel("split data");
		content.add(labelSplitter, constLabel);
		content.add(traitsList, text1GBC);
		constLabel.gridy = 1;
		text1GBC.gridy = 1;
		labelValue = new JLabel("Value");
		content.add(labelValue, constLabel);
		valueField = new JComboBox();
		valueField.addItemListener(this);
		content.add(valueField, text1GBC);
		content.validate();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		// hierarchieList.setEnabled(enabled);
		// label.setEnabled(enabled);
		valueField.setEnabled(enabled);
		traitsList.setEnabled(enabled);
		labelSplitter.setEnabled(enabled);
		labelValue.setEnabled(enabled);
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(traitsList)) {
			if (traitsList.getSelectedItem() != null) {
				valueField.removeAllItems();
				if (traitsList.getSelectedIndex() == 0) {
					valueField.setEnabled(false);
				} else {
					valueField.setEnabled(true);
					if (traits != null) {
						String trait = traitsList.getSelectedItem().toString();
						Vector<String> values = questions.get(trait).values;
						if (values != null)
							for (Comparable o : values) {
								valueField.addItem(o.toString());
							}
					}
				}
			}
		}
		// controller.updatePressed(this);

	}

	@Override
	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		super.setData(traits, questions);
		String empty = "random";
		traitModel.removeAllElements();
		traitModel.addElement(empty);
		for (String trait : traits) {
			Question question = questions.get(trait);
			traitModel.addElement(question.getTrait());
		}
	}

	public String getTrait() {
		if (traitsList.getSelectedItem() != null)
			return traitsList.getSelectedItem().toString();
		else
			return "random";
	}

	public String getValue() {
		if (valueField.getSelectedItem() == null)
			return null;
		else
			return valueField.getSelectedItem().toString();
	}
}
