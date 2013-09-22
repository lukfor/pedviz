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
import java.awt.Panel;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pedviz.view.GraphView;
import pedviz.view.GraphView3D;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;

import core.Application;

public class GraphPanel extends Panel implements Dockable {

	private GraphView graphView2;

	private DockKey key;

	private JScrollBar moveXSlider, moveYSlider;

	public GraphPanel(GraphView graphView, String title) {

		this.graphView2 = graphView;

		setLayout(new BorderLayout());
		add(graphView.getComponent(), BorderLayout.CENTER);

		if (graphView instanceof GraphView3D) {

			moveYSlider = new JScrollBar(JScrollBar.HORIZONTAL);
			moveYSlider.setValue(0);
			moveYSlider.setMaximum(95);
			moveYSlider.setMinimum(-95);
			moveYSlider.addAdjustmentListener(new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent arg0) {
					graphView2.setTranslateX(moveYSlider.getValue() / 70f);
				}
			});
			add(moveYSlider, BorderLayout.SOUTH);

			moveXSlider = new JScrollBar(JScrollBar.VERTICAL);
			moveXSlider.setValue(0);
			moveXSlider.setMaximum(135);
			moveXSlider.setMinimum(-135);
			moveXSlider.addAdjustmentListener(new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent arg0) {
					graphView2.setTranslateY(moveXSlider.getValue() / 70f);
				}
			});
			add(moveXSlider, BorderLayout.EAST);

		}

		key = new DockKey(title);
		key.setIcon(Application.getInstance().getImage("pedigree"));
		key.setAutoHideEnabled(false);
		key.setCloseEnabled(false);
	}

	public DockKey getDockKey() {
		return key;
	}

	public Component getComponent() {
		return this;
	}

	public GraphView getGraphView() {
		return graphView2;
	}

	public void resetSliders() {
		if (moveXSlider != null && moveYSlider != null) {
			moveXSlider.setValue(0);
			moveYSlider.setValue(0);
		}
	}
}
