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
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import view.tools.ToolView;

public class DialogTools extends Dialog {

	private JTabbedPane pane;

	private Vector<ToolView> tools;

	public DialogTools(JFrame parent) {
		super(parent, "Genotypes and Haplotypes");
		setSize(420, 500);
		tools = new Vector<ToolView>();
	}

	@Override
	protected void createUI(JPanel panel) {
		super.createUI(panel);
		panel.setLayout(new BorderLayout());
		pane = new JTabbedPane();
		panel.add(pane, "Center");
	}

	public void addTool(ToolView toolView) {
		toolView.setBorder(new EmptyBorder(12, 12, 12, 12));
		pane.add(toolView.getTitle(), toolView);
		tools.add(toolView);
	}

	public Vector<ToolView> getTools() {
		return tools;
	}

}