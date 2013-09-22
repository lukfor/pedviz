package pedviz.io;

import java.util.ArrayList;

import pedviz.graph.Graph;
import pedviz.graph.Node;

public class PedGraphExporter extends FileGraphExporter {

    private ArrayList<String> captions = new ArrayList<String>();

    public PedGraphExporter(String filename) {
	super(filename);
	captions = new ArrayList<String>();
	captions.add("FAM");
	captions.add("PID");
	captions.add("DAD");
	captions.add("MOM");
	captions.add("SEX");

    }

    @Override
    protected String writeHeader(Graph graph) {
	// header line (TODO: ordentlich machen!!!!)
	for (Node node : graph.getAllNodes()) {
	    if (!node.isDummy() && !node.getId().toString().startsWith("##")) {
		// checks order
		ArrayList<String> temp2 = (ArrayList<String>) captions.clone();
		for (String column : temp2) {
		    if (node.getUserData(column) == null) {
			captions.remove(column);
		    }
		}
		temp2.clear();
		for (Object column : node.getUserDataList()) {
		    if (!captions.contains(column)) {
			captions.add(column.toString());
		    }
		}
		return "";
	    }
	}
	return "";
    }

    @Override
    protected String writeNode(Graph graph, Node node) {
	String nodeLine = "";
	for (String column : captions) {
	    if (!nodeLine.equals(""))
		nodeLine += "\t";
	    if (node.getUserData(column) != null) {
		nodeLine += node.getUserData(column);
	    }
	}
	return nodeLine + "\n";
    }
}
