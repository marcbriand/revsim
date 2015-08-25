package algo;

import java.util.List;

import props.FloatProp;
import props.IntProp;
import rect.NetworkModel;
import rect.Node;

public class RadialInfluence implements IPropAlgorithm {
	
	String propName;
	float threshold;
	float dropoff = 1.0f;
	
	public RadialInfluence(String propName, float threshold, float dropoff) {
		this.propName = propName;
		this.threshold = threshold;
		this.dropoff = dropoff;
	}

	@Override
	public void process(Node node, NetworkModel nm, List<Node> newNodes) {

		if (!node.hasProp(propName)) {
			System.out.println("no prop name");
			return;
		}
		
		if (!node.hasProp("distance")) {
			System.out.println("no distance");
			return;
		}
		
		FloatProp centerProp = (FloatProp)node.getProp(propName);
		IntProp distance = (IntProp)node.getProp("distance");
		float newValue = centerProp.value/(dropoff*(float)distance.value + 1);
		if (Math.abs(newValue) < threshold)
			return;
		
		List<Integer> neighbors = node.getNeighbors();
		for (int i = 0; i < neighbors.size(); i++) {
			Node neighb = nm.getNode(neighbors.get(i));
			if (neighb.hasProp(propName))
				continue;
			neighb.putProp(propName, new FloatProp(newValue));
			neighb.putProp("distance", new IntProp(distance.value + 1));
			newNodes.add(neighb);
		}
		
	}

}
