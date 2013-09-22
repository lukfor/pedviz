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

import javax.swing.JLabel;
import javax.swing.JPanel;

import view.util.DoubleSlider;
import core.tools.ToolController;

public class FisheyeView extends ToolView {

	private DoubleSlider minField, meanField, maxField;

	private JLabel minLabel, meanLabel, maxLabel;

	public FisheyeView(ToolController events) {
		super("Fisheye", events);
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

		// Min
		minLabel = new JLabel("dx");
		constLabel.gridy = 2;
		content.add(minLabel, constLabel);
		minField = new DoubleSlider();
		minField.setDoubleMaximum(12);
		minField.setDoubleValue(7);
		minField.setDoubleMinimum(1);
		constInput.gridy = 2;
		content.add(minField, constInput);
		// Mean
		meanLabel = new JLabel("dy");
		constLabel.gridy = 3;
		content.add(meanLabel, constLabel);
		meanField = new DoubleSlider();
		meanField.setDoubleMaximum(12);
		meanField.setDoubleValue(7);
		meanField.setDoubleMinimum(1);
		constInput.gridy = 3;
		content.add(meanField, constInput);
		// Max
		maxLabel = new JLabel("sz");
		constLabel.gridy = 4;
		content.add(maxLabel, constLabel);
		maxField = new DoubleSlider();
		maxField.setDoubleMaximum(5);
		maxField.setDoubleValue(2);
		maxField.setDoubleMinimum(1);
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

	}

	public double getDY() {
		return meanField.getDoubleValue();
	}

	public double getSZ() {
		return maxField.getDoubleValue();
	}

	public double getDX() {
		return minField.getDoubleValue();
	}

}
