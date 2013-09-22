package pedviz.clustering.clique.calc;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import pedviz.algorithms.Algorithm;
import pedviz.clustering.clique.ui.db.DataBase; //import pedviz.clustering.clique.ui.exception.CalcException;
//import pedviz.clustering.clique.ui.util.ProgressDialog;
import pedviz.clustering.clique.calc.util.struct.Matrix;
import pedviz.graph.*;

//Prima modifica: prende in input un pedigree anziche la matrice di kinship
public class Calc implements Algorithm {

    /* GUI */
    pedviz.clustering.clique.ui.db.DataBase database;

	ChangeListener l;

	int percent = 0;

	String message = "Clique building...";

	String outputtable = "groups";

	String outputstat = "groupstat";

	String resultMsg = "", errorMsg = "";

	/* GUI END */

	boolean automatic = false;

	boolean hadSolutions = false; // se non viene trovata una soluzione...

	// maxSize per dire quale e' stato il +
	// grande

	int maxSize = 0;

	int maxStepAnc = 999999999;

	long luppato = 0;

	int maxLuppato = 100000; // il numero massimo di loop con size<sizeDa

	// prima di abbandonare il clique

	int count = 0;

	boolean force = false; // nella costruzione dei pedigree forza la ricerca

	// di almeno un link

	// fra tutte le persone
	boolean PWRBased = true;

	boolean firstLoopWarning = true; // quando entra in loop perche' troppi

	// clique con size<sizeDa

	boolean rangeSelected = false;

	boolean pwKin = true; // se true si usa la matrice di kinship

	boolean onlyPed = false; // se true si usa la matrice di kinship

	boolean majKin = true; // non serve, raggruppa le persone nella fase

	// iniziale di

	// generazione clique con uno score in base alla maggior
	// kinship fra loro o minor numero di step fra loro
	boolean theBest = true;

	boolean allBest = false;

	boolean explore = true; // fase esplorativa, non vogliamo vedere l'output

	// completo di ogni ciclo x esempio

	boolean permute = false; // considera tutti i gruppi con ripetizione

	boolean view = true; // quando con shuffle (cycles>0) permette di avere

	// in dettaglio il miglior gruppo

	boolean genPed = false; // richiede la generazione dei pedigree

	// è compatibile con un range >= e basta
	Vector gruppiPed, gruppiMinimKin;

	boolean exploreClasses = false; // esplora per classi predefinite di kinship

	boolean loopClasses = false; // esplora per classi predefinite di kinship

	// con step 2 e 3

	boolean fineSearch = false; // ricerca fine x la generazione dei

	// pedigree.parte da >0.25 in giu

	// finche non diminuisce la kin
	int numRaggrClass; // numero di raggruppamento classi per step 1/n

	double rangeDa = 0.00000001; // valore di default per kinship

	double rangeA = 0.0625; // valore di default per kinship

	int sizeDa = 30; // valore di default

	int sizeA = 999999999; // valore di default

	int seed = 1234; // valore di default

	int cycles = 100; // valore di default

	String fileName = "pedigree.ped"; // valore di default

	boolean help = false; // valore di default

	Pedigree kin;

	boolean writeRelatedness = false; // scrive il file kin.txt

	boolean kinStatsAll = true; // scrive le statistiche di kinship per il

	// campione intero

	boolean kinStatsGroups = true; // scrive le statistiche di kinship per ogni

	// gruppo estratto (media e dev. standard)

	int[] selected; // se kinStatsGroups = true allora riempie questo vettore

	// con i selezionati

	// per calcolare le statistiche di kinship su ciascun gruppo
	boolean shuffle = true; // se cycles > 0 shuffle=true

	int numInd = 0;

	final int CLIQUE_CONTINUE = -13;

	final int CLIQUE_ABORT = -12;

	final int CLIQUE_FOUND = -11;

	Matrix matrix;

	Matrix storeMatrix;

	int[] All;

	int actual;

	int[] pointer;

	List best;

	List compsub;

	List shuffler;

	List storeShuffler;

	int numSet = 0;

	int oldSet = 0;

	int countInd, countPWR;

	int countGroups;

	int biggerSet, biggerInd, biggerCountPWR, biggerSetSeed;

	int biggerKinClassSet, biggerKinClassInd, biggerKinCountPWR,
			biggerKinClassSeed;

	double biggerKinDa, biggerKinA;

	int classi_num = 12;

	double[] classi = { 
			0.000000000001, 
			0.00048828125, 
			0.0009765625,
			0.001953125, 
			0.00390625, 
			0.0078125, 
			0.015625, 
			0.03125, 
			0.0625,
			0.125,
			0.25, 
			0.99 };

	/*double[] classi = {
	0.99,
	0.25, 
	0.125,
	0.0625,
	0.03125, 
	0.015625, 
	0.0078125, 
	0.00390625, 
	0.001953125, 
	0.0009765625,
	0.00048828125, 
	0.000000000001};*/
	double[] classiMeioticSteps = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			14, 9999 };

	Vector selgroups;

	int cliqueid;

	/*
	 * 1..cliques 2..pedigree building
	 * 
	 */
	byte runmode = 1;

	/*
	 * 0..regular 1..from residuals 2..explore
	 * 
	 * 
	 */
	int analysistype = 0;
	
	boolean ibdoptim = false;

    
    Graph graph;
    

    /*
     * GUI ProcessDialog
     */

    /*
     * 0..regular 1..from residuals 2..explore
     * 
     * 
     */

	/*
	 * GUI ProcessDialog
	 */

	/*
	 * 0..regular 1..from residuals 2..explore
	 * 
	 * 
	 */
	public void setType(int type) {
		this.analysistype = type;
		switch (type) {
		case 0:
			outputtable = "groups";
			outputstat = "groupstat";
			break;
		case 1:
			outputtable = "residuals";
			outputstat = "residualstat";
			break;
		case 2:
			outputtable = "egroups";
			outputstat = "egroupstat";
			break;
		}
	}
	
	public void setIbdoptim(boolean ibdoptim){
		this.ibdoptim = ibdoptim;
	}

	public String getMessage() {
		return message;
	}

	public int getPercentComplete() {
		return percent;
	}

	public void addChangeListener(ChangeListener l) {
		this.l = l;
	}

	public String getresultMsg() {
		return resultMsg;
	}

	public String geterrorMsg() {
		return errorMsg;
	}

	/*
	 * GUI END ProcessDialog
	 */

	/* GUI */
	public void setDatabase(DataBase database) {
		this.database = database;
	}

	public void setRunMode(byte runmode) {
		this.runmode = runmode;
	}

	public void setSelGroups(Vector selgroups) {
		this.selgroups = selgroups;

	}

	public void setCliqueid(int cliqueid) {
		this.cliqueid = cliqueid;

	}

	public void run() {
		try {
			if (runmode == 1) {
				errorMsg = "";
				message = "Clique building...";
				cliques();
			}
			if (runmode == 2) {
				errorMsg = "";
				message = "Pedigree building...";
				genPedfromCliques(selgroups, cliqueid);
			}
		} catch (Exception ex) {
		    ex.printStackTrace();
		    System.out.println(ex.getMessage());
		}
	}

	protected void cliques() {
		int i, j;
		i = j = 0;
		double conn = 0.0;

		genPed = false;
		numInd = kin.getSelected();
		// Carica la matrice principale con i valori calcolati da pedigree
		matrix = new Matrix(numInd);
		storeMatrix = new Matrix(numInd);

		kin.cloneKinMatrix(matrix);
		// if (shuffle)
		kin.cloneKinMatrix(storeMatrix);

		// Genera la lista iniziale (tutti) degli individui da utilizzare per
		// individuare i cliques
		// Con l'opzione shuffle la lista viene caricata in un vettore shuffler
		// in modo da poterla ripristinare
		// ad ogni ciclo e permutare sulla lista iniziale, cosi' a partire dallo
		// stesso random seed è possibile recuperare
		// l'ordine della lista e i set derivanti
		All = new int[numInd];
		if (shuffle) {
			shuffler = new ArrayList();
			storeShuffler = new ArrayList();
		}
		for (int c = 0; c < numInd; c++) {
			All[c] = c;
			if (shuffle) {
				shuffler.add(new Integer(c));
				storeShuffler.add(new Integer(c));
			}
		}

		// System.out.println("---Start---");
		int db = 0;
		int fine = 0;
		biggerKinClassSet = biggerKinClassInd = biggerKinClassSeed = 0;
		biggerKinDa = biggerKinA = 0.0;
		for (fine = 0; fine < 1 + (fineSearch == true ? 2 : 0); fine++) {
			for (db = 0; db < 1 + (loopClasses == true ? 2 : 0); db++) {

				for (int classe = 0; classe < (classi.length - (db + 1)); classe++) { // se
					// e'
					// stata
					// effettuata
					// la
					// scelta
					// exploreClasses
					// si
					// cicla
					// per
					// i
					// range
					// contenuti
					// nella lista classi, altrimenti un solo giro
					if (!exploreClasses) {
						classe = 9999999; // un solo ciclo, non e' stata
						// selezionata l'opzione
						// exploreClasses
						// System.out.println("User defined range: "+rangeDa+" -
						// "+rangeA);
					} else {
						// rangeDa = classi[classe];
						// rangeA = classi[classe+1+db];
						rangeA = classi[classi.length - 1];
						rangeDa = classi[classe + 1 + db];
						if (db == 1 && classe == 0) {
							message = "->Doubling predefined ranges";
							l.stateChanged(null);
							// System.out.println();
							// System.out.println("->Doubling predefined
							// ranges");
							// System.out.println();
						}
						if (db == 2 && classe == 0) {
							message = "->Tripling predefined ranges";
							l.stateChanged(null);
							// System.out.println();
							// System.out.println("->Tripling predefined
							// ranges");
							// System.out.println();
						}

						/*
						if (classe == 0) {
							System.out
									.println("Predefined range: All reciprocally connected individuals");
						} else {
							if ((classe + 2 + db) == classi.length) {
								// System.out.println("Predefined range:
								// >="+rangeDa);
							} else {
								// System.out.println("Predefined range:
								// "+rangeDa+" - "+rangeA);
								/**
								 * TODO: EXPLORE
								 */
					/*			System.out.println("Predefined range: "
										+ rangeDa + " - MAX");
							}
						}*/
					}

					biggerSet = biggerInd = biggerCountPWR = biggerSetSeed = 0;
					int where = 0;
					for (int cycle = 1; cycle <= cycles; cycle++) { // quanti
						// cicli, se
						// selezionato
						// shuffle
						if (!shuffle)
							cycle = 9999999;
						else {
							if (!explore && cycles > 1)
							{	//System.out.println(" [Cycle#" + cycle + "]");
							    int k;
							}	
							else {
								if (cycles > 1) {

									/*
									 * TODO if(exploreClasses) where = classe /
									 * classi_num; else
									 */
									where = (cycle * 100) / cycles;
									percent = where;
									l.stateChanged(null);
								}
							}
						}
						genClique();
					}

					reportRes();

					if ((shuffle || cycles == 1) && view && biggerInd >= sizeDa) {
						hadSolutions = true;
						// System.out.println();
						// System.out.println("Best result:");
						seed = biggerSetSeed;
						writeShuffle();
						explore = false;
						shuffle = false;
						if (genPed) {
							gruppiPed = new Vector();
							gruppiMinimKin = new Vector();
						}

						genClique();
						if (genPed) {/*
										 * try { generatePedigrees(1, -1);
										 * message = "Pedigree building...";
										 * l.stateChanged(null); } catch
										 * (Exception ex) { /* TODO Exception
										 */
							/*
							 * System.out.println(ex.getMessage() + ":" +
							 * ex.getStackTrace()); }
							 */
						}
						explore = true;
						shuffle = true;
					}
				} // ciclo classi
			} // ciclo double classi
		} // ciclo fine---
		// if (exploreClasses&&explore){ //
		if (exploreClasses && biggerKinClassInd != 0) { // se esploro per classi // Exploration
			// alla fine estraggo
			// anche il piu' grande
			// fra tutti
			hadSolutions = true;
			//System.out.println();
			
			if (theBest && !allBest) {
				
				resultMsg = "Largest subgroup: \nIndividuals: " + biggerKinClassInd
						+ "\nSeed: "  + biggerKinClassSeed;
				/*
				 * System.out.println("Overall bigger group: (#Ind:" +
				 * biggerKinClassInd + " - Seed:" + biggerKinClassSeed + ")");
				 */

			} else {
				resultMsg = "Best partition: \nCliques: "
						+ biggerKinClassSet + "\nIndividuals: " + biggerKinClassInd
						+ "\nSeed: " + biggerKinClassSeed;
				/*
				 * System.out.println("Overall bigger group: (#Groups:" +
				 * biggerKinClassSet + " - #Ind:" + biggerKinClassInd + " -
				 * Seed:" + biggerKinClassSeed + ")");
				 */

			}

		
			resultMsg = resultMsg
					+ "\nKin: "
					+ (biggerKinA != 999999999 ? (biggerKinDa + " - " + biggerKinA)
							: ">=" + biggerKinDa);

			/*
			 * System.out .println(" (#Kin" + (biggerKinA != 999999999 ?
			 * (biggerKinDa + " - " + biggerKinA) : ">=" + biggerKinDa) + ")");
			 */
		}
	}

	/* GUI END */

	public void onlyPedigrees() throws Exception {
		Vector tmpGruppiPed = new Vector();
		gruppiPed = new Vector();
		tmpGruppiPed = kin.getAllIndividuals();
		gruppiPed.add((Vector) tmpGruppiPed.clone());
		generatePedigrees(0, -1);
	}

	public void genPedfromCliques(Vector selgroups, int cliqueid) {

		genPed = true;
		gruppiPed = new Vector();
		// update Statistics
		gruppiMinimKin = new Vector();

		//triangMatrix old_matrix = new triangMatrix(kin.getSelected());
		//matrix.cloneInto(old_matrix);

		for (int i = 0; i < selgroups.size(); i++) {
			double minimKin = 0;
			Vector inputList = (Vector) selgroups.elementAt(i);
			selected = new int[inputList.size()];
			for (int ind = 0; ind < inputList.size(); ind++) {
				int index = 0;
				if (inputList.elementAt(ind) != null) {
					String tmp = (String) inputList.elementAt(ind);
					index = kin.getIndKinmatrixById(Integer.valueOf(tmp));
					selected[ind] = index;
				}
			}
			minimKin = kinStats(pwKin);
			gruppiMinimKin.add(minimKin);
		}
		// ////////////////////
		// //////////////////
		gruppiPed = selgroups;
		generatePedigrees(1, cliqueid);

		//old_matrix.cloneInto(matrix);
	}

	public void genClique() {
		actual = countGroups = countInd = countPWR = numSet = 0;
		oldSet = -1;
		// int CICLI=0;
		if (shuffle) // se shuffle ricarico All col nuovo shuffle e ripeto il
			// ciclo
			writeShuffle();

		compsub = new ArrayList();
		best = new ArrayList();

		while (oldSet != numSet) {
			oldSet = numSet;
			for (int prove = 0; prove < (theBest ? 1 : 10); prove++) {
				luppato = 0;
				maxLuppato = 100000;
				solve(All, 0, numInd);
				// CICLI++;
			}
		}
		if (theBest) {
			countInd = best.size(); // se cerchiamo il migliore il numero di
			// individui e' quello contenuto in best
			countPWR = (best.size() * (best.size() - 1)) / 2;
			numSet = 1;
		}
		if ((!PWRBased && countInd > biggerInd || (PWRBased && countPWR > biggerCountPWR))) { // se
			// shuffle
			// tiene
			// traccia
			// del
			// ciclo
			// che
			// ha
			// unito
			// piu'
			// individui
			biggerInd = countInd;
			biggerCountPWR = countPWR;
			biggerSet = numSet;
			if (shuffle)
				biggerSetSeed = seed - 1;
			else
				biggerSetSeed = seed;
		}
		if ((exploreClasses && ((!PWRBased && countInd > biggerKinClassInd) || (PWRBased && countPWR > biggerKinCountPWR)) ||
				(!PWRBased && (countInd == biggerKinClassInd) && (rangeDa > biggerKinDa))
		)) {// se
			// shuffle
			// ed
			// esplorazione
			// classi
			// tiene
			// traccia
			// del
			// ciclo
			// che
			// ha
			// unito
			// piu'
			// individui
			// in
			// assoluto
			biggerKinClassInd = countInd;
			biggerKinCountPWR = countPWR;
			if (!theBest)
				biggerKinClassSet = numSet;
			biggerKinClassSeed = seed - 1;
			biggerKinDa = rangeDa;
			biggerKinA = rangeA;
		}

		if (!permute && theBest) { // se abbiamo scelto di vedere solo il piu'
			// grande -- per questo countPWR deve essere
			// attivato all'interno di generazione
			// clique
			findBest();
			if ((countInd > biggerInd)) { // se shuffle tiene traccia del
				// ciclo che ha unito piu' individui
				biggerInd = countInd;
				biggerSet = numSet;
				if (shuffle)
					biggerSetSeed = seed - 1;
				else
					biggerSetSeed = seed;
			}
			if (exploreClasses && ((countInd > biggerKinClassInd) ||
					((countInd == biggerKinClassInd) && (rangeDa > biggerKinDa)))) { // se
				// shuffle
				// tiene
				// traccia
				// del ciclo
				// che ha
				// unito
				// piu'
				// individui
				// if ((countInd>biggerKinClassInd)){ //se shuffle tiene traccia
				// del ciclo che ha unito piu' individui
				biggerKinClassInd = countInd;
				biggerKinClassSet = numSet;
				biggerKinClassSeed = seed - 1;
				biggerKinDa = rangeDa;
				biggerKinA = rangeA;
			}
		}
	}

	public void findBest() {
		double minimKin = 0;
		if (best.size() > maxSize)
			maxSize = best.size();
		//if (!explore && best.size() >= sizeDa)
		//	System.out.println("Biggest (ind#:" + best.size() + ")");
		if (best.size() >= sizeDa) {
			hadSolutions = true;
			if (!explore && (kinStatsGroups || genPed))
				selected = new int[best.size()];
			for (int x = 0; x < best.size(); x++) { // se si vogliono tutti i
				// migliori, si eliminano
				// gli individui del
				// migliore selezionato
				if (!explore) {
					/* GUI */
					int index = kin.getIdByIndKinmatrix(((Integer) best.get(x))
							.intValue());
					//System.out.println(index);
					try {

						if (analysistype == 2)
							database.update("INSERT INTO " + outputtable
									+ "(kinship,setid,id) VALUES(" + rangeDa
									+ "," + numSet + "," + index + ")");
						else
							database.update("INSERT INTO " + outputtable
									+ "(setid,id) VALUES(" + numSet + ","
									+ index + ")");

					} catch (Exception e) {
						System.out.println(e.getCause() + e.getMessage());
					}
					/* GUI END */
				}
				if (!explore && (kinStatsGroups || genPed))
					selected[x] = ((Integer) best.get(x)).intValue();
				int posiz = ((Integer) best.get(x)).intValue();
				for (int y = 0; y < matrix.length(); y++) {
					matrix.setTriangMatrix(-1, posiz, y);
				}
			}
			if (!explore && kinStatsGroups) {
				try {

					if (analysistype == 2) {

						DecimalFormat df = new DecimalFormat("0.000000");
						DecimalFormatSymbols dfs =new DecimalFormatSymbols();
						dfs.setDecimalSeparator('.');
						df.setDecimalFormatSymbols(dfs);

								
						database.update("INSERT INTO " + outputstat
								+ "(kinship,setid, TYPE, no) VALUES(" + df.format(rangeDa)
								+ "," + numSet + ",'I'," + best.size() + ")");
						database.update("INSERT INTO " + outputstat
								+ "(kinship,setid, TYPE, no) VALUES(" + df.format(rangeDa)
								+ "," + numSet + ",'K'," + best.size() + ")");
					} else {
						database.update("INSERT INTO " + outputstat
								+ "(setid, TYPE, no) VALUES(" + numSet
								+ ",'I'," + best.size() + ")");
						database.update("INSERT INTO " + outputstat
								+ "(setid, TYPE, no) VALUES(" + numSet
								+ ",'K'," + best.size() + ")");
					}

				} catch (Exception ex) {
					System.out.println("Stat Error:" + ex.getCause()
							+ ex.getMessage());
				}
				minimKin = kinStats(pwKin, numSet);

			}
			if (!explore && genPed)
				addIndForPedigrees(minimKin);
			if (!explore)
			{
			//System.out.println();
			}	
		} else if (!explore){
			//System.out.println();
		}
	}

	private void reportRes() {
		if (!hadSolutions) {
			/* GUI */
			resultMsg = "No solutions for selected parameters\n"
					+ "Bigger set size: " + maxSize;
			/* GUI_END */
			/*
			 * System.out.println(); System.out.println("No solutions for
			 * selected parameters"); System.out.println("(Bigger set size: " +
			 * maxSize + ")"); System.out.println();
			 */
		}

		if ((!theBest || allBest) && biggerInd >= sizeDa) {
			/* GUI */
			resultMsg = "Best partition: \nCliques: " + biggerSet + "\nIndividuals: "
					+ biggerInd + "\nPairwise relationships: " + biggerCountPWR + "\nSeed: "
					+ biggerSetSeed;

			/* GUI_END */
			/*
			 * System.out.println("Best result: (#Groups:" + biggerSet + " -
			 * #Ind:" + biggerInd + " - #PWR:" + biggerCountPWR + " - Seed:" +
			 * biggerSetSeed + ")"); System.out.println();
			 */
		}

		if (((shuffle || explore) && (theBest && !allBest))
				&& biggerInd >= sizeDa) {
			/* GUI */
			resultMsg = "Largest subgroup:\nIndividuals: " + biggerInd + "\nPairwise relationships: "
					+ biggerCountPWR + " \nSeed: " + biggerSetSeed;

			/* GUI_END */
			/*
			 * System.out.println("Bigger group: (#Ind:" + biggerInd + " -
			 * #PWR:" + biggerCountPWR + " - Seed:" + biggerSetSeed + ")");
			 * System.out.println();
			 */
		}
	}

	public int solve(int[] selection, int ne, int ce) {
		// System.out.println((count++)+" ");
		double minimKin = 0.0;
		int fixp = 0;
		int[] news = new int[ce];
		// System.out.println(ce);
		int minnod = ce;
		int nod = 0;
		int newne, newce, i, j, count, pos, p, s, sel;
		int result = CLIQUE_CONTINUE;
		double sumKin = 0;
		double minKin = 0;
		pos = s = 0;
		if (!pwKin)
			minKin = 999999999;

		for (i = actual; i < ce && minnod != 0; i++) {
			p = selection[i];
			count = 0;
			sumKin = 0; // somma le relazioni di kinship per selezionare sia il
			// migliore che
			// quello con la maggior kinship fra tutti
			// o quello col minor numero di step

			/* Count disconnections */
			for (j = ne; j < ce && count < minnod; j++) {
				// if (matrix[p][selection[j]] != 1) {
				if (p != selection[j]
						&& (matrix.retrieveTriangMatrix(p, selection[j]) == -1
								|| matrix.retrieveTriangMatrix(p, selection[j]) < rangeDa || matrix
								.retrieveTriangMatrix(p, selection[j]) > rangeA)) {
					count++;
					/* Save position of potential candidate */
					pos = j;
				} else {
					if (p != selection[j])
						sumKin += matrix.retrieveTriangMatrix(p, selection[j]);
					// System.out.println(matrix.retrieveTriangMatrix(p,
					// selection[j]));
				}
			}

			/* Test new minimum */
			// if (count < minnod ||
			// (majKin&&count==minnod&&sumKin>minKin)||(!majKin&&count==minnod&&sumKin<minKin))
			// { //sembra parecchio migliorato cosi'
			if (count < minnod
					|| (majKin && count == minnod && sumKin > minKin)) { // sembra
				// parecchio
				// migliorato
				// cosi'
				fixp = p;
				minnod = count;
				minKin = sumKin;
				if (i < ne) {
					s = pos;
				} else {
					s = i;
					/* preincr */
					nod = 1;
				}
			}
		}

		/* If fixed point initially chosen from candidates then */
		/* number of disconnections will be preincreased by one */

		/* Backtrackcycle */
		for (nod = minnod + nod; nod >= 1; nod--) {
			/* Interchange */
			p = selection[s];
			selection[s] = selection[ne];
			sel = selection[ne] = p;

			/* Fill new set "not" */
			newne = 0;
			for (i = 0; i < ne; i++) {
				// if (matrix[sel][selection[i]]==1) {
				if (matrix.retrieveTriangMatrix(p, selection[i]) != -1
						&& matrix.retrieveTriangMatrix(p, selection[i]) >= rangeDa
						&& matrix.retrieveTriangMatrix(p, selection[i]) <= rangeA) {
					news[newne++] = selection[i];
				}
			}

			/* Fill new set "cand" */
			newce = newne;
			for (i = ne + 1; i < ce; i++) {
				// if (matrix[sel][selection[i]]==1) {
				if (matrix.retrieveTriangMatrix(sel, selection[i]) != -1
						&& matrix.retrieveTriangMatrix(sel, selection[i]) >= rangeDa
						&& matrix.retrieveTriangMatrix(sel, selection[i]) <= rangeA) {
					news[newce++] = selection[i];
				}
			}
			/* Add to compsub */
			compsub.add(new Integer(sel));

			if (newce == 0 || compsub.size() == sizeA) {
				if (best.size() < compsub.size()) {
					/* found a max clique */
					best.clear();
					best.addAll(compsub);
					if (best.size() > maxSize)
						maxSize = best.size();
				}
				// if (compsub.size()>=sizeDa && compsub.size()<=sizeA &&
				// !theBest) { //se non si e' scelto theBest vanno bene tutti i
				// gruppi compresi nel range da - a
				DecimalFormat df = new DecimalFormat("0.000000");
				DecimalFormatSymbols dfs =new DecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				df.setDecimalFormatSymbols(dfs);

				
				
				if (compsub.size() >= sizeDa && !theBest) {
					hadSolutions = true;
					int posiz;
					++numSet;
					if (!explore && (kinStatsGroups || genPed))
						selected = new int[compsub.size()];
					if (!explore) {
						/*
						 * System.out.println("SET#" + numSet + " SIZE=\t" +
						 * compsub.size());
						 */
						try {
							/*
							 * System.out.println("SET#" + numSet + " SIZE=\t" +
							 * compsub.size());
							 */

							if (analysistype == 2) {

								database.update("INSERT INTO " + outputstat
										+ "(kinship,setid, TYPE, no) VALUES("
										+ df.format(rangeDa) + "," + numSet + ",'I',"
										+ compsub.size() + ")");
								database.update("INSERT INTO " + outputstat
										+ "(kinship,setid, TYPE, no) VALUES("
										+ df.format(rangeDa) + "," + numSet + ",'K',"
										+ compsub.size() + ")");
							} else {
								database.update("INSERT INTO " + outputstat
										+ "(setid, TYPE, no) VALUES(" + numSet
										+ ",'I'," + compsub.size() + ")");
								database.update("INSERT INTO " + outputstat
										+ "(setid, TYPE, no) VALUES(" + numSet
										+ ",'K'," + compsub.size() + ")");
							}
						} catch (Exception ex) {
							System.out.println("stat:" + ex.getCause()
									+ ex.getMessage());
						}
					}

					countInd += compsub.size();
					countPWR += (compsub.size() * (compsub.size() - 1)) / 2;
					for (int x = 0; x < compsub.size(); x++) {
						if (!explore) {
							int itmp = kin
									.getIdByIndKinmatrix(((Integer) compsub
											.get(x)).intValue());
							// System.out.println(itmp);
							try {

								if (analysistype == 2)
									database.update("INSERT INTO "
											+ outputtable
											+ "(kinship,setid,id) VALUES("
											+ df.format(rangeDa) + "," + numSet + ","
											+ itmp + ")");
								else
									database.update("INSERT INTO "
											+ outputtable
											+ "(setid,id) VALUES(" + numSet
											+ "," + itmp + ")");

							} catch (Exception e) {
								System.out.println(e.getCause()
										+ e.getMessage());
							}

						}

						// --> x AVERE i progressivi if
						// (!explore)System.out.println((((Integer)compsub.get(x)).intValue()));
						if (!explore && (kinStatsGroups || genPed))
							selected[x] = ((Integer) compsub.get(x)).intValue();
						posiz = ((Integer) compsub.get(x)).intValue();
						if (!permute) {
							for (int y = 0; y < numInd; y++) {
								matrix.setTriangMatrix(-1, posiz, y);
							}
						}
					}
					if (!explore && kinStatsGroups)
						minimKin = kinStats(pwKin, numSet);
					if (!explore && genPed)
						addIndForPedigrees(minimKin);
					if (!explore){
						//System.out.println();
					}	
					result = CLIQUE_ABORT;
				} else
					result = CLIQUE_CONTINUE; // corretto questo, ma puo'
				// metterci un sacco di tempo se
				// si formano troppi gruppi con
				// dimensione
				// minore a sizeDa - da controllare
				luppato++;
				if (luppato >= maxLuppato) {
					if (firstLoopWarning) {/*
											 * System.out.println();
											 * System.out.println();
											 * System.out.println("Warning -
											 * More than " + maxLuppato + "
											 * cliques with the requested
											 * parameters"); System.out
											 * .println(" trying to find the
											 * best set...please wait");
											 * System.out.println(); System.out
											 * .println("Hint: don't use less
											 * than 100 permutations to explore
											 * the clique space");
											 * System.out.println();
											 * System.out.print("...");
											 */
						/*
						 * TODO add msg
						 */
						message = "More than 10K cliques." +
								" Trying to find the best set."; //+ "\n" +
								//"Hint: don't use less than 100 permutations to explore the clique space";
						l.stateChanged(null);
						
						firstLoopWarning = false;
					}
					// else maxLuppato=-10000;
					luppato = 0;
					result = CLIQUE_ABORT;
					// System.exit(1);
				}
				// result = CLIQUE_ABORT;
				switch (result) {
				case CLIQUE_CONTINUE:
					break;
				case CLIQUE_ABORT:
					compsub.remove(compsub.size() - 1);
					return result;
				}

			} else {
				if (newne < newce) {
					result = solve(news, newne, newce);
					if (result != CLIQUE_CONTINUE) {
						compsub.remove(compsub.size() - 1);
						if (compsub.size() == 1)
							result = CLIQUE_CONTINUE;
						return result;
					}
				}
			}

			/* Remove from compsub */
			compsub.remove(compsub.size() - 1);
			/* Add to "nod" */
			ne++;
			if (nod > 1) {
				// Select a candidate disconnected to the fixed point
				for (s = ne; (matrix.retrieveTriangMatrix(fixp, selection[s]) != -1
						&& matrix.retrieveTriangMatrix(fixp, selection[s]) >= rangeDa && matrix
						.retrieveTriangMatrix(fixp, selection[s]) <= rangeA); s++) {
					// nothing
				}

			} // end selection

		} /* Backtrackcycle */

		return result;
	}

	private void writeShuffle() {// , int seed
		shuffler.clear();
		shuffler.addAll(storeShuffler);
		Random rnd = new Random(seed++);
		Collections.shuffle(shuffler, rnd);
		// System.out.println("seed " + (seed-1));
		for (int c = 0; c < shuffler.size(); c++) {
			All[c] = ((Integer) shuffler.get(c)).intValue();
		}
		storeMatrix.cloneInto(matrix);

	}

	/*
	 * GUI
	 * 
	 * Recalulates Inbr + Kin
	 * 
	 */
	public void updateStat(int setid, Vector inputList, String table) {

		int[] select = new int[inputList.size()];
		for (int ind = 0; ind < inputList.size(); ind++) {
			int index = 0;
			if (inputList.elementAt(ind) != null) {
				String tmp = (String) inputList.elementAt(ind);
				index = kin.getIndKinmatrixById(Integer.valueOf(tmp));
				select[ind] = index;
			}
		}

		double[] dataKin = new double[(select.length * (select.length - 1)) / 2];
		double[] dataInbr = new double[select.length];
		int ind = 0, indy = 0;
		for (int x = 0; x < select.length; x++) {
			dataInbr[indy++] = kin.getinbrValue(select[x]);
			for (int y = x + 1; y < select.length; y++) {
				dataKin[ind++] = storeMatrix.retrieveTriangMatrix(select[x],
						select[y]);
			}
		}
		try {
			Moments mom = new Moments(true);
			// Inbr
			mom.calculateVector(dataInbr, 0);
			database.update("UPDATE " + table + " Set mean = "
					+ Math.floor(mom.getMean() * 10000.0) / 10000.0 + ","
					+ " stdev = " + Math.floor(mom.getStdev() * 10000.0)
					/ 10000.0 + "," + " _min = "
					+ Math.floor(mom.getMin() * 10000.0) / 10000.0 + ","
					+ " _max = " + Math.floor(mom.getMax() * 10000.0) / 10000.0
					+ " WHERE setid =" + setid + " AND TYPE ='I'");
			mom = new Moments(pwKin);
			// Kin
			mom.calculateVector(dataKin,1);
			database.update("UPDATE " + table + " Set mean = "
					+ Math.floor(mom.getMean() * 10000.0) / 10000.0 + ","
					+ " stdev = " + Math.floor(mom.getStdev() * 10000.0)
					/ 10000.0 + "," + " _min = "
					+ Math.floor(mom.getMin() * 10000.0) / 10000.0 + ","
					+ " _max = " + Math.floor(mom.getMax() * 10000.0) / 10000.0
					+ " WHERE setid =" + setid + " AND TYPE ='K'");
		} catch (Exception e) {
			System.out.println(e.getCause() + e.getMessage());
		}
	}

	public void updatePedStat(int setid) {
		Family fam = new Family(0);
		OutTree tmpOutTree = new OutTree();
		try {
			// Run the query, creating a ResultSet
			ResultSet r = database
					.query("Select distinct id,idFather,idMother,sex,affection FROM buildped where famid="
							+ setid);
			r.beforeFirst();
			while (r.next()) {
				fam.addPerson(r.getInt("id"), r.getInt("idFather"), r
						.getInt("idMother"), r.getInt("sex"), r
						.getInt("affection"),-1);
				tmpOutTree.add(r.getString("id"), r.getString("idFather"), r
						.getString("idMother"), r.getString("sex"), r
						.getInt("affection"));
			}

			tmpOutTree.sortGenerationsDown();
			tmpOutTree.sortGenerationsUp();
			tmpOutTree.sortGenerationsZeroDown();
			tmpOutTree.sort();

			database.update("Update BUILDINFO set " + " dim ="
					+ tmpOutTree.size() + "," + " gen="
					+ (-tmpOutTree.memMaxGeneration + 1) + "," + " bits="
					+ (2 * tmpOutTree.numNonFounders - tmpOutTree.numFounders)
					+ " where famId =" + setid);

			/*
			 * fam.reindex(false); fam.sort(); fam.kinship(false); moments mom =
			 * new moments(false); mom.calculate(fam.inbrVector, true); database
			 * .update("Update buildpedstat set" + " MEAN ="+Math.floor(mom.mean *
			 * 10000.0) / 10000.0 + "," + " STDEV="+Math.floor(mom.stat_stdev *
			 * 10000.0) / 10000.0 + "," + " _MIN="+Math.floor(mom.min * 10000.0) /
			 * 10000.0 + "," + " _MAX"+Math.floor(mom.max * 10000.0) / 10000.0 + "
			 * WHERE setid ="+setid+" AND TYPE ='Inbr'");
			 * 
			 * mom = new moments(false); mom.calculate(fam.kinMatrix, true);
			 * database.update("Update buildpedstat set" + " MEAN
			 * ="+Math.floor(mom.mean * 10000.0) / 10000.0 + "," + "
			 * STDEV="+Math.floor(mom.stat_stdev * 10000.0) / 10000.0 + "," + "
			 * _MIN="+Math.floor(mom.min * 10000.0) / 10000.0 + "," + "
			 * _MAX"+Math.floor(mom.max * 10000.0) / 10000.0 + " WHERE setid
			 * ="+setid+" AND TYPE ='Kin'");
			 */
		} catch (Exception e) {
			System.out.println(e.getCause() + e.getMessage());
		}
	}

	private double kinStats(boolean pwKin, int setid) {
		double[] dataKin = new double[(selected.length * (selected.length - 1)) / 2];
		double[] dataInbr = new double[selected.length];
		int ind = 0, indy = 0;
		double temp = 0.0;
		for (int x = 0; x < selected.length; x++) {
			dataInbr[indy++] = kin.getinbrValue(selected[x]);
			// System.out.println(selected[x] + " " +
			// kin.getinbrValue(selected[x]));
			for (int y = x + 1; y < selected.length; y++) {
				dataKin[ind++] = storeMatrix.retrieveTriangMatrix(selected[x],
						selected[y]);
			}
		}

		try {
			Moments mom = new Moments(true);
			// System.out.print("Inbr - ");
			mom.calculateVector(dataInbr,0);

			//ToDo
			double stdev = 0.0;
			Double Stdev = new Double(Math.floor(mom.getStdev() * 10000.0)/ 10000.0);
			
			if(Stdev.isNaN()){
				stdev = 0;
			}else
				stdev = Stdev.doubleValue();

			database.update("UPDATE " + outputstat + " Set mean = "
					+ Math.floor(mom.getMean() * 10000.0) / 10000.0 + ","
					+ " stdev = " + stdev + "," + " _min = "
					+ Math.floor(mom.getMin() * 10000.0) / 10000.0 + ","
					+ " _max = " + Math.floor(mom.getMax() * 10000.0) / 10000.0
					+ " WHERE setid =" + setid + " AND TYPE ='I'");
			/*System.out.println("UPDATE " + outputstat + " Set mean = "
					+ Math.floor(mom.getMean() * 10000.0) / 10000.0 + ","
					+ " stdev = " + Math.floor(mom.getStdev() * 10000.0)
					/ 10000.0 + "," + " _min = "
					+ Math.floor(mom.getMin() * 10000.0) / 10000.0 + ","
					+ " _max = " + Math.floor(mom.getMax() * 10000.0) / 10000.0
					+ " WHERE setid =" + setid + " AND TYPE ='I'");
			*/
			
			mom = new Moments(pwKin);
			// System.out.print("Kin - ");
			temp = mom.calculateVector(dataKin,1);

			//ToDo
			Stdev = new Double(Math.floor(mom.getStdev() * 10000.0)/ 10000.0);
			
			if(Stdev.isNaN()){
				stdev = 0;
			}else
				stdev = Stdev.doubleValue();
				
			database.update("UPDATE " + outputstat + " Set mean = "
					+ Math.floor(mom.getMean() * 10000.0) / 10000.0 + ","
					+ " stdev = " + stdev + "," + " _min = "
					+ Math.floor(mom.getMin() * 10000.0) / 10000.0 + ","
					+ " _max = " + Math.floor(mom.getMax() * 10000.0) / 10000.0
					+ " WHERE setid =" + setid + " AND TYPE ='K'");
			
			
			
		} catch (Exception e) {
			System.out.println("STAT ERROR: "+e.getCause() + e.getMessage());
		}
		return temp;
	}

	/* GUI END */

	private double kinStats(boolean pwKin) {
		double[] dataKin = new double[(selected.length * (selected.length - 1)) / 2];
		double[] dataInbr = new double[selected.length];
		int ind = 0, indy = 0;
		for (int x = 0; x < selected.length; x++) {
			dataInbr[indy++] = kin.getinbrValue(selected[x]);
			for (int y = x + 1; y < selected.length; y++) {
				dataKin[ind++] = storeMatrix.retrieveTriangMatrix(selected[x],
						selected[y]);
			}
		}
		Moments mom = new Moments(true);
		// ##############################
		mom.calculateVector(dataInbr,0);
		mom = new Moments(pwKin);
		// ##############################
		return mom.calculateVector(dataKin,1);
	}

	private void addIndForPedigrees(double minimKin) {
		Vector tmpGruppiPed = new Vector();
		int val;
		for (int x = 0; x < selected.length; x++) {
			val = kin.getIdByIndKinmatrix(selected[x]);
			tmpGruppiPed.add(new String("" + val));
		}
		gruppiPed.add((Vector) tmpGruppiPed.clone());
		gruppiMinimKin.add(new Double(minimKin));
	}

	/*
	 * GUI
	 * 
	 * 0 -p n 1 other
	 * 
	 */
	public void setOnlyPed() {
		onlyPed = true;
	}

	private void generatePedigrees(int type, int cliqueid) {

		People people;
		boolean errors = false;
		boolean errorPed = false;
		int automSteps = 0;

		for (int i = 0; i < gruppiPed.size(); i++) {

			if (runmode == 2) {
				percent = (i * 100)/ gruppiPed.size();
				l.stateChanged(null);
			}
			errorPed = false;
			int errorLoop = 0;
			for (;;) {
				people = new People(gruppiPed, i, kin, database, type, cliqueid, ibdoptim);
				if (automatic && !errorPed) {
					automSteps = (int) (Math.ceil(Math
							.log(0.25 / ((Double) gruppiMinimKin.elementAt(i))
									.doubleValue())
							/ Math.log(2) + 2));
				}
				if (automatic && errorPed){
					automSteps++;
				    errorLoop++;
				}
				if (onlyPed) {
					maxStepAnc = 999999;
				}
				if (automatic) {
					people.setMaxStepsAnc(automSteps, force);
				} else
					people.setMaxStepsAnc(maxStepAnc, force);
				
				errorPed = !people.run();
				if (errorPed && automatic && errorLoop < 5)
					continue;
				if (errorPed && !automatic) {
					errors = true;
					errorMsg = "The requested # of generations is too small.";
					System.out.println(errorMsg);
					return;
				}
				break;
			}
			// store maxStep or autoStep
			if (!(errors)) {
				try {
					int steps;
					/*
					 * TODO	actual steps
					 */
					/*
					if(maxStepAnc >= 999999){
						if(automSteps == 0)
						   steps = people.getDeeperLink();
						else	
						   steps = automSteps;
					}
						else
							steps = maxStepAnc;
					*/
					
					steps = people.getDeeperLink();
					
					if (cliqueid == -1) {
						database.update("Update buildinfo set stepanc = "
								+ steps + " where famid=" + (i + 1));
					} else {
						database.update("Update buildinfo set stepanc = "
								+ steps + " where famid=" + cliqueid);
					}
				} catch (Exception ex) {
					System.out.println(ex.getCause() + ex.getMessage());
				}
			}
		}
	}

	/*
	 * GUI END
	 */

	/* GUI private -> public */
	public void setPedigree(Pedigree ped) {
		kin = ped;
	}

	public void setArguments(String args[]) {
		readArguments(args);
	}

	/* GUI END */

	public void readArguments(String[] args) {
		String arg;
		String par;
		String val;
		if (args.length > 0) {
			for (int x = 0; x < args.length; x++) {
				// arg = args[x];
				// par = arg.substring(0,2);
				// val = arg.substring(2,arg.length());
				par = args[x];
				if (par.equalsIgnoreCase("-f")) {
					x++;
					fileName = args[x];
					// fileName=val;
					continue;
				}
				if (par.equalsIgnoreCase("-p")) {
					x++;
					val = args[x];
					if (val.equalsIgnoreCase("k")) {
						pwKin = true;
						continue;
					}
					// if(val.equalsIgnoreCase("s")){
					// pwKin = false;
					// continue;
					// }
					if (val.equalsIgnoreCase("n")) {
						onlyPed = true;
						continue;
					}
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}
				if (par.equalsIgnoreCase("-m")) {
					maxStepAnc = 999999;
					x++;
					val = args[x];
					if (val.equals("e")) {
						explore = true;
						view = false;
						continue;
					}
					if (val.equalsIgnoreCase("p")) {
						force = false;
						explore = true;
						view = true;
						genPed = true;
						automatic = false;
						continue;
					}
					if (val.equalsIgnoreCase("pa")) {
						force = false;
						explore = true;
						view = true;
						genPed = true;
						automatic = true;
						continue;
					}

					if (val.substring(0, 1).equalsIgnoreCase("p")) {
						if (val.substring(1, 2).equalsIgnoreCase("f")) {
							force = true;
							explore = true;
							view = true;
							genPed = true;
							continue;
						}

						try {
							maxStepAnc = Integer.parseInt(val.substring(1, val
									.length()));
						} catch (NumberFormatException nfe) {
							continue;
						}
						/* GUI */
						automatic = false;
						explore = true;
						view = true;
						genPed = true;
						continue;
					}
					if (val.equalsIgnoreCase("r")) {
						explore = true;
						view = true;
						continue;
					}
					if (val.equalsIgnoreCase("v")) {
						explore = false;
						view = false;
						continue;
					}
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}
				if (par.equalsIgnoreCase("-e")) {
					x++;
					val = args[x];
					if (val.equalsIgnoreCase("u")) {
						theBest = false;
						allBest = false;
						permute = false;
						continue;
					}
					/*
					 * if(val.equals("p")){ theBest=false; allBest=false;
					 * permute=true; continue; }
					 */
					if (val.equalsIgnoreCase("b")) {
						theBest = true;
						allBest = false;
						permute = false;
						continue;
					}
					/*
					 * if(val.equals("a")){ theBest=true; allBest=true;
					 * permute=false; continue; }
					 */
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}
				if (par.equalsIgnoreCase("-b")) { // best is? # of pairs - #of
					// inds
					x++;
					val = args[x];
					if (val.equalsIgnoreCase("p")) {
						PWRBased = true;
						continue;
					}
					if (val.equalsIgnoreCase("i")) {
						PWRBased = false;
						continue;
					}
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}
				if (par.equalsIgnoreCase("-r")) {
					x++;
					val = args[x];
					if (val.equalsIgnoreCase("p")) {
						loopClasses = false;
						exploreClasses = true;
						rangeDa = rangeA = 0;
						continue;
					}
					/*
					 * if(val.equalsIgnoreCase("l")){ loopClasses=true;
					 * exploreClasses=true; rangeDa=rangeA=0; continue; } else {
					 */
					try {
						if (val.indexOf("-") == -1) {
							rangeDa = Double.parseDouble(val);
							rangeA = 999999999;
							// TODO 10.08.2007
							if (rangeA==999999999&&rangeDa==0)
						        rangeDa = rangeDa +1e-20;
							continue;
						} else {
							rangeDa = Double.parseDouble(val.substring(0, (val
									.indexOf("-"))));
							rangeA = Double.parseDouble(val.substring(val
									.indexOf("-") + 1, val.length()));
							loopClasses = false;
							exploreClasses = false;
							// TODO 10.08.2007
							if (rangeA==999999999&&rangeDa==0)
						        rangeDa = rangeDa +1e-20;
							continue;
						}
					} catch (NumberFormatException e) {
					}
					// }
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}
				if (par.equalsIgnoreCase("-s")) {
					x++;
					val = args[x];
					try {
						if (val.indexOf("-") == -1) {
							sizeDa = Integer.parseInt(val);
							sizeA = 999999999;
							continue;
						} else {
							sizeDa = Integer.parseInt(val.substring(0, (val
									.indexOf("-"))));
							sizeA = Integer.parseInt(val.substring(val
									.indexOf("-") + 1, val.length()));
							continue;
						}
					} catch (NumberFormatException e) {
					}
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}

				if (par.equalsIgnoreCase("-n")) {
					x++;
					val = args[x];
					try {
						cycles = Integer.parseInt(val);
						if (cycles <= 0)
							cycles = 1;
						shuffle = true;
						// else shuffle=false;
						continue;
					} catch (NumberFormatException e) {
					}
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}

				if (par.equalsIgnoreCase("-seed")) {
					x++;
					val = args[x];
					try {
						seed = Integer.parseInt(val);
						continue;
					} catch (NumberFormatException e) {
					}
					System.out.println("Error on parameter#" + x + "  " + par
							+ " " + val);
					continue;
				}
				if (par.equalsIgnoreCase("-h")) {
					help = true;
					continue;
				}
				if (par.equalsIgnoreCase("-save")) {
					writeRelatedness = true;
					continue;
				}
				System.out.println("Unknown parameter#" + x + "  " + par);
			}
		}
	}


    public void resetDatabase() {
	try {
	    database.update("DROP TABLE pedigree IF EXISTS");
	    database
		    .update("CREATE TABLE pedigree(ix integer,famId BIGINT, id BIGINT,idFather BIGINT, idMother BIGINT,sex INTEGER,affection INTEGER DEFAULT 0,genotyped INTEGER DEFAULT 0,PRIMARY KEY (id))");
	    database.update("DROP TABLE selected IF EXISTS");
	    database.update("CREATE TABLE selected(id BIGINT)");
	    database.update("DROP TABLE genotyped IF EXISTS");
	    database.update("CREATE TABLE genotyped(id BIGINT)");

	    database.update("DROP TABLE groups IF EXISTS");
	    database.update("CREATE TABLE groups(setid BIGINT, id BIGINT)");
	    database.update("DROP TABLE groupstat IF EXISTS");
	    database
		    .update("CREATE TABLE groupstat(setid BIGINT, TYPE VARCHAR, MEAN DOUBLE,STDEV DOUBLE, _MIN DOUBLE, _MAX DOUBLE, no integer, note VARCHAR)");

	    database.update("DROP TABLE egroups IF EXISTS");
	    database
		    .update("CREATE TABLE egroups(kinship double, setid BIGINT, id BIGINT)");
	    database.update("DROP TABLE egroupstat IF EXISTS");
	    database
		    .update("CREATE TABLE egroupstat(kinship double, setid BIGINT, TYPE VARCHAR, MEAN DOUBLE,STDEV DOUBLE, _MIN DOUBLE, _MAX DOUBLE, no integer)");

	    database.update("DROP TABLE residuals IF EXISTS");
	    database.update("CREATE TABLE residuals(setid BIGINT, id BIGINT)");
	    database.update("DROP TABLE residualstat IF EXISTS");
	    database
		    .update("CREATE TABLE residualstat(setid BIGINT, TYPE VARCHAR, MEAN DOUBLE,STDEV DOUBLE, _MIN DOUBLE, _MAX DOUBLE, no integer)");

	    database.update("DROP TABLE buildped IF EXISTS");
	    database
		    .update("CREATE TABLE buildped(famId INTEGER,gen integer, id INTEGER,idFather INTEGER, idMother INTEGER,sex INTEGER,affection INTEGER,clique INTEGER default 0)");
	    database.update("CREATE INDEX b1 ON buildped(id)");

	    database.update("DROP TABLE buildped_IBD IF EXISTS");
	    database
		    .update("CREATE TABLE buildped_IBD(famId INTEGER,id INTEGER)");

	    database.update("DROP TABLE buildconped IF EXISTS");
	    database
		    .update("CREATE TABLE buildconped(famId BIGINT, id BIGINT,idFather BIGINT, idMother BIGINT,sex INTEGER,affection INTEGER)");
	    database.update("CREATE INDEX b2 ON buildconped(id)");

	    database.update("DROP TABLE buildinfo IF EXISTS");
	    database
		    .update("CREATE TABLE buildinfo(famId BIGINT, dim INTEGER, gen INTEGER, bits INTEGER, stepanc INTEGER)");
	    database.update("DROP TABLE buildpedstat IF EXISTS");
	    database
		    .update("CREATE TABLE buildpedstat(setid BIGINT, TYPE VARCHAR, MEAN DOUBLE,STDEV DOUBLE, _MIN DOUBLE, _MAX DOUBLE)");

	    database.update("DROP TABLE temp_buildped IF EXISTS");
	    database.update("DROP TABLE temp_outputbuildped IF EXISTS");
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public Vector<Integer> getCliques() {
	Vector<Integer> result = new Vector<Integer>();
	try {
	    ResultSet rs = database.query("Select distinct setid as CID, no, mean, stdev, _min , _max FROM groupstat where type = 'K'");
	    rs.beforeFirst();
	    for (; rs.next();){ 
		System.out.println(rs.getInt("CID") + ": " + rs.getInt("no"));
		result.add(rs.getInt("CID"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }
    
    public Vector<Node> getClique(int setid) {
	Vector<Node> result = new Vector<Node>();
	try {
	    ResultSet rs = database.query("select distinct id from groups where setid = " + setid );
	    rs.beforeFirst();
	    for (; rs.next();) {
		result.add(graph.getNode(""+rs.getInt("id")));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph, String aff, String geno) {
        this.graph = graph;
	kin = new Pedigree(graph, aff, geno);
	int numInd = kin.getSelected();
	System.out.println("Pedigree size: " + kin.getPedigreeDim() + "\n"
		+ "Sample size  : " + numInd + "\n\n");
	if (numInd != 0) {
	    System.out.println("--- Inbr Stats ---\n");
	    boolean pwKin = true;
	    Moments mom = new Moments(pwKin);
	    mom.calculate(kin.getInbrVector(), false, 0);
	    System.out.println(mom.getStatistics());
	    mom = new Moments(pwKin);
	    System.out.println("\n--- Kin Stats ---\n");
	    mom.calculate(kin.getKinMatrix(), false, 1);
	    System.out.println(mom.getStatistics());

	}        
    }    
}
