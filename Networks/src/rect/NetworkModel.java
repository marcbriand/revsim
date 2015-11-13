package rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import algo.Geometry;
import algo.Geometry.Gradient2D;
import config.NetworkConfig;
import config.Point2DConfig;
import config.SigmoidConfig;
import revsim.mvc.Model;

public class NetworkModel implements Model {
	
	private static double TwoPi = 2.0*Math.PI;
	
	List<Node> trail = new ArrayList<Node>();
	
	NetworkConfig nc = new NetworkConfig();
	
	private int nextId = 0;
	
	public int getNextId() {
		if (nextId < nodes.keySet().size())
			nextId = nodes.keySet().size();
		return nextId++;
	}
	
	public static class RegionCell
	{
		public final int row;
		public final int col;
		public final boolean valid;
		
		public RegionCell(int r, int c, boolean v) {
			row = r;
			col = c;
			valid = v;
		}
	}
	
	public static class RegionLimits
	{
		final RegionCell a;
		final RegionCell b;
		
		public RegionLimits(RegionCell a, RegionCell b) {
			this.a = a;
			this.b = b;
		}
	}
	
	public static class NewAndSourceNodes {
		public List<Node> newNodes = new ArrayList<Node>();
		public List<Node> srcNodes = new ArrayList<Node>();
		public void addNewAndSourceNodes(Node newNode, Node srcNode) {
			newNodes.add(newNode);
			srcNodes.add(srcNode);
		}
		public void clear() {
			newNodes.clear();
			srcNodes.clear();
		}
		NewAndSourceNodes duplicate() {
			NewAndSourceNodes ret = new NewAndSourceNodes();
			for (Node n : newNodes) {
				ret.newNodes.add(n);
			}
			for (Node n : srcNodes) {
				ret.srcNodes.add(n);
			}
			return ret;
		}
		
	}

	public static class Segment {
		final public int a;
		final public int b;
		
		public Segment(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}
	
	private double gradA = 0.0; // dist
	private double gradB = 1.0; // dist sq
	private double gradC = 0.0; // dist cubed
	private double gradD = 0.0; // dist quad
	
	public static class DebugWindow {
		public int ulx;
		public int uly;
		public int lrx;
		public int lry;
		
		public DebugWindow(int ulx, int uly, int lrx, int lry) {
			this.ulx = ulx;
			this.uly = uly;
			this.lrx = lrx;
			this.lry = lry;
		}
	}
	
	private DebugWindow dbw;
	
	public DebugWindow getDebugWindow() {
		return dbw;
	}
	
	public double getGradA() {
		return gradA;
	}

	public void setGradA(double gradientCoefA) {
		this.gradA = gradientCoefA;
	}

	public double getGradB() {
		return gradB;
	}

	public void setGradB(double gradientCoefB) {
		this.gradB = gradientCoefB;
	}

	public double getGradC() {
		return gradC;
	}

	public void setGradC(double gradientCoefC) {
		this.gradC = gradientCoefC;
	}

	public double getGradD() {
		return gradD;
	}

	public void setGradD(double gradientCoefD) {
		this.gradD = gradientCoefD;
	}

	private List<Segment> newSegments = new ArrayList<Segment>();
	
	public List<Segment> getNewSegments() {
		List<Segment> ret = new ArrayList<Segment>();
		for (Segment ns : newSegments)
			ret.add(ns);
		return ret;
	}
	
	public void clearNewSegments() {
		newSegments.clear();
	}
	
	public static class GenerateBuilder {
		public List<Segment> [][] segmentRegions;
		public List<Integer> [][] nodeRegions;
		public int numRegionRows = 0;
		public int numRegionCols = 0;
		public int width = 0;
		public int height = 0;
		public int minEdgesPerNode = 0;
		public int maxEdgesPerNode = 0;
		public double maxDistance = 0.0;
		public double minDistance = 0.0;
		public double maxDistanceSquared = 0.0;
		public double regionFactor = 0.0;
//		public List<Segment> segments = new ArrayList<Segment>();
		
		public GenerateBuilder setDimensions(int width, int height, double minD, double maxD) {
			this.width = width;
			this.height = height;
			minDistance = minD;
			maxDistance = maxD;
			maxDistanceSquared = maxD*maxD;
			// 1.5 squares = maxD
//			double squareSide = 0.5*maxD;
			double squareSide = 1.1*maxD;
			numRegionRows = (int)(((double)height)/squareSide) + 1;
			numRegionCols = (int)(((double)width)/squareSide) + 1;
			//
			regionFactor = 1/squareSide;
			segmentRegions = new List[numRegionRows][numRegionCols];
			for (int i = 0; i < numRegionRows; i++)
				for (int j = 0; j < numRegionCols; j++)
					segmentRegions[i][j] = new ArrayList<Segment>();
			nodeRegions = new List[numRegionRows][numRegionCols];
			for (int i = 0; i < numRegionRows; i++)
				for (int j = 0; j < numRegionCols; j++)
					nodeRegions[i][j] = new ArrayList<Integer>();

			return this;
		}
		
		public GenerateBuilder setMaxEdgesPerNode(int m) {
			maxEdgesPerNode = m;
			return this;
		}
		
		public RegionCell findRegionCell(Node n) {
			int col = (int)(regionFactor*n.getX());
			int row = (int)(regionFactor*n.getY());
			boolean valid = true;
			if (col < 0 || col >= numRegionCols)
				valid = false;
			if (row < 0 || row >= numRegionRows)
				valid = false;
			return new RegionCell(row, col, valid);
		}
		
		private RegionLimits findRegionLimits(Node a, Node b) {
			RegionCell c1 = findRegionCell(a);
			RegionCell c2 = findRegionCell(b);
			return new RegionLimits(c1, c2);
		}
		
		private void addNode(Node n) {
			RegionCell rc = findRegionCell(n);
			if (!rc.valid)
				return;
			List<Integer> nl = nodeRegions[rc.row][rc.col];
			if (!nl.contains(n.getId()))
			   nl.add(n.getId());
		}
		
		public void removeNode(Node n) {
			for (int i = 0; i < numRegionRows; i++)
				for (int j = 0; j < numRegionCols; j++) {
					List<Segment> segs = segmentRegions[i][j];
					List<Segment> culledSegs = new ArrayList<Segment>();
					for (Segment s : segs) {
						if (s.a != n.getId() && s.b != n.getId())
							culledSegs.add(s);
					}
					segmentRegions[i][j] = culledSegs;
					
					List<Integer> nodes = nodeRegions[i][j];
					List<Integer> culledNodes = new ArrayList<Integer>();
					for (Integer k : nodes) {
						if (k != n.getId())
							culledNodes.add(k);
					}
					nodeRegions[i][j] = culledNodes;
				}
		}
		
		
		public void addSegment(Node a, Node b) {
			addNode(a);
			addNode(b);
			RegionLimits rl = findRegionLimits(a, b);
			if (rl.a.col < 0 || rl.a.col >= numRegionCols)
				return;
			if (rl.a.row < 0 || rl.a.row >= numRegionRows)
				return;
			if (rl.b.col < 0 || rl.b.col >= numRegionCols)
				return;
			if (rl.b.row < 0 || rl.b.row >= numRegionRows)
				return;
			Segment s = new Segment(a.getId(), b.getId());
			int minRow = Math.min(rl.a.row, rl.b.row);
			int maxRow = Math.max(rl.a.row, rl.b.row);
			int minCol = Math.min(rl.a.col, rl.b.col);
			int maxCol = Math.max(rl.a.col, rl.b.col);
			
			for (int i = minRow; i <= maxRow; i++)
				for (int j = minCol; j <= maxCol; j++) {
					List<Segment> sl = segmentRegions[i][j];
					sl.add(s);
				}
		}
		
	}
	


	
	Random r;
	
    public void setRandom(Random r) {
    	this.r = r;
    }
    
    public Random getRandom() {
    	return r;
    }
    
    GenerateBuilder gb;
    
    public void setGenerateBuilder(GenerateBuilder gb) {
    	this.gb = gb;
    }
    
    public GenerateBuilder getGenerateBuilder() {
    	return gb;
    }
    
    NewAndSourceNodes nsn = new NewAndSourceNodes();
    
    public NewAndSourceNodes getNewAndSourceNodes() {
    	return nsn.duplicate();
    }
    
    public void addNewAndSourceNode(Node newNode, Node srcNode) {
    	if (newNode.getId() != 0)
    	   nodes.put(newNode.getId(), newNode);
    	nsn.addNewAndSourceNodes(newNode, srcNode);
    	if (srcNode != null && newNode != null)
    	   gb.addSegment(srcNode, newNode);
    }
    
    public void addNewSegment(Node newNode, Node srcNode) {
    	if (srcNode != null && newNode != null)
    		newSegments.add(new Segment(srcNode.getId(), newNode.getId()));
    }
    
    public void clearNewAndSourceNodes() {
    	nsn.clear();
    }
    
	long calcDistanceSquared(Node a, Node b) {
		int diffx = b.getX() - a.getX();
		int diffy = b.getY() - a.getY();
		return diffx*diffx + diffy*diffy;
	}
	
	Node findNearNode(Node node, List<Integer> nodeKeyList, List<Integer> takenIds, long maxDistanceSquared, Random r)
	{
		Set<Integer> tried = new HashSet<Integer>();
		int keyListSize = nodeKeyList.size();
		while (tried.size() < keyListSize){
			int i = r.nextInt(keyListSize);
			if (tried.contains(i))
				continue;
			tried.add(i);
			Integer key = nodeKeyList.get(i);
			Node existingNode = nodes.get(key);
			if (takenIds.contains(existingNode.getId()))
				continue;
			long distSq = calcDistanceSquared(node, existingNode);
			if (distSq <= maxDistanceSquared) {
				takenIds.add(existingNode.getId());
				return existingNode;
			}
		}
		return null;
	}
	
	private void linkInNode(Node node, List<Integer> nodeKeyList, int width, int height, int maxEdgesPerNode, 
			                long maxDistanceSquared, Random r) {
		int numEdges = 1 + r.nextInt(maxEdgesPerNode-1);
		List<Integer> takenIds = new ArrayList<Integer>();
		for (int i = 0; i < numEdges; i++) {
			Node neighborNode = findNearNode(node, nodeKeyList, takenIds, maxDistanceSquared, r);
			if (neighborNode == null) {
				return;
			}
			node.addNeighbor(neighborNode.getId());
			neighborNode.addNeighbor(node.getId());
		}		
	}
	
	private void unlinkNode(Node n) {
		for (Integer i : n.getNeighbors()) {
			Node nb = nodes.get(i);
			if (nb != null) {
				nb.removeNeighbor(n.getId());
				n.removeNeighbor(nb.getId());
			}
		}
	}
	
	public void generateNodes(int width, int height, int numNodes, int maxEdgesPerNode, int seed, long framecount)
	{
		System.out.println("generating nodes");
		Random r = new Random(seed);
		double unitAreaPerNode = ((double)(width*height))/numNodes;
		double avgCellWidthPerNode = Math.sqrt(unitAreaPerNode);
		double avgNodeDistance = avgCellWidthPerNode/2;
		long maxDistance = Math.round(3*avgCellWidthPerNode);
		long maxDistanceSquared = maxDistance*maxDistance;
		int i = 0;
		int id = 0;
		
		List<Integer> nodeKeyList = new ArrayList<Integer>();
		for (Integer key : nodes.keySet())
			nodeKeyList.add(key);
		
		while (i < width) {
			int j = 0;
			while (j < height) {
				double hoffset = avgNodeDistance/(1 + r.nextInt(10));
				double voffset = avgNodeDistance/(1 + r.nextInt(10));
				double x = i + hoffset;
				x = Math.max(0, x);
				x = Math.min(width, x);
				double y = j + voffset;
				y = Math.max(0, y);
				y = Math.min(height, y);
				Node node = new Node(id, framecount, this);
				int xi = (int)Math.round(x);
				int yi = (int)Math.round(y);
				node.setX(xi);
				node.setY(yi);
				linkInNode(node, nodeKeyList, width, height, maxEdgesPerNode, maxDistanceSquared, r);
				nodes.put(id, node);
				nodeKeyList.add(id);
				j += avgCellWidthPerNode;
				if (id >= numNodes)
					break;
				id++;
			}
			i += avgCellWidthPerNode;
		}
	}
	
	Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	
	
	@Override
	public void deserialize(String arg0) {
	}

	@Override
	public Model duplicate() {
		NetworkModel ret = new NetworkModel();
		Set<Integer> keys = nodes.keySet();
		for (Integer i : keys)
		{
			Node n = nodes.get(i);
			ret.nodes.put(n.getId(), n);
		}
		for (Node n : trail) {
			ret.trail.add(n);
		}
		ret.nc = (NetworkConfig)nc.duplicate();
		return ret;
	}

	@Override
	public String serialize() {
		return null;
	}
	
    public Set<Integer> getNodeKeys() {
    	return nodes.keySet();
    }
	
	public Node getNode(int i) {
		return nodes.get(i);
	}
	
	public void addUnattachedNode(Node n) {
		nodes.put(n.getId(), n);
	}
	
	public void removeNode(Node n) {
		gb.removeNode(n);
		unlinkNode(n);
		nodes.remove(n.getId());
	}
	
	private Node findNearestNode(int x, int y) {
		long sofar = Long.MAX_VALUE;
		int index = 0;
		Set<Integer> keys = nodes.keySet();
		for (Integer i : keys) {
			Node node = nodes.get(i);
			int diffx = x - node.getX();
			int diffy = y - node.getY();
			long dsq = diffx*diffx + diffy*diffy;
			if (dsq < sofar) {
				index = i;
				sofar = dsq;
			}
		}
		return nodes.get(index);
	}
	
    public List<Node> getNodesFromIds(List<Integer> ids) throws IllegalArgumentException {
    	List<Node> ret = new ArrayList<Node>();
    	Set<Integer> keys = nodes.keySet();
    	for (Integer i : ids) {
    	    if (!keys.contains(i))
    		   throw new IllegalArgumentException("node id " + i + " does not exist");
    	   ret.add(nodes.get(i));
    	}
    	return ret;
    }
    
    public static void linkNodes(Node a, Node b) {
    	a.addNeighbor(b.getId());
    	b.addNeighbor(a.getId());
    }
    
    public static void unlinkNodes(Node a, Node b) {
    	a.removeNeighbor(b.getId());
    	b.removeNeighbor(a.getId());
    }
    
    public void setNetworkConfig(NetworkConfig nc) {
    	gb.minDistance = nc.getMinDistance();
		gb.maxDistance = nc.getMaxDistance();
		gb.minEdgesPerNode = nc.getMinNeighbors();
		gb.maxEdgesPerNode = nc.getMaxNeighbors();
		gradA = (Float)nc.getLocal("gradA");
		gradB = (Float)nc.getLocal("gradB");
		gradC = (Float)nc.getLocal("gradC");
		gradD = (Float)nc.getLocal("gradD");   
		if (nc.getUseDebugWindow()) {
			int ulx = (int)nc.getDbulx();
			int uly = (int)nc.getDbuly();
			int lrx = (int)nc.getDblrx();
			int lry = (int)nc.getDblry();
			dbw = new DebugWindow(ulx, uly, lrx, lry);
		}
		this.nc = nc;
    }
    
    public SigmoidConfig getArcsGrowFunc() {
    	return nc.getArcsGrowFunc();
    }
    
    public double getMaxDensity() {
    	return nc.getMaxDensity();
    }
    
    public void printDebugWindow() {
    	if (dbw != null) {
    		for (int i = 0; i < gb.numRegionRows; i++)
    			for (int j = 0; j < gb.numRegionCols; j++) {
    				List<Segment> segs = gb.segmentRegions[i][j];
    				for (Segment s : segs) {
    					Node a = getNode(s.a);
    					Node b = getNode(s.b);
    					if ((a.getX() > dbw.ulx) && (a.getY() > dbw.uly) && (a.getX() < dbw.lrx) && (a.getY() < dbw.lry)) {
    						if ((b.getX() > dbw.ulx) && (b.getY() > dbw.uly) && (b.getX() < dbw.lrx) && (b.getY() < dbw.lry)) {
    							System.out.println("a: id=" + a.getId() + ", (" + a.getX() + "," + a.getY() + ") b: id=" + b.getId() + ", (" + b.getX() + "," + b.getY() + ")");
    						}
    					}
    				}
    			}
    	}
    }
    
    public List<Point2DConfig> getStartPoints() {
    	return nc.getStartPoints();
    }
    
    public SigmoidConfig getGrowConfig() {
    	return nc.getDistGrowFunc();
    }

    public long getMaxGenerateFrame() {
    	return nc.getMaxGenerateFrame();
    }
    
    private boolean isInTrail(Node n) {
    	for (Node node : trail) {
    		if (n.getId() == node.getId())
    			return true;
    	}
    	return false;
    }
    
    public List<Node> getYoungestNeighbors(Node n) {
    	List<Integer> nbs = n.getNeighbors();
    	List<Node> nbnodes = new ArrayList<Node>();
    	long latestBirthday = 0;
    	for (Integer i : nbs) {
    		Node node = getNode(i);
    		if (isInTrail(node))
    			continue;
    		if (node.getBirthday() > latestBirthday)
    			latestBirthday = node.getBirthday();
    		nbnodes.add(node);
    	}
    	List<Node> ret = new ArrayList<Node>();
    	for (Node cand : nbnodes) {
    		if (cand.getBirthday() == latestBirthday)
    			ret.add(cand);
    	}
    	return ret;
    }
    
    public List<Node> getYoungestNodes() {
    	long latestBirthday = 0;
    	for (Integer i : nodes.keySet()) {
    		Node n = nodes.get(i);
    		if (n.getBirthday() > latestBirthday)
    			latestBirthday = n.getBirthday();
    	}
    	List<Node> ret = new ArrayList<Node>();
    	for (Integer i : nodes.keySet()) {
    		Node n = nodes.get(i);
    		if (n.getBirthday() == latestBirthday)
    			ret.add(n);
    	}
    	return ret;
    }
    
    public void addToTrail(Node n) {
    	trail.add(n);
    }
    
    public List<Node> getTrail() {
    	return trail;
    }
    
    public void sortAllNodeNeighbors() {
    	for (Integer i : nodes.keySet()) {
    		Node n = nodes.get(i);
    		Geometry.sortNeighborsByAngle(n, this);
    	}
    }
    
    public static double getDensity(Node node, Set<Integer> exclude, int depth, Map<Integer, Node> nodes) {
    	List<Integer> nbis = node.getNeighbors();
        double sum = 0;
        for (Integer i : nbis) {
        	if (exclude.contains(i))
        		continue;
        	sum += 1.0;
        }
        
        if (depth <= 1)
        	return sum;
        
        exclude.add(node.getId());
    	
    	for (Integer i : nbis) {
    		if (exclude.contains(i))
    			continue;
    		Node n = nodes.get(i);
    		sum += 0.8*getDensity(n, exclude, depth-1, nodes);
    	}
    	
    	return sum;
    }
    
    public static class NodeIndexAndMagnitude {
    	public int nodeIndex;
    	public double magnitude;
    	public NodeIndexAndMagnitude(int id, double m) {
    		nodeIndex = id;
    		magnitude = m;
    	}
    }
    
    public NodeIndexAndMagnitude findMaxDensitySpoke(Node node, int depth) {
    	return findMaxDensitySpoke(node, depth, nodes);
    }
    
    public static NodeIndexAndMagnitude findMaxDensitySpoke(Node node, int depth, Map<Integer, Node> nodes) {
    	List<Integer> nbis = node.getNeighbors();
    	double angleDelta = TwoPi/nbis.size();
    	double vecx = 0.0;
    	double vecy = 0.0;
    	for (int index = 0; index < nbis.size(); index++) {
    		double angle = index*angleDelta;
    		int i = nbis.get(index);
    		Node nb = nodes.get(i);
    		Set<Integer> exclude = new HashSet<Integer>();
    		exclude.add(node.getId());
    		double density = getDensity(nb, exclude, depth, nodes);
    		double dx = density*Math.cos(angle);
    		double dy = density*Math.sin(angle);
    		vecx += dx;
    		vecy += dy;
    	}
    	
    	Gradient2D grad = new Gradient2D(vecx, vecy);
    	double mag = Math.sqrt((vecx*vecx + vecy*vecy));
    	double maxDensityAngle = Geometry.findAngle(grad);
    	int index = (int)Math.floor(maxDensityAngle/angleDelta);
    	double deltaLow = maxDensityAngle - index*angleDelta;
    	double deltaHigh = (index+1)*angleDelta - maxDensityAngle;
    	if (deltaLow < deltaHigh)
    		return new NodeIndexAndMagnitude(index, mag);
    	return index+1 >= nbis.size() ? new NodeIndexAndMagnitude(0, mag) : new NodeIndexAndMagnitude(index+1, mag);
    }
    
	
}
