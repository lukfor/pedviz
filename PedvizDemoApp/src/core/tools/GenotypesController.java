package core.tools;

import java.util.Vector;

import pedviz.graph.Graph;
import pedviz.view.symbols.SymbolGenotypes;
import view.tools.GenotypesView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class GenotypesController implements ToolController {

	private SymbolGenotypes symbol = null;

	public GenotypesController() {

	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {
		// ignored
	}

	public void updatePressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		GenotypesView panel = (GenotypesView) sender;
		Vector<String> traits = panel.getSelectedTaits();

		if (panel.showGenotypes()) {

			String[] temp = new String[traits.size()];
			for (int i = 0; i < temp.length; i++) {
				Object t = traits.get(i);
				temp[i] = t.toString();
			}

			if (symbol == null) {
				symbol = new SymbolGenotypes(temp);
				controller.getDefaultNodeView().addSymbol(symbol);
			} else {
				symbol.setTraits(temp);
			}
			controller.update();
		} else {
			if (symbol != null) {
				controller.getDefaultNodeView().getSymbols().remove(symbol);
				symbol = null;
			}
			controller.update();
		}
	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		AppController controller = Application.getInstance().getController();

		GenotypesView panel = (GenotypesView) sender;
		panel.setData(controller.getTraits(), controller.getQuestions());
		if (symbol != null) {
			controller.getDefaultNodeView().getSymbols().remove(symbol);
			symbol = null;
		}

	}

	public void updateTraits(ToolView sender) {
		AppController controller = Application.getInstance().getController();

		GenotypesView panel = (GenotypesView) sender;
		panel.setData(controller.getTraits(), controller.getQuestions());

	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

}
