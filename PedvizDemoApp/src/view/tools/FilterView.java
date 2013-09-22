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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import pedviz.algorithms.filter.Filter;
import core.Question;
import core.tools.ToolController;

public class FilterView extends ToolView {

	JRadioButton r1;

	JRadioButton r0;

	JPanel panelExtract;

	JRadioButton extract, high;

	JPanel panelHighMode;

	private int n;

	PanelList<ConditionView> list;

	JComboBox modeList;

	JSlider genSlider;

	JButton button;

	JLabel labelGen;

	public FilterView(ToolController events) {
		super("Filter", events, true);
	}

	protected void createControls(JPanel content) {

		content.setLayout(new BorderLayout());
		JPanel modePanel = new JPanel(new BorderLayout());
		modePanel.setBorder(new TitledBorder("Mode"));

		JPanel conPanel = new JPanel(new BorderLayout());
		conPanel.setBorder(new TitledBorder("Conditions"));

		JPanel operatorPanel = new JPanel(new GridLayout(1, 2));
		ButtonGroup group2 = new ButtonGroup();
		r0 = new JRadioButton("or", true);
		r1 = new JRadioButton("and", false);
		group2.add(r0);
		group2.add(r1);
		operatorPanel.add(r0);
		operatorPanel.add(r1);

		list = new PanelList<ConditionView>();
		// list.addItem(new ConditionView());
		conPanel.add(list);
		JPanel buttonPanel = new JPanel(new BorderLayout());
		button = new JButton("+");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				ConditionView con = new ConditionView();
				con.setData(traits, questions);
				list.addItem(con);
			}

		});
		buttonPanel.add(operatorPanel);
		buttonPanel.add(button, BorderLayout.EAST);
		conPanel.add(buttonPanel, BorderLayout.SOUTH);
		content.add(conPanel, BorderLayout.CENTER);

		ButtonGroup group3 = new ButtonGroup();
		high = new JRadioButton("Highlight", true);
		extract = new JRadioButton("Extract Subfamilies", false);
		group3.add(high);
		group3.add(extract);
		JPanel panelHigh = new JPanel(new BorderLayout());
		panelHigh.add(high);

		panelHighMode = new JPanel(new BorderLayout());
		panelHighMode.setBorder(new EmptyBorder(0, 25, 0, 0));
		modePanel.add(panelHigh, BorderLayout.NORTH);
		modeList = new JComboBox(new String[] { "Transparent mode",
				"Blinking mode" });
		panelHighMode.add(modeList);
		panelHigh.add(panelHighMode, BorderLayout.SOUTH);

		panelExtract = new JPanel(new BorderLayout());
		panelExtract.add(extract);

		JPanel panelSlider = new JPanel(new BorderLayout());
		panelSlider.setBorder(new EmptyBorder(0, 25, 0, 0));

		genSlider = new JSlider();
		genSlider.setMaximum(10);
		genSlider.setMinimum(1);
		genSlider.setValue(3);
		genSlider.setSnapToTicks(true);
		genSlider.setMajorTickSpacing(1);
		// genSlider.setPaintTicks(true);
		genSlider.setPaintLabels(true);

		panelSlider.add(genSlider);
		labelGen = new JLabel("Generations:");
		panelSlider.add(labelGen, BorderLayout.NORTH);
		panelExtract.add(panelSlider, BorderLayout.SOUTH);

		modePanel.add(panelExtract, BorderLayout.CENTER);
		// content.add(new JLabel("Conditions:"), BorderLayout.NORTH);
		content.add(modePanel, BorderLayout.SOUTH);

		high.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				genSlider.setEnabled(extract.isSelected());
				modeList.setEnabled(high.isSelected());
				labelGen.setEnabled(extract.isSelected());
			}
		});

		extract.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				genSlider.setEnabled(extract.isSelected());
				modeList.setEnabled(high.isSelected());
				labelGen.setEnabled(extract.isSelected());
			}
		});

		high.setSelected(true);
		genSlider.setEnabled(false);
		labelGen.setEnabled(false);
	}

	@Override
	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		super.setData(traits, questions);
		for (ConditionView item : list.getItmes()) {
			ConditionView view = (ConditionView) item;
			view.setData(traits, questions);
		}
	}

	/*
	 * public void setTraits(HashMap<String, Question> traits) { this.traits =
	 * traits; for (ConditionView item : list.getItmes()) { ConditionView view =
	 * (ConditionView) item; view.setTraits(traits); } }
	 */

	public int getSelectedMode() {
		return r0.isSelected() ? Filter.OR : Filter.AND;
	}

	public Vector<ConditionView> getConditions() {
		return list.getItmes();
	}

	public int getMode() {
		return modeList.getSelectedIndex();
	}

	public boolean isExtract() {
		return extract.isSelected();
	}

	public int getGenerations() {
		return genSlider.getValue();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (ConditionView item : list.getItmes()) {
			item.setEnabled(enabled);
		}
		r1.setEnabled(enabled);
		r0.setEnabled(enabled);
		genSlider.setEnabled(extract.isSelected() & enabled);
		modeList.setEnabled(high.isSelected() & enabled);
		labelGen.setEnabled(extract.isSelected() & enabled);
		high.setEnabled(enabled);
		extract.setEnabled(enabled);
		button.setEnabled(enabled);
		list.setEnabled(enabled);
	}

}
