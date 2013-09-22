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

package core;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import pedviz.algorithms.ErrorChecking;
import pedviz.algorithms.FamilySplitter;
import pedviz.algorithms.GraphRepair;
import pedviz.algorithms.HierarchieUpDown;
import pedviz.algorithms.Highlighter;
import pedviz.algorithms.RubberBands;
import pedviz.algorithms.SameParents;
import pedviz.algorithms.sugiyama.RandomSplitter;
import pedviz.algorithms.sugiyama.Splitter;
import pedviz.algorithms.sugiyama.SugiyamaLayout;
import pedviz.clustering.clique.calc.Calc;
import pedviz.clustering.clique.ui.db.DataBase;
import pedviz.graph.Cluster;
import pedviz.graph.Graph;
import pedviz.graph.GraphMetaData;
import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.haplotype.Haplotypes;
import pedviz.haplotype.MerlinHaplotypes;
import pedviz.io.CsvGraphExporter;
import pedviz.io.CsvGraphLoader;
import pedviz.io.DatabaseGraphLoader;
import pedviz.io.FileGraphLoader;
import pedviz.io.GraphIOException;
import pedviz.io.PedGraphLoader;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView;
import pedviz.view.GraphView2D;
import pedviz.view.GraphView3D;
import pedviz.view.NodeEvent;
import pedviz.view.NodeListener;
import pedviz.view.rules.ColorRule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.rules.ShapeRule2;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;
import pedviz.view.symbols.SymbolSexUndesignated;
import pedviz.view.symbols3d.SymbolSexFemale3d;
import pedviz.view.symbols3d.SymbolSexFemaleLow;
import pedviz.view.symbols3d.SymbolSexMale3d;
import pedviz.view.symbols3d.SymbolSexMaleLow;
import pedviz.view.symbols3d.SymbolSexUndesignated3d;
import pedviz.view.symbols3d.SymbolSexUndesignatedLow;
import view.AppView;
import view.dialogs.DialogAbout;
import view.dialogs.DialogDatabase;
import view.dialogs.DialogErrors;
import view.dialogs.DialogExport;
import view.dialogs.DialogInfo;
import view.dialogs.DialogOpen;
import view.dialogs.DialogProgress;
import view.dialogs.DialogQuestionnaire;
import view.dialogs.DialogTools;
import view.tools.FilterView;
import view.tools.FisheyeView;
import view.tools.GenotypesView;
import view.tools.HaplotypesView;
import view.tools.HighlightingView;
import view.tools.LayoutView;
import view.tools.MaxCliqueView;
import view.tools.QualitativeTraitsView;
import view.tools.ToolView;
import view.tools.TraitsView;
import core.tools.FilterController;
import core.tools.FisheyeController;
import core.tools.GenotypesController;
import core.tools.HaplotypesController;
import core.tools.HighlightingController;
import core.tools.LayoutController;
import core.tools.MaxCliqueController;
import core.tools.QualitativeTraitsController;
import core.tools.TraitsController;

/**
 * AppController
 * 
 * @author Luki
 * 
 */
public class AppController implements NodeListener {

	private static final int VIEW2D = 1;

	private static final int VIEW3D = 2;

	private AppView view;

	private DefaultNodeView defaultNodeView;

	private DefaultEdgeView defaultEdgeView;

	private int highlighting = Highlighter.MATERNAL;

	private boolean synchHighlighter = false;

	private Splitter splitter = new RandomSplitter();

	private Vector<GraphView> graphviews;

	private Vector<String> traitsId, doubleTraitsId;

	private HashMap<String, Question> traits, doubleTraits;

	private Graph cgraph = null;

	private ToolView tool;

	private boolean useQuestions = false;

	private Vector<Graph> graphs;

	private Haplotypes haplotypes;

	private DialogTools dialogGenoHaplo, dialogFisheye, dialogClustering;

	private FisheyeController fisheyeController;

	private HaplotypesView haploView;

	private MaxCliqueView clusteringView;

	private Vector<ToolView> tools;

	private String currentDir = "", graphName = "";

	public AppController() {

		graphviews = new Vector<GraphView>();
		graphs = new Vector<Graph>();

		traitsId = new Vector<String>();
		doubleTraitsId = new Vector<String>();
		traits = new HashMap<String, Question>();
		doubleTraits = new HashMap<String, Question>();

		tools = new Vector<ToolView>();

		defaultNodeView = new DefaultNodeView();
		defaultNodeView.setColor(Color.DARK_GRAY);
		defaultNodeView.addHintAttribute("pid");

		defaultEdgeView = new DefaultEdgeView();
		defaultEdgeView.setHighlightedColor(Color.red);

	}

	private GraphView createGraphview(int mode) {

		GraphView graphview = null;
		if (mode == VIEW2D) {
			graphview = new GraphView2D();
		} else {
			graphview = new GraphView3D();
		}
		graphview.setSelectionEnabled(true);
		graphview.setMultiselection(true);
		graphview.addNodeListener(this);
		graphview.setBackgroundColor(Color.white);

		if (Application.getInstance().isJ3dInstalled() && mode == VIEW3D) {
			GraphView3D graphview3D = (GraphView3D) graphview;
			boolean flag = !Application.getInstance().isSet("no_antialiasing");
			graphview3D.setAntialiasingEnabled(flag);
			flag = !Application.getInstance().isSet("no_transparency");
			graphview3D.setTransparencyEnabled(flag);
			flag = !Application.getInstance().isSet("no_light");
			graphview3D.setLightEnabled(flag);
			flag = !Application.getInstance().isSet("low_quality");
			graphview3D.setMaterialEnabled(flag);
		}

		return graphview;
	}

	/**
	 * Sets the View Object
	 * 
	 * @param view
	 */
	public void setView(AppView view) {
		this.view = view;

		String mode = Application.getInstance()
				.getProperty("pedvizapi.ui.view");
		if (mode != null) {
			if (mode.toUpperCase().contains("2D")) {
				GraphView graphView2d = createGraphview(VIEW2D);
				view.addGraphview(graphView2d);
				graphviews.add(graphView2d);
				view.setShowView2D(true);
			}

			if (mode.toUpperCase().contains("3D")) {
				if (Application.getInstance().isJ3dInstalled()) {
					GraphView graphView3d = createGraphview(VIEW3D);
					view.addGraphview(graphView3d);
					graphviews.add(graphView3d);
					view.setShowView3D(true);
				}
			}
		} else {
			GraphView graphView2d = createGraphview(VIEW2D);
			view.addGraphview(graphView2d);
			graphviews.add(graphView2d);
			view.setShowView2D(true);

			if (Application.getInstance().isJ3dInstalled()) {
				GraphView graphView3d = createGraphview(VIEW3D);
				view.addGraphview(graphView3d);
				graphviews.add(graphView3d);
				view.setShowView3D(true);
			}

		}
		registerTools();
		view.restoreWorkspace();
		view.setFamiliesEnabled(false);
		view.setControlsEnabled(false);
		view.setActionsEnabled(false);
		view.setUseQuestions(false);
	}

	/**
	 * Registers a Tool
	 * 
	 * @param tool
	 * @param dialog
	 */
	private void registerTool(ToolView tool, DialogTools dialog) {
		if (dialog == null) {
			// sidebar
			view.addTool(tool);
		} else {
			dialog.addTool(tool);
		}
		tools.add(tool);
	}

	/**
	 * Registers all Tools
	 * 
	 */
	private void registerTools() {
		registerTool(new TraitsView(new TraitsController()), null);
		registerTool(new QualitativeTraitsView(
				new QualitativeTraitsController()), null);
		registerTool(new FilterView(new FilterController()), null);
		registerTool(new HighlightingView(new HighlightingController()), null);
		if (Application.getInstance().isJ3dInstalled()) {
			registerTool(new LayoutView(new LayoutController()), null);
		}
		// dummy item in sidebar
		registerTool(null, null);

		// tools in dialog
		fisheyeController = new FisheyeController();
		FisheyeView fisheyeView = new FisheyeView(fisheyeController);
		haploView = new HaplotypesView(new HaplotypesController());
		GenotypesView genoView = new GenotypesView(new GenotypesController());
		clusteringView = new MaxCliqueView(new MaxCliqueController());

		dialogGenoHaplo = new DialogTools(view);
		dialogFisheye = new DialogTools(view);
		dialogFisheye.setTitle("Fisheye");

		dialogClustering = new DialogTools(view);
		dialogClustering.setTitle("Clustering");
		dialogClustering.setSize(715, 334);

		registerTool(fisheyeView, dialogFisheye);
		registerTool(haploView, dialogGenoHaplo);
		registerTool(genoView, dialogGenoHaplo);
		registerTool(clusteringView, dialogClustering);

	}

	public void hideGraphView3D(ActionEvent e) {
		JCheckBoxMenuItem s = (JCheckBoxMenuItem) e.getSource();
		if (s.isSelected()) {
			GraphView graphView3d = createGraphview(VIEW3D);
			view.addGraphview(graphView3d);
			graphviews.add(graphView3d);
			view.restoreWorkspace();
			if (cgraph != null) {
				buildRules(cgraph.getMetaData(), graphView3d);
				graphView3d.setGraph(layout(cgraph, VIEW3D));
				graphView3d.centerGraph();
			}
			graphView3d.updateGraphView();
		} else {
			GraphView g = null;
			for (GraphView graphview : graphviews) {
				if (graphview instanceof GraphView3D) {
					g = graphview;
				}
			}
			if (g != null) {
				view.hideGraphview(g);
				graphviews.remove(g);

			}
		}
	}

	public void hideGraphView2D(ActionEvent e) {
		JCheckBoxMenuItem s = (JCheckBoxMenuItem) e.getSource();
		if (s.isSelected()) {
			GraphView graphView2d = createGraphview(VIEW2D);
			view.addGraphview(graphView2d);
			graphviews.add(graphView2d);
			view.restoreWorkspace();
			if (cgraph != null) {
				buildRules(cgraph.getMetaData(), graphView2d);
				graphView2d.setGraph(layout(cgraph, VIEW2D));
				graphView2d.centerGraph();
			}
			graphView2d.updateGraphView();
		} else {
			GraphView g = null;
			for (GraphView graphview : graphviews) {
				if (graphview instanceof GraphView2D) {
					g = graphview;
				}
			}
			if (g != null) {
				view.hideGraphview(g);
				graphviews.remove(g);

			}
		}
	}

	/**
	 * Loads the Graph from File
	 * 
	 */
	public void loadFromFile() {
		try {
			FileGraphLoader loader = null;
			Graph graph = null;
			boolean useErrorChecking = false;

			JFileChooser dialog = new JFileChooser();
			dialog.addChoosableFileFilter(new CsvFilter());
			dialog.setMultiSelectionEnabled(false);
			dialog.showOpenDialog(view);

			if (dialog.getSelectedFile() != null) {
				String filename = dialog.getSelectedFile().getPath();
				currentDir = dialog.getSelectedFile().getParent();
				graph = new Graph();

				if (filename.toLowerCase().endsWith(".ped")) {
					// Pre-makeped
					loader = new PedGraphLoader(filename);
					view.setWaitCursor();

					loader.load(graph);

					useErrorChecking = false; // TODO dialog for error
					// checking
					view.setDefaultCursor();
				} else {
					// csv-file
					loader = createGraphLoader(filename);
					DialogOpen dialogOpen = new DialogOpen(view);
					dialogOpen.setAllowImportTraits(false);

					dialogOpen.setColData(loader.getTraits());

					if (dialogOpen.execute()) {
						loader.setSettings(dialogOpen.getFam(), dialogOpen
								.getId(), dialogOpen.getMom(), dialogOpen
								.getDad(), dialogOpen.getSex());

						view.setWaitCursor();

						loader.load(graph);

						graph.getMetaData()
								.setFemale(dialogOpen.getSexFemale());
						graph.getMetaData().setMale(dialogOpen.getSexMale());

						view.setDefaultCursor();

						useErrorChecking = dialogOpen.useErrorChecking();
					} else {
						graph = null;
						return;
					}
				}
				resetTraits();
				addTraits(loader.getTraits());
				sortTraits();
				graph.setName(filename);

				defaultNodeView.setHintAttribute(graph.getMetaData().get(
						GraphMetaData.PID));
				buildRules(graph.getMetaData());

				loadCompleteGraph(graph);

				if (useErrorChecking) {
					runErrorChecking();
				}
				graph.clear();
				graph = null;
			}
		} catch (GraphIOException e) {
			view.setDefaultCursor();
			Application.getInstance().handleException(e);
		}
	}

	/**
	 * Loads the Graph fom DB
	 * 
	 */
	public void loadFromDatabase() {
		DialogDatabase dialog = new DialogDatabase(view);
		PostgreDB db = new PostgreDB("pedviz.properties");
		if (db.connect()) {

			dialog.setConnection(db.getLabel());
			dialog.setTablesData((db.getTables()));
			if (dialog.execute()) {
				DialogOpen dialogOpen = new DialogOpen(view);
				// !!!!!!!!!!!!
				dialogOpen.setAllowImportTraits(false);
				resetTraits();
				addTraits(db.getColumns(dialog.getTable()));
				dialogOpen.setColData(db.getColumns(dialog.getTable()));
				if (dialogOpen.execute()) {
					view.setWaitCursor();
					Graph graph = new Graph();
					DatabaseGraphLoader loader = new DatabaseGraphLoader(db,
							dialog.getTable());
					loader.setSettings(dialogOpen.getFam(), dialogOpen.getId(),
							dialogOpen.getMom(), dialogOpen.getDad(),
							dialogOpen.getSex());

					loader.load(graph);

					graph.getMetaData().setFemale(dialogOpen.getSexFemale());
					graph.getMetaData().setMale(dialogOpen.getSexMale());

					DialogQuestionnaire d = new DialogQuestionnaire(view);

					if (d.execute()) {
						QuestionnaireController.importTraits(graph, d
								.getSelection());

						for (Question question : d.getSelection()) {
							addQuestion(question);
						}

						useQuestions = true;
					}

					sortTraits();
					graph.setName(dialog.getTable());

					defaultNodeView.setHintAttribute(graph.getMetaData().get(
							GraphMetaData.PID));
					buildRules(graph.getMetaData());

					loadCompleteGraph(graph);

					if (dialogOpen.useErrorChecking()) {
						runErrorChecking();
					}

					view.setDefaultCursor();
				}
			}
			db.close();
		}
	}

	public void runErrorChecking() {
		ErrorChecking errors = new ErrorChecking(graphs);
		errors.run();
		if (errors.getErrors().size() > 0) {
			DialogErrors errDialog = new DialogErrors(view);
			errDialog.setErrors(errors.getErrors());
			if (errDialog.execute()) {
				view.setWaitCursor();
				Vector<String> log = GraphRepair.insertMissingParents(graphs);
				String temp = "";
				for (String message : log) {
					temp += message + "\n";
				}

				Vector<Graph> copyGraphs = (Vector<Graph>) graphs.clone();

				for (Graph graph : copyGraphs) {
					if (graph.getNodes().size() <= 1) {
						graphs.remove(graph);
					}
				}

				DefaultTreeModel model = view.getFamiliesModel();
				DefaultMutableTreeNode root = new DefaultMutableTreeNode();
				root.setUserObject(graphName);
				model.setRoot(root);

				for (int i = 0; i < graphs.size(); i++) {
					Graph family = graphs.get(i);
					family.setName("Family_" + family.getFamId() + " ("
							+ family.getSize() + ")");

					family.buildHierarchie(new HierarchieUpDown());

					DefaultMutableTreeNode child = new DefaultMutableTreeNode();
					child.setUserObject(family);
					root.add(child);
				}

				model.reload();

				view.setDefaultCursor();
				DialogInfo dialog = new DialogInfo(view);
				dialog.setMessage(temp);
				dialog.execute();

			}
		} else {
			JOptionPane.showMessageDialog(null, "No Errors :)");
		}
		update();
	}

	private void buildRules(GraphMetaData meta) {
		for (GraphView graphview : graphviews) {
			buildRules(meta, graphview);
		}
	}

	private void buildRules(GraphMetaData meta, GraphView graphview) {
		String sex = meta.get(GraphMetaData.SEX);
		String femaleChar = meta.getFemale().toString();
		String maleChar = meta.getMale().toString();

		graphview.getRules().clear();
		if (Application.getInstance().isJ3dInstalled()) {
			if (graphview instanceof GraphView3D) {
				if (Application.getInstance().isSet("low_quality")) {
					graphview.addRule(new ShapeRule(sex, femaleChar,
							new SymbolSexFemaleLow()));
					graphview.addRule(new ShapeRule(sex, maleChar,
							new SymbolSexMaleLow()));
					graphview.addRule(new ShapeRule2(sex, femaleChar, maleChar,
							new SymbolSexUndesignatedLow()));
					graphview.addRule(new ColorRule("virtual", "1",
							Color.LIGHT_GRAY));
				} else {
					SymbolSexFemale3d female = new SymbolSexFemale3d();
					female.setStyle(SymbolSexFemale3d.STYLE_TURM);
					graphview.addRule(new ShapeRule(sex, femaleChar, female));
					SymbolSexMale3d male = new SymbolSexMale3d();
					male.setStyle(SymbolSexMale3d.STYLE_TURM);
					graphview.addRule(new ShapeRule(sex, maleChar, male));
					SymbolSexUndesignated3d other = new SymbolSexUndesignated3d();
					other.setStyle(SymbolSexMale3d.STYLE_TURM);
					graphview.addRule(new ShapeRule2(sex, femaleChar, maleChar,
							other));
					graphview.addRule(new ColorRule("virtual", "1",
							Color.LIGHT_GRAY));
				}
			}
		}
		if (graphview instanceof GraphView2D) {
			graphview
					.addRule(new ShapeRule(sex, maleChar, new SymbolSexMale()));
			graphview.addRule(new ShapeRule(sex, femaleChar,
					new SymbolSexFemale()));
			graphview.addRule(new ShapeRule2(sex, femaleChar, maleChar,
					new SymbolSexUndesignated()));
			graphview.addRule(new ColorRule("virtual", "1", Color.LIGHT_GRAY));
		}

	}

	/**
	 * Creates a CsvGraphLoad object
	 * 
	 * @param filename
	 * @return
	 */
	private CsvGraphLoader createGraphLoader(String filename)
			throws GraphIOException {
		CsvGraphLoader loader = new CsvGraphLoader(filename, ",");
		if (loader.getTraits().size() == 1) {
			loader = new CsvGraphLoader(filename, ";");
			if (loader.getTraits().size() == 1) {
				loader = new CsvGraphLoader(filename, " ");
				if (loader.getTraits().size() == 1) {
					loader = new CsvGraphLoader(filename, "\t");
					while (loader.getTraits().size() == 1) {
						String splitter = JOptionPane
								.showInputDialog("Please enter the split-charakter:");
						if (splitter != null) {
							loader = new CsvGraphLoader(filename, splitter);
						} else {
							return null;
						}
					}
				}
			}
		}
		return loader;
	}

	/**
	 * Loads the complete Graph
	 * 
	 * @param graph
	 */
	private void loadCompleteGraph(Graph graph) {
		// reset
		this.cgraph = null;
		for (GraphView graphview : graphviews) {
			graphview.setGraph(null);
			graphview.updateGraphView();
		}
		for (Graph graph2 : graphs) {
			graph2.clear();
		}
		graphs.clear();

		// preproc.
		DialogProgress dialog = new DialogProgress(view, "Load Pedigree...");

		FamilySplitter splitter = new FamilySplitter(graph);
		dialog.addTask(splitter);
		dialog.run();
		graphName = graph.getName();
		view.setPedigreeName(graphName);
		updateQuestionValues(graph);
		graph.clear();
		ArrayList<Graph> families = splitter.getFamilies();
		graphs.addAll(families);
		updateTreeModel();

		// updateTools
		for (ToolView tool : tools) {
			if (tool != null) {
				tool.getController().afterGraphLoaded(tool, graph);
			}
		}

		view.setFamiliesEnabled(true);
		view.setControlsEnabled(false);
		view.setActionsEnabled(false);
		view.setUseQuestions(true);

		haplotypes = null;
		haploView.setEnabled(false);
		haploView.setData(null, null);
	}

	private void updateTreeModel() {
		DefaultTreeModel model = view.getFamiliesModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		root.setUserObject(graphName);
		model.setRoot(root);

		for (int i = 0; i < graphs.size(); i++) {
			Graph family = graphs.get(i);
			family.setName("Family_" + family.getFamId() + " ("
					+ family.getSize() + ")");

			SameParents sameParents = new SameParents(family);
			sameParents.run();

			family.buildHierarchie(new HierarchieUpDown());

			DefaultMutableTreeNode child = new DefaultMutableTreeNode();
			child.setUserObject(family);
			root.add(child);
		}

		model.reload();
	}

	public void importHaplotypes() {
		JFileChooser dialog = new JFileChooser();
		dialog.addChoosableFileFilter(new MerlinChrFilter());
		dialog.setMultiSelectionEnabled(false);
		dialog.showOpenDialog(view);
		if (dialog.getSelectedFile() != null) {
			String filename = dialog.getSelectedFile().getPath();
			JFileChooser dialog2 = new JFileChooser();
			dialog2.addChoosableFileFilter(new MapFilter());
			dialog2.setMultiSelectionEnabled(false);
			dialog2.showOpenDialog(view);
			if (dialog2.getSelectedFile() != null) {
				String mapfile = dialog2.getSelectedFile().getPath();

				haplotypes = new MerlinHaplotypes(filename, mapfile);
				haploView.setEnabled(true);
				haploView.setData(haplotypes.getMarkers(), traits);

			}
		}
	}

	/**
	 * 
	 */
	public void changePhenotype() {
		DialogQuestionnaire d = new DialogQuestionnaire(view);
		if (d.execute()) {
			view.setWaitCursor();
			removeQuestions();
			for (Graph graph : graphs) {
				QuestionnaireController.importTraits(graph, d.getSelection());
			}
			for (Question question : d.getSelection()) {
				addQuestion(question);
			}
			sortTraits();
			updateQuestionValues(graphs);

			for (ToolView tool : tools) {
				if (tool != null) {
					tool.getController().updateTraits(tool);
				}
			}
			view.setDefaultCursor();
		}

	}

	/**
	 * Displays a Graph object
	 * 
	 * @param object
	 */
	public void updatePedigree(Object object) {
		if (object instanceof Graph) {
			Graph graph = (Graph) object;
			if (getGraph() != graph) {
				displayGraph(graph);
				centerGraph();
				this.cgraph = graph;
				for (ToolView tool : tools) {
					if (tool != null) {
						tool.getController().afterGraphSelected(tool, graph);
					}
				}
				for (GraphView graphview : graphviews) {
					graphview.updateGraphView();
				}
			}
		}
	}

	/**
	 * Displays a Graph object
	 * 
	 * @param graph
	 */
	private void displayGraph(Graph graph) {
		for (GraphView graphview : graphviews) {
			int mode = graphview instanceof GraphView2D ? VIEW2D : VIEW3D;
			graphview.setGraph(layout(graph, mode));
			graphview.updateGraphView();
		}
		view.setActionsEnabled(true);
		view.setControlsEnabled(true);
		view.setUseQuestions(useQuestions);
		view.setSplitEnabled(false);
	}

	/**
	 * Updates Details on Demand
	 * 
	 * @param node
	 */
	public void updateDetails(Node node) {
		String[][] data = new String[traitsId.size()][2];
		for (int i = 0; i < traitsId.size(); i++) {
			Question question = traits.get(traitsId.get(i));
			data[i][0] = question.getTrait();
			if (node.getUserData(traitsId.get(i)) != null) {
				data[i][1] = node.getUserData(question.getTrait()).toString();
			} else {
				data[i][1] = "-";
			}
		}
		DefaultTableModel model = view.getDetailsModel();
		model.setDataVector(data, new String[] { "Trait", "Value" });
	}

	public void showGenoHaploDialog() {
		if (dialogGenoHaplo.execute()) {
			for (ToolView tool : dialogGenoHaplo.getTools()) {
				tool.getController().updatePressed(tool);
			}
		}
	}

	public void useFisheye() {
		fisheyeController.turnonoff();
	}

	public void showFisheyeDialog() {
		if (dialogFisheye.execute()) {
			for (ToolView tool : dialogFisheye.getTools()) {
				tool.getController().updatePressed(tool);
			}
		}
	}

	/**
	 * NodeEvents
	 */
	public void onNodeEvent(NodeEvent event) {
		switch (event.getType()) {
		case NodeEvent.MOUSE_ENTER:
			updateDetails(event.getNode());

			if (highlighting != -1) {
				ArrayList<Node> nodes = Highlighter.findLineOfDescents(
						getGraph(), event.getNode(), highlighting);

				if (synchHighlighter) {
					for (GraphView graphview : graphviews) {
						graphview.unHighlightAll();
						graphview.highlight(nodes);
					}
				} else {
					event.getGraphView().unHighlightAll();
					event.getGraphView().highlight(nodes);
				}
			}
			break;
		case NodeEvent.MOUSE_LEAVE:
			if (synchHighlighter) {
				for (GraphView graphview : graphviews) {
					graphview.unHighlightAll();
				}
			} else {
				event.getGraphView().unHighlightAll();
			}
			break;

		case NodeEvent.SELECTED:
			for (GraphView graphview : graphviews) {
				graphview.select(event.getNode());
				if (graphview instanceof GraphView2D) {
					graphview.updateGraphView();
				}
			}
			view.setSplitEnabled(graphviews.get(0).getSelectionCount() > 0);
			break;
		case NodeEvent.DESELECTED:
			for (GraphView graphview : graphviews) {
				graphview.deselect(event.getNode());
				if (graphview instanceof GraphView2D) {
					graphview.updateGraphView();
				}
			}
			view.setSplitEnabled(graphviews.get(0).getSelectionCount() > 0);
			break;
		case NodeEvent.ALL_DESELECTED:
			for (GraphView graphview : graphviews) {
				graphview.deselect();
				if (graphview instanceof GraphView2D) {
					graphview.updateGraphView();
				}
			}
			view.setSplitEnabled(false);
			break;
		}
	}

	/**
	 * Exports Graph as CSV
	 * 
	 */
	public void exportAsCsv() {
		JFileChooser dialog = new JFileChooser();
		dialog.addChoosableFileFilter(new CsvFilter());
		dialog.setMultiSelectionEnabled(false);
		dialog.showSaveDialog(view);
		if (dialog.getSelectedFile() != null) {
			String filename = dialog.getSelectedFile().getPath();
			view.setWaitCursor();
			try {
				CsvGraphExporter exporter = new CsvGraphExporter(filename);
				exporter.save(getGraph());
				view.setDefaultCursor();
				JOptionPane.showMessageDialog(view, "Wrote File: " + filename,
						"Export CSV", JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e) {
				view.setDefaultCursor();
				JOptionPane.showMessageDialog(view,
						"Error:  " + e.getMessage(), "Export CSV",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/**
	 * Export the Viz as JPEG
	 * 
	 */
	public void exportAsJpeg() {
		DialogExport dialog = new DialogExport(view);
		dialog.setFilename(currentDir + "\\" + cgraph.getName() + ".jpg");
		Vector<String> views = new Vector<String>();
		for (GraphView g : graphviews) {
			views.add(g instanceof GraphView2D ? "View 2d" : "View 2,5d");
		}
		dialog.setViews(views);
		if (dialog.execute()) {
			view.setWaitCursor();
			graphviews.get(dialog.getIndex()).exportJPEG(dialog.getFilename(),
					dialog.getScale(), dialog.isGrayScale());
			view.setDefaultCursor();
			JOptionPane.showMessageDialog(view, "Wrote File: "
					+ dialog.getFilename(), "Export JPEG",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Creates the layout for a Graph object.
	 * 
	 * @param graph
	 * @param mode
	 *            AppView.VIEW_3D for 3d-Layout, AppView.VIEW_2D for 2d-Layout
	 * @return
	 */
	private LayoutedGraph layout(Graph graph, int mode) {
		view.setWaitCursor();
		DialogProgress dialog = new DialogProgress(view, "Layout Pedigree...");
		SugiyamaLayout layout = new SugiyamaLayout(graph, defaultNodeView,
				defaultEdgeView);
		if (mode == VIEW3D && splitter != null) {
			layout.setSplitter(splitter);
		}
		dialog.addTask(layout);
		RubberBands rubberBands = new RubberBands(layout.getLayoutGraph());
		rubberBands.setDepth(550f);
		if (mode == VIEW2D)
			rubberBands.setHorizontalGap(3);
		rubberBands.setVerticalSpacing(80f);
		dialog.addTask(rubberBands);
		dialog.run();
		view.setDefaultCursor();
		return layout.getLayoutGraph();
	}

	/**
	 * Extracts selection
	 * 
	 */
	public void splitSelection() {
		if (graphviews.get(0).getSelectionCount() > 1) {
			String splitname = JOptionPane
					.showInputDialog("Please enter a name for this part of the pedigree");
			if (splitname != null) {
				Cluster cluster = new Cluster();
				for (Node node : graphviews.get(0).getSelection()) {
					cluster.addNode(node);
				}
				Graph newGraph = new Graph(cluster);
				newGraph.setName(splitname);
				newGraph.buildHierarchie(new HierarchieUpDown());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						newGraph);
				view.getSelectedNode().add(node);
				for (GraphView graphview : graphviews) {
					graphview.deselect();
					if (graphview instanceof GraphView2D) {
						graphview.updateGraphView();
					}
				}
				view.getFamiliesModel().reload();
				view.setSplitEnabled(graphviews.get(0).getSelectionCount() > 0);
			}
		}
	}

	/**
	 * Clears selection
	 * 
	 */
	public void clearSelection() {
		for (GraphView graphview : graphviews) {
			graphview.deselect();
		}
		view.setSplitEnabled(false);
	}

	/**
	 * Returns all GraphView objects
	 * 
	 * @return
	 */
	public Vector<GraphView> getGraphViews() {
		return graphviews;
	}

	/**
	 * Returns View
	 * 
	 * @return
	 */
	public Frame getView() {
		return view;
	}

	/**
	 * Exit
	 * 
	 */
	public void exit() {
		System.exit(0);
	}

	/**
	 * Turns on/off rotating on X-Axis
	 * 
	 * @param e
	 */
	public void rotateX(ActionEvent e) {
		JCheckBoxMenuItem s = (JCheckBoxMenuItem) e.getSource();
		for (GraphView graphview : graphviews) {
			if (graphview instanceof GraphView3D) {
				((GraphView3D) graphview).setXRotatingEnabled(s.isSelected());
			}
		}
	}

	/**
	 * Turns on/off rotating on Y-Axis
	 * 
	 * @param e
	 */
	public void rotateY(ActionEvent e) {
		JCheckBoxMenuItem s = (JCheckBoxMenuItem) e.getSource();
		for (GraphView graphview : graphviews) {
			if (graphview instanceof GraphView3D) {
				((GraphView3D) graphview).setYRotatingEnabled(s.isSelected());
			}
		}
	}

	/**
	 * Shows About-Dialog
	 * 
	 */
	public void showAboutDialog() {
		new DialogAbout(view).execute();
	}

	public void showClustering() {
		if (dialogClustering.execute()) {

			try {
				DataBase db = new DataBase();

				Calc calc_clique = new Calc();
				calc_clique.setArguments(clusteringView.buildParameters());
				calc_clique.addChangeListener(new ChangeListener() {

					public void stateChanged(ChangeEvent e) {
						// TODO Auto-generated method stub

					}

				});
				// setGraph: Graph->Pedigree -> setPedigree
				// (!!!insertMissingParents!!!)
				calc_clique.setGraph(cgraph, clusteringView.getAffectedTrait(),
						clusteringView.getSelectedTrait());
				calc_clique.setDatabase(db);
				calc_clique.setType(0);
				calc_clique.setRunMode((byte) 1);
				calc_clique.run();

				if (!calc_clique.getresultMsg().equals("")) {
					System.out.println("calc_clique.getresultMsg():\n"
							+ calc_clique.getresultMsg());
				}

				for (Node node : cgraph.getAllNodes()) {
					node.setUserData("setid", "");
				}

				Vector<Integer> cliques = calc_clique.getCliques();
				for (int setid : cliques) {
					Vector<Node> clique = calc_clique.getClique(setid);
					for (Node node : clique) {
						String temp = node.getUserData("setid").toString();
						node.setUserData("setid", temp + " " + setid);
					}
				}

				db.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Turns on/off edges
	 * 
	 */
	public void showEdges() {
		for (GraphView graphview : graphviews) {
			boolean visible = graphview.isEdgeVisible();
			graphview.setEdgeVisible(!visible);
		}
	}

	/**
	 * Centers the Graph
	 * 
	 */
	public void centerGraph() {
		view.resetSliders();
		for (GraphView graphview : graphviews) {
			graphview.centerGraph();
		}
	}

	public int getHighlighting() {
		return highlighting;
	}

	public void setHighlighting(int highlighting) {
		this.highlighting = highlighting;
	}

	public boolean isSynchHighlighter() {
		return synchHighlighter;
	}

	public void setSynchHighlighter(boolean synchHighlighter) {
		this.synchHighlighter = synchHighlighter;
	}

	public void setSplitter(Splitter splitter) {
		this.splitter = splitter;
		if (getGraph() != null) {
			displayGraph(getGraph());
		}
	}

	public Graph getGraph() {
		return cgraph;
	}

	public Vector<Graph> getAllGraphs() {
		return graphs;
	}

	public HashMap<String, Question> getQuestions() {
		return traits;
	}

	public HashMap<String, Question> getDoubleQuestion() {
		return doubleTraits;
	}

	public Vector<String> getTraits() {
		return traitsId;
	}

	public Vector<String> getDoubleTraits() {
		return doubleTraitsId;
	}

	public DefaultNodeView getDefaultNodeView() {
		return defaultNodeView;
	}

	public void update() {
		if (cgraph != null) {
			displayGraph(cgraph);
		}
	}

	public Haplotypes getHaplotypes() {
		return haplotypes;
	}

	private boolean isDouble(String trait) {
		try {
			Double d = Double.parseDouble(trait);
			return true;
		} catch (Exception ex) {
			return false;
		}

	}

	private void resetTraits() {
		traitsId.clear();
		traits.values().clear();
	}

	private void addTraits(Vector<String> traits) {
		for (String trait : traits) {
			addTrait(trait);
		}
	}

	private void addTrait(String trait) {
		Question question = new Question(trait, trait);
		question.setExtern(false);
		question.setTrait(trait);
		traits.put(question.getTrait(), question);
		traitsId.add(question.getTrait());
	}

	private void sortTraits() {
		Collections.sort(traitsId, new Comparator<String>() {

			public int compare(String arg0, String arg1) {
				Question question0 = traits.get(arg0);
				Question question1 = traits.get(arg1);
				if (question0.isExtern() && !question1.isExtern()) {
					return 1;
				} else if (!question0.isExtern() && question1.isExtern()) {
					return -1;
				} else {
					return question0.getTrait().compareTo(question1.getTrait());
				}
			}

		});
	}

	private void addQuestion(Question question) {
		question.setExtern(true);
		traits.put(question.getTrait(), question);
		traitsId.add(question.getTrait());
	}

	public void removeQuestions() {
		Vector<String> copyTraits = (Vector<String>) traitsId.clone();
		for (String trait : copyTraits) {
			Question question = traits.get(trait);
			if (question.isExtern()) {
				traits.remove(question);
				traitsId.remove(trait);
				doubleTraits.remove(question);
				doubleTraitsId.remove(trait);
				for (Graph graph : graphs) {
					for (Node node : graph.getAllNodes()) {
						node.removeUserData(trait);
					}
				}
			}
		}
	}

	private void updateQuestionValues(Vector<Graph> graphs) {
		doubleTraitsId.clear();
		doubleTraits.values().clear();
		for (Question question : traits.values()) {
			String column = question.getTrait();
			if (!question.isExtern() || question.getType() == Question.TEXTAREA) {
				question.values.clear();
			}
			boolean doubles = true;
			boolean found = false;
			for (Graph graph : graphs) {
				for (Node node : graph.getAllNodes()) {
					if (node.getUserData(column) != null) {
						found = true;
						String trait = node.getUserData(column).toString();
						if (doubles) {
							doubles = isDouble(trait);
						}
						if ((!question.isExtern() || question.getType() == Question.TEXTAREA)
								&& !question.values.contains(trait)) {
							question.values.add(trait);
						}
					}
				}
			}
			if (question.values.size() > 0 && found) {
				if (!question.isExtern()
						|| question.getType() == Question.TEXTAREA) {
					Collections.sort(question.values);
				}
				if (doubles) {
					doubleTraits.put(question.getTrait(), question);
					doubleTraitsId.add(question.getTrait());
				}
			}

		}
		Collections.sort(doubleTraitsId);
	}

	private void updateQuestionValues(Graph graph) {
		doubleTraitsId.clear();
		doubleTraits.values().clear();
		for (Question question : traits.values()) {
			String column = question.getTrait();
			if (!question.isExtern() || question.getType() == Question.TEXTAREA) {
				question.values.clear();
			}
			boolean doubles = true;
			boolean found = false;
			for (Node node : graph.getAllNodes()) {
				if (node.getUserData(column) != null) {
					found = true;
					String trait = node.getUserData(column).toString();
					if (doubles) {
						doubles = isDouble(trait);
					}
					if ((!question.isExtern() || question.getType() == Question.TEXTAREA)
							&& !question.values.contains(trait)) {
						question.values.add(trait);
					}
				}
			}
			if (question.values.size() > 0 && found) {
				if (!question.isExtern()
						|| question.getType() == Question.TEXTAREA) {
					Collections.sort(question.values);
				}
				if (doubles) {
					doubleTraits.put(question.getTrait(), question);
					doubleTraitsId.add(question.getTrait());
				}
			}

		}
		Collections.sort(doubleTraitsId);
	}

	class CsvFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith(".csv")
					|| f.getName().toLowerCase().endsWith(".ped");
		}

		public String getDescription() {
			return "Pedigree Files (*.csv; *.ped)";
		}
	}

	class JpegFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith(".jpg");
		}

		public String getDescription() {
			return "JPEGs";
		}
	}

	class MerlinChrFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith("chr");
		}

		public String getDescription() {
			return "Merlin Haplotypes (merlin.chr)";
		}
	}

	class MapFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith("map");
		}

		public String getDescription() {
			return "Map File (*.map)";
		}
	}

}
