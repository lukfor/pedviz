package pedviz.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import pedviz.graph.Graph;
import pedviz.graph.Node;

public abstract class GraphExporter {

    private String idColumn = null;

    private String momColumn = null;

    private String dadColumn = null;

    private String famCol = null;

    /**
     * Loads data in the given Graph object.
     * 
     * @param graph
     *                Gaph object.
     */
    public abstract void save(Graph graph) throws GraphIOException;

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
	idColumn = id;
	momColumn = mom;
	dadColumn = dad;
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

    public String getFamCol() {
	return famCol;
    }

    public void setFamCol(String famCol) {
	this.famCol = famCol;
    }
    
}
