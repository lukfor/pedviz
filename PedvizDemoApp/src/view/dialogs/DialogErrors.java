package view.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import pedviz.algorithms.GraphError;

public class DialogErrors extends Dialog {

	private JTree tree;

	DefaultMutableTreeNode errorNode;

	public DialogErrors(Frame parent) {
		super(parent, "Errors");
		setSize(600, 400);
		errorNode = new DefaultMutableTreeNode("Errors (0)");
		tree = new JTree(errorNode);

		JScrollPane pane = new JScrollPane(tree);
		getContainer().setLayout(new BorderLayout());
		getContainer().add(pane, BorderLayout.CENTER);

		getOkButton().setText("AutoRepair");
	}

	@Override
	public boolean execute() {
		return super.execute();
	}

	public void setErrors(Vector<GraphError> errors) {
		errorNode.removeAllChildren();
		errorNode.setUserObject("Errors (" + errors.size() + " items)");

		for (GraphError i : errors) {
			errorNode.add(new DefaultMutableTreeNode(i));
		}
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
		tree.updateUI();
	}

}
