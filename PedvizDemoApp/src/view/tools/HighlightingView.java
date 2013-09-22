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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.tools.ToolController;

public class HighlightingView extends ToolView implements ItemListener {

	private JComboBox highlightingList;

	private JLabel label;

	private JCheckBox synchHighlighter;

	public HighlightingView(ToolController listener) {
		super("Highlighting", listener);
	}

	@Override
	protected void createControls(JPanel content) {
		content.setLayout(new GridBagLayout());
		// Label
		GridBagConstraints constLabel = new GridBagConstraints();
		constLabel.gridx = 0;
		constLabel.gridy = 0;
		constLabel.weightx = 0;
		constLabel.weighty = 0;
		label = new JLabel("Mode:");
		content.add(label, constLabel);
		// ComboBox
		GridBagConstraints text1GBC = new GridBagConstraints();
		text1GBC.gridx = 1;
		text1GBC.gridy = 0;
		text1GBC.weightx = 1;
		text1GBC.weighty = 0;
		text1GBC.fill = GridBagConstraints.HORIZONTAL;

		highlightingList = new JComboBox(new String[] { "maternal", "paternal",
				"maternal and paternal", "all ancestors", "all successors",
				"all anc. and succ.", "none" });
		highlightingList.addItemListener(this);
		content.add(highlightingList, text1GBC);
		constLabel.gridy = 1;
		content.add(new JLabel(), constLabel);
		text1GBC.gridy = 1;
		synchHighlighter = new JCheckBox("synch Highlighter");
		synchHighlighter.setActionCommand("update");
		synchHighlighter.addActionListener(this);
		content.add(synchHighlighter, text1GBC);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		label.setEnabled(enabled);
		synchHighlighter.setEnabled(enabled);
		highlightingList.setEnabled(enabled);
	}

	public void itemStateChanged(ItemEvent e) {
		controller.updatePressed(this);
	}

	public int getSelectedMode() {
		return highlightingList.getSelectedIndex();
	}

	public boolean isSynchHighlighter() {
		return synchHighlighter.isSelected();
	}
}
