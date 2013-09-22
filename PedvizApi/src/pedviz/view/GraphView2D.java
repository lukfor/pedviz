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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import pedviz.graph.LayoutedGraph;
import pedviz.graph.Node;
import pedviz.view.rules.Rule;
import pedviz.view.symbols.Symbol2D;
import pedviz.view.symbols.SymbolSexUndesignated;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * This class visualizes a LayoutedGraph in a tow-dimensional environment.
 * 
 * @author Lukas Forer
 * @version 0.1
 */
public class GraphView2D extends GraphView {

    private AffineTransform transform;

    private Vector<NodeView> visibleNodes = new Vector<NodeView>();

    private NodeView selNodeview = null;

    private Line2D line = new Line2D.Double();

    private JComponent component;

    private float minSize = 1f;

    private int lastOffsetX;

    private int lastOffsetY;

    private GraphView g;

    /**
     * Creates a new GraphView2D object.
     * 
     */
    public GraphView2D() {
	super();
	g = this;
	component = new JPanel() {
	    @Override
	    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackgroundColor());
		g.fillRect(0, 0, component.getWidth(), component.getHeight());
		drawGraph((Graphics2D) g);
	    }
	};
	component.setOpaque(true);
	component.setDoubleBuffered(true);
	GraphViewHandler handler = new GraphViewHandler();
	component.addComponentListener(handler);
	component.addMouseListener(handler);
	component.addMouseMotionListener(handler);
	component.addMouseWheelListener(new ScaleHandler());
    }

    /**
     * Creates a new GraphView2D object for the given LayoutedGraph object.
     * 
     * @param graph
     */
    public GraphView2D(LayoutedGraph graph) {
	this();
	setGraph(graph);
    }

    @Override
    public void centerGraph() {
	if (getGraph() != null) {
	    super.centerGraph();
	    setTranslateX(0);
	    setTranslateY(0);
	    setScale(getMinimumZoom());
	}
    }

    @Override
    public void exportJPEG(String filename, float scale, boolean grayscale) {
	BufferedImage img = new BufferedImage(
		(int) (graph.getBounds().width * scale), (int) (graph
			.getBounds().height * scale),
		grayscale ? BufferedImage.TYPE_BYTE_GRAY
			: BufferedImage.TYPE_INT_RGB);
	Graphics g = img.createGraphics();

	double x = getTranslateX();
	double y = getTranslateY();
	double s = getScale();
	super.setScale(scale);
	super.setTranslateX(graph.getBounds().width * scale / 2.f);
	super.setTranslateY(graph.getBounds().height * scale / 2.f);
	autozoom = false;
	g.setColor(getBackgroundColor());
	g.fillRect(0, 0, (int) (graph.getBounds().width * scale), (int) (graph
		.getBounds().height * scale));
	g.setClip(0, 0, (int) (graph.getBounds().width * scale), (int) (graph
		.getBounds().height * scale));
	drawGraph((Graphics2D) g);
	try {
	    OutputStream out = new FileOutputStream(filename);
	    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
	    param.setQuality(1f, false);

	    encoder.encode(img);
	    out.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
	setTranslate(x, y);
	setScale(s);
    }

    @Override
    public void updateGraphView() {
	component.repaint();
    }

    @Override
    public Component getComponent() {
	return component;
    }

    private float getMinimumZoom(float widthc, float heightc) {
	if (graph != null) {
	    float width = graph.getBounds().width;
	    float height = graph.getBounds().height;
	    float scaleW = widthc / width;
	    float scaleH = heightc / height;
	    return Math.min(scaleW, scaleH);
	} else {
	    return 1;
	}
    }

    private float getMinimumZoom() {
	if (component != null && graph != null) {
	    return getMinimumZoom(component.getWidth(), component.getHeight());
	} else {
	    return 1;
	}

    }

    protected void drawEdge(Graphics2D g, EdgeView edgeview) {

	NodeView s = null;
	NodeView start = graph.getNodeView(edgeview.getEdge().getStart()
		.getId());
	if (start.isExpand()) {
	    s = edgeview.getStart();
	} else {
	    s = start;
	}
	NodeView e = graph.getNodeView(edgeview.getEdge().getEnd().getId());
	Point2D.Float oldStartPnt = new Point2D.Float(s.getPosX(), s.getPosY());
	Point2D.Float startPnt = trans(oldStartPnt);
	Point2D.Float oldEndPnt = new Point2D.Float(e.getPosX(), e.getPosY());
	Point2D.Float endPnt = trans(oldEndPnt);
	float oldSize1 = s.getSize();
	float oldSize2 = e.getSize();
	float oldHegight = e.getHeight();

	float sizeTop = transSize(oldSize1, oldStartPnt, startPnt);
	float height = transSize(oldHegight, oldStartPnt, startPnt);

	float sizeBottom = transSize(oldSize2, oldEndPnt, endPnt);
	float sizeStart = transSize(s.getSize(), oldStartPnt, startPnt);
	float gt = transSize(defaultEdge.getGapTop(), oldStartPnt, startPnt);
	float gb = transSize(defaultEdge.getGapBottom(), oldEndPnt, endPnt);
	float gaptop = gt + sizeTop / 2f + height;
	float gapbottom = gb + sizeBottom / 2f;

	line.setLine(startPnt.x, startPnt.y + gaptop, endPnt.x, endPnt.y
		- gapbottom);

	if (g.getClipBounds() != null && line.intersects(g.getClipBounds())) {

	    // draws line
	    setPaintMode(g, edgeview.isHidden() && !edgeview.isHighlighted());
	    if (edgeview.isHighlighted()) {
		setPaintAttr(g, edgeview.getHighlightedColor(), edgeview
			.getHighlightedWidth());
	    } else {
		setPaintAttr(g, edgeview.getColor(), edgeview.getWidth());
	    }

	    g.draw(line);

	    // draws short line top
	    if (isVisible(gaptop)) {
		line.setLine(startPnt.x, startPnt.y + (sizeStart / 2.0f)
			+ height, startPnt.x, startPnt.y + gaptop);
		g.draw(line);
	    }

	    // draws short line bottom
	    if (isVisible(gapbottom)) {
		line.setLine(endPnt.x,
			endPnt.y - (gb / 2f) - (sizeBottom / 2f), endPnt.x,
			endPnt.y - gapbottom);
		g.draw(line);
	    }

	}

	if (edgeview.isConnectChildren()
		&& graph.getNodeView(edgeview.getEdge().getEnd().getId())
			.isExpand()) {

	    setPaintMode(g, true);
	    setPaintAttr(g, edgeview.getColor(), edgeview.getWidth());

	    float posy = endPnt.y - (gb / 2f) - (sizeBottom / 2f);

	    Collections.sort(edgeview.getEnds(), new Comparator<NodeView>() {
		public int compare(NodeView arg0, NodeView arg1) {
		    return arg0.compareTo(arg1);
		}
	    });

	    // draws horizontal lines (left)

	    if (g.getClipBounds() != null
		    && (g.getClipBounds().contains(
			    new Point2D.Float(edgeview.getEnds().get(0)
				    .getPosX(), posy)) || g.getClipBounds()
			    .contains(
				    new Point2D.Float(edgeview.getEnds().get(
					    edgeview.getEnds().size() - 1)
					    .getPosX(), posy)))) {

		for (int i = 0; i < edgeview.getEnds().size() / 2; i++) {
		    NodeView end = edgeview.getEnds().get(i);
		    float posx = transX(end.getPosX());

		    boolean t = false;
		    boolean u = false;
		    for (EdgeView ed : end.getInEdges()) {
			t = t || ed.isHighlighted();
			u = u && ed.isHidden();
		    }

		    if (!end.isHidden() && !u)
			setPaintMode(g, false);
		    if (end.isHighlighted() && t) {
			setPaintAttr(g, edgeview.getHighlightedColor(),
				edgeview.getHighlightedWidth());
			setPaintMode(g, false);
		    }

		    float nposx = 0;
		    if (i < (edgeview.getEnds().size() / 2) - 1) {
			nposx = transX(edgeview.getEnds().get(i + 1).getPosX());
		    } else {
			nposx = endPnt.x;
		    }
		    line.setLine(posx, posy, nposx, posy);
		    g.draw(line);
		}

		// draws horizontal lines (right)
		setPaintMode(g, true);
		setPaintAttr(g, edgeview.getColor(), edgeview.getWidth());

		for (int i = edgeview.getEnds().size() - 1; i >= edgeview
			.getEnds().size() / 2; i--) {
		    NodeView end = edgeview.getEnds().get(i);
		    float posx = transX(end.getPosX());

		    boolean t = false;
		    boolean u = false;
		    for (EdgeView ed : end.getInEdges()) {
			t = t || ed.isHighlighted();
			u = u && ed.isHidden();
		    }

		    if (!end.isHidden() && !u)
			setPaintMode(g, false);

		    if (end.isHighlighted() && t) {
			setPaintAttr(g, edgeview.getHighlightedColor(),
				edgeview.getHighlightedWidth());
			setPaintMode(g, false);
		    }
		    if (i > (edgeview.getEnds().size() / 2) + 1) {
			float nposx = transX(edgeview.getEnds().get(i - 1)
				.getPosX());
			line.setLine(posx, posy, nposx, posy);
		    } else {
			line.setLine(posx, posy, endPnt.x, posy);
		    }
		    g.draw(line);
		}
	    }

	}
    }

    private void drawNode(Graphics2D g, NodeView nodeview) {

	Node node = nodeview.getNode();

	Point2D.Float oldPoint = new Point2D.Float(nodeview.getPosX(), nodeview
		.getPosY());
	Point2D.Float point = trans(oldPoint);

	float size = transSize(nodeview.getSize(), oldPoint, point);
	float defaultSize = transSize(defaultNode.getSize(), oldPoint, point);
	float gapBottom = transSize(defaultEdge.getGapBottom(), oldPoint, point);

	nodeview.setBounds(point.x, point.y, size);

	if (g.getClipBounds() != null
		&& !g.getClipBounds().intersects(nodeview.getBounds())) {
	    return;
	}

	if (!isVisible(size))
	    return;

	if (!nodeview.getNode().isDummy()
		|| (nodeview.getNode().isDummy() && !nodeview.isExpand() && nodeview
			.getNode().getNodeCount() > 0)) {
	    if (nodeview.getNode().getParent() == null
		    || (nodeview.getNode().getParent() != null && graph
			    .getNodeView(nodeview.getNode().getParent().getId())
			    .isExpand())) {
		// parents are highlighted?
		boolean parentsHighlighted = false;
		NodeView mom = graph.getNodeView(node.getIdMom());
		if (mom != null)
		    parentsHighlighted = parentsHighlighted
			    || mom.isHighlighted();

		NodeView dad = graph.getNodeView(node.getIdDad());
		if (dad != null)
		    parentsHighlighted = parentsHighlighted
			    || dad.isHighlighted();

		// draws vertical connection lines
		if (defaultEdge.isConnectChildren() && node.hasParents()) {
		    if (isVisible(gapBottom / 2f)) {
			Color color = null;
			float width = -1;
			if (nodeview.isHighlighted() && parentsHighlighted) {
			    color = defaultEdge.getHighlightedColor();
			    width = defaultEdge.getHighlightedWidth();
			} else {
			    color = defaultEdge.getColor();
			    width = defaultEdge.getWidth();
			}
			setPaintMode(g, nodeview.isHidden()
				&& !nodeview.isHighlighted());
			setPaintAttr(g, color, width);

			line.setLine(point.x, point.y - size / 2f, point.x,
				point.y - (gapBottom / 2f) - (size / 2f));
			g.draw(line);
		    }
		}

		Color border = nodeview.getBorderColor();
		Color fill = null;

		if (nodeview.isSelected())
		    fill = nodeview.getSelectedColor();
		else if (nodeview.isHighlighted())
		    fill = nodeview.getHighlightedColor();
		else if (nodeview.isMouseOver())
		    fill = nodeview.getMouseOverColor();
		else
		    fill = nodeview.getColor();

		setPaintAttr(g, border, nodeview.getBorderWidth());
		setPaintMode(g, nodeview.isHidden()
			&& !nodeview.isHighlighted());

		if (nodeview.getSymbols().size() > 0) {
		    // draws symbols
		    for (Symbol symbol1 : nodeview.getSymbols()) {
			if (symbol1 instanceof Symbol2D) {
			    Symbol2D symbol = (Symbol2D) symbol1;
			    symbol.setColors(nodeview.getColors());
			    symbol.drawSymbol(g, point, size, border, fill,
				    nodeview);
			}
		    }
		    if (size * getScale() > 1) {
			visibleNodes.add(nodeview);
		    }
		} else {
		    // draws default symbol
		    Symbol2D symbol = new SymbolSexUndesignated();
		    symbol.setColors(nodeview.getColors());
		    symbol.drawSymbol(g, point, defaultSize, border, fill,
			    nodeview);
		    visibleNodes.add(nodeview);
		}
	    }
	} else {
	    if (nodeview.getNode().isDummy() && node.getNodeCount() == 0) {
		// draws dummy
		setPaintMode(g, nodeview.isHidden()
			&& !nodeview.isHighlighted());
		Color color = null;
		float lineWidth = -1;

		if (nodeview.isHighlighted()) {
		    color = defaultEdge.getHighlightedColor();
		    lineWidth = defaultEdge.getHighlightedWidth();

		} else if (defaultEdge.getColorForLongLines() != null) {
		    color = defaultEdge.getColorForLongLines();
		    lineWidth = defaultEdge.getWidth();

		} else {
		    color = defaultEdge.getColor();
		    lineWidth = defaultEdge.getWidth();
		}
		setPaintAttr(g, color, lineWidth);

		line.setLine(point.x, point.y - size / 2f - gapBottom / 2.0f,
			point.x, point.y + size / 2f);
		g.draw(line);
	    }
	}
    }

    private void setPaintAttr(Graphics2D g, Color color, float width) {
	if (!color.equals(g.getColor())) {
	    g.setColor(color);
	}
	g.setStroke(new BasicStroke(width));
    }

    private void setPaintMode(Graphics2D g, boolean useAlpha) {
	AlphaComposite composite = null;
	if (useAlpha) {
	    composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		    getAlpha());
	} else {
	    composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
	}
	g.setComposite(composite);
    }

    private void drawHint(Graphics2D g) {
	if (selNodeview != null) {
	    String text = selNodeview.getHint();
	    if (text != null) {
		String[] lines = text.split("\n");
		int hintWidth = 0;
		int lineHeight = g.getFontMetrics().getHeight() + 4;
		int hintHeight = 0;
		for (String hint : lines) {
		    if (hint != null) {
			if (hint.equals("{line}")) {
			    hintHeight += lineHeight / 2;
			} else {
			    hintHeight += lineHeight;
			}
		    }
		}
		for (String hint : lines) {
		    int currentWidth = g.getFontMetrics().stringWidth(hint);
		    if (currentWidth > hintWidth)
			hintWidth = currentWidth;
		}

		setPaintMode(g, false);
		setPaintAttr(g, new Color(255, 255, 225), 1);

		Point2D pos = nodeToMouse((float) selNodeview.getBounds()
			.getMaxX(), (float) selNodeview.getBounds().getMaxY()
			- selNodeview.getHeight());

		if (pos.getX() + hintWidth + 20 > g.getClipBounds().getMaxX()) {
		    pos.setLocation(pos.getX()
			    - (pos.getX() + hintWidth + 20 - g.getClipBounds()
				    .getMaxX()), pos.getY());
		}

		if (pos.getY() + hintHeight + 17 > g.getClipBounds().getMaxY()) {
		    pos.setLocation(pos.getX(), pos.getY()
			    - (pos.getY() + hintHeight + 17 - g.getClipBounds()
				    .getMaxY()));
		}

		// draws background
		g.fillRect((int) pos.getX() + 10, (int) pos.getY() + 4,
			hintWidth + 6, hintHeight + 3);

		setPaintAttr(g, Color.BLACK, 1);
		// draws border
		g.drawRect((int) pos.getX() + 10, (int) pos.getY() + 4,
			hintWidth + 6, hintHeight + 3);

		// draws text
		int y = lineHeight + (int) pos.getY();
		for (String hint : lines) {
		    if (hint != null) {
			if (hint.equals("{line}")) {
			    // draws line
			    setPaintAttr(g, Color.GRAY, 1);
			    g.drawLine((int) pos.getX() + 10, y - lineHeight
				    / 2, (int) pos.getX() + 10 + hintWidth + 6,
				    y - lineHeight / 2);
			    y += lineHeight / 2;
			    setPaintAttr(g, Color.BLACK, 1);
			} else {
			    // draws string
			    g.drawString(hint, (int) pos.getX() + 13, y);
			    y += lineHeight;
			}
		    }
		}
	    }
	}
    }

    private void drawGraph(Graphics2D g) {
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	if (autozoom) {
	    centerGraph();
	}
	transform = new AffineTransform();
	transform.translate(getTranslateX(), getTranslateY());
	transform.scale(getScale(), getScale());
	g.setTransform(transform);
	if (graph != null) {
	    if (isEdgeVisible()) {
		for (Object edgeview : graph.getEdges()) {
		    drawEdge(g, (EdgeView) edgeview);
		}
	    }
	    visibleNodes.clear();
	    for (Object nodeview : graph.getAllNodes().values())
		drawNode(g, (NodeView) nodeview);

	    AffineTransform trans = new AffineTransform();
	    g.setTransform(trans);
	    drawHint(g);
	}
    }

    private Point2D mouseToNode(int x, int y) {
	if (transform != null) {
	    double[] posMouse = new double[4];
	    posMouse[0] = x;
	    posMouse[1] = y;

	    AffineTransform toInternal = null;
	    try {
		toInternal = transform.createInverse();
	    } catch (NoninvertibleTransformException nite) {
	    }
	    toInternal.transform(posMouse, 0, posMouse, 0, 1);
	    return new Point2D.Double(posMouse[0], posMouse[1]);
	} else
	    return null;

    }

    private Point2D nodeToMouse(float x, float y) {
	if (transform != null) {
	    double[] posMouse = new double[4];
	    posMouse[0] = x;
	    posMouse[1] = y;

	    transform.transform(posMouse, 0, posMouse, 0, 1);
	    return new Point2D.Double(posMouse[0], posMouse[1]);
	} else
	    return null;

    }

    @Override
    public void setScale(double scale) {
	super.setScale(Math.max(getMinimumZoom(), scale));
    }

    private class ScaleHandler implements MouseWheelListener {
	public void mouseWheelMoved(MouseWheelEvent e) {
	    if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL
		    && isZoomEnabled()) {
		autozoom = false;
		setScale(getScale() + (0.1 * e.getWheelRotation()));
		component.repaint();
	    }
	}
    }

    @Override
    public void setTranslateX(double translateX) {
	if (graph != null) {

	    if (getComponent() != null) {
		if (getComponent().getWidth() >= graph.getBounds().width
			* getScale()) {
		    super.setTranslateX(getComponent().getWidth() / 2f);
		    return;
		}
	    }

	    // checks if new position is valid (right move)
	    if (translateX - getTranslateX() >= 0) {
		if (-translateX >= graph.getBounds().x * getScale()) {
		    super.setTranslateX(translateX);
		} else {
		    super.setTranslateX(-graph.getBounds().x * getScale());
		}
	    }

	    // checks if new position is valid (left move)
	    if (translateX - getTranslateX() <= 0) {
		// for the right side we need the component's width
		if (getComponent() != null) {
		    if (-(translateX - getComponent().getWidth()) <= (graph
			    .getBounds().x + graph.getBounds().width)
			    * getScale()) {
			super.setTranslateX(translateX);
		    } else {
			super.setTranslateX(-((graph.getBounds().x + graph
				.getBounds().width) * getScale())
				+ getComponent().getWidth());
		    }
		} else {
		    super.setTranslateX(translateX);
		}
	    }
	}
    }

    @Override
    public void setTranslateY(double translateY) {
	if (graph != null) {
	    if (getComponent() != null) {
		if (getComponent().getHeight() >= graph.getBounds().height
			* getScale()) {
		    super.setTranslateY(getComponent().getHeight() / 2f);
		    return;
		}
	    }

	    // checks if new position is valid (down move)
	    if (translateY - getTranslateY() >= 0) {
		if (-translateY >= graph.getBounds().y * getScale()) {
		    super.setTranslateY(translateY);
		} else {
		    super.setTranslateY(-graph.getBounds().y * getScale());
		}
	    }

	    // checks if new position is valid (up move)
	    if (translateY - getTranslateY() <= 0) {
		// for the bottom side we need the component's height
		if (getComponent() != null) {

		    if (-(translateY - getComponent().getHeight()) <= (graph
			    .getBounds().y + graph.getBounds().height)
			    * getScale()) {
			super.setTranslateY(translateY);
		    } else {
			super.setTranslateY(-((graph.getBounds().y + graph
				.getBounds().height) * getScale())
				+ getComponent().getHeight());
		    }
		} else {
		    super.setTranslateY(0);
		}
	    }
	}
    }

    private float transX(float x) {
	return getEffect().transformPoint(x, 0).x;
    }

    private Point2D.Float trans(Point2D.Float point) {
	return getEffect().transformPoint(point.x, point.y);
    }

    private float transSize(float size, Point2D.Float point,
	    Point2D.Float newPoint) {
	return getEffect().transformSize(size, point.x, point.y, newPoint.x,
		newPoint.y);
    }

    private boolean isVisible(float size) {
	return size * getScale() > minSize;
    }

    public void updateNodes(Set<pedviz.graph.Node> nodes) {
	updateGraphView();
    }

    class GraphViewHandler implements ComponentListener, MouseListener,
	    MouseMotionListener {
	public void componentShown(ComponentEvent arg0) {
	    centerGraph();
	}

	public void componentResized(ComponentEvent arg0) {
	    if (graph != null){
		Rectangle2D b = null;
		if (getComponent().getBounds().width > getComponent().getBounds().height) {
		    double aspect = (double) getComponent().getBounds().width
			    / (double) getComponent().getBounds().height;
		    double height = graph.getBounds().getWidth() / aspect;
		    b = new Rectangle2D.Double(graph.getBounds().getX(), -height / 2.0,
			    graph.getBounds().getWidth(), height);
		} else {
		    double aspect = (double) getComponent().getBounds().height
			    / (double) getComponent().getBounds().width;
		    double width = graph.getBounds().getHeight() / aspect;
		    b = new Rectangle2D.Double(-width / 2.0, graph.getBounds().getY(),
			    width, graph.getBounds().getHeight());
		}
		//System.out.println(b);
		effect.setBounds(b);
	    }
	    centerGraph();
	    updateGraphView();
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentHidden(ComponentEvent arg0) {

	}

	public void mousePressed(MouseEvent e) {
	    lastOffsetX = e.getX();
	    lastOffsetY = e.getY();
	}

	public void mouseDragged(MouseEvent e) {
	    if (graph != null) {
		autozoom = false;
		int newX = e.getX() - lastOffsetX;
		int newY = e.getY() - lastOffsetY;
		lastOffsetX += newX;
		lastOffsetY += newY;

		Point2D point = mouseToNode(e.getX(), e.getY());
		if (point != null) {
		    if (getEffect() != null && graph.getBounds() != null
			    && getEffect().isAutoUpdateOnDrag()) {
			getEffect().moveFocus(
				(float) -newX * effect.getSpeed(),
				(float) -newY * effect.getSpeed());

			
			updateGraphView();
			setCursor(Cursor.MOVE_CURSOR);
		    }
		}

		// Left Mouse Button: Moving
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK
			&& isMovingEnabled()) {
		    setTranslateX(getTranslateX() + newX);
		    setTranslateY(getTranslateY() + newY);
		    setCursor(Cursor.MOVE_CURSOR);
		    component.repaint();
		}

		// Right Mouse Button: Zooming
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK
			&& isZoomEnabled()) {
		    // lastOffsetY += newY;
		    setScale(getScale() + newY * 0.1);
		    setCursor(Cursor.N_RESIZE_CURSOR);
		    component.repaint();
		}

	    }
	}

	public void mouseClicked(MouseEvent e) {
	    if (graph != null) {
		if (isSelectionEnabled() && graph.getAllNodes() != null) {
		    Point2D point = mouseToNode(e.getX(), e.getY());
		    for (NodeView nodeview : visibleNodes) {
			if (nodeview.getBounds().contains(point)) {
			    if (nodeview.isSelected()) {
				deselect(nodeview.getNode());
				fireNodeEvent(new NodeEvent(g, nodeview
					.getNode(), NodeEvent.DESELECTED));

				setCursor(Cursor.HAND_CURSOR);
			    } else {

				select(nodeview.getNode());
				setCursor(Cursor.HAND_CURSOR);

				fireNodeEvent(new NodeEvent(g, nodeview
					.getNode(), NodeEvent.SELECTED));

			    }
			    return;
			}
		    }
		    // No nodeview clicked
		    deselect();

		    fireNodeEvent(new NodeEvent(g, null,
			    NodeEvent.ALL_DESELECTED));

		}
	    }
	}

	public void mouseMoved(MouseEvent e) {
	    if (graph != null) {
		Point2D point = mouseToNode(e.getX(), e.getY());
		if (point != null) {
		    if (getEffect() != null && graph.getBounds() != null
			    && getEffect().isAutoUpdateOnMove()) {
			getEffect().setFocus((float) point.getX(),
				(float) point.getY());
			updateGraphView();
		    }

		    if (graph.getAllNodes() != null) {
			if (point != null) {
			    for (NodeView nodeview : visibleNodes) {
				if (nodeview.getBounds().contains(point)) {

				    setCursor(Cursor.HAND_CURSOR);

				    if (selNodeview != nodeview) {

					if (selNodeview != null) {

					    fireNodeEvent(new NodeEvent(g,
						    selNodeview.getNode(),
						    NodeEvent.MOUSE_LEAVE));

					    selNodeview.setMouseOver(false);
					}
					selNodeview = nodeview;

					fireNodeEvent(new NodeEvent(g, nodeview
						.getNode(),
						NodeEvent.MOUSE_ENTER));

					nodeview.setMouseOver(true);
					updateGraphView();
				    }
				    return;
				}
			    }

			    if (selNodeview != null) {
				selNodeview.setMouseOver(false);
				component.setCursor(null);

				fireNodeEvent(new NodeEvent(g, selNodeview
					.getNode(), NodeEvent.MOUSE_LEAVE));

				selNodeview = null;
				updateGraphView();
			    }
			}
		    }
		}
	    }
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	    component.setCursor(null);
	}
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
			    && (rule.getMode() == Rule.ONLY_2D || rule
				    .getMode() == Rule.BOTH))
			rule.applyRule(node);
	    }
	    updateGraphView();
	}
    }

}
