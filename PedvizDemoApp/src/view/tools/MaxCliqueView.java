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

package view.tools;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pedviz.clustering.clique.calc.Constants;
import pedviz.clustering.clique.ui.db.DataBase;
import core.Question;
import core.tools.ToolController;

public class MaxCliqueView extends ToolView {

	private JTextField spKinmax;

	private JSpinner spPermutations;

	private DataBase db;

	private JSpinner spRandomSeed;

	private JComboBox cBSubGroup;

	private JTextField spKinmin;

	private JComboBox cbKinship;

	private JSpinner spSGmin;

	private JSpinner spSGmax;

	private ButtonGroup bGrExtractType;

	private JPanel panel_7;

	private JTabbedPane tpParameters;

	private JComboBox affected;

	private JComboBox selected;

	private SpinnerNumberModel spinnerNumberModel_1, spinnerNumberModel,
			spinnerNumberModel_2, spinnerNumberModel_3;

	public MaxCliqueView(ToolController events) {
		super("Max-Clique", events);
	}

	@Override
	protected void createControls(JPanel content) {
		JPanel panel = getPanel_7();
		JPanel panel2 = getPanel_2();
		content.setLayout(new BorderLayout());
		content.add(panel2, BorderLayout.NORTH);
		content.add(panel, BorderLayout.CENTER);
		doLayout();
	}

	protected JPanel getPanel_2() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("Affected: "));
		affected = new JComboBox();
		panel.add(affected);
		panel.add(new JLabel("Selected: "));
		selected = new JComboBox();
		panel.add(selected);
		return panel;
	}

	protected JPanel getPanel_7() {
		if (panel_7 == null) {
			panel_7 = new JPanel();
			panel_7.setLayout(null);
			// panel_7.setBounds(0, 418, 672, 134);
			panel_7.setBounds(0, 0, 672, 134);

			bGrExtractType = new ButtonGroup();

			// tpParameters = new JTabbedPane();
			// tpParameters.setTabPlacement(SwingConstants.BOTTOM);
			// getPanel_7().add(tpParameters);
			// tpParameters.setBounds(0, 0, 672, 134);

			// tpParameters.addTab("Cliques reconstruction", null, panel_7,
			// null);
			// pBasic.setLayout(null);
			// pBasic.setBorder(new TitledBorder(null, "",
			// TitledBorder.DEFAULT_JUSTIFICATION,
			// TitledBorder.DEFAULT_POSITION, null, null));

			final JLabel rangeKinshipLabel = new JLabel();
			rangeKinshipLabel.setBounds(7, 70, 87, 30);
			panel_7.add(rangeKinshipLabel);
			// rangeKinshipLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			rangeKinshipLabel.setText("Kinship range");

			spSGmax = new JSpinner();
			spinnerNumberModel_3 = new SpinnerNumberModel();
			spinnerNumberModel_3.setValue(new Integer(8));
			spinnerNumberModel_3.setStepSize(new Integer(1));
			spinnerNumberModel_3.setMinimum(null);
			spinnerNumberModel_3.setMinimum(null);
			spinnerNumberModel_3.setMinimum(null);
			spinnerNumberModel_3.setMaximum(new Integer(99999));
			spinnerNumberModel_3.setMaximum(null);
			spinnerNumberModel_3.setMinimum(null);
			spinnerNumberModel_3.setMinimum(null);
			spSGmax.setModel(spinnerNumberModel_3);
			spSGmax.setBounds(333, 50, 82, 19);
			panel_7.add(spSGmax);

			spSGmin = new JSpinner();
			spinnerNumberModel_2 = new SpinnerNumberModel();
			spinnerNumberModel_2.setMaximum(new Integer(99999));
			spinnerNumberModel_2.setStepSize(new Integer(1));
			spinnerNumberModel_2.setMaximum(null);
			spinnerNumberModel_2.setValue(new Integer(4));
			spinnerNumberModel_2.setMinimum(new Integer(2));
			spSGmin.setModel(spinnerNumberModel_2);
			spSGmin.setBounds(202, 48, 86, 21);
			panel_7.add(spSGmin);

			final JLabel extractLabel = new JLabel();
			// extractLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			extractLabel.setBounds(8, 14, 83, 30);
			panel_7.add(extractLabel);
			extractLabel.setText("Extract");

			final JLabel subgroupSizeLabel = new JLabel();
			subgroupSizeLabel.setBounds(7, 44, 87, 30);
			panel_7.add(subgroupSizeLabel);
			// subgroupSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			subgroupSizeLabel.setText("Sub-group size");

			final JLabel greaterThanLabel_1_1 = new JLabel();
			greaterThanLabel_1_1.setBounds(305, 44, 120, 30);
			panel_7.add(greaterThanLabel_1_1);
			greaterThanLabel_1_1.setText("to");

			final JLabel textfild10 = new JLabel();
			textfild10.setBounds(305, 72, 19, 30);
			panel_7.add(textfild10);
			textfild10.setText("to");

			spKinmax = new JTextField();
			spKinmax.setHorizontalAlignment(SwingConstants.RIGHT);
			panel_7.add(spKinmax);
			spKinmax.setToolTipText("exp. 0.001");
			spKinmax.setText("1.0");
			spKinmax.setBounds(334, 78, 82, 22);

			cbKinship = new JComboBox();
			cbKinship.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if (cbKinship.getSelectedIndex() == 1) {
						spKinmax.setText("");
						spKinmax.setEditable(false);
					} else {
						spKinmax.setEditable(true);
						spKinmax.setText("1.0");
					}
				}
			});
			cbKinship.setBounds(93, 77, 97, 22);
			panel_7.add(cbKinship);
			cbKinship.setModel(new DefaultComboBoxModel(new String[] {
					"from...to...", "greater than" }));

			spKinmin = new JTextField();
			spKinmin.setHorizontalAlignment(SwingConstants.RIGHT);
			spKinmin.setText("0.0625");
			spKinmin.setToolTipText("exp. 0,0001");
			spKinmin.setBounds(202, 78, 86, 22);
			panel_7.add(spKinmin);

			cBSubGroup = new JComboBox();
			cBSubGroup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (cBSubGroup.getSelectedIndex() == 1) {
						spSGmax.setValue(new Integer(8));
						spSGmax.setEnabled(false);
					} else {
						spSGmax.setEnabled(true);
						spSGmax.setValue(new Integer(8));
					}
				}
			});
			cBSubGroup.setBounds(93, 48, 97, 20);
			panel_7.add(cBSubGroup);
			cBSubGroup.setModel(new DefaultComboBoxModel(new String[] {
					"from...to...", "greater than" }));

			final JRadioButton RBlargestSet = new JRadioButton();
			RBlargestSet.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
				}
			});
			RBlargestSet.setSelected(false);
			bGrExtractType.add(RBlargestSet);

			RBlargestSet.setBounds(249, 20, 174, 23);
			panel_7.add(RBlargestSet);
			RBlargestSet.setText("Largest sub group");

			final JRadioButton RBallUniqueSubgroups = new JRadioButton();
			RBallUniqueSubgroups.setSelected(true);
			bGrExtractType.add(RBallUniqueSubgroups);
			RBallUniqueSubgroups.setBounds(94, 19, 156, 23);
			panel_7.add(RBallUniqueSubgroups);
			RBallUniqueSubgroups.setText("All unique sub-groups");

			final JPanel pAdvanced = new JPanel();
			pAdvanced.setBorder(new TitledBorder(new EtchedBorder(), "",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null));
			pAdvanced.setBounds(430, 28, 228, 74);
			pAdvanced.setLayout(null);
			panel_7.add(pAdvanced);

			final JLabel seedLabel = new JLabel();
			seedLabel.setBounds(18, 12, 77, 14);
			pAdvanced.add(seedLabel);
			seedLabel
					.setToolTipText("Sets the seed of the random number generator");
			seedLabel.setText("Permutations");

			spRandomSeed = new JSpinner();
			spinnerNumberModel = new SpinnerNumberModel();
			spinnerNumberModel.setValue(new Integer(1234));
			spinnerNumberModel.setStepSize(new Integer(1));
			spinnerNumberModel.setMinimum(new Integer(0));
			spRandomSeed.setModel(spinnerNumberModel);
			spRandomSeed.setBounds(111, 39, 99, 20);
			pAdvanced.add(spRandomSeed);
			spRandomSeed
					.setToolTipText("Sets the seed of the random number generator");

			final JLabel seedLabel_1 = new JLabel();
			seedLabel_1
					.setToolTipText("Number of maximum trial for the shuffling algorithm to search the best");
			seedLabel_1.setText("Random seed");
			seedLabel_1.setBounds(17, 43, 77, 14);
			pAdvanced.add(seedLabel_1);

			spPermutations = new JSpinner();
			spinnerNumberModel_1 = new SpinnerNumberModel();
			spinnerNumberModel_1.setValue(new Integer(100));
			spinnerNumberModel_1.setStepSize(new Integer(10));
			spinnerNumberModel_1.setMinimum(new Integer(0));
			spPermutations.setModel(spinnerNumberModel_1);
			spPermutations
					.setToolTipText("Number of maximum trial for the shuffling algorithm to search the best");
			spPermutations.setBounds(109, 11, 98, 20);
			pAdvanced.add(spPermutations);

		}
		return panel_7;
	}

	@Override
	public void setData(Vector<String> traits,
			HashMap<String, Question> questions) {
		super.setData(traits, questions);

		if (traits != null) {
			affected.removeAllItems();
			selected.removeAllItems();
			for (String trait : traits) {
				affected.addItem(trait);
				selected.addItem(trait);
			}
		}

	}

	public String[] buildParameters() {
		String[] s = new String[20];
		int i = 0;

		// Seed
		if (spRandomSeed.getValue().toString() != Constants.SEED) {
			s[i] = "-seed";
			i++;
			s[i] = spRandomSeed.getValue().toString();
			i++;
		}
		// Permutations
		// -n xxx
		if ((spPermutations.getValue()) != Constants.PERMUTATIONS) {
			s[i] = "-n";
			i++;
			s[i] = spPermutations.getValue().toString();
			i++;
		}

		// ("\tPairwise relationship measure");
		// ("\t -p k = kinship coefficients");
		// ("\t -p n = extract from the genealogy the pedigree(s)connecting
		// all");
		// ("\t individuals - n.b. all the other options will be ignored");
		// ("");
		// ("<Mode>");
		// ("\t -m r = report mode. List individuals of each cluster and
		// report");
		// ("\t statistics");
		// ("\t -m e = explore mode. Total number of
		// groups(#Groups),individuals");

		// ("\t (#Ind) and pairwise relationships (#PWR) are shown.");
		// ("\t -m pxx reconstruct pedigrees of each cluster of individuals");
		// ("\t xx: maximum number of generations from a common ancestor");
		// ("\t between each pair of individuals belonging to the cluster");
		// ("\t -m p include all common ancestors in each generated pedigree");
		// ("\t -m pa automatically determine the minimum number of generations
		// ");
		// ("\t a reconstruct the pedigrees");
		// ("");
		// ("\t NOTE: The number of generations '-m pxx' should be consistent");
		// ("\t with the relatedness measure used to cluster");
		// ("\t individuals.");
		// ("\t E.g. if a kinship value >=0.001 is selected to cluster.");
		// ("\t individuals, reconstructing pedigrees using only 2");
		// ("\t generations could return an error.");
		// ("");

		// ---
		/*
		 * if (outlook.getSelectedIndex() == 1) { if
		 * (cbMaximumNumber.isSelected()) { s[i] = "-m"; i++; s[i] = "p" +
		 * spinner.getValue().toString(); i++; } if
		 * (cbincludeAllCommon.isSelected()) { s[i] = "-m"; i++; s[i] = "pa";
		 * i++; } if (cbAll.isSelected()) { s[i] = "-m"; i++; s[i] = "p"; i++; } }
		 */

		// ("<Extract>");
		// ("\tModalita' di estrazione");
		// ("\t -e b search for the largest set of individuals");
		// ("\t -e u search for all sets of individuals");
		for (Enumeration e = bGrExtractType.getElements(); e.hasMoreElements();) {
			JRadioButton rb = (JRadioButton) e.nextElement();
			if (rb.isSelected())
				if (rb.getText() == "Largest sub group") {
					s[i] = "-e";
					i++;
					s[i] = "b";
					i++;
				} // allUniqueSubgroupsRadioButton
				else {
					s[i] = "-e";
					i++;
					s[i] = "u";
					i++;
				}
		}
		// ("");
		// ("<Best if>");
		// ("\t -b p maximize the total number of pairwise relationships");
		// ("\t -b i maximize the total number of extracted individuals");
		// ("");
		// ("\t n.b. they give the same results if using '-e b'");
		// ("");
		// ("<Range>");
		// ("<Size>");
		// ("\tRange for the size of the clusters");
		// ("\t-s xxx-yyy min size - max size");
		// ("\t (when only one value is given it means size >=xxx)");
		// ("\tn.b. using '-e b' only the first value is used, and means
		// >=xxx");
		if (cBSubGroup.getSelectedIndex() == 0) {
			s[i] = "-s";
			i++;
			s[i] = spSGmin.getValue().toString() + "-"
					+ spSGmax.getValue().toString();
			i++;
		} else {
			s[i] = "-s";
			i++;
			s[i] = spSGmin.getValue().toString();
			i++;
		}
		// ("\tRange for the measure of relatedness used to cluster
		// individuals");
		// ("\t-r xxx-yyy cluster individuals with pairwise relationships
		// between");
		// ("\t xxx and yyy (when only one value is given it means >=xxx)");
		// ("\t-r p explore a predefined range of kinship ranges in order to");
		// ("\t detect the possible substructure of the dataset");
		// ("");
		if (cbKinship.getSelectedIndex() == 0) {
			s[i] = "-r";
			i++;
			s[i] = spKinmin.getText() + "-" + spKinmax.getText().toString();
			i++;
		} else {
			s[i] = "-r";
			i++;
			s[i] = spKinmin.getText();
			i++;
		}
		// resize Array
		return resizeArray(s, i);

	}

	protected String[] resizeArray(String[] s, int i) {
		String temp[] = new String[i];
		for (int ii = 0; ii < i; ii++) {
			temp[ii] = s[ii];
		}
		return temp;
	}

	public String getSelectedTrait() {
		return selected.getSelectedItem().toString();
	}

	public String getAffectedTrait() {
		return affected.getSelectedItem().toString();
	}
}
