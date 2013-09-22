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

package pedviz.rpedviz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import pedviz.algorithms.Highlighter;
import pedviz.graph.Graph;
import pedviz.io.CsvGraphExporter;
import pedviz.view.GraphView;
import pedviz.view.LODHighlighter;
import pedviz.view.rules.ColorRule;
import pedviz.view.rules.Rule;

/**
 * This abstract class defines the interface between R and Pedviz.
 * 
 * @author lukas forer
 * 
 */
public abstract class RPedviz extends JFrame implements ActionListener {

    protected Graph graph;

    protected GraphView graphView;

    protected ArrayList<Rule> rules;

    protected int mode = Highlighter.MATERNAL;

    protected HashMap<String, Integer> data;

    protected LODHighlighter lodHighlighter;

    protected JPanel toolBar;

    /**
     * Constructs a new RPedviz object.
     * 
     */
    public RPedviz(String title) {
	super(title);

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    System.out.println(e);
	}

	setSize(800, 600);

	rules = new ArrayList<Rule>();
	createControls();
    }

    private void createControls() {
	data = new HashMap<String, Integer>();
	data.put("maternal", Highlighter.MATERNAL);
	data.put("paternal", Highlighter.PATERNAL);
	data.put("maternal and paternal", Highlighter.MATERNAL_AND_PATERNAL);
	data.put("all ancestors", Highlighter.ANCESTORS);
	data.put("all successors", Highlighter.SUCCESSORS);
	data.put("all ancestors and successors",
		Highlighter.SUCCESSORS_AND_ANCESTORS);

	JMenuBar menu = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem exportGraph = new JMenuItem("Save as csv...");
	exportGraph.setActionCommand("export_graph");
	exportGraph.addActionListener(this);
	JMenuItem exportJpeg = new JMenuItem("Save as jpeg...");
	exportJpeg.setActionCommand("export_jpeg");
	exportJpeg.addActionListener(this);
	JSeparator seperator = new JSeparator();
	JMenuItem exit = new JMenuItem("Exit");
	exit.setActionCommand("exit");
	exit.addActionListener(this);
	fileMenu.add(exportGraph);
	fileMenu.add(exportJpeg);
	fileMenu.add(seperator);
	fileMenu.add(exit);
	menu.add(fileMenu);
	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	setJMenuBar(menu);

	toolBar = new JPanel();
	toolBar.setLayout(new BorderLayout());
	toolBar.setBackground(Color.WHITE);
	JPanel toolBar2 = new JPanel();
	toolBar.add(toolBar2, BorderLayout.LINE_END);
	toolBar2.setLayout(new GridLayout(1, 2));
	toolBar2.setBackground(Color.WHITE);

	JComboBox dataList = new JComboBox(data.keySet().toArray());
	dataList.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		JComboBox item = (JComboBox) e.getSource();
		mode = data.get(item.getSelectedItem());

		lodHighlighter.setMode(mode);
	    }
	});

	JLabel caption = new JLabel("Line of Descents: ");
	caption.setHorizontalAlignment(JLabel.RIGHT);
	toolBar2.add(caption, BorderLayout.LINE_END);
	toolBar2.add(dataList, BorderLayout.LINE_END);
	getContentPane().add(toolBar, BorderLayout.PAGE_END);
    }

    /**
     * Plots the pedigree with hints.
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param seperator
     *                character which separates the values.
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     * @param hints
     *                hint attributes.
     * @param highlighter
     *                shows or hides the highlighter-toolbar.
     */
    abstract public void plot(String format, String seperator, String args[],
	    String[] hints, boolean highlighter);

    /**
     * Plots the pedigree without hints. The default seperator is ";".
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     * @param highlighter
     *                shows or hides the highlighter-toolbar.
     */
    public void plot(String format, String args[], boolean highlighter) {
	plot(format, ";", args, null, highlighter);
    }

    /**
     * Plots the pedigree without hints.
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param seperator
     *                character which separates the values.
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     * @param highlighter
     *                shows or hides the highlighter-toolbar.
     */
    public void plot(String format, String seperator, String args[],
	    boolean highlighter) {
	plot(format, seperator, args, null, highlighter);
    }

    /**
     * Plots the pedigree without hints and with the highlighter-toolbar. The
     * default seperator is ";".
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     */
    public void plot(String format, String args[]) {
	plot(format, ";", args, null, true);
    }

    /**
     * Plots the pedigree without hints and with the highlighter-toolbar.
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param seperator
     *                character which separates the values.
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     */
    public void plot(String format, String seperator, String args[]) {
	plot(format, seperator, args, null, true);
    }

    /**
     * Plots the pedigree with hints and with the highlighter-toolbar. The
     * default seperator is ";".
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     * @param hints
     *                hint attributes.
     */
    public void plot(String format, String args[], String hints[]) {
	plot(format, ";", args, hints, true);
    }

    /**
     * Plots the pedigree with hints and with the highlighter-toolbar.
     * 
     * @param format
     *                defines the column positions
     *                ("PID;MOM;DAD;SEX;TRAIT1;...").
     * @param seperator
     *                character which separates the values.
     * @param args
     *                pedigree data. Every string of the array contains
     *                informations about an idividum.
     * @param hints
     *                hint attributes.
     */
    public void plot(String format, String seperator, String args[],
	    String hints[]) {
	plot(format, seperator, args, hints, true);
    }

    /**
     * Adds a rule.
     * 
     * @param rule
     *                Rule
     */
    public void addRule(Rule rule) {
	rules.add(rule);
    }

    /**
     * Adds a ColorRule.
     * 
     * @param trait
     *                trait.
     * @param value
     *                value.
     * @param r
     *                red-value.
     * @param g
     *                green-value.
     * @param b
     *                blue-value.
     */
    public void addColorRule(String trait, String value, int r, int g, int b) {
	rules.add(new ColorRule(trait, value, new Color(r, g, b)));
    }

    /**
     * Adds a multiple ColorRule.
     * 
     * @param trait
     *                array of traits.
     * @param value
     *                array of values.
     * @param r
     *                array of red-values.
     * @param g
     *                array of green-values.
     * @param b
     *                array of blue-values.
     */
    public void addColorRule(String[] trait, String[] value, int[] r, int[] g,
	    int[] b) {
	ColorRule rule = new ColorRule(trait[0], value[0], new Color(r[0],
		g[0], b[0]));
	for (int i = 1; i < trait.length; i++) {
	    rule.addRule(trait[i], value[i], new Color(r[i], g[i], b[i]));
	}
	rules.add(rule);
    }

    /**
     * Adds a multiple ColorRule.
     * 
     * @param trait
     *                array of traits.
     * @param value
     *                array of values.
     */
    public void addColorRule(String[] trait, String[] value) {
	int r[] = new int[4];
	r[0] = 255;
	r[1] = 0;
	r[2] = 0;
	r[3] = 0;
	int g[] = new int[4];
	g[0] = 0;
	g[1] = 255;
	g[2] = 0;
	g[3] = 255;
	int b[] = new int[4];
	b[0] = 0;
	b[1] = 0;
	b[2] = 255;
	b[3] = 255;
	addColorRule(trait, value, r, g, b);
    }

    public void actionPerformed(ActionEvent arg0) {
	if (arg0.getActionCommand().equals("export_graph")) {
	    JFileChooser dialog = new JFileChooser();
	    dialog.addChoosableFileFilter(new CSVFilter());
	    dialog.setMultiSelectionEnabled(false);
	    dialog.showSaveDialog(this);
	    if (dialog.getSelectedFile() != null) {
		String filename = dialog.getSelectedFile().getPath();
		if (!filename.toLowerCase().endsWith(".csv")) {
		    filename = filename + ".csv";
		}
		CsvGraphExporter exporter = new CsvGraphExporter(filename);
		try {
		    exporter.save(graph);
		    JOptionPane.showMessageDialog(this, "Wrote File: "
			    + filename, "Export CSV",
			    JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
		    JOptionPane.showMessageDialog(this, "Error: "
			    + e.getMessage(), "Export CSV",
			    JOptionPane.INFORMATION_MESSAGE);
		}
	    }
	}

	if (arg0.getActionCommand().equals("export_jpeg")) {
	    JFileChooser dialog = new JFileChooser();
	    dialog.addChoosableFileFilter(new JPEGFilter());
	    dialog.setMultiSelectionEnabled(false);
	    dialog.showSaveDialog(this);
	    if (dialog.getSelectedFile() != null) {
		String filename = dialog.getSelectedFile().getPath();
		if (!filename.toLowerCase().endsWith(".jpg")) {
		    filename = filename + ".jpg";
		}
		graphView.exportJPEG(filename, 2f, false);
		JOptionPane.showMessageDialog(this, "Wrote File: " + filename,
			"Export JPEG", JOptionPane.INFORMATION_MESSAGE);
	    }
	}

	if (arg0.getActionCommand().equals("exit")) {
	    setVisible(false);
	}

    }

    private class CSVFilter extends FileFilter {
	public boolean accept(File f) {
	    if (f.isDirectory())
		return true;
	    return f.getName().toLowerCase().endsWith(".csv");
	}

	public String getDescription() {
	    return "CSV files (*.csv)";
	}
    };

    private class JPEGFilter extends FileFilter {
	public boolean accept(File f) {
	    if (f.isDirectory())
		return true;
	    return f.getName().toLowerCase().endsWith(".jpg");
	}

	public String getDescription() {
	    return "JPEG files (*.jpg)";
	}
    }
}
