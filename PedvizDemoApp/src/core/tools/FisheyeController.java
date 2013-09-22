package core.tools;

import pedviz.graph.Graph;
import pedviz.view.GraphView;
import pedviz.view.GraphView2D;
import pedviz.view.effects.Effect;
import pedviz.view.effects.FisheyeEffect;
import view.tools.FisheyeView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class FisheyeController implements ToolController {

	private FisheyeEffect effect = null;

	private float dx = 7, dy = 7, sz = 2;

	public FisheyeController() {

	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {

	}

	public void updatePressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		FisheyeView view = (FisheyeView) sender;
		if (effect != null) {
			dx = (float) view.getDX();
			dy = (float) view.getDY();
			sz = (float) view.getSZ();
			effect.setDX(dx);
			effect.setDY(dy);
			effect.setSZ(sz);
		}
		for (GraphView graphview : controller.getGraphViews()) {
			if (graphview instanceof GraphView2D) {

				graphview.updateGraphView();

			}
		}
	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {

	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void turnonoff() {
		AppController controller = Application.getInstance().getController();
		for (GraphView graphview : controller.getGraphViews()) {
			if (graphview instanceof GraphView2D) {
				if (effect == null) {
					effect = new FisheyeEffect(dx, dy, sz);
					effect.setMinSize(4f);
					effect.setAutoUpdateOnMove(false);
					effect.setAutoUpdateOnDrag(true);
					effect.setSpeed(3);
					graphview.setEffect(effect);
					graphview.setZoomEnabled(false);
					graphview.setMovingEnabled(false);
					graphview.centerGraph();
					graphview.updateGraphView();
				} else {
					effect = null;
					graphview.setEffect(new Effect());
					graphview.setZoomEnabled(true);
					graphview.setMovingEnabled(true);
					graphview.centerGraph();
					graphview.updateGraphView();
				}
			}
		}
	}

	public void updateTraits(ToolView sender) {

	}

}
