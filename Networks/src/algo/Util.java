package algo;


import java.util.List;
import java.util.Random;

import rect.Node;

public class Util {
    public static double doubleBetween(double min, double max, Random r) {
    	double f = r.nextDouble();
    	return max*f + min*(1-f);
    }
    
    public static Node pickRandomNode(List<Node> nodes, Random r) {
    	if (nodes.isEmpty())
    		return null;
    	int i = r.nextInt(nodes.size());
    	return nodes.get(i);
    }
}
