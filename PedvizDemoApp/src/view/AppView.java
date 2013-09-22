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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import pedviz.graph.Graph;
import pedviz.view.GraphView;
import pedviz.view.GraphView2D;
import view.tools.ToolView;

import com.vlsolutions.swing.docking.DockGroup;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.DockingPreferences;

import core.Application;
import core.ControllerAction;

/**
 * @author Luki
 */
public class AppView extends JFrame {

	private DockGroup mainPanel;

	private JTable detailsTable;

	private JTree familiesTree;

	private DockingDesktop desktop;

	private DockGroup toolsPanel;

	private MyDockingPanel top, bottom, right;

	private JPanel toolPanel;

	private Vector<ToolView> tools;

	private Vector<GraphPanel> panels;

	private JMenuItem aboutMenuItem, exportMenuItem, centerGraphMenuItem,
			saveCsvMenuItem, clearMenuItem, splitMenuItem, rotateXMenuItem,
			rotateYMenuItem, exitMenuItem, fileMenuItem, importMenuItem,
			errorMenuItem, view3dMenuItem, view2dMenuItem;

	private JMenu toolMenu, fisheyeMenu;

	private JCheckBoxMenuItem showEdgesMenuItem;

	private DefaultTreeModel familiesModel;

	private DefaultTableModel detailsModel;

	private JMenuItem questionItem;

	private JMenuItem genohaploMenuItem;

	private DefaultMutableTreeNode node;

	public AppView() {

		panels = new Vector<GraphPanel>();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		JMenuBar menubar = new JMenuBar();
		createMenu(menubar);
		setJMenuBar(menubar);

		DockingPreferences.initHeavyWeightUsage();
		tools = new Vector<ToolView>();

		JPanel container = new JPanel();
		createUI(container);
		getContentPane().add(container);
		setTitle("3d-PedViz " + Application.getInstance().getVersion());
		this.pack();
	}

	public void setFamiliesEnabled(boolean enabled) {
		familiesTree.setEnabled(enabled);
	}

	public void setControlsEnabled(boolean enabled) {
		detailsTable.setEnabled(enabled);
		bottom.setEnabled(enabled);
		top.setEnabled(enabled);

		for (GraphPanel panel : panels) {
			panel.setEnabled(enabled);
		}

		for (ToolView panel : tools) {
			panel.setEnabled(enabled);
		}
	}

	public void setActionsEnabled(boolean enabled) {
		splitMenuItem.setEnabled(enabled);
		clearMenuItem.setEnabled(enabled);
		saveCsvMenuItem.setEnabled(enabled);
		exportMenuItem.setEnabled(enabled);
		rotateXMenuItem.setEnabled(enabled);
		rotateYMenuItem.setEnabled(enabled);
		centerGraphMenuItem.setEnabled(enabled);
		showEdgesMenuItem.setEnabled(enabled);
		genohaploMenuItem.setEnabled(enabled);
		importMenuItem.setEnabled(enabled);
		errorMenuItem.setEnabled(enabled);
		fisheyeMenu.setEnabled(enabled);
	}

	public void setSplitEnabled(boolean enabled) {
		splitMenuItem.setEnabled(enabled);
	}

	public void setUseQuestions(boolean enabled) {
		// questionItem.setEnabled(enabled);
	}

	public void setPedigreeName(String name) {
		setTitle("3dPedViz - " + name);
	}

	public void setWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setDefaultCursor() {
		setCursor(null);
	}

	private void createMenu(JMenuBar menu) {

		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		menu.add(fileMenu);
		fileMenuItem = createMenuItem(fileMenu, "Open File...",
				new ControllerAction("loadFromFile"));
		fileMenuItem = createMenuItem(fileMenu, "Open From Database...",
				new ControllerAction("loadFromDatabase"));
		fileMenu.add(new JSeparator());
		importMenuItem = createMenuItem(fileMenu, "Import Haplotypes...",
				new ControllerAction("importHaplotypes"));
		errorMenuItem = createMenuItem(fileMenu, "ErrorChecking...",
				new ControllerAction("runErrorChecking"));
		fileMenu.add(new JSeparator());
		exitMenuItem = createMenuItem(fileMenu, "Exit", new ControllerAction(
				"exit"));

		JMenu editMenu = new JMenu();
		editMenu.setText("Edit");
		menu.add(editMenu);
		splitMenuItem = createMenuItem(editMenu, "Define Phenotype...",
				new ControllerAction("changePhenotype"));

		splitMenuItem = createMenuItem(editMenu, "Clustering...",
				new ControllerAction("showClustering"));

		splitMenuItem = createMenuItem(editMenu, "Split Selection",
				new ControllerAction("splitSelection"));
		clearMenuItem = createMenuItem(editMenu, "Clear Selection",
				new ControllerAction("clearSelection"));
		editMenu.add(new JSeparator());
		saveCsvMenuItem = createMenuItem(editMenu, "Export as CSV",
				new ControllerAction("exportAsCsv"));

		toolMenu = new JMenu();
		toolMenu.setText("Tools");
		// menu.add(toolMenu);

		JMenu viewMenu = new JMenu();
		viewMenu.setText("View");
		menu.add(viewMenu);
		centerGraphMenuItem = createMenuItem(viewMenu, "Center Graph",
				new ControllerAction("centerGraph"));

		viewMenu.add(new JSeparator());

		view2dMenuItem = new JCheckBoxMenuItem();
		view2dMenuItem.setText("Show 2d View");
		view2dMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Application.getInstance().getController().hideGraphView2D(arg0);
			}
		});
		view2dMenuItem.setVisible(false);
		view2dMenuItem.setSelected(true);
		viewMenu.add(view2dMenuItem);

		view3dMenuItem = new JCheckBoxMenuItem();
		view3dMenuItem.setText("Show 2,5d View");
		view3dMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Application.getInstance().getController().hideGraphView3D(arg0);
			}
		});
		view3dMenuItem.setVisible(false);
		view3dMenuItem.setSelected(true);
		viewMenu.add(view3dMenuItem);

		showEdgesMenuItem = new JCheckBoxMenuItem();
		showEdgesMenuItem.setText("Show Edges");
		showEdgesMenuItem.setSelected(true);
		showEdgesMenuItem.addActionListener(new ControllerAction("showEdges"));
		viewMenu.add(showEdgesMenuItem);
		viewMenu.add(new JSeparator());

		rotateXMenuItem = new JCheckBoxMenuItem();
		rotateXMenuItem.setText("Rotation X");
		rotateXMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Application.getInstance().getController().rotateX(arg0);
			}
		});
		viewMenu.add(rotateXMenuItem);

		rotateYMenuItem = new JCheckBoxMenuItem();
		rotateYMenuItem.setText("Rotation Y");
		rotateYMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Application.getInstance().getController().rotateY(arg0);
			}
		});
		rotateYMenuItem.setSelected(true);
		viewMenu.add(rotateYMenuItem);

		viewMenu.add(new JSeparator());

		fisheyeMenu = new JMenu("Fisheye");
		viewMenu.add(fisheyeMenu);
		JMenuItem fisheyeOn = new JCheckBoxMenuItem();
		fisheyeOn.setText("Use Fisheye");
		fisheyeOn.setSelected(false);
		fisheyeOn.addActionListener(new ControllerAction("useFisheye"));
		fisheyeMenu.add(fisheyeOn);

		JMenuItem fisheyepred = createMenuItem(fisheyeMenu,
				"Fisheye anpassen...",
				new ControllerAction("showFisheyeDialog"));

		viewMenu.add(new JSeparator());

		exportMenuItem = createMenuItem(viewMenu, "Export As Jpeg",
				new ControllerAction("exportAsJpeg"));
		viewMenu.add(new JSeparator());

		genohaploMenuItem = createMenuItem(viewMenu, "Genotypes/Haplotypes..",
				new ControllerAction("showGenoHaploDialog"));

		JMenu helpMenu = new JMenu();
		helpMenu.setText("About");
		menu.add(helpMenu);

		aboutMenuItem = createMenuItem(helpMenu, "About Pedviz 3D",
				new ControllerAction("showAboutDialog"));
	}

	private JMenuItem createMenuItem(JMenu menu, String name, Action action) {
		JMenuItem mi;
		if (action != null) {
			mi = new JMenuItem(action);
		} else {
			mi = new JMenuItem();
		}
		mi.setText(name);
		menu.add(mi);
		return mi;
	}

	private void createUI(JPanel container) {

		container.setLayout(new BorderLayout());
		mainPanel = new DockGroup("main");

		familiesTree = new JTree();
		familiesTree.addTreeSelectionListener(new TreeHandler());
		familiesTree.setCellRenderer(new TreeRenderer());
		familiesTree.setShowsRootHandles(true);
		familiesTree.setModel(null);
		familiesTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		top = new MyDockingPanel("Families");
		top.setLayout(new BorderLayout());
		JScrollPane familiesScrollPane = new JScrollPane(familiesTree);
		top.add(familiesScrollPane, BorderLayout.CENTER);
		top.getDockKey().setDockGroup(toolsPanel);
		top.setPreferredSize(new Dimension(180, 180));
		top.setMinimumSize(new Dimension(180, 180));

		detailsTable = new JTable();
		bottom = new MyDockingPanel("Details");
		bottom.setLayout(new BorderLayout());
		JScrollPane detailsScrollPane = new JScrollPane(detailsTable);
		bottom.add(detailsScrollPane, BorderLayout.CENTER);
		bottom.getDockKey().setDockGroup(toolsPanel);

		right = new MyDockingPanel("Tools");
		right.getDockKey().setDockGroup(toolsPanel);
		right.setLayout(new BorderLayout());
		toolPanel = new JPanel();
		toolPanel.setLayout(new GridBagLayout());
		JScrollPane toolsScrollPane = new JScrollPane(toolPanel);
		right.add(toolsScrollPane, BorderLayout.CENTER);
		right.setMinSize(250);

		desktop = new DockingDesktop("pedviz");
		container.add(desktop, BorderLayout.CENTER);
	}

	public void addGraphview(GraphView graphview) {
		GraphPanel panel = null;
		if (graphview instanceof GraphView2D) {
			panel = new GraphPanel(graphview, "View 2d");
		} else {
			panel = new GraphPanel(graphview, "View 2,5d");
		}
		panel.getDockKey().setDockGroup(mainPanel);
		panel.getDockKey().setResizeWeight(1);
		panels.add(panel);
	}

	public void hideGraphview(GraphView graphview) {
		GraphPanel panel1 = null;
		for (GraphPanel panel : panels) {
			if (panel.getGraphView() == graphview) {
				desktop.remove((Dockable) panel);
				panel1 = panel;
			}
		}
		panels.remove(panel1);
	}

	public void restoreWorkspace() {
		desktop.clear();
		desktop.addDockable(top);
		GraphPanel panel2d = panels.get(0);
		desktop.split(top, panel2d, DockingConstants.SPLIT_RIGHT);
		desktop.split(top, bottom, DockingConstants.SPLIT_BOTTOM);
		desktop.split(panel2d, right, DockingConstants.SPLIT_RIGHT);
		for (int i = 1; i < panels.size(); i++) {
			desktop.split(panels.get(i - 1), panels.get(i),
					DockingConstants.SPLIT_BOTTOM);
		}
	}

	public void addTool(ToolView panel) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = tools.size();
		c.gridheight = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		if (panel != null) {
			toolPanel.add(panel, c);
			tools.add(panel);
		} else {
			c.weighty = 1.0;
			toolPanel.add(Box.createVerticalGlue(), c);
		}
		toolPanel.doLayout();
	}

	public DefaultTreeModel getFamiliesModel() {
		if (familiesModel == null) {
			DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode();
			rootTreeNode.setUserObject(new String("noname"));
			familiesModel = new DefaultTreeModel(rootTreeNode);
			familiesTree.setModel(familiesModel);
		}
		return familiesModel;

	}

	public DefaultTableModel getDetailsModel() {
		if (detailsModel == null) {
			detailsModel = new DefaultTableModel();
			detailsTable.setModel(detailsModel);
		}
		return detailsModel;
	}

	public void resetSliders() {
		for (GraphPanel panel : panels) {
			panel.resetSliders();
		}
	}

	public DefaultMutableTreeNode getSelectedNode() {
		return node;
	}

	class TreeHandler implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent evt) {
			node = (DefaultMutableTreeNode) familiesTree
					.getLastSelectedPathComponent();
			if (node == null)
				return;
			Object data = node.getUserObject();
			Application.getInstance().getController().updatePedigree(data);
		}
	}

	class TreeRenderer extends DefaultTreeCellRenderer {

		public TreeRenderer() {
			super();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			setIconTextGap(8);

			if (node.getUserObject() instanceof Graph) {
				setIcon(Application.getInstance().getImage("pedigree"));
			} else {
				setIcon(Application.getInstance().getImage("folder"));
			}

			return this;
		}
	}

	public void setShowView3D(boolean show) {
		view3dMenuItem.setVisible(show);
	}

	public void setShowView2D(boolean show) {
		view2dMenuItem.setVisible(show);
	}

}
