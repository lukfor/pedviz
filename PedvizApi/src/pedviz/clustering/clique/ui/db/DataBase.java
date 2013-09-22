package pedviz.clustering.clique.ui.db;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.swing.event.ChangeListener;


import pedviz.graph.*;


public class DataBase {

	private Connection conn = null;

	/* GUI */

	ChangeListener l;

	int percent = 0;

	String message = "Pedigree loading...";

	File fileName;

	/*
	 * type 0..pedigree 1..selected 2..genotyped
	 */
	int type;

	/* GUI END */

	/*
	 * GUI ProcessDialog
	 */
	public void setFilename(File filename) {
		this.fileName = filename;
	}

	public void setType(int type) {
		this.type = type;
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

	/*
	 * GUI END ProcessDialog
	 */

	// we dont want this garbage collected until we are done
	public DataBase() throws SQLException{ // note more general exception

		// Load the HSQL Database Engine JDBC driver
		// hsqldb.jar should be in the class path or made part of the current
		// jar
		try {
	        Class.forName("org.hsqldb.jdbcDriver" );
	    } catch (Exception e) {
	        System.out.println("DB ERROR: failed to load HSQLDB JDBC driver.");
	        e.printStackTrace();
	        return;
	    }
			// connect to the database. 
			// It can contain directory names relative to the
			// current working directory
			conn = DriverManager.getConnection("jdbc:hsqldb:jenti", // filenames
					"sa", // username
					""); // password
	}

	public Connection getConnection() {
		return conn;
	}

	public void shutdown() throws SQLException {

		Statement st = conn.createStatement();

		// db writes out to files and performs clean shuts down
		// otherwise there will be an unclean shutdown
		// when program ends
		st.execute("SHUTDOWN");
		conn.close(); // if there are no other open connection
	}

	// use for SQL command SELECT
	public synchronized ResultSet query(String expression) throws SQLException {

		ResultSet rs = null;
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		// Run the query, creating a ResultSet
		rs = st.executeQuery(expression); // run the query
		return rs;
	}

	// use for SQL commands CREATE, DROP, INSERT and UPDATE
	public synchronized void update(String expression) throws SQLException {

		Statement st = null;

		st = conn.createStatement(); // statements
		int i = st.executeUpdate(expression); // run the query

		if (i == -1) {
			System.out.println("db error : " + expression);
		}

		st.close();
	} // void update()

	public static void dump(ResultSet rs) throws SQLException {

		// the order of the rows in a cursor
		// are implementation dependent unless you use the SQL ORDER statement
		ResultSetMetaData meta = rs.getMetaData();
		int colmax = meta.getColumnCount();
		int i;
		Object o = null;

		// the result set is a cursor into the data. You can only
		// point to one row at a time
		// assume we are pointing to BEFORE the first row
		// rs.next() points to next row and returns true
		// or false if there is no next row, which breaks the loop
		for (; rs.next();) {
			for (i = 0; i < colmax; ++i) {
				o = rs.getObject(i + 1); // Is SQL the first column is
											// indexed

				// with 1 not 0
				System.out.print(o.toString() + " ");
			}

			System.out.println(" ");
		}
	} // void dump( ResultSet rs )

	public synchronized void insertData()  {

		RandomAccessFile file;
		boolean error = false;
		String s;
		Integer famId, id, fatherId, motherId, sex, affection, genotyped;
		Double DF = new Double(0.0);
		famId = id = fatherId = motherId = sex = affection = genotyped = null;

		try {
			file = new RandomAccessFile(fileName, "r");
            int ix =0;
			while ((s = file.readLine()) != null) {
				ix++;
				StringTokenizer t = new StringTokenizer(s, ", ;\t\n\r\f");
				if (t.hasMoreTokens())
					famId = new Integer(t.nextToken());
				else
					error = true;
				if (t.hasMoreTokens())
					id = new Integer(t.nextToken());
				else
					error = true;
				if (t.hasMoreTokens())
					fatherId = new Integer(t.nextToken());
				else
					error = true;
				if (t.hasMoreTokens())
					motherId = new Integer(t.nextToken());
				else
					error = true;
				if (t.hasMoreTokens())
					sex = new Integer(t.nextToken());
				else
					error = true;
				if (t.hasMoreTokens())
					affection = new Integer(t.nextToken());
				else
					error = true;
				if (t.hasMoreTokens())
					genotyped = new Integer(t.nextToken());
				else
					genotyped = new Integer(0);
				if (t.hasMoreTokens())
					DF = new Double(t.nextToken());
				update("INSERT INTO pedigree(ix,famId,id,idFather,idMother,sex,affection,genotyped) VALUES("
						+ ix
						+ ","
						+ famId
						+ ","
						+ id
						+ ","
						+ fatherId
						+ ","
						+ motherId
						+ "," + sex + "," + affection + "," + genotyped + ")");
			}
			if (file != null)
				file.close();
		} catch (Exception e) {
			//throw new InsertException("Error at individual "+id+"!");
		}
	}

	
	public synchronized void insertData(Graph graph)  {

		RandomAccessFile file;
		boolean error = false;
		String s;
		Integer famId, id, fatherId, motherId, sex, affection, genotyped;
		Double DF = new Double(0.0);
		famId = id = fatherId = motherId = sex = affection = genotyped = null;
		int ix =0;
		try{
		for (Node node: graph.getAllNodes()){
		    if (!node.isDummy()){
				ix++;
				
					famId = 1;//new Integer(node.getFamId().toString());
		
					id = new Integer(node.getId().toString());
					motherId = 0;
					    if (node.getIdMom() != null
						    && graph.getNode(node.getIdMom()) != null) {
						motherId = new Integer(node.getIdMom().toString());
					    }

					    fatherId = 0;
					    if (node.getIdDad() != null
						    && graph.getNode(node.getIdDad()) != null) {
						fatherId = new Integer(node.getIdDad().toString());
					    }
					
					//fatherId = new Integer(node.getIdDad().toString());
				
					//motherId = new Integer(node.getIdMom().toString());
				
					sex = new Integer(node.getUserData("sex").toString());
				
					affection = new Integer(node.getUserData("aff").toString());
				
					genotyped = new Integer(0);
				
					//DF = new Double(t.nextToken());
				update("INSERT INTO pedigree(ix,famId,id,idFather,idMother,sex,affection,genotyped) VALUES("
						+ ix
						+ ","
						+ famId
						+ ","
						+ id
						+ ","
						+ fatherId
						+ ","
						+ motherId
						+ "," + sex + "," + affection + "," + genotyped + ")");
			}
		}
		} catch (Exception e) {
		    e.printStackTrace();
			//throw new InsertException("Error at individual "+id+"!");
		}
	}

	
	/*
	 * to insert selected individuals
	 */
	public synchronized void updateSelData()  {
		RandomAccessFile file;
		boolean error, endOfData;
		String s;
		Integer id = null;

		try {
			file = new RandomAccessFile(fileName, "r");
			// reset all Affection
			update("UPDATE pedigree set affection = 0");
			while ((s = file.readLine()) != null) {
				StringTokenizer t = new StringTokenizer(s, ", \t\n\r\f");

				if (t.hasMoreTokens())
					id = new Integer(t.nextToken());
				else
					error = true;

				update("UPDATE pedigree set affection = 2" + " where id =" + id);
			}
			endOfData = true;
			if (file != null)
				file.close();
		} catch (Exception e) {
			System.out.println("DB data insert error : " + e);
		}
	}

	/*
	 * to insert genotyped individuals
	 */
	public synchronized void updateGenData()  {
		RandomAccessFile file;
		boolean error, endOfData;
		String s;
		Integer id = null;

		try {
			file = new RandomAccessFile(fileName, "r");
			// reset all Affection
			update("UPDATE pedigree set genotyped = 0");
			while ((s = file.readLine()) != null) {
				StringTokenizer t = new StringTokenizer(s, ", \t\n\r\f");

				if (t.hasMoreTokens())
					id = new Integer(t.nextToken());
				else
					error = true;

				update("UPDATE pedigree set genotyped = 2" + " where id =" + id);
			}
			endOfData = true;
			if (file != null)
				file.close();
		} catch (Exception e) {
			System.out.println("DB data insert error : " + e);
		}
	}
} // class Testdb
