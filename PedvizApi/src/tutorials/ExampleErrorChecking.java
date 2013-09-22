package tutorials;

// 3DPedViz imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import pedviz.algorithms.ErrorChecking;
import pedviz.algorithms.GraphError;
import pedviz.algorithms.GraphRepair;
import pedviz.algorithms.HierarchieUpDown;
import pedviz.algorithms.RubberBands;
import pedviz.algorithms.SameParents;
import pedviz.algorithms.sugiyama.SugiyamaLayout;
import pedviz.graph.Graph;
import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.io.CsvGraphLoader;
import pedviz.io.GraphIOException;
import pedviz.view.DefaultEdgeView;
import pedviz.view.DefaultNodeView;
import pedviz.view.GraphView;
import pedviz.view.GraphView2D;
import pedviz.view.NodeView;
import pedviz.view.rules.ColorRule;
import pedviz.view.rules.Rule;
import pedviz.view.rules.ShapeRule;
import pedviz.view.symbols.SymbolSexFemale;
import pedviz.view.symbols.SymbolSexMale;

public class ExampleErrorChecking extends JFrame {
    static GraphView graphView;

    LayoutedGraph layoutedGraph;

    JTree errorList;

    Graph graph;
    DefaultMutableTreeNode errorNode;

    DefaultNodeView n;

    DefaultEdgeView e;

    public ExampleErrorChecking() {
	super("3DPedViz - SimpleExample");
	setSize(800, 600);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	graph = new Graph();

	// Loads graph from database (user, pass, connection, table)
	CsvGraphLoader loader = new CsvGraphLoader("data/family_93_error.csv",
		",");

	// sets columnnames for id, mom-id and dad-id
	loader.setSettings("PID", "MOM", "DAD");
	try {
	    loader.load(graph);
	} catch (GraphIOException e) {
	    e.printStackTrace();
	}

	// Clusters nodes with same parents
	SameParents sameParents = new SameParents(graph);
	sameParents.run();

	//	
	graph.buildHierarchie(new HierarchieUpDown());

	e = new DefaultEdgeView();
	e.setColor(new Color(100, 100, 100));
	e.setHighlightedColor(Color.RED);
	e.setHighlightedWidth(0.5f);
	e.setColorForLongLines(new Color(200, 200, 200));
	e.setConnectChildren(true);
	e.setGapBottom(5);

	n = new DefaultNodeView();
	n.setColor(new Color(255, 255, 255));
	n.addHintAttribute("PID");
	n.addHintAttribute("MOM");
	n.addHintAttribute("DAD");
	n.addHintAttribute("SEX");
	n.setHighlightedColor(Color.RED);

	// runs sugiyama
	SugiyamaLayout layout = new SugiyamaLayout(graph, n, e);
	layout.run();
	layoutedGraph = layout.getLayoutGraph();

	// calcs coordinates for nodes with the "rubber bands" algorithm
	RubberBands rubberBands = new RubberBands(layoutedGraph);
	rubberBands.run();

	graphView = new GraphView2D();

	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(graphView.getComponent());

	errorList = new JTree();
	errorNode = new DefaultMutableTreeNode("Errors (0)");
	errorList = new JTree(errorNode);

	JScrollPane scrollPane = new JScrollPane(errorList);
	scrollPane.setMinimumSize(new Dimension(100, 100));
	scrollPane.setMaximumSize(new Dimension(100, 100));
	scrollPane.setPreferredSize(new Dimension(100, 100));
	JPanel panel = new JPanel(new BorderLayout());
	panel.add(scrollPane, BorderLayout.CENTER);
	JButton button = new JButton("Auto repair");
	button.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		Vector<String> log = GraphRepair.insertMissingParents(graph);
		String temp = "";
		for (String message : log) {
		    temp += message + "\n";
		}
		JOptionPane.showMessageDialog(null, temp);

		graph.buildHierarchie(new HierarchieUpDown());
		// runs sugiyama
		SugiyamaLayout layout = new SugiyamaLayout(graph, n, e);
		layout.run();
		layoutedGraph = layout.getLayoutGraph();

		// calcs coordinates for nodes with the "rubber bands" algorithm
		RubberBands rubberBands = new RubberBands(layoutedGraph);
		rubberBands.run();
		graphView.setGraph(layoutedGraph);

		checkErrors();
	    }

	});
	panel.add(button, BorderLayout.SOUTH);
	getContentPane().add(panel, BorderLayout.SOUTH);
	// set background-color
	graphView.setBackgroundColor(new Color(255, 255, 255));

	// node look
	graphView.addRule(new ShapeRule("sex", "2", new SymbolSexFemale()));
	graphView.addRule(new ShapeRule("sex", "1", new SymbolSexMale()));
	graphView.addRule(new ColorRule("virtual", "1", Color.GRAY));
	graphView.addRule(new Rule() {

	    @Override
	    public void applyRule(NodeView nodeview) {
		nodeview.setHintText("Y:" + nodeview.getPosY() + " X:"
			+ nodeview.getPosX());

	    }

	});
	graphView.setGraph(layoutedGraph);

	checkErrors();
    }

    private void checkErrors() {
	ErrorChecking errors = new ErrorChecking(graph);
	errors.run();

	errorNode.removeAllChildren();
	errorNode.setUserObject("Errors (" + errors.getErrors().size()
		+ " items)");
	System.out.println(errors.getErrors());
	for (GraphError i : errors.getErrors()) {
	    errorNode.add(new DefaultMutableTreeNode(i));
	}
	int row = 0;
	while (row < errorList.getRowCount()) {
	    errorList.expandRow(row);
	    row++;
	}
	errorList.addTreeSelectionListener(new TreeSelectionListener() {

	    public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) errorList
			.getLastSelectedPathComponent();
		if (treeNode.getUserObject() instanceof GraphError) {
		    GraphError error = (GraphError) treeNode.getUserObject();
		    Node node = error.getNodes().get(0);
		    graphView.unHighlightAll();
		    graphView.highlight(error.getNodes());
		    if (error.getType() == GraphError.INVALID_SEX_DAD) {
			graphView.highlight(graph.getNode(node.getIdDad()));
		    }
		    if (error.getType() == GraphError.INVALID_SEX_MOM) {
			graphView.highlight(graph.getNode(node.getIdMom()));
		    }
		}
	    }

	});
	errorList.updateUI();
    }

    public static void main(String[] args) {
	ExampleErrorChecking example = new ExampleErrorChecking();
	example.setVisible(true);
	graphView.centerGraph();
    }
}
