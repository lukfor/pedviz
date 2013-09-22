package core;

import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import pedviz.graph.Graph;
import pedviz.graph.Node;
import view.AppView;
import view.tools.ToolView;
import core.tools.ToolController;

public class QuestionnaireController implements ToolController {

	DefaultMutableTreeNode qroot = null;

	public void init(ToolView sender) {

	}

	public void afterGraphLoaded(ToolView sender, Graph graph) {
		// ingored
	}

	public void resetPressed(ToolView sender) {
		if (qroot != null) {
			AppView view = Application.getInstance().getView();
			DefaultTreeModel model = view.getFamiliesModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model
					.getRoot();

			root.remove(qroot);
			qroot.removeAllChildren();
			qroot = null;
			model.reload();
		}
	}

	public void updatePressed(ToolView sender) {
		/*
		 * QuestionnaireView panel = (QuestionnaireView) sender; AppController
		 * controller = Application.getInstance().getController();
		 * 
		 * Vector<Question> selection = panel.getSelection(); int generations =
		 * panel.getGenaerations();
		 * 
		 * Filter myFilter = new Filter(); myFilter.setOperator(Filter.AND); for
		 * (Question question : selection) { System.out.println(question);
		 * myFilter.addCondition(new QuestionCondition(question)); }
		 * 
		 * for (Graph graph : controller.getAllGraphs()) { Vector<Node> nodes =
		 * new Vector<Node>(); nodes.addAll(myFilter.execute(graph));
		 * 
		 * resetPressed(sender);
		 * 
		 * if (nodes.size() == 0) {
		 * 
		 * JOptionPane.showMessageDialog(null, "No persons found.", "Error",
		 * JOptionPane.INFORMATION_MESSAGE); } else { AppView view =
		 * Application.getInstance().getView(); DefaultTreeModel model =
		 * view.getFamiliesModel(); if (qroot == null) { DefaultMutableTreeNode
		 * root = (DefaultMutableTreeNode) model .getRoot();
		 * 
		 * qroot = new DefaultMutableTreeNode(); qroot.setUserObject("Results");
		 * root.add(qroot); model.reload();
		 *  } else { qroot.removeAllChildren(); model.reload(); } Vector<Graph>
		 * graphs = TestAlgo.createGraphs(graph, nodes, generations);
		 * qroot.setUserObject("Results (" + graphs.size() + ")"); for (Graph
		 * subgraph : graphs) { DefaultMutableTreeNode child = new
		 * DefaultMutableTreeNode(); child.setUserObject(subgraph);
		 * qroot.add(child); }
		 * 
		 * model.reload(); } }
		 */

	}

	public static Vector<String> getQuestioniaries() {
		PostgreDB db = new PostgreDB("pedviz.properties");

		Vector<String> questionaires = new Vector<String>();
		String q = "Select id, description from question where id like 'group%' order by id";

		if (db.connect()) {

			try {
				ResultSet result = db.executeQuerie(q);
				result.beforeFirst();
				while (result.next()) {
					String set = result.getString("id").replaceAll("group", "");
					String description = result.getString("description");
					questionaires.add(description);
				}
				result.close();
			} catch (Exception e) {
				Application.getInstance().handleException(e);
			}
			db.close();
		}
		return questionaires;
	}

	private static void loadQuestions(PostgreDB db, String set,
			DefaultMutableTreeNode root) {

		DefaultMutableTreeNode temp = root;
		DefaultMutableTreeNode test = root;
		try {
			ResultSet result = db
					.executeQuerie("Select id, description from question where set="
							+ set + " order by id");

			result.beforeFirst();
			while (result.next()) {
				String caption = result.getString("description");
				String id = result.getString("id");
				// Fragebogen
				if (id.endsWith("00.01")) {
					test = new DefaultMutableTreeNode(caption);
					temp = test;
					root.add(test);
					// Gruppe von Fragen
				} else if (id.endsWith(".00")) {
					temp = new DefaultMutableTreeNode(caption);
					test.add(temp);
				} else {
					DefaultMutableTreeNode folder = new DefaultMutableTreeNode(
							caption);
					temp.add(folder);
					ResultSet result1 = db
							.executeQuerie("Select * from questionaireentry where question_id='"
									+ id + "' order by question_sid");

					result1.beforeFirst();
					while (result1.next()) {
						String de = result1.getString("description");
						String qid = result1.getString("id");
						String label = result1.getString("label");
						String layout = result1.getString("layout");
						int type = result1.getInt("q_type");
						boolean isTop = result1.getBoolean("is_top");

						Question question = new Question(qid, de, type);
						question.setParentQuestion(caption);
						question.setTrait(label);
						if (type == Question.RADIOBUTTON) {
							if (isTop) {
								question.addValue("Ja");
								question.addValue("Nein");
								question.addValue("weiﬂ nicht");
								question.addValue("V.a.");
							} else {

								ResultSet result3 = db
										.executeQuerie("Select * from radiobutton where qaire_entry_id="
												+ qid + " order by id");

								result3.beforeFirst();
								while (result3.next()) {
									String temp2 = result3
											.getString("description");
									question.addValue(temp2);
								}
							}
						} else if (type == Question.COMBOBOX) {

							// todo

						} else if (type == Question.CHECKBOX) {
							question.addValue("ja");
							question.addValue("nein");
						}

						DefaultMutableTreeNode child = new DefaultMutableTreeNode(
								question);
						folder.add(child);
					}
					result1.close();
				}
			}
			result.close();
		} catch (Exception e) {
			Application.getInstance().handleException(e);
		}
	}

	public static void loadFromDatabase(DefaultTreeModel model) {
		PostgreDB db = new PostgreDB("pedviz.properties");
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.setUserObject("Questionnaires");

		String q = "Select id, description from question where id like 'group%'";

		if (db.connect()) {

			try {

				ResultSet result = db.executeQuerie(q);
				result.beforeFirst();
				while (result.next()) {

					String set = result.getString("id").replaceAll("group", "");
					String description = result.getString("description");

					DefaultMutableTreeNode setNode = new DefaultMutableTreeNode(
							description);
					root.add(setNode);

					loadQuestions(db, set, setNode);

				}
				result.close();
				model.reload();
			} catch (Exception e) {
				Application.getInstance().handleException(e);
			}
			db.close();
		}

	}

	public static Vector<String> importTraits(Graph graph) {
		String q = "SELECT label, answer, radiobutton_id "
				+ "FROM survey as s  "
				+ "join unsecret as u on s.owner_aid = u.aid "
				+ "join answer as a on s.id = a.survey_id "
				+ "join questionaireentry as q on a.qaire_entry_id = q.id "
				+ "where pid = ";

		String q1 = "SELECT description " + "FROM radiobutton " + "where id = ";

		PostgreDB db = new PostgreDB("pedviz.properties");
		Vector<String> traits = new Vector<String>();
		if (db.connect()) {

			try {

				for (Node node : graph.getNodes()) {
					String pid = node.getId().toString();
					ResultSet result = db.executeQuerie(q + "'" + pid + "'");
					result.beforeFirst();
					while (result.next()) {
						String trait = result.getString("label");
						if (!trait.equals("sex")) {
							// radiobutton
							if (result.getObject("radiobutton_id") != null) {
								int id = result.getInt("radiobutton_id");
								ResultSet result1 = db.executeQuerie(q1 + id);
								result1.beforeFirst();
								while (result1.next()) {
									Object value = result1
											.getObject("description");
									node.setUserData(trait, value);
								}
								result1.close();
							} else {
								Object value = result.getObject("answer");
								node.setUserData(trait, value);
							}
							if (!traits.contains(trait)) {
								traits.add(trait);
							}
						}
					}
					result.close();
				}
			} catch (Exception e) {
				Application.getInstance().handleException(e);
			}
			db.close();
		}

		return traits;
	}

	public static void importTraits(Graph graph, Vector<Question> questions) {

		String q = "SELECT answer, radiobutton_id " + "FROM survey as s  "
				+ "join unsecret as u on s.owner_aid = u.aid "
				+ "join answer as a on s.id = a.survey_id " + "where pid = ";

		String q1 = "SELECT description " + "FROM radiobutton " + "where id = ";

		PostgreDB db = new PostgreDB("pedviz.properties");
		if (db.connect()) {
			for (Question question : questions) {

				try {

					for (Node node : graph.getAllNodes()) {
						String pid = node.getId().toString();
						// System.out.println(q + "'" + pid + "' and q.id = '" +
						// question.getId()+"'");
						ResultSet result = db.executeQuerie(q + "'" + pid
								+ "' and a.qaire_entry_id = "
								+ question.getId() + "");
						result.beforeFirst();
						while (result.next()) {
							String trait = question.getTrait();
							if (!trait.equals("sex")) {
								// radiobutton
								if (result.getObject("radiobutton_id") != null) {
									int id = result.getInt("radiobutton_id");
									ResultSet result1 = db.executeQuerie(q1
											+ id);
									result1.beforeFirst();
									while (result1.next()) {
										Object value = result1
												.getObject("description");
										node.setUserData(trait, value);
									}
									result1.close();
								} else {
									Object value = result.getObject("answer");
									node.setUserData(trait, value);
								}

							}
						}
						result.close();
					}
				} catch (Exception e) {
					Application.getInstance().handleException(e);
				}
			}
			db.close();
		}
	}

	public void afterGraphSelected(ToolView sender, Graph graph) {

	}

	public void updateTraits(ToolView sender) {

	}

}
