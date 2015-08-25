package rect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.NetworkConfig;
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
			model = createFirstNode(currentFrame);
			NetworkModel nm = (NetworkModel)model;
			
			nm = Geometry.generateFirstNeighbors(nm);
			
		}
		else {
			NetworkModel nm = (NetworkModel)model;
			model = Geometry.generateNeighbors(nm);
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
	
	private Model createFirstNode(long framecount) {
		NetworkModel nm = new NetworkModel();
		Node first = new Node();
		first.setId(0);
		first.setX(400);
		first.setY(300);
		nm.addNode(first);
		nm.addNewAndSourceNode(first, null);
		
		NetworkModel.GenerateBuilder gb = new NetworkModel.GenerateBuilder();
		gb.setDimensions(800, 600, maxDistance);

		gb.setMaxEdgesPerNode(8);
		nm.setGenerateBuilder(gb);
		nm.setNetworkConfig(nc);
		nm.setRandom(new Random(framecount));
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
