package core.tools;

import pedviz.algorithms.sugiyama.RandomSplitter;
import pedviz.algorithms.sugiyama.SicknessSplitter;
import pedviz.graph.Graph;
import view.tools.LayoutView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class LayoutController implements ToolController {

	public void resetPressed(ToolView sender) {
		// ignored
	}

	public void init(ToolView sender) {

	}

	public void updatePressed(ToolView sender) {

		AppController controller = Application.getInstance().getController();

		LayoutView panel = (LayoutView) sender;
		String trait = panel.getTrait();
		String value = panel.getValue();

		if (trait.equals("random")) {
			controller.setSplitter(new RandomSplitter());
		} else if (value != null) {
			SicknessSplitter splitter = new SicknessSplitter(
					new String[] { trait }, new String[] { value });
			controller.setSplitter(splitter);
		}
	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {

		AppController controller = Application.getInstance().getController();

		LayoutView panel = (LayoutView) sender;
		panel.setData(controller.getTraits(), controller.getQuestions());
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {
		AppController controller = Application.getInstance().getController();

		LayoutView panel = (LayoutView) sender;
		panel.setData(controller.getTraits(), controller.getQuestions());
	}

}
