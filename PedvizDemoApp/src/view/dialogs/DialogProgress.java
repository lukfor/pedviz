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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pedviz.algorithms.Algorithm;

public class DialogProgress extends JDialog {

	private Vector<Runnable> tasks = new Vector<Runnable>();

	private JLabel messageLabel = null;

	private JProgressBar progressBar = null;

	private String completionMessage = null;

	public DialogProgress(Frame parent, String title, String message) {
		super(parent, title, true);
		init(parent, message);
	}

	public DialogProgress(Frame parent, String message) {
		super(parent, "Progress Information", true);
		init(parent, message);
	}

	public DialogProgress(Dialog parent, String title, String message) {
		super(parent, title, true);
		init(parent, message);
	}

	private void init(Component parent, String message) {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(300, 100);
		JPanel container = new JPanel();
		container.setBorder(new EmptyBorder(new Insets(12, 12, 11, 11)));
		container.setLayout(new BorderLayout(5, 5));
		getContentPane().add(container);

		if (message == null)
			message = "Please wait...";
		messageLabel = new JLabel(message);
		progressBar = new JProgressBar();
		container.add(messageLabel, BorderLayout.NORTH);
		container.add(progressBar, BorderLayout.CENTER);
		pack();
		setSize(300, 100);
		setLocationRelativeTo(parent);
	}

	public void addTask(Runnable r) {
		tasks.add(r);
	}

	public void setCompletionMessage(String msg) {
		completionMessage = msg;
	}

	public void run() {
		progressBar.setMaximum(tasks.size() * 100);
		WorkThread w = new WorkThread();
		w.start();
		show();
	}

	public void finished() {
		if (completionMessage == null)
			DialogProgress.this.dispose();

		else {
			messageLabel.setText(completionMessage);

			getContentPane().remove(progressBar);
			JButton closeButton = new JButton("Close");
			getContentPane().add(closeButton, BorderLayout.SOUTH);
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DialogProgress.this.dispose();
				}
			});

			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			invalidate();
			pack();
		}
	}

	private class WorkThread extends Thread implements ChangeListener {
		private Runnable task;

		private int i;

		public void run() {
			for (i = 0; i < tasks.size(); progressBar.setValue(++i * 100))
				try {
					task = (Runnable) tasks.get(i);
					if (task instanceof Algorithm) {
						String msg = ((Algorithm) task).getMessage();
						progressBar.setString(msg);
						progressBar.setStringPainted(msg != null);

						((Algorithm) task).addChangeListener(this);
					}
					task.run();
				} catch (Throwable t) {
				}
			finished();
		}

		public void stateChanged(ChangeEvent e) {
			try {
				int percent = ((Algorithm) task).getPercentComplete() % 100;
				progressBar.setValue(i * 100 + percent);

				String msg = ((Algorithm) task).getMessage();
				progressBar.setStringPainted(msg != null);
				progressBar.setString(msg);
			} catch (Exception ex) {
			}
		}
	}
}
