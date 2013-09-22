package view.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.Application;

public class PanelList<e extends PanelListItem> extends JScrollPane {

	private Vector<e> items;

	private JPanel itemPanel;

	private int maxItems = -1;

	private Vector<JPanel> panels;

	private boolean useDeleteButton = true;

	public PanelList() {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		items = new Vector<e>();
		panels = new Vector<JPanel>();
		JPanel container = new JPanel(new GridBagLayout());
		container.setBackground(Color.WHITE);
		setViewportView(container);
		// add(container);
		// this.setLayout(new GridBagLayout());
		// this.setBackground(Color.WHITE);
		// createUI(this);
		createUI(container);
	}

	private void createUI(JPanel container) {
		itemPanel = new JPanel(new GridBagLayout());
		itemPanel.setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 1.0;
		container.add(itemPanel, c);
		c.gridy = 1;
		c.weighty = 1.0;
		container.add(Box.createVerticalGlue(), c);
	}

	public boolean addItem(e item) {
		if (items.size() < maxItems || maxItems == -1) {
			GridBagConstraints constraint = new GridBagConstraints();
			constraint.fill = GridBagConstraints.HORIZONTAL;
			constraint.gridx = 0;
			constraint.gridy = items.size();
			constraint.weighty = 0.0;
			constraint.weightx = 1.0;
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBackground(Color.WHITE);
			JPanel controls = new JPanel();
			controls.setBackground(Color.WHITE);
			item.createUI(controls);
			panel.add(controls);

			JLabel deleteButton = new JLabel(Application.getInstance()
					.getImage("delete"));
			deleteButton.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			deleteButton.setVerticalAlignment(JLabel.CENTER);
			deleteButton.setHorizontalAlignment(JLabel.CENTER);
			deleteButton.setBackground(controls.getBackground());
			deleteButton.addMouseListener(new MouseListener() {

				public void mouseReleased(MouseEvent arg0) {
				}

				public void mousePressed(MouseEvent arg0) {
				}

				public void mouseExited(MouseEvent arg0) {
				}

				public void mouseEntered(MouseEvent arg0) {
				}

				public void mouseClicked(MouseEvent arg0) {
					JLabel label = (JLabel) arg0.getSource();
					JPanel panel = (JPanel) label.getParent();
					e item = getItemForPanel(panel);
					removePanel(item, panel);
				}

			});
			if (useDeleteButton) {
				panel.add(deleteButton, BorderLayout.EAST);
			}
			itemPanel.add(panel, constraint);
			itemPanel.updateUI();
			items.add(item);
			panels.add(panel);
			return true;
		} else {
			return false;
		}
	}

	private JPanel getPanelForItem(e item) {
		int i = items.indexOf(item);
		return panels.get(i);
	}

	private e getItemForPanel(JPanel panel) {
		int i = panels.indexOf(panel);
		return items.get(i);
	}

	private void removePanel(e item, JPanel panel) {
		itemPanel.remove(panel);
		itemPanel.updateUI();
		items.remove(item);
		panels.remove(panel);
	}

	public void removeItems(e item) {
		JPanel panel = getPanelForItem(item);
		removePanel(item, panel);
	}

	public Vector<e> getItmes() {
		return items;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension s = super.getMinimumSize();
		return new Dimension(s.width, 150);
	}

	public int getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}

	public boolean isUseDeleteButton() {
		return useDeleteButton;
	}

	public void setUseDeleteButton(boolean useDeleteButton) {
		this.useDeleteButton = useDeleteButton;
	}

}
