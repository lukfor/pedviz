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
 * This abstract class is the super class for all rules. You must override the
 * method applyRule, with it can adapt the NodeView. More informations about
 * writing your own rules, you will find in the tutorial section.
 * 
 * @author Lukas Forer
 * 
 */
public abstract class Rule {

    private boolean enabled = true;

    /**
     * can be used in GraphView2D and GraphView3D.
     */
    public static final int BOTH = 0;

    /**
     * can be used only in GraphView2D.
     */
    public static final int ONLY_2D = 1;

    /**
     * can be used only in GraphView3D.
     */
    public static final int ONLY_3D = 2;

    private int _mode = BOTH;

    /**
     * Override this method, with it you get the NodeView and can adapt it. The
     * class NodeView provides the method getNode() for getting a reference to
     * the Node. *
     * 
     * @param nodeview
     */
    public abstract void applyRule(NodeView nodeview);

    /**
     * Returns true, if the rule is enabled.
     * 
     * @return true, if the rule is enabled.
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Enables or disables the rule.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    /**
     * Sets the mode. (BOTH, ONLY_2D, ONLY_3D)
     * 
     * @param _mode
     *                the mode.
     */
    public void setMode(int _mode) {
	this._mode = _mode;
    }

    /**
     * Returns the mode. (BOTH, ONLY_2D, ONLY_3D)
     * 
     * @return the mode.
     */
    public int getMode() {
	return _mode;
    }

}
