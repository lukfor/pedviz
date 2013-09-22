package view.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.CellRendererPane;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

import core.Question;

public class WideCombobox extends JComboBox {
	public final static String SEPARATOR = "SEPARATOR";

	public WideCombobox() {
		setRenderer(new ComboBoxRenderer());
		addActionListener(new BlockComboListener(this));
	}

	public WideCombobox(final Object items[]) {
		super(items);
	}

	public WideCombobox(Vector items) {
		super(items);
		setRenderer(new ComboBoxRenderer());
		addActionListener(new BlockComboListener(this));
	}

	public WideCombobox(ComboBoxModel aModel) {
		super(aModel);
		setRenderer(new ComboBoxRenderer());
		addActionListener(new BlockComboListener(this));
	}

	public JToolTip createToolTip() {
		return new JMultiLineToolTip();
	}

	/*
	 * private boolean layingOut = false;
	 * 
	 * public void doLayout() { try { layingOut = true; super.doLayout(); }
	 * finally { layingOut = false; } }
	 */

	/*
	 * public Dimension getSize() { Dimension dim = super.getSize(); if
	 * (!layingOut) dim.width = Math.max(dim.width, getPreferredSize().width);
	 * return dim; }
	 */

	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		JSeparator separator;

		public ComboBoxRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(JSeparator.HORIZONTAL);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
			if (SEPARATOR.equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
				// if (value instanceof Question){
				// setText(((Question)value).getDescription());
				// }else{
				// setText(value.toString());
				// }
			}
			setFont(list.getFont());
			// if (value instanceof Question){
			// if (getSelectedIndex() == index){
			// setText(((Question)value).getTrait());
			// }else{
			// setText(((Question)value).getDescription());
			// }
			// }else{
			if (value instanceof Question) {
				setText(((Question) value).getTrait());
			} else {
				setText(value.toString());
			}
			// }
			// setText(str);
			if (value instanceof Question) {
				if (isSelected) {
					Question question = ((Question) value);
					if (question.getDescription() == null
							|| question.getDescription().trim().equals("")) {
						if (question.getParentQuestion() == null
								|| question.getParentQuestion().trim().equals(
										"")) {
							setToolTipText("-");
						} else {
							setToolTipText(question.getParentQuestion().trim());
						}
					} else {
						setToolTipText(question.getDescription().trim());
					}
				}
			}
			return this;
		}
	}

	class BlockComboListener implements ActionListener {
		JComboBox combo;
		Object currentItem;

		BlockComboListener(JComboBox combo) {
			this.combo = combo;
			if (combo.getItemCount() > 0) {
				combo.setSelectedIndex(0);
				currentItem = combo.getSelectedItem();
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (combo.getSelectedItem() instanceof String) {
				String tempItem = (String) combo.getSelectedItem();
				if (SEPARATOR.equals(tempItem)) {
					if (currentItem != null) {
						combo.setSelectedItem(currentItem);
					}
				} else {
					currentItem = combo.getSelectedItem();
				}
			} else {
				currentItem = combo.getSelectedItem();
			}
		}
	}

	class JMultiLineToolTip extends JToolTip {
		private static final String uiClassID = "ToolTipUI";

		String tipText;
		JComponent component;

		public JMultiLineToolTip() {
			updateUI();
		}

		public void updateUI() {
			setUI(MultiLineToolTipUI.createUI(this));
		}

		public void setColumns(int columns) {
			this.columns = columns;
			this.fixedwidth = 0;
		}

		public int getColumns() {
			return columns;
		}

		public void setFixedWidth(int width) {
			this.fixedwidth = width;
			this.columns = 0;
		}

		public int getFixedWidth() {
			return fixedwidth;
		}

		protected int columns = 0;
		protected int fixedwidth = 0;
	}

	static class MultiLineToolTipUI extends BasicToolTipUI {
		static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
		Font smallFont;
		static JToolTip tip;
		protected CellRendererPane rendererPane;

		private static JTextArea textArea;

		public static ComponentUI createUI(JComponent c) {
			return sharedInstance;
		}

		public MultiLineToolTipUI() {
			super();
		}

		public void installUI(JComponent c) {
			super.installUI(c);
			tip = (JToolTip) c;
			rendererPane = new CellRendererPane();
			c.add(rendererPane);
		}

		public void uninstallUI(JComponent c) {
			super.uninstallUI(c);

			c.remove(rendererPane);
			rendererPane = null;
		}

		public void paint(Graphics g, JComponent c) {
			Dimension size = c.getSize();
			textArea.setBackground(c.getBackground());
			rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1,
					size.height - 1, true);
		}

		public Dimension getPreferredSize(JComponent c) {
			String tipText = ((JToolTip) c).getTipText();
			if (tipText == null)
				return new Dimension(0, 0);
			textArea = new JTextArea(tipText);
			rendererPane.removeAll();
			rendererPane.add(textArea);
			textArea.setWrapStyleWord(true);
			int width = ((JMultiLineToolTip) c).getFixedWidth();
			int columns = ((JMultiLineToolTip) c).getColumns();

			if (columns > 0) {
				textArea.setColumns(columns);
				textArea.setSize(0, 0);
				textArea.setLineWrap(true);
				textArea.setSize(textArea.getPreferredSize());
			} else if (width > 0) {
				textArea.setLineWrap(true);
				Dimension d = textArea.getPreferredSize();
				d.width = width;
				d.height++;
				textArea.setSize(d);
			} else
				textArea.setLineWrap(false);

			Dimension dim = textArea.getPreferredSize();

			dim.height += 1;
			dim.width += 1;
			return dim;
		}

		public Dimension getMinimumSize(JComponent c) {
			return getPreferredSize(c);
		}

		public Dimension getMaximumSize(JComponent c) {
			return getPreferredSize(c);
		}
	}
}