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

package view.util;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;

public class DoubleField extends JFormattedTextField {

	NumberFormat t;

	public DoubleField(double value) {
		super(NumberFormat.getNumberInstance());
		setColumns(18);
		t = NumberFormat.getNumberInstance();
		t.setMinimumFractionDigits(2);
		t.setMaximumIntegerDigits(5);
		this.setText(t.format(value));
	}

	public double getNumber() {
		try {
			Number n = NumberFormat.getNumberInstance().parse(getText());
			return n.doubleValue();
		} catch (ParseException nfe) {
			return 0;
		}
	}

	public void setNumber(double number) {
		this.setText(t.format(number));
	}
};
