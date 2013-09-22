package view.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DialogInfo extends Dialog {

	private String message = "";

	private JTextArea textarea;

	public DialogInfo(Frame parent) {
		super(parent, "Information");
		setSize(500, 450);
		getCancelButton().setVisible(false);
	}

	@Override
	protected void createUI(JPanel container) {
		container.setLayout(new BorderLayout());
		textarea = new JTextArea(message);
		textarea.setEditable(false);
		textarea.setFont(container.getFont());
		JScrollPane pane = new JScrollPane(textarea);
		container.add(pane, BorderLayout.CENTER);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		textarea.setText(message);
		this.message = message;
	}
}
