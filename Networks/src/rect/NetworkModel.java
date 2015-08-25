package rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import config.NetworkConfig;
import revsim.mvc.Model;

public class NetworkModel implements Model {
	
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
		public int maxEdgesPerNode = 0;
		public double maxDistance = 0.0;
		public double maxDistanceSquared = 0.0;
		public double regionFactor = 0.0;
//		public List<Segment> segments = new ArrayList<Segment>();
		
		public GenerateBuilder setDimensions(int width, int height, double maxD) {
			this.width = width;
			this.height = height;
			maxDistance = maxD;
			maxDistanceSquared = maxD*maxD;
			// 1.5 squares = maxD
			double squareSide = 0.5*maxD;
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
		
		public void addNode(Node n) {
			RegionCell rc = findRegionCell(n);
			if (!rc.valid)
				return;
			List<Integer> nl = nodeRegions[rc.row][rc.col];
			if (!nl.contains(n.getId()))
			   nl.add(n.getId());
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
    	   nodes.add(newNode);
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
	
	Node findNearNode(Node node, List<Integer> takenIds, long maxDistanceSquared, Random r)
	{
		Set<Integer> tried = new HashSet<Integer>();
		while (tried.size() < nodes.size()){
			int i = r.nextInt(nodes.size());
			if (tried.contains(i))
				continue;
			tried.add(i);
			Node existingNode = nodes.get(i);
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
	
	private void linkInNode(Node node, int width, int height, int maxEdgesPerNode, 
			                long maxDistanceSquared, Random r) {
		int numEdges = 1 + r.nextInt(maxEdgesPerNode-1);
		List<Integer> takenIds = new ArrayList<Integer>();
		for (int i = 0; i < numEdges; i++) {
			Node neighborNode = findNearNode(node, takenIds, maxDistanceSquared, r);
			if (neighborNode == null) {
				return;
			}
			node.addNeighbor(neighborNode.getId());
			neighborNode.addNeighbor(node.getId());
		}		
	}
	
	public void generateNodes(int width, int height, int numNodes, int maxEdgesPerNode, int seed)
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
				Node node = new Node();
				int xi = (int)Math.round(x);
				int yi = (int)Math.round(y);
				node.setX(xi);
				node.setY(yi);
				node.setId(id);
				linkInNode(node, width, height, maxEdgesPerNode, maxDistanceSquared, r);
				nodes.add(node);
				j += avgCellWidthPerNode;
				if (id >= numNodes)
					break;
				id++;
			}
			i += avgCellWidthPerNode;
		}
	}
	
	List<Node> nodes;
	
	public NetworkModel() {
		nodes = new ArrayList<Node>();
		
	}
	
	@Override
	public void deserialize(String arg0) {
	}

	@Override
	public Model duplicate() {
		NetworkModel ret = new NetworkModel();
		for (int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			ret.nodes.add(n.duplicate());
		}
		return ret;
	}

	@Override
	public String serialize() {
		return null;
	}
	
	public int getNumNodes() {
		return nodes.size();
	}
	
	public Node getNode(int i) {
		return nodes.get(i);
	}
	
	public void addNode(Node n) {
		nodes.add(n);
	}
	
	public Node findNearestNode(int x, int y) {
		long sofar = Long.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < nodes.size(); i++) {
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
    	for (Integer i : ids) {
    	   if (i < 0 || i >= nodes.size())
    		   throw new IllegalArgumentException("node index out of range");
    	   ret.add(nodes.get(i));
    	}
    	return ret;
    }
    
    public static void linkNodes(Node a, Node b) {
    	a.addNeighbor(b.getId());
    	b.addNeighbor(a.getId());
    }
    
    public void setNetworkConfig(NetworkConfig nc) {
		gb.maxDistance = nc.getMaxDistance();
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

	
}
