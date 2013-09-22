package view.tools;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import view.util.WideCombobox;
import core.Question;

public class ConditionView implements PanelListItem, ItemListener {

	String[] modes = new String[] { "equals", "not equals", "greater",
			"lesser", "between", "not between" };

	HashMap<String, Question> questions;

	Vector<String> traits;

	DefaultComboBoxModel traitModel;

	JComboBox value;

	JComboBox value2;

	JComboBox mode;

	WideCombobox trait;

	JPanel container;

	JLabel operator;

	public ConditionView() {
		traitModel = new DefaultComboBoxModel();
	}

	public void createUI(JPanel container) {
		this.container = container;
		GridBagLayout layout = new GridBagLayout();
		container.setLayout(layout);
		// setBorder(new EmptyBorder(2,2,2,2));
		GridBagConstraints constLabel = new GridBagConstraints();
		constLabel.ipadx = 3;
		constLabel.gridx = 0;
		constLabel.gridy = 0;
		constLabel.weightx = 1;
		constLabel.weighty = 0;
		constLabel.gridwidth = 3;
		constLabel.insets = new Insets(2, 2, 2, 2);
		constLabel.fill = GridBagConstraints.HORIZONTAL;

		trait = new WideCombobox();
		// trait.setPreferredSize(arg0)
		trait.setModel(traitModel);
		trait.addItemListener(this);
		container.add(trait, constLabel);
		constLabel.gridwidth = 1;
		constLabel.gridy = 1;
		constLabel.weightx = 0;
		mode = new JComboBox(modes);
		mode.addItemListener(this);
		constLabel.gridx = 0;
		container.add(mode, constLabel);
		constLabel.weightx = 0.5;
		value = new JComboBox();
		constLabel.gridx = 1;
		container.add(value, constLabel);

		value2 = new JComboBox();
		constLabel.gridx = 2;
		container.add(value2, constLabel);

		if (traits != null && traits.size() > 0) {
			value.removeAllItems();
			value2.removeAllItems();
			if (traitModel.getSelectedItem() instanceof Question) {
				Vector<String> values = ((Question) traitModel
						.getSelectedItem()).values;
				if (values != null)
					for (Object o : values) {
						value.addItem(o.toString());
						value2.addItem(o.toString());
					}
			}

			// maxField[i].removeAllItems();
		}

		value2.setVisible(false);

		constLabel.gridy = 2;
		constLabel.gridx = 0;
		constLabel.gridwidth = 3;
		operator = new JLabel("or");
		operator.setHorizontalAlignment(JLabel.CENTER);
		JPanel temp = new JPanel();
		temp.setBackground(Color.WHITE);
		temp.setBorder(new EmptyBorder(3, 3, 3, 3));
		temp.add(new JSeparator());
		container.add(temp, constLabel);

	}

	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		this.traits = traits;
		this.questions = questions;
		traitModel.removeAllElements();
		if (traits != null) {
			boolean extern = false;
			for (String trait : traits) {
				Question question = questions.get(trait);
				if (!extern && question.isExtern()) {
					extern = true;
					traitModel.addElement(WideCombobox.SEPARATOR);
				}
				traitModel.addElement(question);
			}
		}
	}

	public String getTrait() {
		if (traitModel.getSelectedItem() instanceof Question) {
			return ((Question) traitModel.getSelectedItem()).getTrait();
		}
		return null;
	}

	public int getComperator() {
		return mode.getSelectedIndex();
	}

	public String getMinValue() {
		return value.getSelectedItem().toString();
	}

	public String getMaxValue() {
		return value2.getSelectedItem().toString();
	}

	public void itemStateChanged(ItemEvent e) {
		JComboBox selectedChoice = (JComboBox) e.getSource();

		if (selectedChoice.equals(mode)) {
			if (selectedChoice.getSelectedIndex() > 3) {
				value2.setVisible(true);
				container.updateUI();
			} else {
				value2.setVisible(false);
				container.updateUI();
			}
		}

		if (selectedChoice.equals(trait)) {
			if (traitModel.getSelectedItem() != null) {
				value.removeAllItems();
				value2.removeAllItems();
				// maxField[i].removeAllItems();
				if (traitModel.getSelectedItem() instanceof Question) {
					Vector<String> values = ((Question) traitModel
							.getSelectedItem()).values;
					if (values != null)
						for (Object o : values) {
							value.addItem(o.toString());
							value2.addItem(o.toString());
						}
				}

			}
		}

	}

	public void setEnabled(boolean enabled) {
		value.setEnabled(enabled);
		mode.setEnabled(enabled);
		trait.setEnabled(enabled);

	}

}
