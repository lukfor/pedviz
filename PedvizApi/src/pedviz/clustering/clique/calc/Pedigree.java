package pedviz.clustering.clique.calc;

import java.io.File;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

import pedviz.clustering.clique.calc.util.struct.Matrix;
import pedviz.clustering.clique.ui.db.DataBase;
import pedviz.graph.*;

public class Pedigree {

    Family fam;

    public Pedigree(Graph graph, String aff, String geno) {

	fam = new Family(0);
	int i = 0;
	String sexColumn = graph.getMetaData().get(GraphMetaData.SEX);

	for (Node node : graph.getAllNodes()) {
	    if (!node.isDummy()) {
		Integer id = Integer.parseInt(node.getId().toString());

		Integer mom = 0;
		// exists mom
		if (node.getIdMom() != null
			&& graph.getNode(node.getIdMom()) != null) {
		    mom = new Integer(node.getIdMom().toString());
		}

		Integer dad = 0;
		if (node.getIdDad() != null
			&& graph.getNode(node.getIdDad()) != null) {
		    dad = new Integer(node.getIdDad().toString());
		}

		Integer sex = new Integer(node.getUserData(sexColumn)
			.toString());

		// System.out.println(id + " " + dad + " " + mom + " " + sex + "
		// " +
		// 0 + " " + 0);
		i++;
		fam.addPerson(id, dad, mom, sex, new Integer(node.getUserData(
			aff).toString()), new Integer(node.getUserData(geno)
			.toString()));
	    }
	}

	fam.reindex(false);
	fam.sort();
	// Don't write a file!
	fam.kinship(false);
    }

    /* GUI */
    public Pedigree(DataBase db) {

	fam = new Family(0);
	try {
	    // Run the query, creating a ResultSet
	    ResultSet r = db
		    .query("Select distinct ix,id,idFather,idMother,sex,affection,genotyped FROM pedigree order by ix");
	    r.beforeFirst();
	    while (r.next()) {
		fam.addPerson(r.getInt("id"), r.getInt("idFather"), r
			.getInt("idMother"), r.getInt("sex"), r
			.getInt("affection"), r.getInt("genotyped"));
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage() + ":" + e.getCause());

	}
	fam.reindex(false);
	fam.sort();
	// Don't write a file!
	fam.kinship(false);
    }

    /* GUI END */
    /* GUI */
    public Pedigree(DataBase db, Hashtable affection) {

	fam = new Family(0);
	try {
	    // Run the query, creating a ResultSet
	    ResultSet r = db
		    .query("Select distinct id,idFather,idMother,sex,genotyped FROM pedigree");
	    r.beforeFirst();
	    int aff = 0;
	    while (r.next()) {

		if (affection.contains(new Integer(r.getInt("id"))))
		    aff = 2;
		else
		    aff = 0;

		fam.addPerson(r.getInt("id"), r.getInt("idFather"), r
			.getInt("idMother"), r.getInt("sex"), aff, r
			.getInt("genotyped"));
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage() + ":" + e.getCause());

	}
	fam.reindex(false);
	fam.sort();
	// Don't write a file!
	fam.kinship(false);
    }

    /* GUI */
    public void writeKin(File file) {
	fam.kinship(true, file);
    }

    /* GUI END */

    public void cloneKinMatrix(Matrix where) {
	fam.kinMatrix.cloneInto(where);
    }

    public Matrix getKinMatrix() {
	return fam.kinMatrix;
    }

    public double[] getInbrVector() {
	return fam.inbrVector;
    }

    public int getIdByIndKinmatrix(int IndKinmatrix) {
	return fam.getIdByIndKinmatrix(IndKinmatrix);
    }

    public int getIndKinmatrixById(int _Id) {
	return fam.getIndKinmatrixById(_Id);
    }

    public double getinbrValue(int IndInbreVect) {
	return fam.getinbrValue(IndInbreVect);
    }

    public int getSelected() {
	return fam.selected;
    }

    public Family getFam() {
	return this.fam;
    }

    public int getPedigreeDim() {
	return this.fam.getNumIndividuals();
    }

    public Vector getAllIndividuals() {
	return this.fam.getAllAffected();
    }

}
