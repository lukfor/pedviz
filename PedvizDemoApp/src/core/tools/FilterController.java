package core.tools;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import pedviz.algorithms.TestAlgo;
import pedviz.algorithms.filter.Filter;
import pedviz.algorithms.filter.NumberCondition;
import pedviz.algorithms.filter.TextCondition;
import pedviz.graph.Graph;
import pedviz.graph.Node;
import pedviz.view.GraphView;
import pedviz.view.animations.BlinkAnimation;
import view.AppView;
import view.tools.ConditionView;
import view.tools.FilterView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class FilterController implements ToolController {

	private Vector<BlinkAnimation> animations;

	private boolean filterUsed = false;

	DefaultMutableTreeNode qroot = null;

	public FilterController() {
		animations = new Vector<BlinkAnimation>();
	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		AppController controller = Application.getInstance().getController();
		FilterView filter = (FilterView) sender;
		filter.setData(controller.getTraits(), controller.getQuestions());
	}

	public void updateTraits(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		FilterView filter = (FilterView) sender;
		filter.setData(controller.getTraits(), controller.getQuestions());
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {
		FilterView filter = (FilterView) sender;
		if (filterUsed && !filter.isExtract()) {
			updatePressed(sender);
		}
	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {

		AppController controller = Application.getInstance().getController();
		FilterView filter = (FilterView) sender;

		if (!filter.isExtract()) {

			for (BlinkAnimation animation : animations) {
				animation.stop();
			}
			animations.clear();

			for (GraphView view : controller.getGraphViews()) {
				view.showAll();
			}

			filterUsed = false;
		} else {
			if (qroot != null) {
				AppView view = Application.getInstance().getView();
				DefaultTreeModel model = view.getFamiliesModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) model
						.getRoot();

				root.remove(qroot);
				qroot.removeAllChildren();
				qroot = null;
				model.reload();
			}
		}
	}

	public void updatePressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();

		FilterView filter = (FilterView) sender;

		Filter myFilter = new Filter();
		myFilter.setOperator(filter.getSelectedMode());
		for (ConditionView con : filter.getConditions()) {
			try {
				double min = Double.parseDouble(con.getMinValue());
				double max = Double.parseDouble(con.getMaxValue());
				myFilter.addCondition(new NumberCondition(con.getTrait(), con
						.getComperator(), min, max));
				System.out.println("....");

			} catch (Exception e) {
				myFilter
						.addCondition(new TextCondition(con.getTrait(), con
								.getComperator(), con.getMinValue(), con
								.getMaxValue()));

			}
		}

		if (!filter.isExtract()) {

			for (BlinkAnimation animation : animations) {
				animation.stop();
			}
			animations.clear();

			for (GraphView view : controller.getGraphViews()) {
				view.showAll();
			}

			Set<Node> test = new HashSet<Node>();
			test.addAll(myFilter.execute(controller.getGraph()));

			if (filter.getMode() == 0) {
				for (GraphView view : controller.getGraphViews()) {
					view.hideAll();
				}
				for (Node node : test) {
					for (GraphView view : controller.getGraphViews()) {
						view.show(node);
					}
				}
			}

			if (filter.getMode() == 1) {
				for (GraphView view : controller.getGraphViews()) {
					BlinkAnimation animation = new BlinkAnimation(view, test,
							Color.RED, Color.RED);
					animation.setInterval(500);
					animation.start();
					animations.add(animation);
				}

			}

			filterUsed = true;
		} else {
			int generations = filter.getGenerations();

			resetPressed(sender);

			AppView view = Application.getInstance().getView();
			DefaultTreeModel model = view.getFamiliesModel();

			if (qroot == null) {
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) model
						.getRoot();

				qroot = new DefaultMutableTreeNode();
				qroot.setUserObject("Results");
				root.add(qroot);
				model.reload();

			} else {
				qroot.removeAllChildren();
				model.reload();
			}

			for (Graph graph : controller.getAllGraphs()) {
				Vector<Node> nodes = new Vector<Node>();
				nodes.addAll(myFilter.execute(graph));
				Vector<Graph> graphs = TestAlgo.createGraphs(graph, nodes,
						generations);

				for (Graph subgraph : graphs) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode();
					child.setUserObject(subgraph);
					qroot.add(child);
				}

			}
			qroot.setUserObject("Results (" + qroot.getChildCount() + ")");
			if (qroot.getChildCount() == 0) {

				JOptionPane.showMessageDialog(null, "No persons found.",
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
			model.reload();
		}

	}

}
