package core.tools;

import java.awt.Color;
import java.util.Vector;

import pedviz.graph.Graph;
import pedviz.view.GraphView;
import pedviz.view.rules.ColorRule;
import view.tools.ToolView;
import view.tools.TraitsView;
import core.AppController;
import core.Application;

public class TraitsController implements ToolController {

	private ColorRule colorRule = null;

	public TraitsController() {

	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {
		// ignored
	}

	public void updatePressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		TraitsView panel = (TraitsView) sender;
		Object[][] table = panel.getRows();

		// remove old colorRule
		if (colorRule != null) {
			for (GraphView view : controller.getGraphViews()) {
				view.removeRule(colorRule);
			}
			colorRule = null;
		}

		// selection
		Vector<Object[]> selection = new Vector<Object[]>();
		for (int i = 0; i < table.length; i++) {
			Object[] row = table[i];
			if (((Boolean) row[0])) {
				selection.add(row);
			}
		}

		// colorRule
		if (selection.size() > 0) {
			Object[] row1 = selection.get(0);
			colorRule = new ColorRule((String) row1[1], (String) row1[2],
					(Color) row1[3]);

			for (int i = 1; i < selection.size(); i++) {
				Object[] row = selection.get(i);
				colorRule.addRule((String) row[1], (String) row[2],
						(Color) row[3]);
			}
			for (GraphView view : controller.getGraphViews()) {
				view.addRule(colorRule);
			}
		}

		// updateRules
		for (GraphView view : controller.getGraphViews()) {
			view.updateRules();
		}

	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		AppController controller = Application.getInstance().getController();

		TraitsView panel = (TraitsView) sender;
		panel.setData(controller.getTraits(), controller.getQuestions());
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {
		AppController controller = Application.getInstance().getController();

		TraitsView panel = (TraitsView) sender;
		panel.setData(controller.getTraits(), controller.getQuestions());
	}

}
