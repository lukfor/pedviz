package pedviz.io;

import java.util.*;

import pedviz.graph.*;
import pedviz.graph.Node;

public class CsvGraphExporter extends FileGraphExporter {

    private ArrayList<String> captions;

    private String seperator = ",";

    private Vector<String> columns;

    public CsvGraphExporter(String filename) {
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

		String firstLine = "";

		columns = new Vector<String>();
		columns.add(graph.getMetaData().get(GraphMetaData.FAM));
		columns.add(graph.getMetaData().get(GraphMetaData.PID));
		columns.add(graph.getMetaData().get(GraphMetaData.DAD));
		columns.add(graph.getMetaData().get(GraphMetaData.MOM));
		columns.add(graph.getMetaData().get(GraphMetaData.SEX));

		Vector<String> temp2 = (Vector<String>) graph.getMetaData()
			.getUserTraits().clone();
		Collections.sort(temp2);
		columns.addAll(temp2);
		for (int i = 0; i < columns.size(); i++) {
		    if (!firstLine.equals(""))
			firstLine += seperator;
		    if (i < captions.size()) {
			firstLine += captions.get(i);
		    } else {
			firstLine += columns.get(i);
		    }
		}
		return firstLine + "\n";
	    }
	}
	return "";
    }

    @Override
    protected String writeNode(Graph graph, Node node) {
	String nodeLine = "";
	for (String column : columns) {
	    if (!nodeLine.equals(""))
		nodeLine += seperator;
	    if (node.getUserData(column) != null) {
		nodeLine += node.getUserData(column);
	    }
	}
	return nodeLine + "\n";
    }

    public String getSeperator() {
	return seperator;
    }

    public void setSeperator(String seperator) {
	this.seperator = seperator;
    }

}
