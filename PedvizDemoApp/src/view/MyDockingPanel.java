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

package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;

public class MyDockingPanel extends JPanel implements Dockable {

	private String title;

	protected DockKey key;

	private int minSize = 180;

	public MyDockingPanel(String title) {
		this(title, false);
	}

	public MyDockingPanel(String title, boolean buttons) {
		this.title = title;
		key = new DockKey(title);
		key.setMaximizeEnabled(false);
		key.setAutoHideEnabled(false);
		setLayout(new BorderLayout());
		createUI(this);
	}

	public String getTitle() {
		return title;
	}

	public DockKey getDockKey() {
		return key;
	}

	public Component getComponent() {
		return this;
	}

	protected void createUI(JPanel container) {

	}

	@Override
	public Dimension getMinimumSize() {
		Dimension size = super.getMinimumSize();
		size.setSize(minSize, size.getHeight());
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getMinimumSize();
		size.setSize(minSize, size.getHeight());
		return size;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

}
