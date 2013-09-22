package view.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import view.util.CheckTreeManager;
import core.Question;
import core.QuestionnaireController;

public class DialogQuestionnaire extends Dialog {

	private JTree tree;

	private DefaultTreeModel model;

	private CheckTreeManager checkTreeManager;

	private Vector<Question> selection;

	public DialogQuestionnaire(Frame parent) {
		super(parent, "Questionnaires");
		setSize(600, 400);
		selection = new Vector<Question>();

	}

	@Override
	protected void createUI(JPanel container) {
		tree = new JTree();
		checkTreeManager = new CheckTreeManager(tree);
		QuestionnaireController.loadFromDatabase(getModel());
		container.setLayout(new BorderLayout());

		JPanel questPanel = new JPanel();
		questPanel.setLayout(new BorderLayout());
		questPanel.setBorder(new TitledBorder("Questions"));

		JScrollPane pane2 = new JScrollPane(tree);
		questPanel.add(pane2, BorderLayout.CENTER);
		container.add(questPanel);

	}

	public void addChildPaths(TreePath path, TreeModel model, List result) {
		Object item = path.getLastPathComponent();
		int childCount = model.getChildCount(item);
		for (int i = 0; i < childCount; i++)
			result.add(path.pathByAddingChild(model.getChild(item, i)));
	}

	public ArrayList getDescendants(TreePath paths[], TreeModel model) {
		ArrayList result = new ArrayList();
		Stack pending = new Stack();
		pending.addAll(Arrays.asList(paths));
		while (!pending.isEmpty()) {
			TreePath path = (TreePath) pending.pop();
			addChildPaths(path, model, pending);
			result.add(path);
		}
		return result;
	}

	public ArrayList getAllCheckedPaths(CheckTreeManager manager, JTree tree) {
		return getDescendants(manager.getSelectionModel().getSelectionPaths(),
				tree.getModel());
	}

	public Vector<Question> getSelection() {
		selection.clear();
		if (!checkTreeManager.getSelectionModel().isSelectionEmpty()) {
			ArrayList<TreePath> checkedPaths = getAllCheckedPaths(
					checkTreeManager, tree);
			for (TreePath path : checkedPaths) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				Object data = node.getUserObject();
				if (data instanceof Question) {
					selection.add((Question) data);
				}
			}
		}
		return selection;
	}

	public DefaultTreeModel getModel() {
		if (model == null) {
			model = new DefaultTreeModel(new DefaultMutableTreeNode());
			tree.setModel(model);
		}
		return model;
	}

}
