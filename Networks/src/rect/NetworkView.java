package rect;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import algo.Geometry;
import algo.Geometry.Gradient2D;
import beings.TriangleBeing;
import props.FloatProp;
import props.IntProp;
import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.mvc.Model;
import revsim.mvc.view.AbstractView;
import revsim.rendering2.CircleRender;
import revsim.rendering2.LineRender;

public class NetworkView extends AbstractView {
	
	int renderCount = 0;
	
	@Override
	public void init(File env, ConfigObject configObj, float ppu, int width,
			int height) throws ConfigException {
		
	}
	
	private boolean nodeIsInSegment(Node n, NetworkModel.Segment s) {
		return s.a == n.getId() || s.b == n.getId();
	}
	
	private boolean areInNewSegment(Node a, Node b, NetworkModel nm) {
		List<NetworkModel.Segment> newSegments = nm.getNewSegments();
		for (NetworkModel.Segment s : newSegments) {
			if (nodeIsInSegment(a, s) && nodeIsInSegment(b, s))
				return true;
		}
		return false;
	}

	@Override
	public Image render(Model model, int arg1, int arg2) {
        BufferedImage bim = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = bim.getGraphics();
        
        NetworkModel nm = (NetworkModel)model;
        
        g.setColor(Color.white);
        Set<Integer> keys = nm.getNodeKeys();
        for (Integer i : keys) {
        	Node node = nm.getNode(i);
        	g.setColor(new Color(255, 255, 255));
        	int fromx = node.getX();
        	int fromy = node.getY();
        	g.drawOval(fromx-2, fromy-2, 4, 4);
        	List<Integer> neighbors = node.getNeighbors();
        	for (int j = 0; j < neighbors.size(); j++) {
        		Node destNode = nm.getNode(neighbors.get(j));
        		int tox = destNode.getX();
        		int toy = destNode.getY();
        		g.drawLine(fromx, fromy, tox, toy);
        	}
        }
        
        g.setColor(Color.green);
        Node n = nm.getNode(510);
        System.out.println("------------------------------");
        for (Integer i : nm.getNodeKeys()) {
        	System.out.print(i + ",");
        }
        System.out.println();
        if (n != null) {
        	g.drawOval(n.getX()-2, n.getY()-2, 4, 4);
        	NetworkModel.NodeIndexAndMagnitude nam = nm.findMaxDensitySpoke(n, 4);
        	List<Integer> nbs = n.getNeighbors();
        	Integer id = nbs.get(nam.nodeIndex);
        	Node toNode = nm.getNode(id);
        	int fromx = n.getX();
        	int fromy = n.getY();
        	int tox = toNode.getX();
        	int toy = toNode.getY();
        	g.drawLine(fromx, fromy, tox, toy);
        }
        
        NetworkModel.DebugWindow dbw = nm.getDebugWindow();
        if (dbw != null) {
        	g.setColor(Color.red);
        	g.drawRect(dbw.ulx, dbw.uly, (dbw.lrx - dbw.ulx + 1), (dbw.lry - dbw.uly + 1));
        }

        renderCount++;
        
		return bim;
	}

}
