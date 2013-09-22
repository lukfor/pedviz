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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import core.Question;
import core.tools.ToolController;

public class ToolView extends JPanel implements ActionListener {

	protected ToolController controller;

	private String title;

	private JPanel container;

	private Box buttonBox;

	protected JButton updateButton, resetButton;

	protected HashMap<String, Question> questions;

	protected Vector<String> traits;

	public ToolView(String title, ToolController listener) {
		this(title, listener, false);
	}

	public ToolView(String title) {
		this(title, null, false);
	}

	public ToolView(String title, ToolController controller, boolean buttons) {
		this.title = title;
		this.controller = controller;

		setBorder(new TitledBorder(title));

		container = new JPanel();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;

		createControls(container);

		add(container, c);

		if (buttons) {
			buttonBox = new Box(BoxLayout.X_AXIS);
			buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
			updateButton = new JButton("Update");
			updateButton.setActionCommand("update");
			updateButton.addActionListener(this);
			resetButton = new JButton("Reset");
			resetButton.setActionCommand("reset");
			resetButton.addActionListener(this);
			buttonBox.add(updateButton);
			buttonBox.add(Box.createHorizontalStrut(5));
			buttonBox.add(resetButton);

			// setBorder(new EmptyBorder(3, 3, 3, 3));
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			c.weighty = 0.0;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			add(buttonBox, c);
		}
		if (controller != null) {
			controller.init(this);
		}
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (updateButton != null) {
			updateButton.setEnabled(enabled);
		}
		if (resetButton != null) {
			resetButton.setEnabled(enabled);
		}
	}

	public void addButton(JButton button) {
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(button);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("update")) {
			updatePressed();
			controller.updatePressed(this);
		}
		if (event.getActionCommand().equals("reset")) {
			resetPressed();
			controller.resetPressed(this);
		}
	}

	public Component getComponent() {
		return this;
	}

	protected void createControls(JPanel container) {

	}

	public JPanel getContainer() {
		return container;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension size = super.getMinimumSize();
		size.setSize(170, size.getHeight());
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getMinimumSize();
		size.setSize(170, size.getHeight());
		return size;
	}

	public ToolController getController() {
		return controller;
	}

	public void setController(ToolController controller) {
		this.controller = controller;
		controller.init(this);
	}

	public void showAsFrame() {
		JFrame frame = new JFrame(getTitle());
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setSize(600, 400);
		frame.setVisible(true);
	}

	protected void updatePressed() {

	}

	protected void resetPressed() {

	}

	/*
	 * public HashMap<String, Question> getQuestions() { return questions; }
	 */

	/*
	 * protected Vector<String> getValues(String trait){ return
	 * questions.get(trait).values; }
	 * 
	 * 
	 * public Vector<String> getTraits() { return traits; }
	 */

	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		this.traits = traits;
		this.questions = questions;
	}
}
