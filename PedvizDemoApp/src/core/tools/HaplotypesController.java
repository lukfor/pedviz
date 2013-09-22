package core.tools;

import java.util.Vector;

import pedviz.graph.Graph;
import pedviz.view.symbols.SymbolHaplotypes;
import view.tools.HaplotypesView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class HaplotypesController implements ToolController {

	private SymbolHaplotypes symbol = null;

	public HaplotypesController() {

	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {
		// ignored
	}

	public void updatePressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		HaplotypesView panel = (HaplotypesView) sender;
		if (panel.showGenotypes()) {
			if (symbol == null) {
				symbol = new SymbolHaplotypes(controller.getHaplotypes());
				controller.getDefaultNodeView().addSymbol(symbol);
			}
			Vector<String> traits = panel.getSelectedTaits();
			controller.getHaplotypes().hideAllMarkers();
			for (String marker : traits) {
				int i = controller.getHaplotypes().getMarkers().indexOf(marker);
				controller.getHaplotypes().showMarker(i);
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

		if (symbol != null) {
			controller.getDefaultNodeView().getSymbols().remove(symbol);
			symbol = null;
		}

	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {

	}

}
