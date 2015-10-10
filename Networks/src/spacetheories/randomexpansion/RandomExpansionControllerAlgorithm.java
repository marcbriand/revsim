package spacetheories.randomexpansion;

import java.util.List;

import algo.Geometry;
import algo.Util;
import config.NetworkConfig;
import rect.IControllerAlgorithm;
import rect.NetworkController;
import rect.NetworkModel;
import rect.Node;
import revsim.mvc.Model;

public class RandomExpansionControllerAlgorithm implements IControllerAlgorithm {

	@Override
	public Model createFirstNodes(NetworkController ncont, NetworkConfig ncfg, long currentFrame) {
		
		Model model = ncont.createFirstNodes(currentFrame);
		NetworkModel nm = (NetworkModel)model;
		nm.setNetworkConfig(ncfg);
		
		return Geometry.generateFirstNeighbors2(nm, currentFrame);
	}

	@Override
	public Model growNetwork(NetworkController ncont, Model model, long currentFrame) {
		NetworkModel nm = (NetworkModel)model;
		long maxGenerateFrame = nm.getMaxGenerateFrame();
		if (currentFrame < maxGenerateFrame + 3) {		
		   ncont.removeLonersOlderThan(currentFrame - 2, nm);
		   nm.sortAllNodeNeighbors();
		}
		else {
			List<Node> trail = nm.getTrail();
			if (trail.isEmpty()) {
				Node trailNode = nm.getNode(0);
				nm.addToTrail(trailNode);
			}
			else {
				Node lastTrailNode = trail.get(trail.size()-1);
				List<Node> youngest = nm.getYoungestNeighbors(lastTrailNode);
				Node y = Util.pickRandomNode(youngest, nm.getRandom());
				if (y != null)
				   nm.addToTrail(y);
			}
		}
		if (currentFrame < maxGenerateFrame)
		   model = Geometry.generateNeighbors(nm, currentFrame);
		return model;
	}

}
