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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.util.DoubleSlider;
import core.Question;
import core.tools.ToolController;

public class QualitativeTraitsView extends ToolView implements ItemListener {

	private DoubleSlider minField, meanField, maxField;

	private JLabel minLabel, meanLabel, maxLabel, traitLabel;

	private JCheckBox useBars;

	private JComboBox traitsList;

	private DefaultComboBoxModel traitModel;

	public QualitativeTraitsView(ToolController events) {
		super("Qualitative Traits", events, true);
	}

	@Override
	protected void createControls(JPanel content) {
		content.setLayout(new GridBagLayout());

		GridBagConstraints constLabel = new GridBagConstraints();
		constLabel.gridx = 0;
		constLabel.gridy = 0;
		constLabel.weightx = 0;

		GridBagConstraints constInput = new GridBagConstraints();
		constInput.gridx = 1;
		constInput.gridy = 0;
		constInput.weightx = 1;
		constInput.fill = GridBagConstraints.HORIZONTAL;

		// TraitLabel
		traitLabel = new JLabel("Trait");
		content.add(traitLabel);
		// TraitList
		traitModel = new DefaultComboBoxModel();
		traitsList = new JComboBox();
		traitsList.setEditable(false);
		traitsList.setModel(traitModel);
		traitsList.addItemListener(this);
		content.add(traitsList, constInput);
		// Dummy
		constLabel.gridy = 1;
		content.add(new JLabel(""), constLabel);
		// UseBars
		useBars = new JCheckBox("colour bar (2D)");
		useBars.setActionCommand("useBar");
		useBars.addActionListener(this);
		useBars.setVisible(false);
		constInput.gridy = 1;
		content.add(useBars, constInput);
		// Min
		minLabel = new JLabel("min");
		minLabel.setForeground(Color.BLUE);
		constLabel.gridy = 2;
		content.add(minLabel, constLabel);
		minField = new DoubleSlider();
		constInput.gridy = 2;
		content.add(minField, constInput);
		// Mean
		meanLabel = new JLabel("mean");
		meanLabel.setForeground(new Color(0, 128, 0));
		constLabel.gridy = 3;
		content.add(meanLabel, constLabel);
		meanField = new DoubleSlider();
		constInput.gridy = 3;
		content.add(meanField, constInput);
		// Max
		maxLabel = new JLabel("max");
		constLabel.gridy = 4;
		maxLabel.setForeground(Color.RED);
		content.add(maxLabel, constLabel);
		maxField = new DoubleSlider();
		constInput.gridy = 4;
		content.add(maxField, constInput);
		doLayout();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		minLabel.setEnabled(enabled);
		maxLabel.setEnabled(enabled);
		meanLabel.setEnabled(enabled);
		minField.setEnabled(enabled);
		maxField.setEnabled(enabled);
		meanField.setEnabled(enabled);
		useBars.setEnabled(enabled);
		traitLabel.setEnabled(enabled);
		traitsList.setEnabled(enabled);
	}

	@Override
	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		super.setData(traits, questions);

		traitModel.removeAllElements();
		for (String trait : traits) {
			Question question = questions.get(trait);
			traitModel.addElement(question.getTrait());
		}
	}

	public void setColourBarEnabled(boolean enabled) {
		useBars.setVisible(enabled);
		updateUI();
	}

	public double getMean() {
		return meanField.getDoubleValue();
	}

	public double getMax() {
		return maxField.getDoubleValue();
	}

	public double getMin() {
		return minField.getDoubleValue();
	}

	public String getTrait() {
		return traitModel.getSelectedItem().toString();
	}

	public boolean isBarSelected() {
		return useBars.isSelected();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("useBar")) {
			controller.updatePressed(this);
		}
		super.actionPerformed(event);
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(traitsList)) {
			if (traitsList.getSelectedItem() != null) {
				if (questions != null) {
					String trait = traitsList.getSelectedItem().toString();
					if (questions.get(trait) != null) {
						double max = -1;
						double min = -1;
						double mean = -1;
						try {
							min = Double.parseDouble(Collections.min(
									questions.get(trait).values).toString());
							max = Double.parseDouble(Collections.max(
									questions.get(trait).values).toString());
							mean = ((max - min) / 2.0) + min;
							minField.setEnabled(true);
							minField.setDoubleMinimum(min);
							minField.setDoubleMaximum(max);
							minField.setDoubleValue(min);

							maxField.setEnabled(true);
							maxField.setDoubleMinimum(min);
							maxField.setDoubleMaximum(max);
							maxField.setDoubleValue(max);

							meanField.setEnabled(true);
							meanField.setDoubleMinimum(min);
							meanField.setDoubleMaximum(max);
							meanField.setDoubleValue(mean);
							updateButton.setEnabled(true);

						} catch (Exception ex) {
							minField.setEnabled(false);
							maxField.setEnabled(false);
							meanField.setEnabled(false);
							updateButton.setEnabled(false);
						}

					}
				}
			}
		}

	}

}
