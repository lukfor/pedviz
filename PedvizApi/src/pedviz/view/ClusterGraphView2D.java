package pedviz.view;

import pedviz.graph.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ClusterGraphView2D extends GraphView2D{

    HashMap<Integer, Color> colors;
    {
	colors = new HashMap<Integer, Color>();
	colors.put(1, Color.BLUE);
	colors.put(2, Color.RED);
	colors.put(3, Color.GREEN);
	colors.put(4, Color.YELLOW);
	colors.put(5, Color.MAGENTA);
	colors.put(6, Color.CYAN);
	colors.put(7, Color.PINK);
	colors.put(8, Color.GRAY);
	colors.put(9, Color.ORANGE);
    }
    
    @Override
    protected void drawEdge(Graphics2D g, EdgeView edgeview) {
	Node start = edgeview.getStart().getNode();
	Node end = edgeview.getEnd().getNode();
	if (start.getUserData("setid") != null && end.getUserData("setid") != null && !start.getUserData("setid").equals("")){
	if (start.getUserData("setid").equals(end.getUserData("setid"))){
	    edgeview.setColor(colors.get(new Integer(start.getUserData("setid").toString().trim())));
	    edgeview.setWidth(1);
	}
	}
	super.drawEdge(g, edgeview);

    }
   
}
