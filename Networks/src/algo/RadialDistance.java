package algo;

import java.util.List;

import props.FloatProp;
import props.IntProp;
import rect.NetworkModel;
import rect.Node;

public class RadialDistance implements IPropAlgorithm {
	
	String propName;
	
	public RadialDistance(String propName) {
		this.propName = propName;
	}

	@Override
	public void process(Node node, NetworkModel nm, List<Node> newNodes) {

		if (!node.hasProp(propName)) {
			System.out.println("no prop name");
			return;
		}
		
		IntProp centerProp = (IntProp)node.getProp(propName);
		
		List<Integer> neighbors = node.getNeighbors();
		for (int i = 0; i < neighbors.size(); i++) {
			Node neighb = nm.getNode(neighbors.get(i));
			if (neighb.hasProp(propName))
				continue;
			neighb.putProp(propName, new IntProp(centerProp.value + 1));
			newNodes.add(neighb);
		}
		
	}

}
