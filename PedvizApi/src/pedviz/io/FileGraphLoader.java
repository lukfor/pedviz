package pedviz.io;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import pedviz.graph.*;
import pedviz.graph.Graph;
import pedviz.graph.Node;

public abstract class FileGraphLoader implements GraphLoader {

    protected String filename;

    protected ArrayList<String> attributes;

    protected HashMap<String, Integer> types;

    public static final int INTEGER = 0;

    public static final int DOUBLE = 1;

    protected boolean asResource = false;

    protected boolean header = false;

    private String idColumn = null;

    private String momColumn = null;

    private String dadColumn = null;

    private String famColumn = null;
    
    private String sexColumn = null;

    public FileGraphLoader(String filename) {
	this.filename = filename;
	this.attributes = new ArrayList<String>();
	this.types = new HashMap<String, Integer>();
    }

    public FileGraphLoader(String filename, boolean asResource) {
	this(filename);
	this.asResource = asResource;
    }


    public void setSettings(String fam, String id, String mom, String dad, String sex) {
	famColumn = fam;
	idColumn = id;
	momColumn = mom;
	dadColumn = dad;
	sexColumn = sex;
    }

    public void setSettings(String fam, String id, String mom, String dad) {
	setSettings(fam, id, mom, dad, null);
    }
    
    /**
     * Sets the field names, in which the id, mom's id and dad's id are saved.
     * 
     * @param id
     *                field name from the id
     * @param mom
     *                field name from the mom's id
     * @param dad
     *                field name from the dad's id
     */
    public void setSettings(String id, String mom, String dad) {
	setSettings(null, id, mom, dad, null);
    }

    /**
     * Returns the field name from the dad's id
     * 
     * @return field name from the dad's id
     */
    public String getDadColumn() {
	return dadColumn;
    }

    /**
     * Returns the field name from the id
     * 
     * @return field name from the id
     */
    public String getIdColumn() {
	return idColumn;
    }

    /**
     * Returns the field name from the mom's id
     * 
     * @return field name from the mom's id
     */
    public String getMomColumn() {
	return momColumn;
    }

    public String getFamColumn() {
	return famColumn;
    }

    public void setFamCol(String famCol) {
	this.famColumn = famCol;
    }

    public void load(Graph graph) throws GraphIOException {

	try {
	    InputStream is = null;
	    if (!asResource) {
		File f = new File(filename);
		graph.setName(f.getName());
		is = new FileInputStream(f);
	    } else {
		is = getClass().getResourceAsStream(filename);
		graph.setName(filename);
	    }

	    //load header
	    GraphMetaData metaData = parseMetaData(is);
	    graph.setMetaData(metaData);
	    
	    if (!asResource) {
		File f = new File(filename);
		graph.setName(f.getName());
		is = new FileInputStream(f);
	    } else {
		is = getClass().getResourceAsStream(filename);
		graph.setName(filename);
	    }
	    
	    //load nodes
	    Vector<Node> nodes = parseNodes(is);
	    for (Node node : nodes) {
		graph.addNode(node);
	    }
	    is.close();
	    nodes.clear();

	    // create Edges
	    for (Node node : graph.getNodes()) {
		Node dad = graph.getNode(node.getIdDad());
		if (dad != null) {
		    Edge edge = new Edge(dad, node);
		    graph.addEdge(edge);
		}
		Node mom = graph.getNode(node.getIdMom());
		if (mom != null) {
		    Edge edge = new Edge(mom, node);
		    graph.addEdge(edge);
		}
	    }

	} catch (IOException e) {
	    throw new GraphIOException(e);
	} catch (GraphIOException e) {
	    throw e;
	}

    }

    /**
     * Sets the type for the given column.
     * 
     * @param column
     *                the name of the column.
     * @param type
     *                Type (STRING(default)/INTEGER/DOUBLE)
     */
    public void setColumnType(String column, int type) {
	types.put(column, type);
    }

    public Vector<String> getTraits() throws GraphIOException {
	FileInputStream fis = null;
	try {
	    File f = new File(filename);
	    fis = new FileInputStream(f);
	    GraphMetaData traits = parseMetaData(fis);
	    fis.close();
	    return traits.getTraits();
	} catch (IOException e) {
	    throw new GraphIOException(e);
	} catch (GraphIOException e) {
	    throw e;
	}
    }

    protected abstract Vector<Node> parseNodes(InputStream is)
	    throws GraphIOException;

    protected abstract GraphMetaData parseMetaData(InputStream is)
	    throws GraphIOException;

    public String getSexColumn() {
        return sexColumn;
    }

    public void setSexColumn(String sexColumn) {
        this.sexColumn = sexColumn;
    }

}
