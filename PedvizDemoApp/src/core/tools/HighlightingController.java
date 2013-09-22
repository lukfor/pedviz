package core.tools;

import pedviz.algorithms.Highlighter;
import pedviz.graph.Graph;
import pedviz.view.GraphView;
import view.tools.HighlightingView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class HighlightingController implements ToolController {

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {
		// ignored
	}

	public void updatePressed(ToolView sender) {

		AppController controller = Application.getInstance().getController();

		HighlightingView panel = (HighlightingView) sender;

		boolean synchHighlighter = panel.isSynchHighlighter();
		controller.setSynchHighlighter(synchHighlighter);

		int index = panel.getSelectedMode();
		switch (index) {
		case 0:
			controller.setHighlighting(Highlighter.MATERNAL);
			break;
		case 1:
			controller.setHighlighting(Highlighter.PATERNAL);
			break;
		case 2:
			controller.setHighlighting(Highlighter.MATERNAL_AND_PATERNAL);
			break;
		case 3:
			controller.setHighlighting(Highlighter.ANCESTORS);
			break;
		case 4:
			controller.setHighlighting(Highlighter.SUCCESSORS);
			break;
		case 5:
			controller.setHighlighting(Highlighter.SUCCESSORS_AND_ANCESTORS);
			break;
		case 6:
		default:
			controller.setHighlighting(-1);
			break;
		}

		for (GraphView view : controller.getGraphViews()) {
			view.setSelectionEnabled(controller.getHighlighting() > -1);
		}

	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		// ignored
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {

	}

}
