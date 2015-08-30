package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;

import rect.NetworkModel;
import rect.Node;

public class Geometry {
	
	static class AngleComparator implements Comparator<NodeAndAngle> {

		@Override
		public int compare(NodeAndAngle o1, NodeAndAngle o2) {
			if (o1.angle < o2.angle)
				return -1;
			if (o1.angle == o2.angle)
				return 0;
			return 1;
		}
		
	}
	
	public static class Gradient2D {
		private final double deltaX;
		private final double deltaY;
		
		public Gradient2D(double deltaX, double deltaY) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}
		
		public double getDeltaX() {
			return deltaX;
		}
		
		public double getDeltaY() {
			return deltaY;
		}
	}
	
	final static double TwoPi = 2.0*Math.PI;
	
	public static boolean tooSmall(double num, double denom, double alpha) {
		// |num/denom| < alpha
		// num < alpha*denom
		return Math.abs(num) > Math.abs(alpha*denom);
	}
	
	static class IntersectData {
		public int minxa;
		public int maxxa;
		public int ya1;
		public int ya2;
		
		public int minxb;
		public int maxxb;
		public int yb1;
		public int yb2;
		
		double diffxa;
		double diffya;
		double diffxb;
		double diffyb;
		
		double Ma;
		double Ba;
		double Mb;
		double Bb;
		
		public IntersectData(Node a1, Node a2, Node b1, Node b2) {
	        if (a1.getX() <= a2.getX()) {
	        	minxa = a1.getX();
	        	maxxa = a2.getX();
	        	ya1 = a1.getY();
	        	ya2 = a2.getY();
	        	diffya = ya2 - ya1;
	        }
	        else {
	        	minxa = a2.getX();
	        	maxxa = a1.getX();
	        	ya1 = a2.getY();
	        	ya2 = a1.getY();
	        	diffya = ya1 - ya2;
	        }
 
	        
	        
//	        int minxb;
//	        int maxxb;
//	        int yb1;
//	        int yb2;
	        
	        if (b1.getX() <= b2.getX()) {
	        	minxb = b1.getX();
	        	maxxb = b2.getX();
	        	yb1 = b1.getY();
	        	yb2 = b2.getY();
	        }
	        else {
	        	minxb = b2.getX();
	        	maxxb = b1.getX();
	        	yb1 = b2.getY();
	        	yb2 = b1.getY();
	        }
	        
	        diffxa = maxxa - minxa;
	        diffya = ya2 - ya1;
	        diffxb = maxxb - minxb;
	        diffyb = yb2 - yb1;
	        
	        if (Math.abs(diffya) < diffxa) {
	        	Ma = diffya/diffxa;
	        	Ba = ya1 - Ma*minxa;
	        }
	        else {
	        	Ma = diffxa/diffya;
	        	Ba = minxa - Ma*ya1;
	        }

	        if (Math.abs(diffyb) < diffxb) {
	        	Mb = diffyb/diffxb;
	        	Bb = yb1 - Mb*minxb;
	        }
	        else {
	        	Mb = diffxb/diffyb;
	        	Bb = minxb - Mb*yb1;
	        }
			
		}
		
	}
	
	public static boolean areColinear(Node a1, Node a2, Node b1, Node b2) {

		IntersectData ixd = new IntersectData(a1, a2, b1, b2);
		
        if (Math.abs(ixd.diffya) < ixd.diffxa) {        	
        	if (Math.abs(ixd.diffyb) < ixd.diffxb) {
        		if (ixd.diffya > 0) {
        			if (ixd.diffyb <= 0) {
        				return false;
        			}
        			
        		}
        		else {
        			if (ixd.diffyb > 0) {
        				return false;
        			}
        		}
        		double Ma = ixd.diffya/ixd.diffxa;
        		double Mb = ixd.diffyb/ixd.diffxb;
        		
        		double diffM = Math.abs(Mb - Ma);
        		double avgM = Math.abs(0.5*(Ma + Mb));
        		
        		if (100000000.0*diffM > avgM) {
        			return false;
        		}
        		
        		double Ba = ixd.ya1 - Ma*ixd.minxa;
        		double Bb = ixd.yb1 - Mb*ixd.minxb;
        		
        		double diffB = Math.abs(Bb - Ba);
        		double avgB = Math.abs(0.5*(Ba + Bb));
        		
        		if (100000000.0*diffB > avgB) {
        			return false;
        		}
        		
        		if (ixd.minxb <= ixd.minxa) {
        			return (ixd.maxxb >= ixd.minxa);
        		}
        		return (ixd.minxb <= ixd.maxxa);
        		
        	}
        	else {
        		return false;
        	}
        }
        else {
        	if (Math.abs(ixd.diffyb) >= ixd.diffxb) {
        		if (ixd.diffya > 0) {
        			if (ixd.diffyb <= 0) {
        				return false;
        			}
        		}
        		else {
        			if (ixd.diffyb > 0) {
        				return false;
        			}
        		}
        		
        		double Ma = ixd.diffxa/ixd.diffya;
        		double Mb = ixd.diffxb/ixd.diffyb;
        		
        		double diffM = Math.abs(Mb - Ma);        		
        		double avgM = Math.abs(0.5*(Ma + Mb));
        		
        		if (100000000.0*diffM > avgM) {
        			return false;
        		}
        		
        		double Ba = ixd.minxa - Ma*ixd.ya1;
        		double Bb = ixd.minxb - Mb*ixd.yb1;
        		
        		double diffB = Math.abs(Bb - Ba);
        		double avgB = Math.abs(0.5*(Ba + Bb));
        		
        		if (100000000.0*diffB > avgB) {
        			return false;
        		}

        		if (ixd.yb1 <= ixd.ya1) {
        			return (ixd.yb2 >= ixd.ya1);
        		}
        		return (ixd.yb1 <= ixd.ya2);

        	}
        	else
        	{
        		return false;
        	}
        }

	}

	static class SegDebugInfo
	{
		public int a1id;
		public int a1x;
		public int a1y;
		
		public int a2id;
		public int a2x;
		public int a2y;
		
		public int b1id;
		public int b1x;
		public int b1y;
		
		public int b2id;
		public int b2x;
		public int b2y;
		
		public String meth;
		public boolean didIntersect;
	}
		
	public static boolean doIntersect(Node a1, Node a2, Node b1, Node b2) {
		
		if ((a1.getId() == 7 && a2.getId() == 5) || (a1.getId() == 5 && a2.getId() == 7)) {
//			if ((b1.getId() == 0 && b2.getId() == 6) || (b1.getId() == 6 && b2.getId() == 0)) {
				System.out.println("culprit");
//			}
		}
		
        if (a1.getX() == b1.getX() && a1.getY() == b1.getY())
        	return false;
        if (a2.getX() == b1.getX() && a2.getY() == b1.getY())
        	return false;
        if (a1.getX() == b2.getX() && a1.getY() == b2.getY())
        	return false;
        if (a2.getX() == b2.getX() && a2.getY() == b2.getY())
        	return false;
		
		IntersectData ixd = new IntersectData(a1, a2, b1, b2);
		
        if (Math.abs(ixd.diffya) < ixd.diffxa) {        	
        	if (Math.abs(ixd.diffyb) < ixd.diffxb) {
        		boolean bi = doIntersectHH(ixd.diffxa, ixd.diffya,
        				             ixd.minxa, ixd.ya1,
        				             ixd.diffxb, ixd.diffyb,
        				             ixd.minxb, ixd.yb1,
        				             ixd.maxxb, ixd.yb2);
        		return bi;
        	}
        	else {
        		boolean bi = doIntersectHV(ixd.diffxa, ixd.diffya, 
        				             ixd.minxa, ixd.ya1,
        				             ixd.diffxb, ixd.diffyb,
        				             ixd.minxb, ixd.yb1,
        				             ixd.maxxb, ixd.yb2);
        		return bi;
        	}
        }
        else {
        	if (Math.abs(ixd.diffyb) >= ixd.diffxb) {
        		boolean bi =  doIntersectVV(ixd.diffxa, ixd.diffya, 
        				             ixd.minxa, ixd.ya1,
        				             ixd.diffxb, ixd.diffyb,
        				             ixd.minxb, ixd.yb1,
        				             ixd.maxxb, ixd.yb2);
        		return bi;        				 
        	}
        	else {
        		boolean bi = doIntersectVH(ixd.diffxa, ixd.diffya,
        				             ixd.minxa, ixd.ya1,
        				             ixd.maxxa, ixd.ya2,
        				             ixd.minxb, ixd.yb1,
        				             ixd.maxxb, ixd.yb2);
        		return bi;
        	}
        }
		
	}
	
	public static boolean doIntersect2(Node a1, Node a2, Node b1, Node b2) {
		
        if (a1.getX() == b1.getX() && a1.getY() == b1.getY())
        	return false;
        if (a2.getX() == b1.getX() && a2.getY() == b1.getY())
        	return false;
        if (a1.getX() == b2.getX() && a1.getY() == b2.getY())
        	return false;
        if (a2.getX() == b2.getX() && a2.getY() == b2.getY())
        	return false;
		
		IntersectData ixd = new IntersectData(a1, a2, b1, b2);
		
        if (Math.abs(ixd.diffya) < ixd.diffxa) {        	
        	if (Math.abs(ixd.diffyb) < ixd.diffxb) {
        		return doIntersectHH2(ixd);
        	}
        	else {
        		return doIntersectHV2(ixd);
        	}
        }
        else {
        	if (Math.abs(ixd.diffyb) >= ixd.diffxb) {
        		return doIntersectVV2(ixd);				 
        	}
        	else {
        		return doIntersectVH2(ixd);
        	}
        }
		
	}
	
	private static boolean doIntersectHH(double diffxa, double diffya,
			                             int xa1, int ya1,
			                             double diffxb, double diffyb,
			                             int xb1, int yb1, 
			                             int xb2, int yb2) {
		double Ma = diffya/diffxa;
		double Ba = ya1 - Ma*xa1;
		
		double Mb = diffyb/diffxb;
		
		if (Ma == Mb)
			return false;
		
		if (Mb < Ma) {
			return (yb1 >= Ma*xb1 + Ba) && (yb2 <= Ma*xb2 + Ba);
		}
		
		return (yb1 <= Ma*xb1 + Ba) && (yb2 >= Ma*xb2 + Ba);
	}
	
	private static boolean doIntersectHH2(IntersectData ixd) {
		
		double num = ixd.Bb - ixd.Ba;
		double denom = ixd.Ma - ixd.Mb;
		if (Geometry.tooSmall(num, denom, 1000000000.0)) {
			return false;
		}
		
		double xInt = num/denom;
		
		return ((ixd.minxa <= xInt) && (xInt <= ixd.maxxa) && (ixd.minxb <= xInt) && (xInt <= ixd.maxxb));
	}
	
	
	private static boolean doIntersectHV(double diffxa, double diffya,
			                             int xa1, int ya1,
			                             double diffxb, double diffyb,
			                             int xb1, int yb1,
			                             int xb2, int yb2) {
		double Ma = diffya/diffxa;
		double Ba = ya1 - Ma*xa1;
		double y_at_xb1 = Ma*xb1 + Ba;
		
		if (diffyb < 0) {
			if (xb1 == xb2) {
				return (yb1 >= y_at_xb1) && (yb2 <= y_at_xb1);
			}
			return (yb1 >= y_at_xb1) && (yb2 <= Ma*xb2 + Ba);
		}
		else {
			if (xb1 == xb2) {
				return (yb1 <= y_at_xb1) && (yb2 >= y_at_xb1);
			}
			return (yb1 <= y_at_xb1) && (yb2 >= Ma*xb2 + Ba);
		}
	}
	
	private static boolean doIntersectHV2(IntersectData ixd) {
        
		double num = ixd.Ma*ixd.Bb + ixd.Ba;
		double denom = 1 - ixd.Ma*ixd.Mb;
		
		if (Geometry.tooSmall(num, denom, 1000000000.0))
			return false;
		
		double yInt = num/denom;
		double xInt = ixd.Mb*yInt + ixd.Bb;
		if (ixd.diffyb < 0) {
			return ((ixd.minxa <= xInt) && (xInt <= ixd.maxxa) && (ixd.yb1 >= yInt) && (ixd.yb2 <= yInt));
		}
		return ((ixd.minxa <= xInt) && (xInt <= ixd.maxxa) && (ixd.yb1 <= yInt) && (ixd.yb2 >= yInt));
	}

	
	private static boolean doIntersectVV(double diffxa, double diffya,
			                             int xa1, int ya1,
			                             double diffxb, double diffyb,
			                             int xb1, int yb1,
			                             int xb2, int yb2) {
		// degenerate case
		if (diffya == 0)
			return false;
		
		double Ma = diffxa/diffya;
		double Ba = xa1 - Ma*ya1;
		
		if (diffxb == 0) {
			if (yb1 == yb2)
				return false;
			
			if (yb1 >= yb2) {
				return (xb1 <= Ma*yb1 + Ba) && (xb2 >= Ma*yb2 + Ba);
			}
			else {
				return (xb1 >= Ma*yb1 + Ba) && (xb2 <= Ma*yb2 + Ba);
			}
		}
		
		double Mb = diffxb/diffyb;
		
		if (Ma == Mb)
			return false;
		
		if (Mb <= Ma) {
			return (xb1 <= Ma*yb1 + Ba) && (xb2 >= Ma*yb2 + Ba);
		}
		else {
			return (xb1 >= Ma*yb1 + Ba) && (xb2 <= Ma*yb2 + Ba);
		}
	}
	
	private static boolean doIntersectVV2(IntersectData ixd) {

		double denom = ixd.Ma - ixd.Mb;
		double num = ixd.Bb - ixd.Ba;
		if (Geometry.tooSmall(num,  denom,  1000000000.0)) {
			return false;
		}
		
		double yInt = num/denom;
		
		if (ixd.ya1 < ixd.ya2) {
			if (ixd.ya1 > yInt || ixd.ya2 < yInt)
				return false;
			if (ixd.yb1 < ixd.yb2)
				return (ixd.yb1 <= yInt && ixd.yb2 >= yInt);
			else
				return (ixd.yb1 >= yInt && ixd.yb2 <= yInt);
		}
		else {
			if (ixd.ya1 < yInt || ixd.ya2 > yInt)
				return false;
			if (ixd.yb1 < ixd.yb2)
				return (ixd.yb1 <= yInt && ixd.yb2 >= yInt);
			else
				return (ixd.yb1 >= yInt && ixd.yb2 <= yInt);

		}
		
	}
	
	private static boolean doIntersectVH(double diffxa, double diffya,
			                             int xa1, int ya1,
			                             int xa2, int ya2,
			                             int xb1, int yb1,
			                             int xb2, int yb2) {
        if (ya1 == ya2) {
        	return false;
        }
		
		if (xa1 == xa2) {
			return (xb1 <= xa1) && (xb2 >= xa1);
		}
		
		double Ma = diffxa/diffya;
		double Ba = xa1 - Ma*ya1;
		
		return (xb1 <= Ma*yb1 + Ba) && (xb2 >= Ma*yb2 + Ba);
	}
	
	private static boolean doIntersectVH2(IntersectData ixd) {
		// x = May + Ba
		// y = Mbx + Bb
		// Mbx = MbMay + MbBa
		// -Mbx = Bb - y
		// 0 = (MbMa - 1)y + MbBa + Bb
		// y(MbMa - 1) = -MbBa - Bb
		// y(1 - MbMa) = MbBa + Bb
		// y = (MbBa + Bb)/(1 - MbMa)
		double num = ixd.Mb*ixd.Ba + ixd.Bb;
		double denom = 1 - ixd.Mb*ixd.Ma;
		
		if (Geometry.tooSmall(num, denom, 1000000000.0))
			return false;
		
		double yInt = num/denom;
		double xInt = ixd.Ma*yInt + ixd.Ba;
		return (ixd.minxa <= xInt) && (ixd.maxxa >= xInt) && (ixd.minxb <= xInt && ixd.maxxb >= xInt);
	}
	
	
	private static int getRegionCol(Node n, NetworkModel.GenerateBuilder gb) {
		if (n.getX() < 0)
			return -1;
		int i = (int)(gb.regionFactor*n.getX());
		if (i >= gb.numRegionCols)
			return -1;
		return i;
	}
	
	private static int getRegionRow(Node n, NetworkModel.GenerateBuilder gb) {
		if (n.getY() < 0)
			return -1;
		int i = (int)(gb.regionFactor*n.getY());
		if (i >= gb.numRegionRows)
			return -1;
		return i;		
	}
	
    private static int pickBetween(int lowest, int highest, boolean pickEven, Random r) {
    	if (lowest > highest)
    		return highest;
		while (true) {
			int n = r.nextInt(highest+1);
			if (n < lowest)
				continue;
			if (pickEven) {
			    if (n % 2 == 0)
				    return n;
			}
			else
			{
				if (n % 2 != 0)
					return n;
			}
		}
    }
    
    private static double modRotate(double startAngle, double increment) {
    	if (increment >= 0) {
    		while(startAngle + increment >= TwoPi) {
    			startAngle -= TwoPi;
    		}
    	}
    	else {
    	    while(startAngle + increment < 0) {
    		    startAngle += TwoPi;
    	    }
    	}
    	return startAngle + increment;
    }
    
    private static boolean containsNode(Node n, List<Node> nodes) {
    	if (n == null)
    		return false;
    	for (Node nn : nodes) {
    		if (nn == null)
    			continue;
    		if (nn.getId() == n.getId())
    			return true;
    	}
    	return false;
    }
    
    private static boolean isIdPair(Node a1, Node a2, int id1, int id2) {
    	return (a1.getId() == id1 && a2.getId() == id2) || (a2.getId() == id1 && a1.getId() == id2);
    }
    
    private static boolean isSegment(NetworkModel.Segment s, int id1, int id2) {
    	return (s.a == id1 && s.b == id2) || (s.b == id1 && s.a == id2);
    }
    
    private static boolean touchesASegment(Node a1, Node a2, List<NetworkModel.Segment> segments, NetworkModel nm) {
    	for (NetworkModel.Segment s : segments) {
    		Node b1 = nm.getNode(s.a);
    		Node b2 = nm.getNode(s.b);
    		if (Geometry.isIdPair(a1, a2, 13, 88)) {
    			if (Geometry.isSegment(s, 1, 6))
    				System.out.println("hoo");
    		}
    		if (doIntersect2(a1, a2, b1, b2))
    			return true;
    		if (areColinear(a1, a2, b1, b2))
    			return true;
    	}
    	return false;
    }
    
    private static boolean angleFitsBetween(double angle, double leastAngle, double mostAngle) {
    	while (angle <= mostAngle) {
    		if (leastAngle <= angle && angle <= mostAngle)
    			return true;
    		angle += Math.PI*2.0;
    	}
    	return false;
    }
    
	public static double findAngle(Node from, Node to) {
		int xdiff = to.getX() - from.getX();
		int ydiff = from.getY() - to.getY(); // remember, y is flipped on screen
		
		if (xdiff == 0) {
			if (ydiff == 0) {
				return 0.0;
			}
			return ydiff > 0 ? 0.5*Math.PI : 1.5*Math.PI;
		}
		
		if (ydiff == 0) {
			return xdiff > 0 ? 0.0 : Math.PI;
		}
		
		double dxdiff = (double)xdiff;
		double dydiff = (double)ydiff;
		double hyp = Math.sqrt(xdiff*xdiff + ydiff*ydiff);
		if (dxdiff > 0) {
			if (dydiff > 0) {
				if (dydiff < dxdiff) {
					return Math.asin(dydiff/hyp);
				}
				else {
					return 0.5*Math.PI - Math.asin(dxdiff/hyp);
				}
			}
			else {
				if ((-dydiff) < dxdiff) {
					return 2*Math.PI + Math.asin(dydiff/hyp);
				}
				else {
					return 1.5*Math.PI + Math.asin(dxdiff/hyp);
				}
			}
		}
		else {
			if (dydiff > 0) {
				if (dydiff < (-dxdiff)) {
					return Math.PI - Math.asin(dydiff/hyp);
				}
				else {
					return 0.5*Math.PI - Math.asin(dxdiff/hyp);
				}
			}
			else {
				if ((-dydiff) < (-dxdiff)) {
					return Math.PI - Math.asin(dydiff/hyp);
				}
				else {
					return 1.5*Math.PI + Math.asin(dxdiff/hyp);
				}
			}
		}
	}
    
    public static double findAngle(Gradient2D grad) {
    	if (Math.abs(grad.getDeltaX()) >= Math.abs(grad.getDeltaY())) {
    		if (grad.getDeltaX() == 0) {
    			return 0.0;
    		}
    		if (grad.getDeltaX() > 0) {
    			if (grad.getDeltaY() >= 0)
    			    return Math.atan(grad.getDeltaY()/grad.getDeltaX());
    			else
    				return TwoPi + Math.atan(grad.getDeltaY()/grad.getDeltaX());
    		}
    		else {
    			return Math.PI + Math.atan(grad.getDeltaY()/grad.getDeltaX());
    		}
    	}
    	else {
    		if (grad.getDeltaY() > 0) {
    			return 0.5*Math.PI - Math.atan(grad.getDeltaX()/grad.getDeltaY());
    		}
    		else {
    			return 1.5*Math.PI - Math.atan(grad.getDeltaX()/grad.getDeltaY());
    		}
    	}
    }
    
    private static double normalizeAngle(double angle) {
    	double twopi = 2.0*Math.PI;
        while(angle < 0.0) {
    		angle += twopi;
    	}
    	while(angle > twopi){
    		angle -= twopi;
    	}
    	return angle;
    }
    
    // --------------------------------------------------------------------
    
    public static double genAngle(double nominalAngle, double maxVariance, Random r) {
		double variance = maxVariance*r.nextDouble();
		if (r.nextBoolean())
			variance *= -1.0;
		return nominalAngle + variance; 	
    }
    
    public static void linkNodes(Node a, Node b) {
    	a.addNeighbor(b.getId());
    	b.addNeighbor(a.getId());     
    }
   
    public static NetworkModel generateFirstNeighbors(NetworkModel nm, long framecount) {
    	NetworkModel.NewAndSourceNodes nsn = nm.getNewAndSourceNodes();
    	if (nsn.newNodes.isEmpty())
    		return nm;
    	Node firstNode = nsn.newNodes.get(0);
    	
    	nm.clearNewAndSourceNodes();
    	
    	NetworkModel.GenerateBuilder gb = nm.getGenerateBuilder();
    	Random r = nm.getRandom();
    	int numSpokes = Geometry.pickBetween(4, gb.maxEdgesPerNode, true, r);
    	double avgAngle = 2.0*Math.PI/numSpokes;
    	double maxVariance = 0.1*avgAngle;
    	for (int i = 0; i < numSpokes; i++) {
    		double angle = genAngle(i*avgAngle, maxVariance, r);
    		double length = Util.doubleBetween(gb.minDistance, gb.maxDistance, r);
    		int newX = firstNode.getX() + (int)(Math.cos(angle)*length);
    		int newY = firstNode.getY() + (int)(Math.sin(angle)*length);
    		Node newNode = new Node(i+1, framecount, nm);
    		newNode.setX(newX);
    		newNode.setY(newY);
    		nm.addNewAndSourceNode(newNode, firstNode);
    		nm.addNewSegment(newNode, firstNode);
    		linkNodes(firstNode, newNode);
    	}
    	
    	return nm;
    }
    
    public static NetworkModel generateFirstNeighbors2(NetworkModel nm, long framecount) {
    	NetworkModel.NewAndSourceNodes nsn = nm.getNewAndSourceNodes();
    	if (nsn.newNodes.isEmpty())
    		return nm;
    	
    	List<Node> firstNodes = nsn.newNodes;
    	
    	nm.clearNewAndSourceNodes();
    	
    	NetworkModel.GenerateBuilder gb = nm.getGenerateBuilder();
    	Random r = nm.getRandom();
    	
    	for (Node firstNode : firstNodes) {
    	
    	    int numSpokes = Geometry.pickBetween(4, gb.maxEdgesPerNode, true, r);
    	    double avgAngle = 2.0*Math.PI/numSpokes;
    	    double maxVariance = 0.1*avgAngle;
    	    for (int i = 0; i < numSpokes; i++) {
    		    double angle = genAngle(i*avgAngle, maxVariance, r);
    		    double length = Util.doubleBetween(gb.minDistance, gb.maxDistance, r);
    		    int newX = firstNode.getX() + (int)(Math.cos(angle)*length);
    		    int newY = firstNode.getY() + (int)(Math.sin(angle)*length);
    		    Node newNode = new Node(nm.getNextId(), framecount, nm);
    		    newNode.setX(newX);
    		    newNode.setY(newY);
    		    nm.addNewAndSourceNode(newNode, firstNode);
    		    nm.addNewSegment(newNode, firstNode);
    		    linkNodes(firstNode, newNode);
    	    }
    	
    	}
    	
    	return nm;
    	
    }
    
    private static int findNumNewNeighbors(Node newNode, NetworkModel nm) {
    	int currentNumNeighbors = newNode.getNeighbors().size();
    	boolean pickEven = currentNumNeighbors % 2 == 0;
    	int numNewNeighbors = 0;
    	if (currentNumNeighbors < 4)
    		numNewNeighbors = Geometry.pickBetween((4 - currentNumNeighbors), nm.getGenerateBuilder().maxEdgesPerNode, pickEven, nm.getRandom());
    	else
    		numNewNeighbors = Geometry.pickBetween(0, (nm.getGenerateBuilder().maxEdgesPerNode - currentNumNeighbors), pickEven, nm.getRandom());
    	return numNewNeighbors;
    }
    
    public static List<Node> filterByMaxDistSquared(Node n, List<Node> nodes, double maxDistSq) {
    	List<Node> ret = new ArrayList<Node>();
    	for (Node node : nodes) {
    		double diffx = node.getX() - n.getX();
    		double diffy = node.getY() - n.getY();
    		double distSq = (diffx*diffx) + (diffy*diffy);
    		if (distSq <= maxDistSq)
    			ret.add(node);
    	}
    	return ret;
    }
    
    private static void addToNodesAndAngles(Node n, List<Node> nearbies, List<NodeAndAngle> nans) {
    	for (Node nrb : nearbies) {
    		double angle = findAngle(n, nrb);
    		angle = normalizeAngle(angle);
    		NodeAndAngle nan = new NodeAndAngle(nrb, angle);
    		nans.add(nan);
    	}
    }
/*    
    private static List<Integer> filterOutDuplicates(List<Integer> list, Set<Integer> alreadySeen) {
    	List<Integer> ret = new ArrayList<Integer>();
    	for (Integer i : list) {
    		if (alreadySeen.contains(i))
    			continue;
    		alreadySeen.add(i);
    		ret.add(i);
    	}
    	return ret;
    }
*/  
    private static List<Node> filterOutDuplicates(List<Node> list, Set<Integer> alreadySeen) {
    	List<Node> ret = new ArrayList<Node>();
    	for (Node n : list) {
    		if (alreadySeen.contains(n.getId()))
    			continue;
    		alreadySeen.add(n.getId());
    		ret.add(n);
    	}
    	return ret;
    }
    
    private static List<Integer> filterOutSelf(Node n, List<Integer> indexes) {
    	List<Integer> ret = new ArrayList<Integer>();
    	for (Integer i : indexes) {
    		if (i != n.getId())
    			ret.add(i);
    	}
    	return ret;
    }
    
    private static List<NodeAndAngle> findAngleSortedNearbyNodes(Node node, NetworkModel nm) {
    	List<NodeAndAngle> ret = new ArrayList<NodeAndAngle>();
    	NetworkModel.GenerateBuilder gb = nm.getGenerateBuilder();
    	int row = getRegionRow(node, gb);
    	if (row < 0)
    		return ret;
    	int col = getRegionCol(node, gb);
    	if (col < 0)
    		return ret;
    	
    	int rowMin = Math.max(0, row-1);
    	int rowMax = Math.min(row+1, gb.numRegionRows-1);
    	int colMin = Math.max(0, col-1);
    	int colMax = Math.min(col+1, gb.numRegionCols-1);
    	
    	Set<Integer> alreadySeen = new HashSet<Integer>();
    	
    	for (int i = rowMin; i <= rowMax; i++)
    		for (int j = colMin; j <= colMax; j++) {
    			List<Integer> nodeIndexesInRegion = gb.nodeRegions[i][j];
    			nodeIndexesInRegion = filterOutSelf(node, nodeIndexesInRegion);
    			List<Node> nodesInRegion = nm.getNodesFromIds(nodeIndexesInRegion);
    			List<Node> nearbyNodesInRegion = Geometry.filterByMaxDistSquared(node, nodesInRegion, gb.maxDistanceSquared);
                nearbyNodesInRegion = filterOutDuplicates(nearbyNodesInRegion, alreadySeen);
    			addToNodesAndAngles(node, nearbyNodesInRegion, ret);
    		}
    	
    	Collections.sort(ret, new AngleComparator());
    	
    	return ret;
    }
    
    private static int addConnectionsAround(Node node, List<NodeAndAngle> nearbySorted,
    		                                 NetworkModel.RegionCell rc,
    		                                 double startAngle, int maxToFind, NetworkModel nm) {
    	if (maxToFind <= 0)
    		return 0;
    	int startIndex = nearbySorted.size()/2;
    	double minDistance = Float.MAX_VALUE;
    	for (int i = 0; i < nearbySorted.size(); i++) {
    		double dist = Math.abs(nearbySorted.get(i).angle - startAngle);
    		if (dist < minDistance) {
    			minDistance = dist;
    			startIndex = i;
    		}
    	}
    	
    	int numConnections = 0;
    	int posGoingIndex = startIndex;
    	int negGoingIndex = startIndex-1;
    	
    	int minRow = Math.max(rc.row-1, 0);
    	int maxRow = Math.min(rc.row+1, nm.getGenerateBuilder().numRegionRows-1);
    	int minCol = Math.max(rc.col-1, 0);
    	int maxCol = Math.min(rc.col+1, nm.getGenerateBuilder().numRegionCols-1);
    	
    	boolean checkPos = true;
    	while(negGoingIndex >= 0 || posGoingIndex < nearbySorted.size()) {
    		
    		if (numConnections >= maxToFind)
    			return numConnections;
    		
    		Node cand = null;
    		if (checkPos) {
    			if (posGoingIndex < nearbySorted.size()) {
    				Node temp = nearbySorted.get(posGoingIndex).node;
    				if (temp.getId() != node.getId())
    				   cand = temp;   					
    				++posGoingIndex;
    			}
    			checkPos = false;
    		}
    		else {
    			if (negGoingIndex >= 0) {
    				Node temp = nearbySorted.get(negGoingIndex).node;
    				if (temp.getId() != node.getId())
     				   cand = temp;   					
    				--negGoingIndex;
    			}
    			checkPos = true;
    		}
    		if (cand == null)
    			return numConnections;

    		boolean isOk = true;
    		for (int i = minRow; i <= maxRow; i++) {
    			for (int j = minCol; j <= maxCol; j++) {
    				List<NetworkModel.Segment> segments = nm.getGenerateBuilder().segmentRegions[i][j];
    				if (touchesASegment(node, cand, segments, nm)) {
    					isOk = false;
    					break;
    				}
    			}
    			if (!isOk)
    				break;
    		}
    		if (isOk) {
    			
    			nm.getGenerateBuilder().addSegment(node, cand);
    			nm.addNewSegment(cand, node);
    			NetworkModel.linkNodes(cand, node);
    			numConnections++;
    		}
    		
    	}
    	
    	return numConnections;
    }
    
    private static double randPickDoubleBetween(double low, double high, Random r) {
    	double f = r.nextDouble();
    	return low*(1.0 - f) + f*high;
    }
    
    private static void generateUnconnectedNeighbors(Node node, 
    		                                         NetworkModel.RegionCell rc, 
    		                                         double gradAngle, 
    		                                         int numToGen, 
    		                                         NetworkModel nm,
    		                                         long framecount) {
    	if (numToGen <= 0)
    		return;
    	Random r = nm.getRandom();
    	double startAngle = gradAngle - 0.5*Math.PI;
    	double avgAngle = Math.PI/numToGen;
    	double maxVariance = 0.1*avgAngle;
    	
    	int minRow = Math.max(rc.row-1, 0);
    	int maxRow = Math.min(rc.row+1, nm.getGenerateBuilder().numRegionRows-1);
    	int minCol = Math.max(rc.col-1, 0);
    	int maxCol = Math.min(rc.col+1, nm.getGenerateBuilder().numRegionCols-1);

    	int id = nm.getNextId();
    	
    	for (int i = 0; i < numToGen; i++) {
    		double angleVar = r.nextDouble()*maxVariance;
    		double angle = startAngle + i*avgAngle + angleVar;
    		double maxLength = nm.getGenerateBuilder().maxDistance;
    		double minLength = nm.getGenerateBuilder().minDistance;
   // 		double length = randPickDoubleBetween(0.25*maxLength, maxLength, r);
    		double length = Util.doubleBetween(minLength, maxLength, r);
    		boolean ok = true;
    		Node newNode = null;
    		for (int j = 0; j < 4; j++) {
    			ok = true;
    			int x = node.getX() + (int)(length*Math.cos(angle));
    			int y = node.getY() + (int)(length*Math.sin(angle));
    			newNode = new Node(id, framecount, nm);
    			newNode.setX(x);
    			newNode.setY(y);
    			for (int m = minRow; m <= maxRow; m++) {
    				for (int n = minCol; n <= maxCol; n++) {
    					List<NetworkModel.Segment> segments = nm.getGenerateBuilder().segmentRegions[m][n];
    					if (Geometry.touchesASegment(node, newNode, segments, nm)) {
    						newNode = null;
    						length *= 0.5;
    						ok = false;
    						break;
    					}
    				}
    				if (!ok)
    					break;
    			}
    			if (length < minLength)
    				break;
    		}
    		if (newNode != null) {
    			nm.addNewAndSourceNode(newNode, node);
    			NetworkModel.linkNodes(node, newNode);
    			nm.addNewSegment(newNode, node);
    			id = nm.getNextId();
    		}
    		
    	}
    }
    
    private static void generateNeighbors(Node newNode, Node srcNode, NetworkModel nm, long framecount) {
    	NetworkModel.RegionCell rc = nm.getGenerateBuilder().findRegionCell(newNode);
    	if (!rc.valid)
    		return;
    	
    	Gradient2D grad = Geometry.findGradientAt(newNode, nm);
    	double gradAngle = findAngle(grad);
    	List<NodeAndAngle> nearbyNodes = Geometry.findAngleSortedNearbyNodes(newNode, nm);
    	double startAngle = normalizeAngle(gradAngle + Math.PI);
    	int currentNumNeighbors = newNode.getNeighbors().size();
    	int maxToFind = nm.getGenerateBuilder().maxEdgesPerNode - currentNumNeighbors;
    	int numConn = Geometry.addConnectionsAround(newNode, nearbyNodes, rc, startAngle, maxToFind, nm);
    	
    	int maxToGenerate = nm.getGenerateBuilder().maxEdgesPerNode - numConn - currentNumNeighbors;
    	Geometry.generateUnconnectedNeighbors(newNode, rc, gradAngle, maxToGenerate, nm, framecount);
    	
    }
    
    public static NetworkModel generateNeighbors(NetworkModel nm, long framecount) {
    	NetworkModel.NewAndSourceNodes nsn = nm.getNewAndSourceNodes();
    	nm.clearNewAndSourceNodes();
    	nm.clearNewSegments();
    	for (int i = 0; i < nsn.newNodes.size(); i++) {
    		Node newNode = nsn.newNodes.get(i);
    		Node srcNode = nsn.srcNodes.get(i);
    		if (newNode.getDensity() < nm.getMaxDensity())
    		    generateNeighbors(newNode, srcNode, nm, framecount);
    	}
    	return nm;
    }
    
	public static Gradient2D findGradientAt(Node n, NetworkModel nm) {
		double dx = 0.0;
		double dy = 0.0;

		Set<Integer> keys = nm.getNodeKeys();
		for (Integer i : keys) {
			Node other = nm.getNode(i);
			if (other.getId() == n.getId())
				continue;
			double diffx = n.getX() - other.getX();
			double diffy = n.getY() - other.getY();
			double dsq = diffx*diffx + diffy*diffy;
			double num = nm.getGradB()*dsq;
			double d = 0.0;
			if (nm.getGradA() > 0.0) {
				d = Math.sqrt(dsq);
				num += nm.getGradA()*d;
			}
			if (nm.getGradC() > 0.0) {
				if (d == 0.0)
					d = Math.sqrt(dsq);
				num += nm.getGradC()*d*dsq;
			}
			if (nm.getGradD() > 0.0) {
				num += nm.getGradD()*dsq*dsq;
			}
			
//			dsq *= dsq;
//			dsq = Math.sqrt(dsq);
			
			if (diffx != 0.0) {
				if (Math.abs(diffx) < Math.abs(1000000.0*num)) {
					dx += diffx/num;
				}
			}
			
			if (diffy != 0.0) {
				if (Math.abs(diffy) < Math.abs(1000000.0*num)) {
					dy += diffy/num;
				}
			}
		}

//		dx = n.getX() - 400;
//		dy = n.getY() - 300;
		
		return new Gradient2D(dx, dy);
	}
/*    
    private static NodeAndAngle lookForNonIntersectingNode(Node srcNode, List<Node> newNodes, double leastAngle, double maxAngle,
    		                                               NetworkModel.GenerateBuilder gb, NetworkModel nm) {
    	int col = (int) Math.round(gb.regionFactor*srcNode.getX());
		int row = (int) Math.round(gb.regionFactor*srcNode.getY());
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i < 0 || i >= gb.numRegionRows || j < 0 || j >= gb.numRegionCols)
					continue;
				List<Integer> candidates = gb.regions[i][j];
				for (Integer k : candidates) {
					Node cand = nm.getNode(k);
					if (containsNode(cand, newNodes))
						continue;
					double angle = FixupNodes.findAngle(srcNode, cand);
					if (!angleFitsBetween(angle, leastAngle, maxAngle))
						continue;
					if (touchesASegment(srcNode, cand, gb.segments, nm))
						continue;
					return new NodeAndAngle(cand, angle);
				}
			}
		}
		return new NodeAndAngle(null, 0);
    }
    
    private static NodeAndAngle generateNonIntersectingNode(Node srcNode, List<Node> newNodes, double leastAngle, double maxAngle,
                                                            NetworkModel.GenerateBuilder gb, NetworkModel nm, Random r) {
    	for (int i = 0; i < 30; i++) {
		   double hyp = (0.2 + 0.8*r.nextDouble())*gb.maxDistance;
		   double angleRatio = r.nextDouble();
		   double tentativeAngle = (1.0 - angleRatio)*leastAngle + angleRatio*maxAngle;
		   double newX = srcNode.getX() + hyp*Math.cos(tentativeAngle);
		   double newY = srcNode.getY() + hyp*Math.sin(tentativeAngle);
		   Node newNode = new Node();
		   newNode.setX((int)newX);
		   newNode.setY((int)newY);
		   newNode.setId(nm.getNumNodes());
		   if (touchesASegment(srcNode, newNode, gb.segments, nm))
		      continue;
		   newNode.addNeighbor(srcNode.getId());
		   srcNode.addNeighbor(newNode.getId());
		   gb.addSegment(srcNode.getId(), newNode.getId());
		   nm.addNode(newNode);
		   newNodes.add(newNode);
		   int col = (int) Math.round(gb.regionFactor*newX);
		   int row = (int) Math.round(gb.regionFactor*newY);
		   if (col >= gb.numRegionCols || row >= gb.numRegionRows || col < 0 || row < 0)
			   ;
		   else {
			   gb.regions[row][col].add(newNode.getId());
		   }
		   return new NodeAndAngle(newNode, tentativeAngle);
    	}
        return new NodeAndAngle(null, 0);                                         	
    }
*/    
    private static class NodeAndAngle {
    	public Node node;
    	public double angle;
    	public NodeAndAngle(Node node, double angle) {
    		this.node = node;
    		this.angle = angle;
    	}
    }
/*		
	private static List<Node> generateNonIntersectingNodesEvenFrom(Node node, Node srcNode, NetworkModel.GenerateBuilder gb, Random r, NetworkModel nm)
	{
		List<Node> newNodes = new ArrayList<Node>();
		
		int originalNumNeighbors = node.getNeighbors().size();
		int numNewSpokes = 0;
		double entryAngle = 0.0;
		if (srcNode != null)
		   entryAngle = FixupNodes.findAngle(srcNode, node);
		int numCurrentNeighbors = node.getNeighbors().size();
		boolean pickEven = numCurrentNeighbors % 2 == 0;
		if (gb.maxEdgesPerNode - numCurrentNeighbors > 0)
		    numNewSpokes = pickBetween(4, gb.maxEdgesPerNode - numCurrentNeighbors, pickEven, r);
		
		double avgAngle = 2.0*Math.PI/numNewSpokes;
		double angleRange = avgAngle/2.0;
		double cumulativeAngle = 0.0;
		for (int i = 0; i < numNewSpokes; i++) {
			double tentativeAngle = entryAngle + (i+1)*avgAngle;
			if (tentativeAngle <= cumulativeAngle) {
				tentativeAngle = cumulativeAngle + avgAngle;
			}
			double maxAngle = tentativeAngle + angleRange;
			NodeAndAngle newNodeAndAngle = lookForNonIntersectingNode(node, newNodes, tentativeAngle, maxAngle, gb, nm);
			
			if (newNodeAndAngle.node == null) {
				NodeAndAngle genNodeAndAngle = Geometry.generateNonIntersectingNode(node, newNodes, tentativeAngle, maxAngle, gb, nm, r);
				if (genNodeAndAngle.node != null) {
					cumulativeAngle = genNodeAndAngle.angle;
					genNodeAndAngle.node.addNeighbor(node.getId());
					node.addNeighbor(genNodeAndAngle.node.getId());
					newNodes.add(genNodeAndAngle.node);
				}
			}
			else {
				cumulativeAngle = newNodeAndAngle.angle;
				newNodeAndAngle.node.addNeighbor(node.getId());
				node.addNeighbor(newNodeAndAngle.node.getId());
				gb.addSegment(node.getId(), newNodeAndAngle.node.getId());
				newNodes.add(newNodeAndAngle.node);
			}
			
		}
		return newNodes;
		
	}
	
	public static NewAndSourceNodes generateNonIntersectingNodesEvenFrom(List<Node> startNodes,
			                                                      List<Node> srcNodes,
			                                                      NetworkModel.GenerateBuilder gb,
			                                                      Random r,
			                                                      NetworkModel nm) {
		List<Node> newNodes = new ArrayList<Node>();
		List<Node> newSrcNodes = new ArrayList<Node>();
		for (int i = 0; i < startNodes.size(); i++) {
			Node srcNode = srcNodes.get(i);
			Node startNode = startNodes.get(i);
			List<Node> temp = generateNonIntersectingNodesEvenFrom(startNode, srcNode, gb, r, nm);
			for (Node t : temp) {
				newSrcNodes.add(startNode);
			}
			newNodes.addAll(temp);
		}
		
		NewAndSourceNodes ret = new NewAndSourceNodes();
		ret.newNodes = newNodes;
		ret.srcNodes = newSrcNodes;
		
		return ret;
	}
	
	public static void generateNonIntersectingNodesEven(int width, int height, int numNodes, int maxEdgesPerNode, int seed) {
		Random r = new Random(seed);
		double unitAreaPerNode = ((double)(width*height))/numNodes;
		double avgCellWidthPerNode = Math.sqrt(unitAreaPerNode);
		double avgNodeDistance = avgCellWidthPerNode/2;
		long maxDistance = Math.round(3*avgCellWidthPerNode);
		long maxDistanceSquared = maxDistance*maxDistance;
		
	}
*/	

}
