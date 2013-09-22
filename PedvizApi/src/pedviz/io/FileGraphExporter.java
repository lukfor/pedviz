package pedviz.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import pedviz.graph.*;

public class FileGraphExporter extends GraphExporter {

    private String filename;
   

    public FileGraphExporter(String filename) {
	super();
	this.filename = filename;
    }

    @Override
    public void save(Graph graph) throws GraphIOException {
	BufferedWriter out = null;
	try {
	    out = new BufferedWriter(new FileWriter(filename));
	    out.write(writeHeader(graph));
	    // header line (TODO: ordentlich machen!!!!)
	    for (Node node : graph.getAllNodes()) {
		if (!node.isDummy()) {
		    out.write(writeNode(graph, node));
		}
	    }
	    out.close();
	} catch (Exception e) {
	    throw new GraphIOException(e);
	}

    }

    protected String writeNode(Graph graph, Node node) {
	return node.getId().toString();
    }

    protected String writeHeader(Graph graph) {
	return "";
    }


}
