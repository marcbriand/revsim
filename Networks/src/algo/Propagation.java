package algo;

import java.util.ArrayList;
import java.util.List;

import rect.NetworkModel;
import rect.Node;

public class Propagation {

	public static void doOverNodes(NetworkModel nm, Node startNode, IPropAlgorithm alg) {
		
		List<Node> nextNodes = new ArrayList<Node>();
		nextNodes.add(startNode);
		
		while (!nextNodes.isEmpty()) {
			List<Node> tempNewNodes = doOverNodes(nm, nextNodes, alg);
			nextNodes.clear();
			for (Node node : tempNewNodes) {
				nextNodes.add(node);
			}
		}
		
	}
	
	public static List<Node> doOverNodes(NetworkModel nm, List<Node> nodes, IPropAlgorithm alg) {
		System.out.println("------");
		System.out.println("doOverNodes, starting with " + nodes.size() + " nodes");
		List<Node> tempNewNodes = new ArrayList<Node>();
		for (Node node : nodes) {
			alg.process(node, nm, tempNewNodes);
		}
		System.out.println("..returning " + tempNewNodes.size() + " new nodes");
		return tempNewNodes;
	}
}
