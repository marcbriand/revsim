package algo;

import java.util.List;

import rect.NetworkModel;
import rect.Node;

public interface IPropAlgorithm {
	
	void process(Node node, NetworkModel nm, List<Node> newNodes);

}
