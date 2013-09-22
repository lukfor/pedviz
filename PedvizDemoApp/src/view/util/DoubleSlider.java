package view.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DoubleSlider extends JPanel implements ChangeListener {

	private double precision = 100.0;

	private JSlider slider;

	private DoubleField field;

	public DoubleSlider() {
		super();
		slider = new JSlider();
		field = new DoubleField(50.0);
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDoubleValue(field.getNumber());
			}
		});
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(slider);
		add(field);
		slider.addChangeListener(this);

		setDoubleMinimum(0.0);
		setDoubleMaximum(100.0);
		setDoubleValue(50.0);
	}

	public DoubleSlider(double min, double max, double val) {
		super();
		setDoubleMinimum(min);
		setDoubleMaximum(max);
		setDoubleValue(val);
	}

	public void stateChanged(ChangeEvent arg0) {
		field.setNumber(getDoubleValue());
	}

	public double getDoubleMaximum() {
		return (slider.getMaximum() / precision);
	}

	public double getDoubleMinimum() {
		return (slider.getMinimum() / precision);
	}

	public double getDoubleValue() {
		return (slider.getValue() / precision);
	}

	public void setDoubleMaximum(double max) {
		slider.setMaximum((int) (max * precision));
	}

	public void setDoubleMinimum(double min) {
		slider.setMinimum((int) (min * precision));
	}

	public void setDoubleValue(double val) {
		slider.setValue((int) (val * precision));
		setToolTipText(Double.toString(val));
	}

	public void setEnabled(boolean enabled) {
		slider.setEnabled(enabled);
		field.setEnabled(enabled);
	}

}