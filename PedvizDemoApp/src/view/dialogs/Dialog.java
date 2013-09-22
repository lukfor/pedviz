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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class Dialog extends JDialog implements ActionListener {

	private JButton okButton;

	private JButton cancelButton;

	private boolean okPressed;

	private JPanel container;

	public Dialog(Frame parent) {

		this(parent, "");

	}

	public Dialog(Frame parent, String title) {

		super(parent, title, true);

		container = new JPanel();
		container.setBorder(new EmptyBorder(new Insets(12, 12, 11, 11)));
		container.setLayout(new GridLayout(5, 2, 10, 10));
		getContentPane().add(container);

		createUI(container);

		okButton = new JButton("OK");
		getRootPane().setDefaultButton(okButton);
		cancelButton = new JButton("Cancel");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		buttonBox.add(okButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(cancelButton);
		buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);

		okButton.addActionListener(this);
		okButton.setActionCommand("okButton");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancleButton");

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setSize(350, 230);
		setResizable(false);
		pack();
		setLocationRelativeTo(parent);
	}

	protected void createUI(JPanel panel) {

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("okButton")) {
			okPressed();
		}
		if (event.getActionCommand().equals("cancleButton")) {
			canclePressed();
		}
	}

	protected void okPressed() {
		okPressed = true;
		setVisible(false);
	}

	protected void canclePressed() {
		okPressed = false;
		setVisible(false);
	}

	public boolean execute() {
		okPressed = false;
		setModal(true);
		setVisible(true);
		return okPressed;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public JPanel getContainer() {
		return container;
	}

}
