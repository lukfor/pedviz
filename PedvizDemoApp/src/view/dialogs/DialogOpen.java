/* Copyright © 2007 by Christian Fuchsberger and Lukas Forer info@pedvizapi.org.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License <http://www.pedvizapi.org/gpl.txt>
 * for more details. 
 */

package view.dialogs;

import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogOpen extends Dialog {

	private String defaultId = "pid".toUpperCase();

	private String defaultSex = "sex".toUpperCase();

	private String defaultMom = "mom".toUpperCase();

	private String defaultDad = "dad".toUpperCase();

	private String defaultSexMale = "1";

	private String defaultSexFemale = "2";

	private String defaultFam = "fam".toUpperCase();

	private DefaultComboBoxModel famModel;

	private DefaultComboBoxModel idModel;

	private DefaultComboBoxModel sexModel;

	private DefaultComboBoxModel momModel;

	private DefaultComboBoxModel dadModel;

	private JComboBox famComboBox;

	private JComboBox idComboBox;

	private JComboBox sexComboBox;

	private JComboBox momComboBox;

	private JComboBox dadComboBox;

	private JTextField maleText;

	private JTextField femaleText;

	private JCheckBox useQuestionarie, checkErrors;

	public DialogOpen(Frame parent) {
		super(parent, "Import-Settings");
		setSize(350, 300);
	}

	@Override
	protected void createUI(JPanel container) {
		container.setLayout(new GridLayout(8, 2, 10, 10));

		container.add(new JLabel("Fam"));
		famComboBox = new JComboBox();
		famModel = new DefaultComboBoxModel();
		famComboBox.setModel(famModel);
		container.add(famComboBox);

		container.add(new JLabel("Id"));
		idComboBox = new JComboBox();
		idModel = new DefaultComboBoxModel();
		idComboBox.setModel(idModel);
		container.add(idComboBox);

		container.add(new JLabel("Dad"));
		dadComboBox = new JComboBox();
		dadModel = new DefaultComboBoxModel();
		dadComboBox.setModel(dadModel);
		container.add(dadComboBox);

		container.add(new JLabel("Mom"));
		momComboBox = new JComboBox();
		momModel = new DefaultComboBoxModel();
		momComboBox.setModel(momModel);
		container.add(momComboBox);

		container.add(new JLabel("Sex"));
		sexComboBox = new JComboBox();
		sexModel = new DefaultComboBoxModel();
		sexComboBox.setModel(sexModel);
		container.add(sexComboBox);

		container.add(new JLabel("Male"));
		maleText = new JTextField();
		container.add(maleText);

		container.add(new JLabel("Female"));
		femaleText = new JTextField();
		container.add(femaleText);

		useQuestionarie = new JCheckBox("Import traits from questionarie");
		container.add(useQuestionarie);

		checkErrors = new JCheckBox("Check for Errors");
		container.add(checkErrors);

	}

	public void setColData(Vector<String> cols) {
		idModel.removeAllElements();
		sexModel.removeAllElements();
		momModel.removeAllElements();
		dadModel.removeAllElements();
		famModel.removeAllElements();

		for (String col : cols) {
			idModel.addElement(col.toUpperCase());
			sexModel.addElement(col.toUpperCase());
			momModel.addElement(col.toUpperCase());
			dadModel.addElement(col.toUpperCase());
			famModel.addElement(col.toUpperCase());
		}

		if (famModel.getIndexOf(defaultFam.toUpperCase()) > -1) {
			famModel.setSelectedItem(defaultFam);
		}

		if (idModel.getIndexOf(defaultId.toUpperCase()) > -1) {
			idModel.setSelectedItem(defaultId);
		}
		if (sexModel.getIndexOf(defaultSex.toUpperCase()) > -1) {
			sexModel.setSelectedItem(defaultSex);
		}
		if (momModel.getIndexOf(defaultMom.toUpperCase()) > -1) {
			momModel.setSelectedItem(defaultMom);
		}
		if (dadModel.getIndexOf(defaultDad.toUpperCase()) > -1) {
			dadModel.setSelectedItem(defaultDad);
		}
		maleText.setText(defaultSexMale);
		femaleText.setText(defaultSexFemale);
	}

	public String getId() {
		return (String) idModel.getSelectedItem();
	}

	public String getSex() {
		return (String) sexModel.getSelectedItem();
	}

	public String getMom() {
		return (String) momModel.getSelectedItem();
	}

	public String getDad() {
		return (String) dadModel.getSelectedItem();
	}

	public String getFam() {
		return (String) famModel.getSelectedItem();
	}

	public String getSexFemale() {
		return femaleText.getText();
	}

	public String getSexMale() {
		return maleText.getText();
	}

	public boolean useQuestionaire() {
		return useQuestionarie.isSelected();
	}

	public void setAllowImportTraits(boolean allow) {
		useQuestionarie.setVisible(allow);
	}

	public boolean useErrorChecking() {
		return checkErrors.isSelected();
	}

}
