package spacetheories.relsize.seventwowithzero;

import java.util.ArrayList;
import java.util.List;

import config.NetworkConfig;
import rect.IControllerAlgorithm;
import rect.NetworkController;
import rect.NetworkModel;
import rect.Node;
import revsim.mvc.Model;
import revsim.rendering.IntPoint2D;
import config.Point2DConfig;

public class ControllerAlgorithm7_2_0 implements IControllerAlgorithm {

	@Override
	public Model createFirstNodes(NetworkController ncont, NetworkConfig ncfg,
			long currentFrame) {

		NetworkModel nm = new NetworkModel();
		NetworkModel.GenerateBuilder gb = new NetworkModel.GenerateBuilder();
		nm.setGenerateBuilder(gb);
		
		nm.setNetworkConfig(ncfg);
		
		List<Point2DConfig> things = ncfg.getStartPoints();
		List<IntPoint2D> thingsT = new ArrayList<IntPoint2D>();
		List<ApparentPoint2D> appPoints = new ArrayList<ApparentPoint2D>();
		int screenWidth = 800;
		int screenHeight = 600;
		int arenaWidth = screenWidth - 25;
		int arenaHeight = screenHeight - 25;
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;
		
		for (Point2DConfig p : things) {
			if (p.getX() < xmin)
				xmin = p.getX();
			if (p.getX() > xmax)
				xmax = p.getX();
			if (p.getY() < ymin)
				ymin = p.getY();
			if (p.getY() > ymax)
				ymax = p.getY();
		}
		int xdelta = xmax - xmin;
		int ydelta = ymax - ymin;
		double pixelsPerThingX = 0.0;
		if (xdelta != 0)
			pixelsPerThingX = ((double)arenaWidth/(double)xdelta);
		double pixelsPerThingY = 0.0;
		if (ydelta != 0)
			pixelsPerThingY = ((double)arenaHeight/(double)ydelta);
		
		if (xdelta == 0) {
			int x = arenaWidth/2;
			if (ydelta == 0) {
//				thingsT.add(new IntPoint2D(x, arenaHeight/2));
				Point2DConfig configP = things.get(0);
				ApparentPoint2D appP = new ApparentPoint2D(x, arenaHeight/2);
				appP.apparentx = configP.getX();
				appP.apparenty = configP.getY();
				appPoints.add(appP);
			}
			else {
				for (Point2DConfig p : things) {
					int y = (int)((p.getY() - ymin)*pixelsPerThingY + 20);
//					thingsT.add(new IntPoint2D(x, y));
					ApparentPoint2D appP = new ApparentPoint2D(x, y, p.getX(), p.getY());
					appPoints.add(appP);
				}
			}
		}
		else {
			if (ydelta == 0) {
				int y = arenaHeight/2;
				for (Point2DConfig p : things) {
					int x = (int)((p.getX() - xmin)*pixelsPerThingX + 20);
//					thingsT.add(new IntPoint2D(x, y));
					ApparentPoint2D appP = new ApparentPoint2D(x, y, p.getX(), p.getY());
					appPoints.add(appP);
				}
			}
			else {
				for (Point2DConfig p : things) {
					int x = (int)((p.getX() - xmin)*pixelsPerThingX + 20);
					int y = (int)((p.getY() - ymin)*pixelsPerThingY + 20);
//					thingsT.add(new IntPoint2D(x, y));
					ApparentPoint2D appP = new ApparentPoint2D(x, y, p.getX(), p.getY());
					appPoints.add(appP);
				}				
			}
		}
		
//		for (IntPoint2D pt : thingsT) {
		for (ApparentPoint2D pt: appPoints) {
			int id = nm.getNextId();
			ApparentNode n = new ApparentNode(id, currentFrame, nm);
			n.setX(pt.x);
			n.setY(pt.y);
			n.setApparentX(pt.apparentx);
			n.setApparentY(pt.apparenty);
			nm.addUnattachedNode(n);
			nm.addNewAndSourceNode(n, null);
		}
		
		return nm;
		
	}

	@Override
	public Model growNetwork(NetworkController ncont, Model model,
			long currentFrame) {

		return null;
	}

}
