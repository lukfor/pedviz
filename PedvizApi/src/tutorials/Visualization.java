package tutorials;

//3DPedViz imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import pedviz.algorithms.Highlighter;
import pedviz.algorithms.Sugiyama;
import pedviz.algorithms.filter.*;
import pedviz.algorithms.filter.Filter;
import pedviz.graph.Graph;
import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.view.*;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView;
import pedviz.view.GraphView2D;
import pedviz.view.LODHighlighter;
import pedviz.view.NodeView;
import pedviz.view.PathHighlighter;
import pedviz.view.rules.Rule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;
import pedviz.view.symbols.SymbolSexUndesignated;
import pedviz.view.symbols.SymbolText;

public class Visualization extends JFrame {

    private JComboBox comboBox;

    Graph graph = new Graph();

    public static GraphView graphView;

    int mode = Highlighter.ANCESTORS;

    HashMap<Integer, Color> colors;

    public Visualization(Graph graph2) {
	setSize(600, 480);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	colors = new HashMap<Integer, Color>();
	colors.put(1, Color.BLUE);
	colors.put(2, Color.RED);
	colors.put(3, Color.GREEN);
	colors.put(4, Color.YELLOW);
	colors.put(5, Color.MAGENTA);
	colors.put(6, Color.CYAN);
	colors.put(7, Color.PINK);
	colors.put(8, Color.GRAY);
	colors.put(9, Color.ORANGE);

	graph = graph2;

	DefaultEdgeView e = new DefaultEdgeView();
	e.setConnectChildren(true);
	e.setColor(new Color(50, 50, 50));
	e.setColorForLongLines(new Color(200, 200, 200));
	e.setHighlightedColor(new Color(255, 0, 0));
	e.setGapBottom(5);
	e.setWidth(0.01f);
	e.setHighlightedColor(Color.BLUE);
	e.setHighlightedWidth(1f);
	// e.setHighlightedWidth(1.0f);

	DefaultNodeView n = new DefaultNodeView();
	n.setColor(new Color(255, 255, 255));
	n.setHighlightedColor(new Color(200, 200, 200));
	n.addHintAttribute("pid");
	n.addHintAttribute("mom");
	n.addHintAttribute("dad");
	n.addHintAttribute("setid");

	Sugiyama sugiyama = new Sugiyama(graph, n, e);
	sugiyama.run();
	LayoutedGraph layoutedGraph = sugiyama.getLayoutedGraph();

	// for 2d:
	graphView = new ClusterGraphView2D();

	getContentPane().add(graphView.getComponent());

	// set background-color
	graphView.setBackgroundColor(new Color(255, 255, 255));

	JPanel graphPanel = new JPanel();
	graphPanel.setLayout(new BorderLayout());
	graphPanel.add(graphView.getComponent(), BorderLayout.CENTER);

	JPanel infoPanel = new JPanel();
	infoPanel.setLayout(new BorderLayout());

	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(graphPanel, BorderLayout.CENTER);
	getContentPane().add(infoPanel, BorderLayout.SOUTH);

	final LODHighlighter lodHighlighter = new LODHighlighter(mode);

	final String[] data = { "all ancestors", "all successors",
		"all ancestors and successors", "maternal", "paternal",
		"maternal and paternal" };
	comboBox = new JComboBox(data);
	comboBox.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		JComboBox selectedChoice = (JComboBox) e.getSource();
		if (selectedChoice.getSelectedItem().equals(data[0])) {
		    mode = Highlighter.ANCESTORS;
		}
		if (selectedChoice.getSelectedItem().equals(data[1])) {
		    mode = Highlighter.SUCCESSORS;
		}
		if (selectedChoice.getSelectedItem().equals(data[2])) {
		    mode = Highlighter.SUCCESSORS_AND_ANCESTORS;
		}
		if (selectedChoice.getSelectedItem().equals(data[3])) {
		    mode = Highlighter.MATERNAL;
		}
		if (selectedChoice.getSelectedItem().equals(data[4])) {
		    mode = Highlighter.PATERNAL;
		}
		if (selectedChoice.getSelectedItem().equals(data[5])) {
		    mode = Highlighter.MATERNAL_AND_PATERNAL;
		}
		lodHighlighter.setMode(mode);
		/*
		 * if (selectedChoice.getSelectedItem().equals(data[6])) { mode =
		 * -1; graphView.unHighlightAll(); graphView.deselect(); }
		 */
	    }
	});

	infoPanel.add(comboBox);

	setTitle("Pedigree View");


	graphView.addRule(new ShapeRule("sex", 0, new SymbolSexUndesignated()));
	graphView.addRule(new ShapeRule("sex", "1", new SymbolSexMale()));
	graphView.addRule(new ShapeRule("sex", "2", new SymbolSexFemale()));

	// create your own rule
	graphView.addRule(new Rule() {
	    public void applyRule(NodeView nodeview) {
		Node node = nodeview.getNode();
		if (!node.isDummy()) {
		    // Integer clique = (Integer) node.getUserData("clique");
		    // Integer genotyped = (Integer)
		    // node.getUserData("genotyped");
		    String set = (String) node.getUserData("setid");
		    // if (affection.intValue() == 2)
		    // nodeview.setBorderWidth(1);
		    // nodeview.setColor(Color.red);
		    if (set != null) {
			// if (set.intValue() > 1){
			// nodeview.addSymbol(new SymbolText(set));
			// }

			if (!set.equals("")) {
			    // nodeview.setBorderWidth(1);
			    nodeview.setColor(colors
				    .get(new Integer(set.trim())));
			}

		    }
		    // if (genotyped.intValue() == 2)
		    // || ((clique.intValue() == 0) && (affection
		    // .intValue() == 2)))
		    // nodeview.setColor(Color.gray);
		    // if (clique.intValue() == 1)
		    // nodeview.setColor(Color.red);
		}
	    }
	});

	graphView.setGraph(layoutedGraph);

	// set a listener
	graphView.setSelectionEnabled(true);
	// graphView.setMultiselection(true);

	graphView.addNodeListener(lodHighlighter);
	graphView.addNodeListener(new PathHighlighter());
	
	graphView.showAll();
	Filter filter = new Filter();
	filter.addCondition(new TextCondition("setid",Condition.EQUALS,""));
	graphView.hide(filter.execute(graph));

    }

    public void centerGraph() {
	graphView.centerGraph();
    }

    public static GraphView getGraphView() {
	return graphView;
    }
}