package rect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.NetworkConfig;
import config.Point2DConfig;
import beings.IBeing;
import beings.TriangleBeing;
import props.DProp;
import props.FloatProp;
import props.IntProp;
import algo.FixupNodes;
import algo.Geometry;
import algo.Geometry.Gradient2D;
import algo.IPropAlgorithm;
import algo.Propagation;
import algo.RadialDistance;
import algo.RadialInfluence;
import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.mvc.Controller;
import revsim.mvc.Model;

public class NetworkController implements Controller {
	
    int minDistance = 12;
	int maxDistance = 50;
	Float gradA;
	Float gradB;
	Float gradC;
	Float gradD;
	NetworkConfig nc;
	IPropAlgorithm alg = new RadialDistance("xval");
	List<Node> nextNodes = new ArrayList<Node>();

	@Override
	public Model inc(Model prevModel, long currentFrame) {
		System.out.println("NetworkController.inc(), currentFrame = " + currentFrame);
		Model model = prevModel;
		if (model == null) {
			model = createFirstNodes(currentFrame);
			NetworkModel nm = (NetworkModel)model;
			nm.setNetworkConfig(nc);
			
			nm = Geometry.generateFirstNeighbors2(nm, currentFrame);
			
			Set<Integer> keys = nm.getNodeKeys();
			for (Integer i : keys) {
				Node n = nm.getNode(i);
//				System.out.println("node " + n.getId() + ", density: " + n.getDensity());
			}
			
		}
		else {
			NetworkModel nm = (NetworkModel)model;
			removeLonersOlderThan(currentFrame - 2, nm);
			model = Geometry.generateNeighbors(nm, currentFrame);
		}
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("..end of inc");
		
		return model;
	}
	
	private void removeLonersOlderThan(long frame, NetworkModel nm) {
		Set<Integer> keys = nm.getNodeKeys();
		List<Node> toRemove = new ArrayList<Node>();
		for (Integer i : keys) {
			Node n = nm.getNode(i);
			if (n.getNeighbors().size() <= 1 && n.getBirthday() < frame) {
				toRemove.add(n);
			}
		}
		for (Node n : toRemove)
			nm.removeNode(n);
	}
	
	private Model createFirstNodes(long framecount) {
		NetworkModel nm = new NetworkModel();
		NetworkModel.GenerateBuilder gb = new NetworkModel.GenerateBuilder();
		nm.setGenerateBuilder(gb);

		nm.setNetworkConfig(nc);
		gb.setDimensions(800, 600, minDistance, maxDistance);

		nm.setRandom(new Random(framecount));

/*
		Node first = new Node(0, framecount, nm);
		first.setX(400);
		first.setY(300);
		nm.addUnattachedNode(first);
		nm.addNewAndSourceNode(first, null);
*/
		List<Point2DConfig> startPoints = nm.getStartPoints();
		for (Point2DConfig pt : startPoints) {
			int id = nm.getNextId();
			Node n = new Node(id, framecount, nm);
			n.setX(pt.getX());
			n.setY(pt.getY());
			nm.addUnattachedNode(n);
			nm.addNewAndSourceNode(n, null);
		}
		
		

		return nm;
	}

	@Override
	public void init(File arg0, ConfigObject arg1) throws ConfigException {

		Object ncObj = arg1.getLocal("net");
		if (ncObj == null)
			throw new ConfigException("Missing 'net' property");
		if (ncObj instanceof NetworkConfig) {
			nc = (NetworkConfig)ncObj;
			System.out.println("max distance = " + nc.getMaxDistance());
			minDistance = nc.getMinDistance();
			maxDistance = nc.getMaxDistance();
			gradA = (Float)nc.getLocal("gradA");
			gradB = (Float)nc.getLocal("gradB");
			gradC = (Float)nc.getLocal("gradC");
			gradD = (Float)nc.getLocal("gradD");
		}
		else
			throw new ConfigException("'net' property was not of type NetworkConfig");

	}

}
