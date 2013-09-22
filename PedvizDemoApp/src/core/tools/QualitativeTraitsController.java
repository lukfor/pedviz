package core.tools;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JOptionPane;

import pedviz.graph.Graph;
import pedviz.view.GraphView;
import pedviz.view.GraphView3D;
import pedviz.view.rules.GradientRule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.SymbolQualitativeTrait;
import view.tools.QualitativeTraitsView;
import view.tools.ToolView;
import core.AppController;
import core.Application;

public class QualitativeTraitsController implements ToolController {

	private ShapeRule barRule;

	private HashMap<Double, Color> colors;

	private GradientRule gradientRule;

	public QualitativeTraitsController() {

		AppController controller = Application.getInstance().getController();

		colors = new HashMap<Double, Color>();
		gradientRule = new GradientRule("", colors);
		gradientRule.setEnabled(false);
		barRule = new ShapeRule(new SymbolQualitativeTrait("-", 0, 13, 6, 0));
	}

	public void init(ToolView sender) {

	}

	public void resetPressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		gradientRule.setEnabled(false);
		barRule.setEnabled(false);
		for (GraphView view : controller.getGraphViews()) {
			view.updateRules();
		}
	}

	public void updatePressed(ToolView sender) {
		AppController controller = Application.getInstance().getController();

		for (GraphView view : controller.getGraphViews()) {
			if (view instanceof GraphView3D) {
				if (!view.getRules().contains(gradientRule)) {
					view.addRule(gradientRule);
				}
			} else {
				if (!view.getRules().contains(barRule)) {
					view.addRule(barRule);
				}
			}
		}

		gradientRule.setEnabled(true);
		barRule.setEnabled(true);

		QualitativeTraitsView qtrait = (QualitativeTraitsView) sender;

		if (qtrait.getMin() < qtrait.getMean()
				&& qtrait.getMean() < qtrait.getMax()) {
			colors.clear();
			colors.put(qtrait.getMin(), Color.blue);
			colors.put(qtrait.getMean(), Color.green);
			colors.put(qtrait.getMax(), Color.red);
			gradientRule.setUserdata(qtrait.getTrait());
			barRule.setShape(new SymbolQualitativeTrait(qtrait.getTrait(),
					qtrait.getMin(), qtrait.getMax(), qtrait.getMean(), 0));

			for (GraphView view : controller.getGraphViews()) {
				view.updateRules();
			}
		} else {
			JOptionPane.showMessageDialog(controller.getView(),
					"Invalid values: min < mean < max", "Error",
					JOptionPane.ERROR_MESSAGE);

		}

	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		AppController controller = Application.getInstance().getController();

		colors = new HashMap<Double, Color>();
		gradientRule = new GradientRule("", colors);
		gradientRule.setEnabled(false);
		barRule = new ShapeRule(new SymbolQualitativeTrait("-", 0, 13, 6, 0));

		QualitativeTraitsView qtrait = (QualitativeTraitsView) sender;
		qtrait.setData(controller.getDoubleTraits(), controller
				.getDoubleQuestion());
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {
		AppController controller = Application.getInstance().getController();
		QualitativeTraitsView qtrait = (QualitativeTraitsView) sender;
		qtrait.setData(controller.getDoubleTraits(), controller
				.getDoubleQuestion());
	}

}
