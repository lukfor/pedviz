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

package pedviz.view.rules;

import java.awt.Color;

import pedviz.view.NodeView;
import pedviz.view.symbols3d.SymbolFamily3d;

/**
 * With this Rule it's possible to draw nodes in an abstract way dependent on a
 * given trait.
 * 
 * @author lukas forer
 * 
 */

public class AbstractionRule3d extends Rule {

    public AbstractionRule3d() {
	super();
    }

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.isExpand()) {
	    nodeview.getSymbols().clear();
	    nodeview.getSymbols().add(new SymbolFamily3d(0, 0, 0));
	    nodeview.setSize(nodeview.getSize() * 0.5f);
	    nodeview.setColor(Color.DARK_GRAY);
	}
    }

}
