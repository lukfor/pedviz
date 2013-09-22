package pedviz.clustering.clique.calc;

//22-09-02 Modificato per dividere i gruppi di persone separati da "-" e passare un gruppo per volta.
// gli altri gruppi vengono valutati per evitare che si scelga nella ricostruzione qualcuno gia' censito negli altri gruppi
// Inserito il parametro MAX_STEP nell'unire gli alberi viene valutato che non siano stati superati MAX_STEP step meiotici
// il valore 0 indica che non c'e' selezione per step
// Inserito il parametro MAX_STEP_ANC nel creare nuovi link fra alberi gia' uniti non si va oltre MAX_STEP_ANC step meiotici
import java.util.Vector;

import pedviz.clustering.clique.calc.util.Tree;
import pedviz.clustering.clique.calc.util.struct.Group;
import pedviz.clustering.clique.calc.util.struct.Link;
import pedviz.clustering.clique.calc.util.struct.Person;
import pedviz.clustering.clique.calc.util.struct.PersonData;
import pedviz.clustering.clique.ui.db.DataBase;

public class People { // extends Thread {
	boolean active = true;

	boolean force = false;

	int treeIndex;

	Vector trees;

	Vector vjointTrees;

	/* GUI */
	// PrintWriter out;
	DataBase db;

	/* GUI END */
	final int numGen = 0; // Numero massimo di generazioni da percorrere, se =

	// 0 Tutte

	int MAX_STEP = 4; // Numero massimo di step meiotici per l'unione di

	// alberi, se = 0 Tutti

	int MAX_STEP_ANC = 10; // Numero massimo di step meiotici per l'unione di

	// alberi, se = 0 Tutti

	int MAX_STEP_ANC_NC = 10; // Numero massimo di step meiotici per l'unione

	// di alberi non gia' connessi, se = 0 Tutti

	final boolean transitiva = true;

	final boolean generazioni = false;

	int count = 0;

	int count2 = 1;

	int type = 1;

	int cliqueid = -1;

	int deeperLink;

	Pedigree PED;

	boolean ibdoptim = false;

	private Vector inputList; // 09/06/2006 lista degli individui, serve per

	// recuperare l'affection

	// - pero' bisogna passargli un gruppo per volta - WARNING

	private Vector altri = new Vector();

	public People(Vector gruppi, int count, Pedigree PED, String fileName) {
		this.PED = PED;
		// Costruttore
		// Apre il file di log in scrittura e genera un TREE per ogni persona,
		// inserendo
		// tutti i TREE in un vettore.
		// Un altro vettore tiene traccia degli stati Attivo/Disattivo di ogni
		// albero
		// Sarebbe meglio riportare questa caratteristica sugli alberi stessi
		this.count = count;
		inputList = (Vector) gruppi.elementAt(count);

		/*
		 * try { //out = new PrintWriter(new BufferedWriter(new
		 * FileWriter(fileName,true))); } catch (IOException e) {
		 * System.out.println("Error creating file "+fileName); System.exit(0); }
		 * 
		 */
		trees = new Vector();
		vjointTrees = new Vector();
		int sex = 0;
		// String bplace;
		PersonData tmpPers;
		// int zazza = 0;
		for (int ind = 0; ind < inputList.size(); ind++) {
			// for (int ind=temp.size()-1;ind >= 0; ind--){
			if (inputList.elementAt(ind) != null) {
				Tree Mario = new Tree((String) inputList.elementAt(ind),
						new Integer(ind));
				tmpPers = (PersonData) PED.getFam().getIndividualById(
						((new Integer(((String) inputList.elementAt(ind))
								.toString()).intValue())));
				if (tmpPers != null)
					sex = tmpPers.getSex();
				else
					sex = 0;
				Mario.setPersonSex(new String("" + sex));
				trees.add(Mario);
			}
		}
		/*
		 * for (int indx=0;indx < gruppi.size(); indx++){ if (indx != count) {
		 * inputList = (Vector)gruppi.elementAt(indx); for (int indy=0;indy <
		 * inputList.size(); indy++){
		 * altri.add((String)inputList.elementAt(indy)); } } }
		 */
	}

	public People(Vector gruppi, int count, Pedigree PED, DataBase db,
			int type, int cliqueid, boolean ibdoptim) {
		this.PED = PED;
		// Costruttore
		// Apre il file di log in scrittura e genera un TREE per ogni persona,
		// inserendo
		// tutti i TREE in un vettore.
		// Un altro vettore tiene traccia degli stati Attivo/Disattivo di ogni
		// albero
		// Sarebbe meglio riportare questa caratteristica sugli alberi stessi
		this.count = count;
		this.db = db;
		this.type = type;
		// update a pedigree from a single clique
		this.cliqueid = cliqueid;
		this.ibdoptim = ibdoptim;

		inputList = (Vector) gruppi.elementAt(count);

		trees = new Vector();
		vjointTrees = new Vector();
		int sex = 0;
		PersonData tmpPers;
		for (int ind = 0; ind < inputList.size(); ind++) {
			// for (int ind=temp.size()-1;ind >= 0; ind--){
			if (inputList.elementAt(ind) != null) {
				Tree Mario = new Tree((String) inputList.elementAt(ind),
						new Integer(ind));
				tmpPers = (PersonData) PED.getFam().getIndividualById(
						((new Integer(((String) inputList.elementAt(ind))
								.toString()).intValue())));
				if (tmpPers != null)
					sex = tmpPers.getSex();
				else
					sex = 0;
				Mario.setPersonSex(new String("" + sex));
				trees.add(Mario);
			}
		}
	}

	public boolean run() {
		boolean errors = false;
		Vector gen;
		Person person;
		String IDPerson;
		String father;
		String mother;
		Tree tmpTree;
		int tmpInt = 0;
		int x = 1;
		treeIndex = 0;
		// System.out.println("------Costruisce Alberi------");

		while (true) {
			for (int t = 0; t < trees.size(); t++) {
				tmpTree = (Tree) trees.elementAt(t);
				// if (tmpTree.getLastGeneration() > MAX_STEP && MAX_STEP > 0)
				// tmpTree.inactivate();
				if (tmpTree.isActive()) {

					if (tmpTree.getLastGeneration() >= x) {
						gen = tmpTree.getGeneration(x);
						for (int ind = 1; ind < gen.size(); ind++) {
							person = (Person) gen.elementAt(ind);
							if (person != null && person.isActive()) {
								IDPerson = person.getIDPerson();
								tmpInt = PED.getFam().getFather(
										(new Integer(IDPerson).intValue()));
								if (tmpInt == 0)
									father = null;
								else
									father = new Integer(tmpInt).toString();

								tmpInt = PED.getFam().getMother(
										(new Integer(IDPerson).intValue()));
								if (tmpInt == 0)
									mother = null;
								else
									mother = new Integer(tmpInt).toString();

								if (father != null && mother != null) {

									/*
									 * per risalire per linea materna, cambiare
									 * tmpTree.setFather(IDPerson, x, father,
									 * true); con tmpTree.setFather(IDPerson, x,
									 * father, false); e non cercare in
									 * inOtherTree
									 */
									if (!inOtherTree(father, tmpTree, x,
											(ind * 2 - 1))) { // x =
										// generazione,
										// ind =
										// posizione
										tmpTree.setFather(IDPerson, x, father,
												true);
									}

									else {
										tmpTree.setFather(IDPerson, x, father,
												false);
										// tmpTree.inactivate(); //al primo link
										// che trova si blocca. conviene per
										// minimo common ancestor
									}
									/*
									 * per risalire per linea paterna, cambiare
									 * tmpTree.setMother(IDPerson, x, mother,
									 * true); con tmpTree.setMother(IDPerson, x,
									 * mother, false); e non cercare in
									 * inOtherTree
									 */
									if (!inOtherTree(mother, tmpTree, x,
											(ind * 2))) { // x = generazione,
										// ind = posizione
										tmpTree.setMother(IDPerson, x, mother,
												true);
									} else {
										tmpTree.setMother(IDPerson, x, mother,
												false);
									}
								}
								// }// end non genotipizzati
							}
						} // end for
					} // generazioni < x
					else
						tmpTree.inactivate(); // inactivated
				} // not active
			} // end for
			if (!activeTrees())
				break;
			x++;
			if (x > numGen && numGen != 0)
				break;

		} // end while

		errors = jointTrees();
		// out.flush();
		// out.close();
		active = false;
		return errors;
	}

	public void clusterTree(OutTree tmpOutTree) {

	}

	public boolean activeTrees() {
		Tree tmpTree;
		for (int t = 0; t < trees.size(); t++) {
			tmpTree = (Tree) trees.elementAt(t);
			if (tmpTree.isActive())
				return true;
		}
		return false;
	}

	public int getDeeperLink() {
		return deeperLink;
	}

	public boolean jointTrees() { // return error
		Vector treesNum;
		Vector link;
		Tree tmpTree;
		Link tmpLink;
		Group tmpNiceLittleGroup;
		Vector NiceLittleGroups = new Vector();

		int numMaxLinkedTree = 0;
		int maxSize = 0;
		boolean trovato;
		// System.out.println("------Unisce Alberi------");
		for (int t = 0; t < trees.size(); t++) {
			tmpTree = (Tree) trees.elementAt(t);
			tmpNiceLittleGroup = null;

			treesNum = tmpTree.getLinkedTrees();
			for (int t3 = 0; t3 < treesNum.size(); t3++) {
				for (int t4 = 0; t4 < NiceLittleGroups.size(); t4++) {
					if (((Group) NiceLittleGroups.elementAt(t4))
							.isHere((Integer) (treesNum.elementAt(t3)))) {
						tmpNiceLittleGroup = (Group) NiceLittleGroups
								.elementAt(t4);
						t3 = treesNum.size();
						break;
					}
				}
			}
			if (tmpNiceLittleGroup == null) {
				tmpNiceLittleGroup = new Group(new Integer(t));
				NiceLittleGroups.add(tmpNiceLittleGroup);
			}

			for (int t3 = 0; t3 < treesNum.size(); t3++) {
				tmpNiceLittleGroup.add((Integer) treesNum.elementAt(t3));
			}
		}

		if (transitiva) {
			Group tmpNiceLittleGroup2;
			for (int t = 0; t < NiceLittleGroups.size(); t++) {
				tmpNiceLittleGroup = (Group) NiceLittleGroups.elementAt(t);
				for (int t2 = 0; t2 < tmpNiceLittleGroup.size(); t2++) {
					for (int t3 = t + 1; t3 < NiceLittleGroups.size(); t3++) {
						tmpNiceLittleGroup2 = (Group) NiceLittleGroups
								.elementAt(t3);
						if (tmpNiceLittleGroup2
								.isHere((Integer) (tmpNiceLittleGroup
										.linkedTree(t2)))) {
							for (int t4 = 0; t4 < tmpNiceLittleGroup2.size(); t4++) {
								tmpNiceLittleGroup.add(tmpNiceLittleGroup2
										.linkedTree(t4));
							}
							tmpNiceLittleGroup2.removeLinkedTrees();
						}
					}
				}
			}
		}

		/*
		 * for (int t=0;t < NiceLittleGroups.size(); t++){ tmpNiceLittleGroup =
		 * (NiceLittleGroup)NiceLittleGroups.elementAt(t);
		 * if(tmpNiceLittleGroup.size() == 1){ tmpTree = (Tree)
		 * trees.elementAt(tmpNiceLittleGroup.linkedTree(0).intValue());
		 * tmpTree.alone = true; //System.out.println("singolo " +
		 * tmpNiceLittleGroup.linkedTree(0).intValue()); return false; } }
		 */

		int quanti = 0;
		int quale = 0;
		for (int t = 0; t < NiceLittleGroups.size(); t++) {
			tmpNiceLittleGroup = (Group) NiceLittleGroups.elementAt(t);
			if (tmpNiceLittleGroup.size() != 0) {
				quanti++;
				quale = t;
			}
		}
		if (MAX_STEP_ANC < 999999) {
			if (quanti > 1) {
				// System.out.println("quanti " + quanti);
				return false;
			}
			// System.out.println(NiceLittleGroups.size());
			//TODO email 250507
			//if (NiceLittleGroups.size() > 2) {
				// System.out.println("size " + NiceLittleGroups.size());
			//	return false;
			//}
		}
		deeperLink = 0;
		tmpNiceLittleGroup = (Group) NiceLittleGroups.elementAt(quale);
		for (int t = 0; t < tmpNiceLittleGroup.size(); t++) {
			tmpTree = (Tree) trees.elementAt(tmpNiceLittleGroup.linkedTree(t)
					.intValue());
			if (tmpTree.getDeeperLink() > deeperLink)
				deeperLink = tmpTree.getDeeperLink();
		}

		// cicla su tutti i NiceLittleGroup
		int idFam = 0;
		for (int zah = 0; zah < NiceLittleGroups.size(); zah++) {
			tmpNiceLittleGroup = (Group) NiceLittleGroups.elementAt(zah);

			if (tmpNiceLittleGroup.size() > 0) {
				idFam++;

				OutTree tmpOutTree = new OutTree();
				Vector tmpGeneration;
				Vector tmpParentGeneration;
				Person tmpPerson;
				Person tmpSpouse;
				Person tmpFather;
				Person tmpMother;
				String personID;
				String spouseID;
				String FatherID;
				String MotherID;
				String sex = "";
				boolean write;
				boolean onlySpouse;
				int spousePosition = 0;

				for (int t = 0; t < tmpNiceLittleGroup.size(); t++) {

					tmpTree = (Tree) trees.elementAt(tmpNiceLittleGroup
							.linkedTree(t).intValue());
					link = tmpTree.getLink();
					for (int ind = tmpTree.getLastGeneration(); ind > 0; ind--) {

						tmpGeneration = tmpTree.getGeneration(ind);
						for (int ind2 = 0; ind2 < tmpGeneration.size(); ind2++) {

							tmpPerson = (Person) tmpGeneration.elementAt(ind2);
							// tmpFather = null;
							// tmpMother = null;

							if (tmpPerson != null) {
								write = false;
								onlySpouse = false;
								personID = tmpPerson.getIDPerson();
								if (tmpTree.isUnactiveLink(personID, ind - 1,
										ind2)) {
									tmpPerson.activateForReconstruction();

									if (ind == 1)
										continue;
									write = false;

									if (Math.rint((ind2 / 2) * 2) != ind2) {
										spousePosition = ind2 + 1;
										sex = "2";
									}
									if (Math.rint((ind2 / 2) * 2) == ind2) {
										spousePosition = ind2 - 1;
										sex = "1";
									}
									tmpPerson = (Person) tmpGeneration
											.elementAt(spousePosition);

									if (tmpPerson == null
											|| tmpPerson
													.isActivatedForReconstruction())
										continue;
									spouseID = tmpPerson.getIDPerson();
									if (tmpTree.isActiveLink(spouseID, ind - 1,
											spousePosition)
											|| tmpTree.isUnactiveLink(spouseID,
													ind - 1, spousePosition))
										continue;
									FatherID = MotherID = "0";

									if (ind != tmpTree.getLastGeneration()) {
										tmpParentGeneration = tmpTree
												.getGeneration(ind + 1);
										tmpFather = (Person) tmpParentGeneration
												.elementAt(((spousePosition) * 2) - 1);
										tmpMother = (Person) tmpParentGeneration
												.elementAt(((spousePosition) * 2));
										if (tmpFather != null
												&& tmpMother != null) {
											if (tmpFather
													.isActivatedForReconstruction()
													|| tmpMother
															.isActivatedForReconstruction()) {
												FatherID = tmpTree.getFather(
														spouseID, ind,
														spousePosition);
												MotherID = tmpTree.getMother(
														spouseID, ind,
														spousePosition);
											}
										}
									}

									// 09/06/2006 tmpOutTree.add (spouseID,
									// FatherID, MotherID, sex,
									// PED.getFam().getIndividualById(Integer.parseInt(spouseID)).getAffection());
									tmpOutTree
											.add(
													spouseID,
													FatherID,
													MotherID,
													sex,
													inputList.indexOf(spouseID) == -1 ? 0
															: 2);
									continue;
								}
								// 24-06-2002
								// Aggiunto per rendere attive le persone che
								// appartengono ad un tree singolo:
								// se non c'e' unione fra le persone richieste
								// per la ricostruzione, o almeno un loop sulla
								// stessa famiglia
								// la famiglia intera viene saltata perche' non
								// esiste nemmeno un link.
								// Modificato da:
								// if (tmpTree.isActiveLink(personID, ind-1,
								// ind2)) {
								// a
								// if (tmpTree.isActiveLink(personID, ind-1,
								// ind2) || tmpTree.alone=true) {

								// if (tmpTree.isActiveLink(personID, ind-1,
								// ind2) || tmpTree.alone==true) {
								if (tmpTree.isActiveLink(personID, ind - 1,
										ind2)) {
									write = true;
								} else {
									if (ind != tmpTree.getLastGeneration()) {
										tmpParentGeneration = tmpTree
												.getGeneration(ind + 1);
										tmpFather = (Person) tmpParentGeneration
												.elementAt(((ind2) * 2) - 1);
										tmpMother = (Person) tmpParentGeneration
												.elementAt(((ind2) * 2));
										if (tmpFather == null
												|| tmpMother == null)
											continue;
										if (tmpFather
												.isActivatedForReconstruction()
												|| tmpMother
														.isActivatedForReconstruction())
											write = true;
									}
								}
								if (write == true) {
									// scrive e attiva per la ricostruzione
									FatherID = MotherID = "0";
									if (ind != tmpTree.getLastGeneration()) {
										tmpParentGeneration = tmpTree
												.getGeneration(ind + 1);
										tmpFather = (Person) tmpParentGeneration
												.elementAt(((ind2) * 2) - 1);
										tmpMother = (Person) tmpParentGeneration
												.elementAt(((ind2) * 2));
										if (tmpFather != null
												&& tmpMother != null) {
											if (tmpFather
													.isActivatedForReconstruction()
													|| tmpMother
															.isActivatedForReconstruction()) {
												FatherID = tmpTree.getFather(
														personID, ind, ind2);
												MotherID = tmpTree.getMother(
														personID, ind, ind2);
											}
										}
									}

									if (Math.rint((ind2 / 2) * 2) != ind2)
										sex = "1";
									if (Math.rint((ind2 / 2) * 2) == ind2)
										sex = "2";
									if (ind == 1)
										sex = tmpTree.getPersonSex();
									// 09/06/2006 tmpOutTree.add (personID,
									// FatherID, MotherID, sex,
									// PED.getFam().getIndividualById(Integer.parseInt(personID)).getAffection());
									tmpOutTree
											.add(
													personID,
													FatherID,
													MotherID,
													sex,
													inputList.indexOf(personID) == -1 ? 0
															: 2);
									tmpPerson.activateForReconstruction();

									// /* Scrivere il marito/moglie
									if (ind == 1)
										continue;
									write = false;
									if (sex.equals("1"))
										spousePosition = ind2 + 1;
									if (sex.equals("2"))
										spousePosition = ind2 - 1;
									tmpPerson = (Person) tmpGeneration
											.elementAt(spousePosition);

									if (tmpPerson == null
											|| tmpPerson
													.isActivatedForReconstruction())
										continue;
									spouseID = tmpPerson.getIDPerson();
									if (tmpTree.isActiveLink(spouseID, ind - 1,
											spousePosition)
											|| tmpTree.isUnactiveLink(spouseID,
													ind - 1, spousePosition))
										continue;
									if (sex.equals("1"))
										sex = "2";
									else
										sex = "1";
									FatherID = MotherID = "0";

									if (ind != tmpTree.getLastGeneration()) {
										tmpParentGeneration = tmpTree
												.getGeneration(ind + 1);
										tmpFather = (Person) tmpParentGeneration
												.elementAt(((spousePosition) * 2) - 1);
										tmpMother = (Person) tmpParentGeneration
												.elementAt(((spousePosition) * 2));
										if (tmpFather != null
												&& tmpMother != null) {
											if (tmpFather
													.isActivatedForReconstruction()
													|| tmpMother
															.isActivatedForReconstruction()) {
												FatherID = tmpTree.getFather(
														spouseID, ind,
														spousePosition);
												MotherID = tmpTree.getMother(
														spouseID, ind,
														spousePosition);
											}
										}
									}

									// 09/06/2006 tmpOutTree.add (spouseID,
									// FatherID, MotherID, sex,
									// PED.getFam().getIndividualById(Integer.parseInt(spouseID)).getAffection());
									tmpOutTree
											.add(
													spouseID,
													FatherID,
													MotherID,
													sex,
													inputList.indexOf(spouseID) == -1 ? 0
															: 2);
								}
							}

						}
					}
				}
				// System.out.println("------sort------");
				// out.flush();

				if (tmpOutTree.size() != 0) {
					Family fam = new Family(0);
					// System.out.println("------Ordina Generazioni------");
					// Per ora la migliore pare Down - Up - ZeroDown
					tmpOutTree.sortGenerationsDown();
					tmpOutTree.sortGenerationsUp();
					tmpOutTree.sortGenerationsZeroDown();
					tmpOutTree.sort();

					/*
					 * TODO
					 */
					if (generazioni) {
						// out.println("------ Scrivi Generazioni Ped:"+
						// (count+1));
						for (int t = 0; t < tmpOutTree.size(); t++) {
							// System.out.println(tmpOutTree.elementAt(t));
							// out.println(tmpOutTree.elementAt(t));
						}
					}
					/*
					 * Add Ped-Stats
					 */
					/*
					 * System.out .println("Pedigree " + (count + 1) + " - Dim:" +
					 * tmpOutTree.size() + " - Gen:" +
					 * (-tmpOutTree.memMaxGeneration + 1) + " - Bits:" + (2 *
					 * tmpOutTree.numNonFounders - tmpOutTree.numFounders));
					 */
					try {

						if (cliqueid == -1) {
							db
									.update("INSERT INTO BUILDINFO(famId,dim,gen,bits) VALUES("
											+ (count + 1)
											+ ","
											+ tmpOutTree.size()
											+ ","
											+ (-tmpOutTree.memMaxGeneration + 1)
											+ ","
											+ (2 * tmpOutTree.numNonFounders - tmpOutTree.numFounders)
											+ ")");

						} else {
							// only updates have to be performed
							db.update("Delete from buildped where famId="
									+ cliqueid);
							db.update("Delete from buildinfo where famId="
									+ cliqueid);
							db.update("Delete from buildpedstat where setId="
									+ cliqueid);

							db
									.update("INSERT INTO BUILDINFO(famId,dim,gen,bits) VALUES("
											+ cliqueid
											+ ","
											+ tmpOutTree.size()
											+ ","
											+ (-tmpOutTree.memMaxGeneration + 1)
											+ ","
											+ (2 * tmpOutTree.numNonFounders - tmpOutTree.numFounders)
											+ ")");

						}
					} catch (Exception e) {
						System.out.println(e.getCause() + e.getMessage());
					}
					for (int t = 0; t < tmpOutTree.size(); t++) {
						// System.out.println(tmpOutTree.elementCyrillicAt(t));
						// out.println(idFam + tmpOutTree.elementCyrillicAt(t));
						// out.println((count+1) +
						// tmpOutTree.elementCyrillicAt(t));
						// insert into db
						tmpOutTree.elementCyrillicAt(t, (count + 1), db, type,
								cliqueid);
						fam.addPerson(tmpOutTree.IDAt(t), tmpOutTree
								.IDFatherAt(t), tmpOutTree.IDMotherAt(t),
								tmpOutTree.SexAt(t), tmpOutTree.AffectionAt(t),
								-1);

					}

					/*
					 * TODO Pedigree Create Statistics
					 */
					fam.reindex(false);
					fam.sort();
					fam.kinship(false);
					Moments mom = new Moments(false);
					// System.out.print("Inbr - ");
					mom.calculate(fam.inbrVector, true, 0);
					try {
						if (cliqueid == -1) {
							db
									.update("Insert into buildpedstat(setid, TYPE, MEAN,STDEV, _MIN, _MAX) values("
											+ (count + 1)
											+ ",'"
											+ "Inbr',"
											+ Math.floor(mom.mean * 10000.0)
											/ 10000.0
											+ ","
											+ Math
													.floor(mom.stat_stdev * 10000.0)
											/ 10000.0
											+ ","
											+ Math.floor(mom.min * 10000.0)
											/ 10000.0
											+ ","
											+ Math.floor(mom.max * 10000.0)
											/ 10000.0 + ")");

						} else {
							db
									.update("Insert into buildpedstat(setid, TYPE, MEAN,STDEV, _MIN, _MAX) values("
											+ cliqueid
											+ ",'"
											+ "Inbr',"
											+ Math.floor(mom.mean * 10000.0)
											/ 10000.0
											+ ","
											+ Math
													.floor(mom.stat_stdev * 10000.0)
											/ 10000.0
											+ ","
											+ Math.floor(mom.min * 10000.0)
											/ 10000.0
											+ ","
											+ Math.floor(mom.max * 10000.0)
											/ 10000.0 + ")");

						}

					} catch (Exception e) {
						System.out.println(e.getCause() + e.getMessage());
					}
					mom = new Moments(false);
					// System.out.print("Kin - ");
					mom.calculate(fam.kinMatrix, true, 1);
					try {

						double stdev = 0.0;
						Double Stdev = new Double(Math
								.floor(mom.getStdev() * 10000.0) / 10000.0);

						if (Stdev.isNaN()) {
							stdev = 0;
						} else
							stdev = Stdev.doubleValue();

						if (cliqueid == -1) {
							db
									.update("Insert into buildpedstat(setid, TYPE, MEAN,STDEV, _MIN, _MAX) values("
											+ (count + 1)
											+ ",'"
											+ "Kin',"
											+ Math.floor(mom.mean * 10000.0)
											/ 10000.0
											+ ","
											+ stdev
											+ ","
											+ Math.floor(mom.min * 10000.0)
											/ 10000.0
											+ ","
											+ Math.floor(mom.max * 10000.0)
											/ 10000.0 + ")");

						} else {
							db
									.update("Insert into buildpedstat(setid, TYPE, MEAN,STDEV, _MIN, _MAX) values("
											+ cliqueid
											+ ",'"
											+ "Kin',"
											+ Math.floor(mom.mean * 10000.0)
											/ 10000.0
											+ ","
											+ stdev
											+ ","
											+ Math.floor(mom.min * 10000.0)
											/ 10000.0
											+ ","
											+ Math.floor(mom.max * 10000.0)
											/ 10000.0 + ")");
						}
					} catch (Exception e) {
						System.out
								.println(e.getCause() + ": " + e.getMessage());
					}
				}
				if (MAX_STEP_ANC == 999999)
					count++;
				count2++;
			}
		}// Fine ciclo per considerare tutti i nicelittlegroups
		OutTree tmpOutTree = new OutTree();
		if (1 == 1)
			return true;
		return true;
	}

	public boolean inOtherTree(String IDPerson, Tree caller, int generazione,
			int posizione) {
		boolean trovato = false;

		Tree tmpTree;
		int numTree = trees.indexOf(caller);
		for (int t = 0; t < trees.size(); t++) {
			tmpTree = (Tree) trees.elementAt(t);
			if (tmpTree.isHere(IDPerson)) {
				int[] f = tmpTree.lookCoordinatesFor(IDPerson);
				// se max step e' impostato, se i due alberi sono gia' collegati
				// posso aggiungere altri link

				// da rimettere cosi'. fatto solo per paola 5/6/2003 (if
				// ((f[0]-1) + generazione < MAX_STEP_ANC ) {)
				// if (((f[0]-1) + generazione < MAX_STEP||MAX_STEP==0) ||
				// (tmpTree.containLinkedTree(trees.indexOf(caller)) && (f[0]-1)
				// + generazione < MAX_STEP_ANC )) {
				// qua sotto variato per collegare fino a maxstep, ma se gli
				// alberi non sono linkati si puo' andare avanti
				// if ((f[0]-1) + generazione <= MAX_STEP_ANC ||
				// (!tmpTree.containLinkedTree(trees.indexOf(caller))&&
				// ((f[0]-1) + generazione <= MAX_STEP_ANC_NC)) ) {

				// if (((f[0]-1) + generazione <= MAX_STEP||MAX_STEP==0) ||
				// (tmpTree.containLinkedTree(trees.indexOf(caller)) && (f[0]-1)
				// + generazione < MAX_STEP_ANC ) ||
				// (!tmpTree.containLinkedTree(trees.indexOf(caller))&&
				// ((f[0]-1) + generazione <= MAX_STEP_ANC_NC))) {
				if ((tmpTree.containLinkedTree(trees.indexOf(caller))
						&& (f[0] - 1) + generazione < MAX_STEP_ANC && !force)
						|| (!tmpTree.containLinkedTree(trees.indexOf(caller)) && (((f[0] - 1)
								+ generazione <= MAX_STEP_ANC_NC) || force))) {

					// if (!tmpTree.containLinkedTree(trees.indexOf(caller)) ||
					// (tmpTree.containLinkedTree(trees.indexOf(caller)) &&
					// (f[0]-1) + generazione < MAX_STEP_ANC )) {
					trovato = true;
					tmpTree.setLink(IDPerson, f[0] - 1, f[1], true);
					tmpTree.setLinkedTree(trees.indexOf(caller), generazione,
							(f[0] - 1));
					caller.setLink(IDPerson, generazione, posizione, false);
					caller.setLinkedTree(trees.indexOf(tmpTree));
				}
			}

		}
		return trovato;
	}

	public void setMaxStepsAnc(int maxStepsAnc, boolean mustForce) {
		// MAX_STEP = maxSteps;
		MAX_STEP_ANC = maxStepsAnc;
		MAX_STEP_ANC_NC = maxStepsAnc;
		force = mustForce;
	}
}
