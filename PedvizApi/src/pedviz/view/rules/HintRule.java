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

import pedviz.view.NodeView;

/**
 * This abstract class is the super class for rules, which adapt the hint text.
 * If you want to use one or more traits as hint text, then is the method
 * addHintAttribute() from the GraphView class the right for you.
 * 
 * @author Lukas Forer
 * 
 */
public abstract class HintRule extends Rule {

    @Override
    public void applyRule(NodeView nodeview) {
	if (!nodeview.getNode().isDummy()) {
	    nodeview.setHintText(getHint(nodeview));
	}
    }

    /**
     * You can define the hint text, if you override this method.
     * 
     * @param nodeview
     * @return the hint text
     */
    public abstract String getHint(NodeView nodeview);
}
