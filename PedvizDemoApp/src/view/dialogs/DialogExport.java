package view.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class DialogExport extends Dialog implements ActionListener {

	public String filename;

	JTextField filenameText;

	JComboBox scale, views;

	JCheckBox gray;

	JButton button;

	Vector<String> viewsString = new Vector<String>();

	public DialogExport(Frame parent) {
		super(parent, "Export as JPEG");
		setSize(400, 300);
	}

	@Override
	protected void createUI(JPanel container) {
		GridBagLayout layout = new GridBagLayout();

		container.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(new JLabel("Filename"), c);
		c.gridx = 1;
		c.weightx = 1.0;
		filenameText = new JTextField();
		filenameText.setEditable(false);
		container.add(filenameText, c);
		c.gridx = 2;
		c.weightx = 0.0;
		button = new JButton("...");
		button.setMargin(new Insets(1, 1, 1, 1));
		button.addActionListener(this);
		container.add(button, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		container.add(new JLabel("View"), c);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		if (viewsString != null) {
			views = new JComboBox(viewsString);
		} else {
			views = new JComboBox();
		}
		container.add(views, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.0;
		container.add(new JLabel("Scale"), c);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		scale = new JComboBox(new String[] { "100%", "200%", "300%", "400%",
				"500%" });
		container.add(scale, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 1.0;
		gray = new JCheckBox("Grayscale");
		container.add(gray, c);
		c.gridy = 4;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weighty = 1.0;
		c.weightx = 1.0;
		container.add(Box.createVerticalGlue(), c);
	}

	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		if (arg0.getSource().equals(button)) {
			JFileChooser dialog = new JFileChooser();
			dialog.addChoosableFileFilter(new JpegFilter());
			dialog.setMultiSelectionEnabled(false);
			dialog.setSelectedFile(new File(filename));
			dialog.showSaveDialog(this);
			if (dialog.getSelectedFile() != null) {
				filename = dialog.getSelectedFile().getPath();
				filenameText.setText(filename);
			}
		}

	}

	public void setFilename(String filename) {
		this.filename = filename;
		filenameText.setText(filename);
	}

	public String getFilename() {
		return filenameText.getText();
	}

	public boolean isGrayScale() {
		return gray.isSelected();
	}

	public float getScale() {
		return scale.getSelectedIndex() + 1;
	}

	public void setViews(Vector<String> views) {
		viewsString = views;
		this.views.setModel(new DefaultComboBoxModel(viewsString));
	}

	public int getIndex() {
		return views.getSelectedIndex();
	}

	class JpegFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith(".jpg");
		}

		public String getDescription() {
			return "JPEGs";
		}
	}
}
