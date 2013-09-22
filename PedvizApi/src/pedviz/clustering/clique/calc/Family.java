package pedviz.clustering.clique.calc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import pedviz.clustering.clique.calc.util.struct.Matrix;
import pedviz.clustering.clique.calc.util.struct.PersonData;
public class Family {
	private Vector personsId;

	private Vector persons;

	int id;

	int idPerson;

	int selected;

	int[] IndKinmatrixToID;

	Matrix kinMatrix;

	double[] inbrVector;

	/* GUI */
	File file = null;

	/* GUI END */

	public Family(int id) {
		persons = new Vector();
		personsId = new Vector();
		this.id = id;
		idPerson = 1;
		selected = 0;
	}

	public boolean addPerson(int id, int father, int mother, int sex,
			int affection, int genotyped) {
		// no double entries
		if (!isInserted(id)) {
			persons.add(new PersonData(id, father, mother, sex, affection,
					idPerson,genotyped));
			personsId.add(new Integer(id));
			if (affection == 2)
				selected++;
			idPerson++;
			return true;
		} else
			return false;
	}
	
	public boolean addPerson(PersonData pd) {
		// no double entries
		int id = pd.getId();
		int affection = pd.getAffection();
		if (!isInserted(id)) {
			persons.add(pd);
			personsId.add(new Integer(id));
			if (affection == 2)
				selected++;
			idPerson++;
			return true;
		} else
			return false;
	}
	
	

	// Sort delle persone per avere i genitori sempre prima dei figli: serve per
	// il calcolo kinship da Lange
	public void sort() {
		PersonData tmpPerson, tmpFather, tmpMother;
		int fatherIndex, motherIndex, personIndex;
		// 1 - tutti gli ancestor hanno index = 0
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);
			if (!tmpPerson.isFounder()) {
				tmpPerson.setIndex(1);
			}
		}
		// 2 - tutti i figli dopo i genitori
		int maxInd = 1;
		boolean modified = true;
		while (modified) {
			modified = false;
			for (int x = 0; x < persons.size(); x++) {
				tmpPerson = (PersonData) persons.elementAt(x);
				if (!tmpPerson.isFounder()) {
				try	{
					personIndex = tmpPerson.getIndex();
					tmpFather = (PersonData) persons.elementAt(tmpPerson
							.getFatherIndex() - 1);
					tmpMother = (PersonData) persons.elementAt(tmpPerson
							.getMotherIndex() - 1);
					fatherIndex = tmpFather.getIndex();
					motherIndex = tmpMother.getIndex();
					if (!(personIndex > fatherIndex)) {
						tmpPerson.setIndex(++personIndex);
						if (personIndex > maxInd)
							maxInd = personIndex;
						modified = true;
					}
					if (!(personIndex > motherIndex)) {
						tmpPerson.setIndex(++personIndex);
						if (personIndex > maxInd)
							maxInd = personIndex;
						modified = true;
					}
				}catch (Exception ex) {
					System.out.println((tmpPerson.getMotherIndex() - 1));
				}
				}	
			}
		}
		// riindicizzo con i nuovi indici
		Collections.sort(persons, new Comparer());
		personsId.clear();
		int newIndex = 1;
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);
			tmpPerson.setPersonIndex(newIndex);
			newIndex++;
			// System.out.println(tmpPerson.getId());
			personsId.add(new Integer(tmpPerson.getId()));
		}
		reindex(true);
	}

	// ricontrolla le persone, assegnando ad ognuna il padre e la madre
	// prelevati da idPersons
	public void reindex(boolean addChilds) {
		PersonData tmpPerson, tmpFather, tmpMother;
		int fatherPos, motherPos;
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);

			if (tmpPerson.getFather() != 0) {
				fatherPos = personsId
						.indexOf(new Integer(tmpPerson.getFather())) + 1;
				motherPos = personsId
						.indexOf(new Integer(tmpPerson.getMother())) + 1;
				tmpPerson.setFatherIndex(fatherPos);
				tmpPerson.setMotherIndex(motherPos);
				if (addChilds) {
					tmpFather = (PersonData) persons.elementAt(fatherPos - 1);
					tmpMother = (PersonData) persons.elementAt(motherPos - 1);
					tmpFather.addChild(x);
					tmpMother.addChild(x);
				}
			}
		}
	}

	// calcola la matrice di kinship
	public void kinship(boolean writeKin) {
		Matrix allKinMatrix = new Matrix(persons.size());
		int posInbr = 0;
		PersonData tmpPerson, tmpPerson2;
		int fatherPos, motherPos;
		// System.out.println("...calcolo matrice kinship...");
		for (int x = 0; x < persons.size(); x++) {

			tmpPerson = (PersonData) persons.elementAt(x);

			if (tmpPerson.isFounder()) {
				allKinMatrix.setTriangMatrix(0.5, x, x);
				for (int y = 0; y < x; y++) {
					allKinMatrix.setTriangMatrix(0.0, x, y);
				}
			} else {
				fatherPos = tmpPerson.getFatherIndex() - 1;
				motherPos = tmpPerson.getMotherIndex() - 1;
				allKinMatrix.setTriangMatrix(0.5 + (0.5 * allKinMatrix
						.retrieveTriangMatrix(fatherPos, motherPos)), x, x);
				for (int y = 0; y < x; y++) {
					allKinMatrix.setTriangMatrix(0.5 * (allKinMatrix
							.retrieveTriangMatrix(fatherPos, y) + allKinMatrix
							.retrieveTriangMatrix(motherPos, y)), x, y);
				}
			}
		}

		kinMatrix = new Matrix(selected);
		IndKinmatrixToID = new int[selected];
		inbrVector = new double[selected];

		int indx = -1;
		int indy;

		PrintWriter kin = null;
		try {

			/* GUI */
			if (writeKin) {
				if (file == null)
					kin = new PrintWriter(new BufferedWriter(new FileWriter(
							"kin.txt")));
				else
					kin = new PrintWriter(new BufferedWriter(new FileWriter(
							file)));
			}
			/* GUI END */

			for (int x = 0; x < persons.size(); x++) {
				tmpPerson = (PersonData) persons.elementAt(x);
				if (tmpPerson.getAffection() == 2) {
					indx++;
					IndKinmatrixToID[indx] = tmpPerson.getId();
					indy = indx;
					inbrVector[posInbr++] = allKinMatrix.retrieveTriangMatrix(
							x, x) - 0.5;
					// System.out.println(personsId.elementAt(x)+"\t"+(allKinMatrix.retrieveTriangMatrix(x,x)-0.5));
					for (int y = x + 1; y < persons.size(); y++) {
						tmpPerson2 = (PersonData) persons.elementAt(y);
						if (tmpPerson2.getAffection() == 2) {
							indy++;
							kinMatrix.setTriangMatrix(allKinMatrix
									.retrieveTriangMatrix(x, y), indx, indy);
							if (writeKin)
								kin.println(personsId.elementAt(x)
										+ "\t"
										+ personsId.elementAt(y)
										+ "\t"
										+ allKinMatrix.retrieveTriangMatrix(x,
												y));
							// if (writeKin)
							// kin.println(indx+"\t"+indy+"\t"+allKinMatrix.retrieveTriangMatrix(x,y));
						}
					}
				}
			}

			if (writeKin)
				kin.close();
			// if (writeKin) System.out.println("Kinship matrix stored in file
			// kin.txt");
		} catch (Exception ex) {
			// System.out.println("Error creating file kin.txt");
			/*
			 * 
			 * TODO
			 */
			System.out.println("Family: " + ex.getMessage() + ex.getCause());
			// System.exit(1);
		}
	}

	/* GUI */
	public void kinship(boolean writeKin, File file) {
		this.file = file;
		kinship(writeKin);
	}

	/* GUI END */

	public double getinbrValue(int x) {
		return inbrVector[x];
	}

	// calcola la matrice degli step meiotici
	public void steps(boolean writeKin) {
		Matrix allKinMatrix = new Matrix(persons.size());
		double byFather, byMother;

		PersonData tmpPerson, tmpPerson2;
		int fatherPos, motherPos;

		for (int x = 0; x < persons.size(); x++) {

			tmpPerson = (PersonData) persons.elementAt(x);

			if (tmpPerson.isFounder()) {
				allKinMatrix.setTriangMatrix(0, x, x);
				for (int y = 0; y < x; y++) {
					allKinMatrix.setTriangMatrix(0.0, x, y);
				}
			} else {
				fatherPos = tmpPerson.getFatherIndex() - 1;
				motherPos = tmpPerson.getMotherIndex() - 1;
				allKinMatrix.setTriangMatrix(0, x, x);
				for (int y = 0; y < x; y++) {
					if (y == fatherPos || y == motherPos) {
						allKinMatrix.setTriangMatrix(1, x, y);
						continue;
					}
					byFather = allKinMatrix.retrieveTriangMatrix(fatherPos, y);
					byMother = allKinMatrix.retrieveTriangMatrix(motherPos, y);
					if (byFather != 0
							&& (byFather <= byMother || byMother == 0))
						allKinMatrix.setTriangMatrix(byFather + 1, x, y);
					else {
						if (byMother != 0
								&& (byMother <= byFather || byFather == 0))
							allKinMatrix.setTriangMatrix(byMother + 1, x, y);
						else
							allKinMatrix.setTriangMatrix(0, x, y);
					}
				}
			}
		}

		kinMatrix = new Matrix(selected);
		IndKinmatrixToID = new int[selected];

		int indx = -1;
		int indy;

		PrintWriter kin = null;
		try {
			if (writeKin)
				kin = new PrintWriter(new BufferedWriter(new FileWriter(
						"steps.txt")));

			for (int x = 0; x < persons.size(); x++) {
				tmpPerson = (PersonData) persons.elementAt(x);
				if (tmpPerson.getAffection() == 2) {
					indx++;
					IndKinmatrixToID[indx] = tmpPerson.getId();
					indy = indx;
					for (int y = x + 1; y < persons.size(); y++) {
						tmpPerson2 = (PersonData) persons.elementAt(y);
						if (tmpPerson2.getAffection() == 2) {
							indy++;
							kinMatrix.setTriangMatrix(allKinMatrix
									.retrieveTriangMatrix(x, y), indx, indy);
							if (writeKin)
								kin.println(personsId.elementAt(x)
										+ "\t"
										+ personsId.elementAt(y)
										+ "\t"
										+ (int) Math.round(allKinMatrix
												.retrieveTriangMatrix(x, y)));
							// kin.println(indx+"\t"+indy+"\t"+allKinMatrix.retrieveTriangMatrix(x,y));
						}
					}
				}
			}

			if (writeKin)
				kin.close();
			// if (writeKin) System.out.println("Meiotic steps matrix stored in
			// file steps.txt");
		} catch (Exception ex) {
			// System.out.println("Error creating file steps.txt");
			/*
			 * 
			 * TODO
			 */
			// System.exit(1);
			System.out.println("Family: " + ex.getMessage() + ex.getCause());
		}
	}

	public int getFather(int IDPerson) {
		// System.out.println(IDPerson);
		PersonData tmpPersonData;
		if (personsId.indexOf(new Integer(IDPerson)) == -1)
			return 0;
		tmpPersonData = (PersonData) persons.elementAt(personsId
				.indexOf(new Integer(IDPerson)));
		return tmpPersonData.getFather();
	}

	public int getMother(int IDPerson) {
		PersonData tmpPersonData;
		if (personsId.indexOf(new Integer(IDPerson)) == -1)
			return 0;
		tmpPersonData = (PersonData) persons.elementAt(personsId
				.indexOf(new Integer(IDPerson)));
		return tmpPersonData.getMother();
	}

	public int getIdByIndKinmatrix(int IndKinmatrix) {
		return IndKinmatrixToID[IndKinmatrix];
	}

	public int getIndKinmatrixById(int _Id) {

		for (int x = 0; x < IndKinmatrixToID.length; x++)

			if (IndKinmatrixToID[x] == _Id)
				return x;

		return 999999999; // not found (strange impossible error)

	}

	public PersonData getIndividual(int numPerson) {
		return (PersonData) persons.elementAt(numPerson);
	}

	public PersonData getIndividualById(int IDPerson) {
		return (PersonData) persons.elementAt(personsId.indexOf(new Integer(
				IDPerson)));
	}

	public boolean isInserted(int IDPerson) {
		if (personsId.indexOf(new Integer(IDPerson)) == -1)
			return false;
		else
			return true;
	}

	public boolean isSelectedForExtraction(int IDPerson) {
		return ((PersonData) persons.elementAt(personsId.indexOf(new Integer(
				IDPerson)))).getAffected();
	}

	public int getNumIndividuals() {
		return persons.size();
	}

	public Vector getParents(int person) {
		return new Vector();
	}

	public void outputAll() {
		PersonData tmpPerson;
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);
			System.out.println(tmpPerson.getId() + ","
					+ tmpPerson.getFatherIndex() + ","
					+ tmpPerson.getMotherIndex() + "," + tmpPerson.getSex()
					+ "," + tmpPerson.getAffection());
		}
	}

	public Vector getAll() {
		Vector all = new Vector();
		PersonData tmpPerson;
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);
			all.add(new Integer(tmpPerson.getId()));
		}
		return all;
	}

	public Vector getAllAffected() {
		Vector all = new Vector();
		PersonData tmpPerson;
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);
			if (tmpPerson.getAffection() == 2) {
				all.add(new String("" + tmpPerson.getId()));
			}
		}
		return all;
	}

	public Vector getAllInformativeChildren(Vector IDPerson) {
		Vector all = new Vector();
		PersonData tmpPerson;
		for (int x = 0; x < persons.size(); x++) {
			tmpPerson = (PersonData) persons.elementAt(x);
			boolean include = false;
			if (tmpPerson.getAffection() == 2 || tmpPerson.getGenotyped() == 2) {
				for (Enumeration e = IDPerson.elements(); e.hasMoreElements();) {
					int id = ((Integer) (e.nextElement())).intValue();
					if (tmpPerson.getFather() == id
							|| tmpPerson.getMother() == id) {
						include = true;
						break;
					}
				}
				if(include)
					all.add(new Integer(tmpPerson.getId()));
			}
		}
		return all;
	}

	void setTriangMatrix(double[][] matrix, double value, int key1, int key2) {
		int dim = matrix.length - 1;
		if (key1 > key2)
			matrix[key2][dim - key1] = value;
		else
			matrix[key1][dim - key2] = value;
	}

	double retrieveTriangMatrix(double[][] matrix, int key1, int key2) {
		int dim = matrix.length - 1;
		if (key1 > key2)
			return matrix[key2][dim - key1];
		else
			return matrix[key1][dim - key2];
	}

}

class Comparer implements Comparator {
	public int compare(Object obj1, Object obj2) {
		int i1 = ((PersonData) (obj1)).getIndex();
		int i2 = ((PersonData) (obj2)).getIndex();

		return Math.abs(i1) - Math.abs(i2);
	}
}
