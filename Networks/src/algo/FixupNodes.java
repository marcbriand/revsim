package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rect.NetworkModel;
import rect.Node;

public class FixupNodes {
	
	private static class AngleIndex {
		public double angle;
		public int index;
		
		public AngleIndex(double angle, int index) {
			this.angle = angle;
			this.index = index;
		}
	}
	
	private static class Comp implements Comparator<AngleIndex> {

		@Override
		public int compare(AngleIndex o1, AngleIndex o2) {
			
			if (o1.angle < o2.angle)
				return -1;
			if (o1.angle == o2.angle)
				return 0;
			return 1;
		}
		
	}
/*
	public static void fixupNode(Node n, NetworkModel nm) {
		List<Integer> neighbors = n.getNeighbors();
		List<AngleIndex> angles = new ArrayList<AngleIndex>();
		for (Integer i : neighbors) {
			Node nei = nm.getNode(i);
			double angle = findAngle(n, nei);
			angles.add(new AngleIndex(angle, i));
		}
		
		Collections.sort(angles, new Comp());
		
		List<Integer> newNeighbors = new ArrayList<Integer>();
		for (AngleIndex ai : angles) {
			newNeighbors.add(ai.index);
		}
		
		n.setNeighbors(newNeighbors);
	}
	
	public static void fixupNodes(NetworkModel nm) {
		int numNodes = nm.getNumNodes();
		for (int i = 0; i < numNodes; i++) {
			Node n = nm.getNode(i);
			fixupNode(n, nm);
		}
	}
*/	

}
