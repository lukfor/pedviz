package core.tools;

import pedviz.graph.Graph;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class MaxCliqueController implements ToolController {

	public MaxCliqueController() {

	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {

	}

	public void updatePressed(ToolView sender) {

	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		AppController controller = Application.getInstance().getController();
		sender.setData(controller.getTraits(), controller.getQuestions());
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		sender.setData(controller.getTraits(), controller.getQuestions());
	}

}
