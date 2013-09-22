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

package pedviz.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.Set;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Screen3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import pedviz.graph.LayoutedGraph;
import pedviz.view.rules.Rule;
import pedviz.view.symbols3d.ShapeCreator3D;
import pedviz.view.symbols3d.Symbol3D;
import pedviz.view.symbols3d.SymbolSexUndesignated3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * This class visualizes a LayoutedGraph object in a three-dimensional
 * environment.
 * 
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class GraphView3D extends GraphView {

    private Switch edgeSwitch, nodeSwitch;

    private SimpleUniverse su;

    private Locale locale;

    private OrbitBehavior orbit;

    private BoundingBox bounds;

    private PickCanvas pickCanvas;

    private Canvas3D canvas;

    private boolean firstDrag = false;

    private NodeView mouseOverNodeView = null;

    private LineArray lineArray;

    private GraphView g;

    private MouseRotate mouseRotate;

    private boolean useTransparency = false, useAntialiasing = false,
	    useMaterial = true, useLight = true;

    private BranchGroup bgRoot;

    private TransformGroup tgRoot;

    private boolean visbleSphere = false;

    private boolean xRotating = false;

    private boolean yRotating = true;

    /**
     * Creates a new GraphView object.
     * 
     */
    public GraphView3D() {
	this(SimpleUniverse.getPreferredConfiguration());
    }

    /**
     * Creates a new GraphView object and displays the given LayoutedGaph
     * object.
     * 
     * @param graph
     */
    public GraphView3D(LayoutedGraph graph) {
	this(SimpleUniverse.getPreferredConfiguration(), graph);
    }

    /**
     * Constructs a GraphView with the given configuration and displays the
     * given LayoutedGaph object.
     * 
     * @param config
     *                Configuration
     * @param graph
     *                LayoutedGraph object
     */
    public GraphView3D(GraphicsConfiguration config, LayoutedGraph graph) {
	this(config);
	setGraph(graph);
    }

    /**
     * Constructs a GraphView with the given configuration.
     * 
     * @param config
     *                Configuration
     */
    public GraphView3D(GraphicsConfiguration config) {
	super();
	g = this;
	canvas = new Canvas3D(config);
	su = new SimpleUniverse(canvas);
	su.getViewingPlatform().setNominalViewingTransform();
	su.getViewer().setDvrFactor(0.1f);
	su.getViewer().getView().setBackClipDistance(200);
	su.getViewer().getView().setFrontClipDistance(0.0001);
	locale = new Locale(su);
	bounds = new BoundingBox(new Point3d(-20000, -200000, -200000),
		new Point3d(200000, 200000, 200000));
	// Orbit
	orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL);
	orbit.setRotateEnable(false);
	orbit.setTranslateEnable(false);
	orbit.setRotFactors(0.3, 0);
	orbit.setTransFactors(0.2, 0.2);
	orbit.setZoomFactor(0.1);
	orbit.setSchedulingBounds(bounds);
	su.getViewingPlatform().setViewPlatformBehavior(orbit);
	// Pick Canvas
	pickCanvas = new PickCanvas(canvas, locale);
	pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
	pickCanvas.setTolerance(0);
	GraphViewHandler handler = new GraphViewHandler();
	canvas.addMouseListener(handler);
	canvas.addMouseMotionListener(handler);
    }

    @Override
    public void setGraph(LayoutedGraph graph) {
	super.setGraph(graph);
	buildGraph();
    }

    @Override
    public Component getComponent() {
	return canvas;
    }

    @Override
    public void centerGraph() {
	Transform3D transform2 = new Transform3D();
	transform2.rotX(0);
	transform2.rotY(0);
	transform2.rotZ(0);

	Transform3D transform = new Transform3D();
	float scalex = 1f / graph.getBounds().width;
	float scaley = 0.5f / graph.getBounds().height;
	transform.setScale(Math.min(scalex, scaley));
	transform2.mul(transform);

	tgRoot.setTransform(transform2);
	su.getViewingPlatform().setNominalViewingTransform();
    }

    @Override
    public void setScale(double scale) {
	super.setScale(scale);
	if (su != null) {
	    TransformGroup vpTrans = su.getViewingPlatform()
		    .getViewPlatformTransform();
	    Transform3D trans = new Transform3D();
	    Transform3D temp = new Transform3D();
	    vpTrans.getTransform(trans);
	    temp.setScale(getScale());
	    trans.mul(temp);
	    vpTrans.setTransform(trans);
	}
    }

    @Override
    public void setTranslateX(double translateX) {
	if (su != null) {
	    TransformGroup vpTrans = su.getViewingPlatform()
		    .getViewPlatformTransform();
	    Transform3D trans = new Transform3D();
	    Transform3D temp = new Transform3D();
	    vpTrans.getTransform(trans);
	    temp.setTranslation(new Vector3d(-(translateX - getTranslateX()),
		    0, 0));
	    trans.mul(temp);
	    vpTrans.setTransform(trans);
	}
	super.setTranslateX(translateX);
    }

    @Override
    public void setTranslateY(double translateY) {
	if (su != null) {
	    TransformGroup vpTrans = su.getViewingPlatform()
		    .getViewPlatformTransform();
	    Transform3D trans = new Transform3D();
	    Transform3D temp = new Transform3D();
	    vpTrans.getTransform(trans);
	    temp
		    .setTranslation(new Vector3d(0, translateY
			    - getTranslateY(), 0));
	    trans.mul(temp);
	    vpTrans.setTransform(trans);
	}
	super.setTranslateY(translateY);
    }

    @Override
    public void unHighlightAll() {
	for (NodeView n : highlightedNodes.values()) {
	    n.setHighlighted(false);
	    resetNodeColor(n);
	}
	for (NodeView n : highlightedNodes.values()) {
	    for (EdgeView e : n.getInEdges()) {
		changeEdgeColor(e, e.getColor());
	    }
	    for (EdgeView e : n.getOutEdges()) {
		changeEdgeColor(e, e.getColor());
	    }
	}
	super.unHighlightAll();
    }

    @Override
    public void highlight(pedviz.graph.Node node) {

	NodeView nodeview = graph.getNodeView(node.getId());
	nodeview.setHighlighted(true);
	changeNodeColor(nodeview, nodeview.getHighlightedColor());
	for (EdgeView e : nodeview.getInEdges()) {
	    if (e.getStart().isHighlighted()) {
		changeEdgeColor(e, e.getHighlightedColor());
	    }
	}
	for (EdgeView e : nodeview.getOutEdges()) {
	    boolean k = false;
	    for (NodeView endNode : e.getEnds())
		k = k || endNode.isHighlighted() || endNode.isMouseOver();

	    if (k) {
		changeEdgeColor(e, e.getHighlightedColor());
	    }
	}
	super.highlight(node);
    }

    @Override
    public void highlight(Collection<pedviz.graph.Node> nodes) {
	for (pedviz.graph.Node node : nodes) {
	    highlight(node);
	}
	super.highlight(nodes);
    }

    @Override
    public void deselect() {
	for (Object id : selectedNodes.keySet()) {
	    NodeView nodeview = graph.getNodeView(id);
	    nodeview.setSelected(false);
	    resetNodeColor(nodeview);
	}
	super.deselect();
    }

    @Override
    public void deselect(pedviz.graph.Node node) {
	super.deselect(node);
	NodeView nodeview = graph.getNodeView(node.getId());
	resetNodeColor(nodeview);
    }

    @Override
    public void show(pedviz.graph.Node node) {
	NodeView nodeview = graph.getNodeView(node.getId());
	changeNodeAlpha(nodeview, 1);
	for (EdgeView e : nodeview.getInEdges()) {
	    if (!e.getStart().isHidden()) {
		changeEdgeAlpha(e, 1);
	    }
	}
	for (EdgeView e : nodeview.getOutEdges()) {
	    boolean k = false;
	    for (NodeView endNode : e.getEnds())
		k = k || !endNode.isHidden();

	    if (k) {
		changeEdgeAlpha(e, 1);
	    }
	}
	super.show(node);
    }

    @Override
    public void hide(pedviz.graph.Node node) {
	NodeView nodeview = graph.getNodeView(node.getId());
	changeNodeAlpha(nodeview, getAlpha());
	for (EdgeView e : nodeview.getInEdges()) {
	    changeEdgeAlpha(e, getAlpha());
	}
	for (EdgeView e : nodeview.getOutEdges()) {
	    changeEdgeAlpha(e, getAlpha());
	}
	super.hide(node);
    }

    @Override
    public void showAll() {
	for (NodeView nodeview : hiddenNodes.values()) {
	    changeNodeAlpha(nodeview, 1);
	    for (EdgeView e : nodeview.getInEdges()) {
		changeEdgeAlpha(e, e.getAlpha());
	    }
	    for (EdgeView e : nodeview.getOutEdges()) {
		changeEdgeAlpha(e, e.getAlpha());
	    }
	}
	super.showAll();
    }

    @Override
    public void hideAll() {
	for (Object o : graph.getAllNodes().values()) {
	    NodeView nodeview = (NodeView) o;
	    hide(nodeview.getNode());
	}
	super.hideAll();
    }

    @Override
    public void select(pedviz.graph.Node node) {
	super.select(node);
	if (!isMultiselection()) {
	    deselect();
	}
	NodeView nodeview = graph.getNodeView(node.getId());
	changeNodeColor(nodeview, nodeview.getSelectedColor());

    }

    @Override
    public void exportJPEG(String filename, float scale, boolean grayscale) {

	Canvas3DExporter offScreenCanvas = new Canvas3DExporter(SimpleUniverse
		.getPreferredConfiguration());
	Screen3D sOn = canvas.getScreen3D();
	Screen3D sOff = offScreenCanvas.getScreen3D();
	sOff.setSize(sOn.getSize());
	sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
	sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());
	Dimension dim = canvas.getSize();
	dim.width *= scale;
	dim.height *= scale;
	View view = su.getViewer().getView();
	view.addCanvas3D(offScreenCanvas);
	view.stopView();
	view.renderOnce();
	view.startView();
	offScreenCanvas.export(filename, dim, true, grayscale);
	view.removeCanvas3D(offScreenCanvas);
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
	super.setBackgroundColor(backgroundColor);
    }

    @Override
    public void setEdgeVisible(boolean flag) {
	super.setEdgeVisible(flag);
	if (isEdgeVisible()) {
	    edgeSwitch.setWhichChild(Switch.CHILD_ALL);
	} else {
	    edgeSwitch.setWhichChild(Switch.CHILD_NONE);
	}
    }

    @Override
    public void updateNodes(Set<pedviz.graph.Node> nodes) {
	for (pedviz.graph.Node node : nodes) {
	    if (!node.isDummy()) {
		NodeView nodeview = graph.getNodeView(node.getId());
		resetNodeColor(nodeview);
	    }
	}
    }

    @Override
    public void setZoomEnabled(boolean flag) {
	orbit.setZoomEnable(flag);
	super.setZoomEnabled(flag);
    }

    @Override
    public void setMovingEnabled(boolean flag) {
	orbit.setTranslateEnable(flag);
	super.setMovingEnabled(flag);
    }

    /**
     * Enables or disables rotating on the y-axis..
     * 
     * @param flag
     */
    public void setYRotatingEnabled(boolean yRotating) {
	if (mouseRotate != null) {
	    if (yRotating)
		mouseRotate.setFactor(0.005, xRotating ? 0.005 : 0);
	    else
		mouseRotate.setFactor(0, xRotating ? 0.005 : 0);
	}
	this.yRotating = yRotating;
    }

    /**
     * Enables or disables rotating on the x-axis.
     * 
     * @param flag
     */
    public void setXRotatingEnabled(boolean xRotating) {
	if (mouseRotate != null) {
	    if (xRotating) {
		mouseRotate.setFactor(yRotating ? 0.005 : 0, 0.005);
	    } else {
		mouseRotate.setFactor(yRotating ? 0.005 : 0, 0);
	    }
	}
	this.xRotating = xRotating;
    }

    /**
     * Returns true if the visualization uses transparency.
     * 
     * @return true if the visualization uses transparency.
     */
    public boolean isTransparencyEnabled() {
	return useTransparency;
    }

    /**
     * Enables or disables transparency.
     * 
     * @param useTransparency
     */
    public void setTransparencyEnabled(boolean useTransparency) {
	this.useTransparency = useTransparency;
    }

    /**
     * Returns true if the visualization uses antialiasing.
     * 
     * @return true if the visualization uses antialiasing.
     */
    public boolean isAntialiasingEnabled() {
	return useAntialiasing;
    }

    /**
     * Enables or disables antiliasing.
     * 
     * @param useAntialiasing
     */
    public void setAntialiasingEnabled(boolean useAntialiasing) {
	this.useAntialiasing = useAntialiasing;
    }

    /**
     * Returns true if the nodes use matrials.
     * 
     * @return true if transparency will be used.
     */
    public boolean isMaterialEnabled() {
	return useMaterial;
    }

    /**
     * Enables or disables materials.
     * 
     * @param useMaterial
     */
    public void setMaterialEnabled(boolean useMaterial) {
	this.useMaterial = useMaterial;
    }

    /**
     * Returns true if the visualization uses lights.
     * 
     * @return true if the visualization uses lights.
     */
    public boolean isLightEnabled() {
	return useLight;
    }

    /**
     * Enables or disables transparency lights.
     * 
     * @param useLight
     */
    public void setLightEnabled(boolean useLight) {
	this.useLight = useLight;
    }

    private Appearance buildAppearance(boolean wireFrame) {
	Appearance appear = new Appearance();
	ColoringAttributes coloringAttributes = new ColoringAttributes(
		new Color3f(1, 1, 1), ColoringAttributes.FASTEST);
	coloringAttributes.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
	coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
	appear.setColoringAttributes(coloringAttributes);

	Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.0f);
	Color3f emissiveColor = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f diffuseColor = new Color3f(1.0f, 1.0f, 0.0f);
	Color3f specularColor = new Color3f(1.0f, 1.0f, 1.0f);
	float shininess = 64.0f;

	appear.setMaterial(new Material(ambientColor, emissiveColor,
		diffuseColor, specularColor, shininess));
	PolygonAttributes pa = new PolygonAttributes();
	if (!wireFrame)
	    pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
	else
	    pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
	if (!useMaterial) {
	    pa.setCullFace(PolygonAttributes.CULL_NONE);
	}
	appear.setPolygonAttributes(pa);

	if (useAntialiasing) {
	    LineAttributes lineAttributes = new LineAttributes();
	    lineAttributes.setLineWidth(1.0f);
	    lineAttributes.setLineAntialiasingEnable(true);
	    appear.setLineAttributes(lineAttributes);
	}
	if (useTransparency) {
	    TransparencyAttributes tattr = new TransparencyAttributes();
	    tattr.setTransparencyMode(TransparencyAttributes.FASTEST);
	    appear.setTransparencyAttributes(tattr);
	}
	return appear;
    }

    private Switch buildNodes() {

	Switch sNodes = new Switch(Switch.CHILD_ALL);
	Appearance appNode = buildAppearance(false);
	for (Object o : graph.getAllNodes().values()) {
	    NodeView nodeview = (NodeView) o;

	    if (!nodeview.getNode().isDummy()) {
		if (nodeview.getNode().getParent() == null
			|| (nodeview.getNode().getParent() != null && graph
				.getNodeView(
					nodeview.getNode().getParent().getId())
				.isExpand())) {
		    boolean firstSymbol = true;
		    if (nodeview.getSymbols().size() > 0) {
			for (Symbol symbol1 : nodeview.getSymbols()) {
			    // ingnores S2d symbols
			    if (symbol1 instanceof Symbol3D) {

				Symbol3D symbol = (Symbol3D) symbol1;
				symbol.setColors(nodeview.getColors());

				Point3f pos = new Point3f(nodeview.getPosX(),
					-nodeview.getPosY(), nodeview.getPosZ());
				GeometryArray[] array = symbol
					.createShape(pos, nodeview.getSize(),
						nodeview.getColor());
				array[0]
					.setUserData(nodeview.getNode().getId());

				Shape3D shapeNode = new Shape3D();
				shapeNode.setAppearance(appNode);
				shapeNode.addGeometry(array[0]);
				tgRoot.addChild(shapeNode);

				if (firstSymbol) {
				    nodeview.setData(array[0]);
				    firstSymbol = false;
				}

				for (int i = 1; i < array.length; i++) {
				    if (array[i] != null) {
					shapeNode = new Shape3D();
					shapeNode.setAppearance(appNode);
					shapeNode.addGeometry(array[i]);
					tgRoot.addChild(shapeNode);
					array[i].setUserData(nodeview.getNode()
						.getId());
				    }
				}
			    }
			}
		    } else {
			Symbol3D symbol = new SymbolSexUndesignated3d();
			symbol.setColors(nodeview.getColors());

			Point3f pos = new Point3f(nodeview.getPosX(), -nodeview
				.getPosY(), nodeview.getPosZ());

			GeometryArray[] array = symbol.createShape(pos,
				nodeview.getSize(), nodeview.getColor());
			array[0].setUserData(nodeview.getNode().getId());

			Shape3D shapeNode = new Shape3D();
			shapeNode.setAppearance(appNode);
			shapeNode.addGeometry(array[0]);
			tgRoot.addChild(shapeNode);

			nodeview.setData(array[0]);
			firstSymbol = false;
		    }
		}
	    } else if (!nodeview.isExpand()
		    && nodeview.getNode().getNodeCount() > 0) {
		boolean firstSymbol = true;

		if (nodeview.getSymbols().size() > 0) {
		    for (Symbol symbol1 : nodeview.getSymbols()) {
			// ingnores S2d symbols
			if (symbol1 instanceof Symbol3D) {

			    Symbol3D symbol = (Symbol3D) symbol1;
			    symbol.setColors(nodeview.getColors());

			    Point3f pos = new Point3f(nodeview.getPosX(),
				    -nodeview.getPosY(), nodeview.getPosZ());
			    GeometryArray[] array = symbol.createShape(pos,
				    nodeview.getSize(), nodeview.getColor());
			    array[0].setUserData(nodeview.getNode().getId());

			    Shape3D shapeNode = new Shape3D();
			    shapeNode.setAppearance(appNode);
			    shapeNode.addGeometry(array[0]);
			    tgRoot.addChild(shapeNode);

			    if (firstSymbol) {
				nodeview.setData(array[0]);
				firstSymbol = false;
			    }

			    for (int i = 1; i < array.length; i++) {
				if (array[i] != null) {
				    shapeNode = new Shape3D();
				    shapeNode.setAppearance(appNode);
				    shapeNode.addGeometry(array[i]);
				    tgRoot.addChild(shapeNode);
				    array[i].setUserData(nodeview.getNode()
					    .getId());
				}
			    }
			}
		    }
		} else {
		    Symbol3D symbol = new SymbolSexUndesignated3d();
		    symbol.setColors(nodeview.getColors());

		    Point3f pos = new Point3f(nodeview.getPosX(), -nodeview
			    .getPosY(), nodeview.getPosZ());
		    GeometryArray[] array = symbol.createShape(pos, nodeview
			    .getSize(), nodeview.getColor());
		    array[0].setUserData(nodeview.getNode().getId());

		    Shape3D shapeNode = new Shape3D();
		    shapeNode.setAppearance(appNode);
		    shapeNode.addGeometry(array[0]);
		    tgRoot.addChild(shapeNode);

		    nodeview.setData(array[0]);
		    firstSymbol = false;
		}
	    }
	}
	return sNodes;
    }

    private Switch buildEdges() {

	if (graph.getEdges().size() * 2 > 0) {
	    int format = 0;
	    if (useTransparency) {
		format = LineArray.COORDINATES | LineArray.COLOR_4;
	    } else {
		format = LineArray.COORDINATES | LineArray.COLOR_3;
	    }

	    lineArray = new LineArray(graph.getEdges().size() * 2, format);
	    lineArray.setCapability(LineArray.ALLOW_COLOR_WRITE);

	    int count = 0;
	    int countColor = 0;
	    for (Object o : graph.getEdges()) {
		EdgeView edgeview = (EdgeView) o;
		if (!edgeview.getEdge().isDummy()) {
		    edgeview.setData(countColor);
		    if (useTransparency) {
			Color4f c = new Color4f(edgeview.getColor());
			c.w = edgeview.getAlpha();
			lineArray.setColor(countColor++, c);
			lineArray.setColor(countColor++, c);
		    } else {
			Color3f c = new Color3f(edgeview.getColor());
			lineArray.setColor(countColor++, c);
			lineArray.setColor(countColor++, c);
		    }
		    float startY = -edgeview.getStart().getPosY();
		    float endY = -edgeview.getEnd().getPosY();

		    if (!(edgeview.getStart().getNode().isDummy() && edgeview
			    .getStart().getNode().getNodeCount() == 0))
			startY -= edgeview.getStart().getSize() / 2.0f;

		    if (!(edgeview.getEnd().getNode().isDummy() && edgeview
			    .getEnd().getNode().getNodeCount() == 0))
			endY += edgeview.getStart().getSize() / 2.0f;

		    NodeView start = graph.getNodeView(edgeview.getEdge()
			    .getStart().getId());
		    NodeView end = graph.getNodeView(edgeview.getEdge()
			    .getEnd().getId());

		    if (start.isExpand()) {
			lineArray.setCoordinate(count++, new float[] {
				edgeview.getStart().getPosX(), startY,
				edgeview.getStart().getPosZ() });
		    } else {
			lineArray.setCoordinate(count++, new float[] {
				start.getPosX(), startY, start.getPosZ() });
		    }
		    if (end.isExpand()) {
			lineArray.setCoordinate(count++, new float[] {
				edgeview.getEnd().getPosX(), endY,
				edgeview.getEnd().getPosZ() });
		    } else {
			lineArray.setCoordinate(count++, new float[] {
				end.getPosX(), endY, end.getPosZ() });
		    }
		}
	    }
	}

	LineAttributes lineAttributes = new LineAttributes();
	lineAttributes.setLineWidth(graph.getDefaultEdgeView().getWidth());
	lineAttributes.setLineAntialiasingEnable(useAntialiasing);

	Appearance appLines = new Appearance();
	appLines.setLineAttributes(lineAttributes);

	if (useTransparency) {
	    TransparencyAttributes tattr = new TransparencyAttributes();
	    tattr.setTransparencyMode(TransparencyAttributes.FASTEST);
	    appLines.setTransparencyAttributes(tattr);
	}
	Shape3D shapeLines = new Shape3D();
	shapeLines.setAppearance(appLines);
	shapeLines.addGeometry(lineArray);

	Switch edgeSwitch = new Switch(Switch.CHILD_ALL);
	edgeSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
	edgeSwitch.addChild(shapeLines);
	return edgeSwitch;
    }

    private void buildGraph() {
	if (graph != null) {
	    // su.getViewingPlatform().setNominalViewingTransform();
	    canvas.stopRenderer();
	    if (useTransparency) {
		su.getViewer().getView().setTransparencySortingPolicy(
			View.TRANSPARENCY_SORT_GEOMETRY);
	    }

	    selectedNodes.clear();

	    BranchGroup oldbgRoot = bgRoot;

	    bgRoot = new BranchGroup();
	    bgRoot.setPickable(true);
	    bgRoot.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
	    bgRoot.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
	    bgRoot.setCapability(BranchGroup.ALLOW_DETACH);

	    Background background = new Background(new Color3f(
		    getBackgroundColor()));
	    background.setApplicationBounds(bounds);
	    background.setColor(new Color3f(getBackgroundColor()));
	    bgRoot.addChild(background);

	    tgRoot = new TransformGroup();
	    tgRoot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    tgRoot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	    Transform3D transform = new Transform3D();
	    float scalex = 1f / graph.getBounds().width;
	    float scaley = 0.5f / graph.getBounds().height;
	    transform.setScale(Math.min(scalex, scaley));
	    tgRoot.setTransform(transform);

	    mouseRotate = new MouseRotate(tgRoot);
	    mouseRotate.setSchedulingBounds(bounds);
	    mouseRotate.setFactor(yRotating ? 0.005 : 0, xRotating ? 0.005 : 0);

	    tgRoot.addChild(mouseRotate);

	    if (useLight) {
		AmbientLight ambientLight = new AmbientLight(new Color3f(0.2f,
			0.2f, 0.2f));
		ambientLight.setInfluencingBounds(bounds);
		Vector3f lightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
		DirectionalLight light = new DirectionalLight(new Color3f(1, 1,
			1), lightDir);
		light.setInfluencingBounds(bounds);
		bgRoot.addChild(ambientLight);
		bgRoot.addChild(light);
	    }
	    bgRoot.addChild(tgRoot);
	    if (visbleSphere) {
		GeometryArray array = ShapeCreator3D.createSphere(0, -50, 0,
			100, true, 0, 20);
		Color4f color = new Color4f(0.6f, 0.6f, 0.92f, 0.8f);
		array.setColors(0, ShapeCreator3D.getColorArray(color, array
			.getVertexCount()));
		Shape3D shapeNode = new Shape3D();
		shapeNode.setAppearance(buildAppearance(false));
		shapeNode.addGeometry(array);
		tgRoot.addChild(shapeNode);
	    }
	    nodeSwitch = buildNodes();
	    tgRoot.addChild(nodeSwitch);
	    edgeSwitch = buildEdges();
	    tgRoot.addChild(edgeSwitch);

	    if (oldbgRoot == null) {
		locale.addBranchGraph(bgRoot);
	    } else {
		locale.replaceBranchGraph(oldbgRoot, bgRoot);
	    }
	    su.getViewingPlatform().setNominalViewingTransform();
	    canvas.startRenderer();
	}
    }

    private void changeEdgeColor(EdgeView e, Color color) {
	if (e.getData() != null) {
	    int index = (Integer) e.getData();
	    if (useTransparency) {
		Color4f color2 = new Color4f(color);
		if (e.isHidden() && !e.isHighlighted()) {
		    color2.w = getAlpha();
		} else {
		    color2.w = e.getAlpha();
		}

		lineArray.setColor(index, color2);
		lineArray.setColor(index + 1, color2);
	    } else {
		Color3f color2 = new Color3f(color);
		lineArray.setColor(index, color2);
		lineArray.setColor(index + 1, color2);
	    }
	}
    }

    private void changeNodeColor(NodeView n, Color color) {
	GeometryArray box = (GeometryArray) n.getData();
	if (box != null) {
	    Color4f g = new Color4f(color);
	    if (n.isHidden() && !n.isHighlighted()) {
		g.w = getAlpha();
	    }

	    box.setColors(0, ShapeCreator3D.getColorArray(g, box
		    .getVertexCount()));
	}
    }

    private void resetNodeColor(NodeView n) {
	if (n.isSelected()) {
	    changeNodeColor(n, n.getSelectedColor());
	} else if (n.isHighlighted()) {
	    changeNodeColor(n, n.getHighlightedColor());
	} else {
	    changeNodeColor(n, n.getColor());
	}
    }

    private Object getSelectedNode(MouseEvent e) {
	pickCanvas.setShapeLocation(e);
	Point3d eyePos = pickCanvas.getStartPosition();
	PickResult result = pickCanvas.pickClosest();
	if (result != null) {
	    PickIntersection pi = result.getClosestIntersection(eyePos);
	    if (pi != null) {
		GeometryArray array = pi.getGeometryArray();
		if (array != null) {
		    Object id = (Object) array.getUserData();
		    return id;
		}
	    }
	}
	return null;
    }

    private void changeEdgeAlpha(EdgeView e, float alpha) {
	int index = (Integer) e.getData();
	if (useTransparency) {
	    Color4f color2 = new Color4f();
	    lineArray.getColor(index, color2);
	    if (e.isHidden()) {
		color2.w = alpha;
	    }
	    lineArray.setColor(index, color2);
	    lineArray.setColor(index + 1, color2);
	} else {
	    if (alpha == 1) {
		Color3f color2 = new Color3f(e.getHighlightedColor());
		lineArray.setColor(index, color2);
		lineArray.setColor(index + 1, color2);
	    } else {
		Color3f color2 = new Color3f(e.getColor());
		lineArray.setColor(index, color2);
		lineArray.setColor(index + 1, color2);
	    }
	}
    }

    private void changeNodeAlpha(NodeView n, float alpha) {
	GeometryArray box = (GeometryArray) n.getData();
	if (box != null) {
	    Color4f color = new Color4f();
	    if (useTransparency) {
		box.getColor(0, color);
		if (n.isHidden()) {
		    color.w = alpha;
		}
		box.setColors(0, ShapeCreator3D.getColorArray(color, box
			.getVertexCount()));
	    } else {
		if (alpha == 1) {
		    box.setColors(0, ShapeCreator3D.getColorArray(new Color4f(n
			    .getHighlightedColor()), box.getVertexCount()));
		} else {
		    box.setColors(0, ShapeCreator3D.getColorArray(new Color4f(n
			    .getColor()), box.getVertexCount()));

		}
	    }
	}
    }

    class GraphViewHandler implements MouseListener, MouseMotionListener {
	public void mousePressed(MouseEvent e) {
	    firstDrag = true;
	}

	public void mouseReleased(MouseEvent e) {
	    canvas.setCursor(null);
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	    // select node and notify GraphViewListener
	    if (isSelectionEnabled()) {
		Object id = getSelectedNode(e);
		if (id != null) {
		    if (!selectedNodes.containsKey(id)) {
			select(graph.getGraph().getNode(id));

			fireNodeEvent(new NodeEvent(g, graph.getGraph()
				.getNode(id), NodeEvent.SELECTED));

		    } else {
			deselect(graph.getGraph().getNode(id));
			fireNodeEvent(new NodeEvent(g, graph.getGraph()
				.getNode(id), NodeEvent.DESELECTED));

		    }
		} else {
		    deselect();

		    fireNodeEvent(new NodeEvent(g, null,
			    NodeEvent.ALL_DESELECTED));

		}
	    }
	}

	public void mouseMoved(MouseEvent e) {
	    // if mouse is over a node, show handcursor
	    if (isSelectionEnabled()) {
		Object id = getSelectedNode(e);
		if (id != null) {

		    setCursor(Cursor.HAND_CURSOR);
		    NodeView nodeview = graph.getNodeView(id);
		    if (mouseOverNodeView != nodeview) {
			if (mouseOverNodeView != null) {
			    fireNodeEvent(new NodeEvent(g, mouseOverNodeView
				    .getNode(), NodeEvent.MOUSE_LEAVE));

			    mouseOverNodeView.setMouseOver(false);
			}
			mouseOverNodeView = nodeview;
			fireNodeEvent(new NodeEvent(g, mouseOverNodeView
				.getNode(), NodeEvent.MOUSE_ENTER));

			nodeview.setMouseOver(true);
			updateGraphView();
		    }
		} else {
		    if (mouseOverNodeView != null) {
			fireNodeEvent(new NodeEvent(g, mouseOverNodeView
				.getNode(), NodeEvent.MOUSE_LEAVE));

			mouseOverNodeView.setMouseOver(false);
			canvas.setCursor(null);
			mouseOverNodeView = null;
			updateGraphView();
		    }
		}
	    }
	}

	public void mouseDragged(MouseEvent e) {
	    // shows a resize cursor during rotating
	    if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
		if (firstDrag) {
		    firstDrag = false;
		    setCursor(Cursor.E_RESIZE_CURSOR);
		    return;
		}
	    }
	    // selection-brush
	    if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
		if (isSelectionEnabled()) {
		    Object id = getSelectedNode(e);
		    if (id != null) {
			if (!selectedNodes.containsKey(id)) {
			    select(graph.getGraph().getNode(id));

			    fireNodeEvent(new NodeEvent(g, graph.getGraph()
				    .getNode(id), NodeEvent.SELECTED));

			}
		    }
		} else {
		    canvas.setCursor(null);
		}
	    }
	}
    }

    /**
     * Returns true, if the sphere is visible.
     * 
     * @return true, if the sphere is visible.
     */
    public boolean isVisbleSphere() {
	return visbleSphere;
    }

    /**
     * Sets the visibilty of the sphere.
     * 
     * @param visbleSphere
     *                the visibilty of the sphere.
     */
    public void setVisbleSphere(boolean visbleSphere) {
	this.visbleSphere = visbleSphere;
    }

    @Override
    public void updateRules() {
	if (getGraph() != null) {
	    for (Object o : getGraph().getAllNodes().values()) {
		NodeView node = (NodeView) o;
		node.reset(graph.getDefaultNodeView());
		// if (node.isExpand())
		for (Rule rule : getRules())
		    if (rule.isEnabled()
			    && (rule.getMode() == Rule.ONLY_3D || rule
				    .getMode() == Rule.BOTH))
			rule.applyRule(node);
	    }
	    updateGraphView();
	}
	buildGraph();
    }

}
