/* Copyright © 2007 by Christian Fuchsberger and Lukas Forer info@pedvizapi.org.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License <http://www.pedvizapi.org/gpl.txt>
 * for more details. 
 */

package pedviz.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a individual in a pedigree (Graph).
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class Node {
    private ArrayList<Edge> outEdges;

    private ArrayList<Edge> inEdges;

    private ArrayList<Node> subNodes;

    protected HashMap<String, Object> userdata;

    private Node parent = null;

    private Object idMom;

    private Object idDad;

    private Object id;

    private boolean dummy = false;

    private int familieRank = -1;

    private Hierarchy hierachie = null;

    private int level = -1;
    
    private String famId = "1";
   
    private Graph graph;

    /**
     * Creates a new node with the given id.
     * 
     * @param id
     *                id
     */
    public Node(Object id) {
	this.id = id;
	outEdges = new ArrayList<Edge>();
	inEdges = new ArrayList<Edge>();
	subNodes = new ArrayList<Node>();
	userdata = new HashMap<String, Object>();
    }

    /**
     * Clones the Node object.
     */
    public Object clone() {
	Node node = new Node(id);

	node.setDummy(dummy);
	node.setIdMom(idMom);
	node.setIdDad(idDad);

	for (Node subNode : getNodes()) {
	    Node subNodeClone = (Node) subNode.clone();
	    subNodeClone.setParent(node);
	    node.addNode(subNodeClone);
	}

	node.userdata = (HashMap<String, Object>) userdata.clone();

	return node;
    }

    /**
     * Returns the number of ingoing edges (parents).
     * 
     * @return number of ingoing edges.
     */
    public int getInDegree() {
	return inEdges.size();
    }

    /**
     * Returns the number of outgoing edges (childrens).
     * 
     * @return number of outngoing edges.
     */
    public int getOutDegree() {
	return outEdges.size();
    }

    /**
     * Returns the id.
     * 
     * @return the id.
     */
    public Object getId() {
	return id;
    }

    /**
     * Returns a collection of ingoing edges (parents).
     * 
     * @return list of ingoining edges
     */
    public ArrayList<Edge> getInEdges() {
	return inEdges;
    }

    /**
     * Returns a collection of outgoing edges (childrens)
     * 
     * @return collection of outgoing edges
     */
    public ArrayList<Edge> getOutEdges() {
	return outEdges;
    }

    /**
     * Returns true, if the node is a dummy.
     * 
     * @return
     */
    public boolean isDummy() {
	return dummy;
    }

    /**
     * Sets
     * 
     * @param dummy
     */
    public void setDummy(boolean dummy) {
	this.dummy = dummy;
    }

    /**
     * Returns a collection of all subnodes.
     * 
     * @return
     */
    public ArrayList<Node> getNodes() {
	return subNodes;
    }

    /**
     * Stores an trait that can be identified by the given identifier.
     * 
     * @param title
     *                identifier
     * @param data
     *                data
     */
    public void setUserData(String title, Object data) {
	userdata.put(title.toUpperCase(), data);
    }

    /**
     * Returns the trait that can be identified by the given title.
     * 
     * @param title
     * @return
     */
    public Object getUserData(String title) {
	return userdata.get(title.toUpperCase());
    }

    /**
     * Returns a array of all traits.
     * 
     * @return list of all saved userdatas.
     */
    public Object[] getUserDataList() {
	return userdata.keySet().toArray();
    }

    /**
     * Returns the cluster node, i the node is a subnode.
     * 
     * @return if the node is a subnode, it returns the cluster node, else null.
     */
    public Node getParent() {
	return parent;
    }

    /**
     * Returns the node's familie id.
     * 
     * @return familie id
     */
    public int getFamilieRank() {
	return familieRank;
    }

    /**
     * Sets the node's familie id.
     * 
     * @param familieRank
     *                new familie id
     */
    public void setFamilieRank(int familieRank) {
	this.familieRank = familieRank;
    }

    /**
     * Returns the ancestors and successors for the given node. You can define
     * the number of max generations, that should be considered.
     * 
     * @param Number
     *                of max generations that should be considered.
     * @return Returns the cluster node. of ancestors and successors
     */
    public Set<Node> getParentsAndChilds(int max) {
	Set<Node> result = new HashSet<Node>();
	result.addAll(getParents(0, max - 1));
	result.addAll(getChilds(getId(), 0, max - 1));
	return result;
    }

    /**
     * Returns a collection of all ancestors and successor.
     * 
     * @param node
     *                The node
     * @return a collection of all ancestors and successors
     */
    public Set<Node> getParentsAndChilds() {
	Set<Node> result = new HashSet<Node>();
	result.addAll(getParents(0, -1));
	result.addAll(getChilds(getId(), 0, -1));
	return result;
    }

    /**
     * Returns a collection of all ancestors.
     * 
     * @return a collection of all ancestors.
     */
    public Set<Node> getParents() {
	return getParents(0, -1);
    }

    /**
     * Returns a collection of all ancestors. The parameter max defines the
     * number of max generations, which should be considered.
     * 
     * @param Number
     *                the number of max generations which should be considered.
     * @return a collection of ancestors
     */
    public Set<Node> getParents(int max) {
	return getParents(0, max - 1);
    }

    /**
     * Returns a collection of all successor.
     * 
     * @return a collection of all successor.
     */
    public Set<Node> getChilds() {
	return getChilds(getId(), 0, -1);
    }

    /**
     * Returns a collection of all successor. You can define the number of max
     * generations, which should be considered.
     * 
     * @param Number
     *                Number the number of max generations which should be
     *                considered.
     * @return list of successor
     */
    public Set<Node> getChilds(int max) {
	return getChilds(getId(), 0, max - 1);
    }

    /**
     * Returns the node's dad id.
     * 
     * @return the node's dad id
     */
    public Object getIdDad() {
	return idDad;
    }

    /**
     * Sets the node's dad id.
     * 
     * @param idDad
     *                dad's id.
     */
    public void setIdDad(Object idDad) {
	this.idDad = idDad;
    }

    /**
     * Returns the node's mom id.
     * 
     * @return the node's mom id
     */
    public Object getIdMom() {
	return idMom;
    }

    /**
     * Sets the node's mom id.
     * 
     * @param idDad
     *                mom's id.
     */
    public void setIdMom(Object idMom) {
	this.idMom = idMom;
    }

    /**
     * Returns the number of sub nodes.
     * 
     * @return number of sub nodes.
     */
    public int getNodeCount() {
	return subNodes.size();
    }

    /**
     * Returns the number of outgoing and ingoing edges.
     * 
     * @return the number of outgoing and ingoing edges
     */
    public int getDegree() {
	return getInDegree() + getOutDegree();
    }

    /**
     * Returns true, if the node has parents.
     * 
     * @returntrue, if the node has parents
     */
    public boolean hasParents() {
	if (parent == null)
	    return getInDegree() > 0;
	else
	    return parent.getInDegree() > 0;
    }

    /**
     * Returns a collection of all Nodes on the path between this node and the
     * given node.
     * 
     * @param node
     * @return a collection of nodes, which are on the path between this node
     *         and the given node.
     */

    public Set<Node> getPathTo(Node node) {
	Set<Node> result = new HashSet<Node>();

	if (parent == null) {

	    for (Edge edge : getInEdges()) {
		// Dummy
		if (edge.getStart().isDummy()
			&& edge.getStart().getNodeCount() == 0) {
		    if (edge.getStart().getParents().contains(node)) {
			result.add(edge.getStart());
			result.addAll(edge.getStart().getPathTo(node));
		    }
		} else if (!edge.getStart().isDummy()) {
		    if (edge.getStart().getParents().contains(node)) {
			result.add(edge.getStart());
			result.addAll(edge.getStart().getPathTo(node));
		    }
		} else {
		    for (Node node3 : edge.getStart().getNodes()) {
			if (node3.getId().equals(getIdMom())
				|| node3.getId().equals(getIdDad())) {
			    Set<Node> l = node3.getParents();
			    if (l.contains(node)) {
				result.add(node3);
				result.addAll(node3.getPathTo(node));
			    }
			}
		    }
		}
	    }

	} else {
	    result.addAll(parent.getPathTo(node));
	}
	return result;
    }

    /**
     * Returns the node's level in hierarchy.
     * 
     * @return node's level
     */
    public int getLevel() {
	return level;
    }

    /**
     * Returns the node's hierarchy.
     * 
     * @return the node's hierarchy.
     */
    public Hierarchy getHierarchy() {
	return hierachie;
    }

    protected void removeNode(Node node) {
	subNodes.remove(node);
    }

    protected void setParent(Node parent) {
	this.parent = parent;
    }

    protected void addNode(Node node) {
	subNodes.add(node);
    }

    protected void addInEdge(Edge edge) {
	inEdges.add(edge);
    }

    protected void removeInEdge(Edge edge) {
	inEdges.remove(edge);
    }

    protected void addOutEdge(Edge edge) {
	outEdges.add(edge);

    }

    protected void removeOutEdge(Edge edge) {
	outEdges.remove(edge);
    }

    private Set<Node> getParents(int current, int max) {
	Set<Node> result = new HashSet<Node>();

	if (parent == null) {
	    for (Edge edgeIn : getInEdges()) {
		Node parent = edgeIn.getStart();

		for (Node node : parent.getNodes())
		    if (node.getId().equals(getIdMom())
			    || node.getId().equals(getIdDad()))
			result.add(node);

		result.add(parent);

		// rekursiv
		if ((max == -1) || (current < max)
			|| (parent.isDummy() && parent.getNodeCount() == 0))
		    result.addAll(parent.getParents(current + 1, max));
	    }
	} else {
	    // rekursiv
	    result.addAll(parent.getParents(current + 1, max));

	}
	return result;
    }

    private Set<Node> getChilds(Object id, int current, int max) {
	Set<Node> result = new HashSet<Node>();

	if (parent == null) {
	    for (Edge edgeOut : getOutEdges()) {
		Node child = edgeOut.getEnd();
		if (child.getNodes().size() == 0) {
		    if (id.equals(-1) || id.equals(child.getIdMom())
			    || id.equals(child.getIdDad())) {
			result.add(edgeOut.getEnd());

			// rekursiv
			if ((max == -1)
				|| (current < max)
				|| (child.isDummy() && child.getNodeCount() == 0))
			    result
				    .addAll(child.getChilds(-1, current + 1,
					    max));
		    }
		}

		for (Node node : child.getNodes()) {
		    if (id.equals(-1) || id.equals(node.getIdMom())
			    || id.equals(node.getIdDad())) {
			result.add(node);

			if ((max == -1) || (current < max)
				|| (node.isDummy() && node.getNodeCount() == 0))
			    result.addAll(node.getChilds(-1, current + 1, max));
		    }
		}
	    }
	} else
	    result.addAll(parent.getChilds(id, current, max));
	return result;
    }

    protected void setLevel(int layer) {
	this.level = layer;
	for (Node node : getNodes()) {
	    node.setLevel(layer);
	}
    }

    protected void setHierachie(Hierarchy hierachie) {
	this.hierachie = hierachie;
    }

    public String toString() {
	return getId().toString();
    }
    
    public void setFamId(String famId) {
        this.famId = famId;
    }
    
    public String getFamId() {
        return famId;
    }

    public void removeUserData(String trait){
	userdata.keySet().remove(trait);
    }
    
    protected void setGraph(Graph graph){
	this.graph = graph;
    }
    
    public GraphMetaData getMetaData(){
	return graph.getMetaData();
    }

    public Graph getGraph(){
	return graph;
    }
    
}
