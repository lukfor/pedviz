package pedviz.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import pedviz.graph.Graph;
import pedviz.graph.Node;

public class DatabaseGraphExporter extends GraphExporter{

    @Override
    public void save(Graph graph) throws GraphIOException {
	throw new GraphIOException("not yet implemented");
    }

}
