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

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DialogAbout extends Dialog {
	public DialogAbout(JFrame parent) {
		super(parent, "About");
		setSize(420, 250);
	}

	@Override
	protected void createUI(JPanel panel) {
		super.createUI(panel);
		panel.setLayout(new BorderLayout());
		Box b = Box.createVerticalBox();
		b.add(Box.createGlue());
		b
				.add(new JLabel(
						"Copyright © 2007 by Christian Fuchsberger and Lukas Forer info@pedvizapi.org."));
		b.add(new JLabel("All rights reserved."));
		b.add(new JLabel(""));
		b
				.add(new JLabel(
						"This program is free software; you can redistribute it and/or modify"));
		b
				.add(new JLabel(
						" it under the terms of the GNU General Public License as published by"));
		b
				.add(new JLabel(
						" the Free Software Foundation; either version 2 of the License."));
		b.add(new JLabel(""));
		b
				.add(new JLabel(
						" This program is distributed in the hope that it will be useful,"));
		b
				.add(new JLabel(
						" but WITHOUT ANY WARRANTY; without even the implied warranty of"));
		b.add(new JLabel(
				" MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE."));
		b
				.add(new JLabel(
						" See the GNU General Public License <http://www.pedvizapi.org/gpl.txt>"));
		b.add(new JLabel(" for more details.     "));

		b.add(Box.createGlue());
		panel.add(b, "Center");
	}

}