package beings;

import java.util.List;

import props.IntProp;
import rect.NetworkModel;
import rect.Node;

public class TriangleBeing implements IBeing {
	
	Node a, b, c;
	int id = 0;

	@Override
	public void inc(NetworkModel nm, long framecount) {
        // find next node for a
	    List<Integer> neighbors = a.getNeighbors();
	    int minDist = Integer.MAX_VALUE;
	    int minIndex = 0;
	    for (Integer i : neighbors) {
		    Node n = nm.getNode(i);
		    IntProp d = (IntProp)n.getProp("xval");
		    if (d.value < minDist) {
			    minIndex = i;
			    minDist = d.value;
		    }
	    }
	   
	    String propName = "tid_" + id;
	   
	    a.removeProp(propName);
	    b.removeProp(propName);
	    c.removeProp(propName);
	   
	    a = nm.getNode(minIndex);
	    a.putProp(propName, new IntProp(id));
	   
//	    b = findClosestNeighbor(a, -1, nm);
//		c = findClosestNeighbor(b, a.getId(), nm);
	    b = findNeighbor(a, a, nm);
	    if (b == null)
	    	return;
	    c = findNeighbor(b, a, nm);
	    if (c == null)
	    	return;
		
		b.putProp(propName, new IntProp(id));
		c.putProp(propName, new IntProp(id));
		
		System.out.println("a = " + a.getId() + ", b = " + b.getId() + ", c = " + c.getId());
	}
	
	private boolean isVert(int diffx, int diffy, float threshold) {
		float fdiffx = (float)diffx;
		float fdiffy = (float)diffy;
		if (Math.abs(fdiffy) >= Math.abs(fdiffx)/threshold)
			return true;
		return false;
	}
	
	private boolean isHorz(int diffx, int diffy, float threshold) {
		float fdiffx = (float)diffx;
		float fdiffy = (float)diffy;
		if (Math.abs(fdiffx) >= Math.abs(fdiffy)/threshold)
			return true;
		return false;		
	}
	
	private boolean areColinear(Node n1, Node n2, Node n3, float threshold) {
		
		// if abs(y2-y1) > abs(x2-x1)/threshold
		// is vertical
		// if abs(x2-x1) > abs(y2-y1)/threshold
		// is horz
		
		int xdiff12 = n1.getX()-n2.getX();
		int ydiff12 = n1.getY()-n2.getY();
		int xdiff23 = n2.getX() - n3.getX();
		int ydiff23 = n2.getY() - n3.getY();

		if (isVert(xdiff12, ydiff12, threshold)) {			
			return isVert(xdiff23, ydiff23, threshold);
		}
		
		if (isHorz(xdiff12, ydiff12, threshold)) {
			return isHorz(xdiff23, ydiff23, threshold);
		}
		
		if (Math.abs(xdiff12) > Math.abs(ydiff12)) {
			if (Math.abs(xdiff23) > Math.abs(ydiff23)) {
				float slope12 = ydiff12/xdiff12;
				float slope23 = ydiff23/xdiff23;
				float slopediff = slope12-slope23;
				return (Math.abs(slopediff) <= threshold*slope12);				   
			}
			return false;
		}
		if (Math.abs(ydiff23) > Math.abs(xdiff23)) {
			float slope12 = xdiff12/ydiff12;
			float slope23 = xdiff23/ydiff23;
			float slopediff = slope12-slope23;
			return (Math.abs(slopediff) <= threshold*slope12);
		}
        return false;		
		
	}
	
	private Node findNeighbor(Node n, Node skip, NetworkModel nm) {
		List<Integer> neighbors = n.getNeighbors();
		for (Integer i : neighbors) {
			if (i == skip.getId())
				continue;
			return nm.getNode(i);
		}
		return null;
	}
	
	private Node findClosestNeighbor(Node a, int skip, NetworkModel nm) {
		long leastDist = Long.MAX_VALUE;
		int leastIndex = 0;
		Node skipNode = null;
		if (skip >= 0)
			skipNode = nm.getNode(skip);
		List<Integer> neighbors = a.getNeighbors();
		for (Integer i : neighbors) {
			if (skip == i) {
				System.out.println("skip = i");
				continue;
			}
			Node nb = nm.getNode(i);
			if (skipNode != null) {
			    if (areColinear(a, skipNode, nb, 1000000.0f))
			    {
			       System.out.println("colinear");
				   continue;
			    }
			}
			int diffx = nb.getX() - a.getX();
			int diffy = nb.getY() - a.getY();
			long distSq = diffx*diffx + diffy*diffy;
			if (distSq < leastDist) {
				leastIndex = i;
				leastDist = distSq;
			}
		}
		return nm.getNode(leastIndex);
	}
	
	public void init(Node a, int id, NetworkModel nm) {
		this.a = a;
		this.id = id;
		
//		b = findClosestNeighbor(a, -1, nm);
//		c = findClosestNeighbor(b, a.getId(), nm);
		b = findNeighbor(a, a, nm);
		c = findNeighbor(b, a, nm);
		
		System.out.println("a = " + a.getId() + ", b = " + b.getId() + ", c = " + c.getId());
		System.out.println("a: (" + a.getX() + "," + a.getY() + ")");
		System.out.println("b: (" + b.getX() + "," + b.getY() + ")");
		System.out.println("c: (" + c.getX() + "," + c.getY() + ")");
		
		String propName = "tid_" + id;
		
		a.putProp(propName, new IntProp(id));
		b.putProp(propName, new IntProp(id));
		c.putProp(propName, new IntProp(id));
	}
	
	public Node getNodeA() {
		return a;
	}
	
	public Node getNodeB() {
		return b;
	}
	
	public Node getNodeC() {
		return c;
	}

}
