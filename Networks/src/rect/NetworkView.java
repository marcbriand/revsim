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
        
//        CircleRender cr = new CircleRender();
//        cr.setFillTaper(100);
//        cr.fill(400, 300, 200, Color.red, bim);
        LineRender lr = new LineRender();
        lr.setLineWidth(40);
        lr.setTaper(20);
        lr.drawLineWithCaps(300, 300, 500, 200, Color.green, bim);
        

        
        Graphics g = bim.getGraphics();
        
        g.setColor(Color.red);
        g.drawLine(300, 300, 100, 200);
        g.drawLine(100, 300, 100, 200);
        NetworkModel nm = (NetworkModel)model;
        
        g.setColor(Color.white);
        Set<Integer> keys = nm.getNodeKeys();
        for (Integer i : keys) {
        	Node node = nm.getNode(i);
        	if (node.getId() == 1 || node.getId() == 6 || node.getId() == 13 || node.getId() == 88)
        		g.drawOval(node.getX()-2, node.getY()-2, 4, 4);
        	g.setColor(new Color(255, 255, 255));
        	int fromx = node.getX();
        	int fromy = node.getY();
        	List<Integer> neighbors = node.getNeighbors();
        	for (int j = 0; j < neighbors.size(); j++) {
        		Node destNode = nm.getNode(neighbors.get(j));
//        		if (areInNewSegment(node, destNode, nm))
//        			g.setColor(new Color(0, 255, 0));
        		int tox = destNode.getX();
        		int toy = destNode.getY();
        		g.drawLine(fromx, fromy, tox, toy);
        	}
        }
        
        NetworkModel.DebugWindow dbw = nm.getDebugWindow();
        if (dbw != null) {
        	g.setColor(Color.red);
        	g.drawRect(dbw.ulx, dbw.uly, (dbw.lrx - dbw.ulx + 1), (dbw.lry - dbw.uly + 1));
        }
/*        
        g.setColor(Color.red);
        NetworkModel.NewAndSourceNodes nsn = nm.getNewAndSourceNodes();
        for (Node nn : nsn.newNodes) {
        	Gradient2D grad = Geometry.findGradientAt(nn, nm);
        	int gx = nn.getX() + (int)(50*grad.getDeltaX()/nm.getNumNodes());
        	int gy = nn.getY() + (int)(50*grad.getDeltaY()/nm.getNumNodes());
        	g.drawLine(nn.getX(), nn.getY(), gx, gy);
        }
*/        
//        NetworkModel.NewAndSourceNodes nsn = nm.getNewAndSourceNodes();
//        for (Node nn : nsn.newNodes) {
//        	if (nn.getId() == 7 || nn.getId() == 5)
//        		g.drawOval(nn.getX()-2, nn.getY()-2, 4, 4);
//        }
/*        
        g.setColor(new Color(0, 0, 255));
        NetworkModel.NewAndSourceNodes nsn = nm.getNewAndSourceNodes();
        for (Node newNode : nsn.newNodes) {
        	int ulx = newNode.getX() - 2;
        	int uly = newNode.getY() - 2;
        	int llx = newNode.getX() + 2;
        	int lly = newNode.getY() + 2;
        	g.drawOval(ulx, uly, 4, 4);
        }
*/
/*
        g.setColor(new Color(255, 0, 0));
        for (int i = 0; i < nm.getNumNodes(); i++) {
        	Node gn = nm.getNode(i);
        	Gradient2D grad = Geometry.findGradientAt(gn, nm);
        	int newx = gn.getX() + (int)(10*grad.getDeltaX());
        	int newy = gn.getY() + (int)(10*grad.getDeltaY());
        	g.drawLine(gn.getX(), gn.getY(), newx, newy);
        	
        }
*/
/*        
        g.setColor(new Color(255, 0, 0));
        for (Node newNode : nsn.newNodes) {
        	Gradient2D grad = Geometry.findGradientAt(newNode, nm);
        	double angle = Geometry.findAngle(grad);
        	int dx = (int)(30*Math.cos(angle));
        	int dy = (int)(30*Math.sin(angle));
        	int x = newNode.getX() + dx;
        	int y = newNode.getY() + dy;
        	g.drawLine(newNode.getX(), newNode.getY(), x, y);
        }
*/        
        renderCount++;
        
		return bim;
	}

}
