package algo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import rect.NetworkModel;
import rect.Node;

public class TestNetworkModel {

	@Test
	public void testDensity() {
		
		Map<Integer, Node> nodes = new HashMap<Integer, Node>();

		NetworkModel nm = new NetworkModel();
		Node nb0 = new Node(1, 1, nm);
		nodes.put(1, nb0);
		
		Node nb0nb0 = new Node(2, 1, nm);
		nodes.put(2, nb0nb0);
		nb0.addNeighbor(2);
		nb0nb0.addNeighbor(1);
		
		Node nb1 = new Node(3, 1, nm);
		nodes.put(3, nb1);
		
		Node nb1nb0 = new Node(4, 1, nm);
		nodes.put(4, nb1nb0);
		nb1.addNeighbor(4);
		nb1nb0.addNeighbor(3);
		
		Node nb1nb1 = new Node(5, 1, nm);
		nodes.put(5, nb1nb1);
		nb1.addNeighbor(5);
		nb1nb1.addNeighbor(3);
		
		Node nb2 = new Node(6, 1, nm);
		nodes.put(6, nb2);
		
		Node nb2nb0 = new Node(7, 1, nm);
		nodes.put(7, nb2nb0);
		nb2.addNeighbor(7);
		nb2nb0.addNeighbor(6);
		
		Node nb2nb1 = new Node(8, 1, nm);
		nodes.put(8, nb2nb1);
		nb2.addNeighbor(8);
		nb2nb1.addNeighbor(6);
		
		Node nb2nb2 = new Node(9, 1, nm);
		nodes.put(9, nb2nb2);
		nb2.addNeighbor(9);
		nb2nb2.addNeighbor(6);
		
		Node node = new Node(10, 1, nm);
		nodes.put(10, node);
		node.addNeighbor(1);
		nb0.addNeighbor(10);
		node.addNeighbor(3);
		nb1.addNeighbor(10);
		node.addNeighbor(6);
		nb2.addNeighbor(10);
		
		
		Set<Integer> exclude = new HashSet<Integer>();
		NetworkModel.NodeIndexAndMagnitude nam = NetworkModel.findMaxDensitySpoke(node, 2, nodes);
		
	}
	

}
