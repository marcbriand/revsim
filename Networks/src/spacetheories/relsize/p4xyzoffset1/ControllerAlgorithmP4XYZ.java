package spacetheories.relsize.p4xyzoffset1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import algo.Geometry;
import config.NetworkConfig;
import config.Point3DConfig;
import rect.IControllerAlgorithm;
import rect.NetworkController;
import rect.NetworkModel;
import rect.Node;
import revsim.mvc.Model;
import revsim.rendering.IntPoint2D;
import spacetheories.relsize.XYZ;
import config.Point2DConfig;

public class ControllerAlgorithmP4XYZ implements IControllerAlgorithm {
	
	private static class SpanningSequence {
		public boolean includeStartNode;
		public boolean includeEndNode;
		public List<Integer> nodeSizes;
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("SpanningSequence:\n");
			sb.append("includeStartNode: ");
			if (includeStartNode)
				sb.append("true\n");
			else
				sb.append("false\n");
			sb.append("includeEndNode: ");
			if (includeEndNode)
				sb.append("true\n");
			else
				sb.append("false\n");
			boolean isFirst = true;
			for (Integer s : nodeSizes) {
				if (!isFirst)
					sb.append(",");
				isFirst = false;
				String nstr = Integer.toString(s);
				sb.append(nstr);
			}
			return sb.toString();
		}

	}
	
	
	private static class RequiredProportions {
		public float xp;
		public float yp;
		public float zp;
		public float zerop;
	}
	
	BiEnumXYZ0 makeBiEnum(EnumXYZ e, int diff) {
		switch (e) {
		   case X: return diff >= 0 ? BiEnumXYZ0.PosX : BiEnumXYZ0.NegX;
		   case Y: return diff >= 0 ? BiEnumXYZ0.PosY : BiEnumXYZ0.NegY;
		   case Z: return diff >= 0 ? BiEnumXYZ0.PosZ : BiEnumXYZ0.NegZ;
		   default: return BiEnumXYZ0.Zero;
		}
	}
	
	private void cantBeNull(Node n1, Node n2) {
		if (n1 == null || n2 == null) {
			throw new RuntimeException("one or both nodes were null");
		}
	}
	
	private boolean areGraphSeparated(ApparentPoint3D p1, ApparentPoint3D p2) {
		return (p1.x != p2.x) || (p1.y != p2.y); 
	}
	
	private boolean areApparentSeparated(ApparentPoint3D p1, ApparentPoint3D p2) {
		return (p1.apparentPoint.x != p2.apparentPoint.x) || 
			   (p1.apparentPoint.y != p2.apparentPoint.y) ||
			   (p1.apparentPoint.z != p2.apparentPoint.z);
	}
	
	private RequiredProportions calcRequiredProportions(XYZ adiffs, int minZeroSteps) {
		RequiredProportions ret = new RequiredProportions();
		float total = adiffs.x + adiffs.y + adiffs.z + minZeroSteps;
		ret.xp = ((float)adiffs.x/total);
		ret.yp = ((float)adiffs.y/total);
		ret.zp = ((float)adiffs.z/total);
		ret.zerop = ((float)minZeroSteps/total);
		return ret;
	}
	
	private boolean countsUnfulfilled(XYZ adiffs, int minZeroSteps, XYZ xyzCounts, int zeroCounts) {
		if (xyzCounts.x < adiffs.x)
			return true;
		if (xyzCounts.y < adiffs.y)
			return true;
		if (xyzCounts.z < adiffs.z)
			return true;
		if (zeroCounts < minZeroSteps)
			return true;
		return false;
	}
	
	private float calcXProportion(XYZ xyzCounts, int zeroCounts) {
		float total = xyzCounts.x + xyzCounts.y + xyzCounts.z + zeroCounts;
		if (total == 0)
			return 0;
		return ((float)xyzCounts.x/total);
	}
	
	private float calcYProportion(XYZ xyzCounts, int zeroCounts) {
		float total = xyzCounts.x + xyzCounts.y + xyzCounts.z + zeroCounts;
		if (total == 0)
			return 0;
        return ((float)xyzCounts.y/total);		
	}
	
	private float calcZProportion(XYZ xyzCounts, int zeroCounts) {
		float total = xyzCounts.x + xyzCounts.y + xyzCounts.z + zeroCounts;
		if (total == 0)
			return 0;
        return ((float)xyzCounts.z/total);		
	}
	
	private float calcZeroProportion(XYZ xyzCounts, int zeroCounts) {
		float total = xyzCounts.x + xyzCounts.y + xyzCounts.z + zeroCounts;
		if (total == 0)
			return 0;
        return ((float)zeroCounts/total);		
	}
	
	private void addXInst(XYZ diffs, List<BiEnumXYZ0> instructions, XYZ counts) {
		if (diffs.x < 0)
			instructions.add(BiEnumXYZ0.NegX);
		else
			instructions.add(BiEnumXYZ0.PosX);
		counts.x++;
	}
	
	private void addYInst(XYZ diffs, List<BiEnumXYZ0> instructions, XYZ counts) {
		if (diffs.y < 0)
			instructions.add(BiEnumXYZ0.NegY);
		else
			instructions.add(BiEnumXYZ0.PosY);
		counts.y++;
	}
	
	private void addZInst(XYZ diffs, List<BiEnumXYZ0> instructions, XYZ counts) {
		if (diffs.z < 0)
			instructions.add(BiEnumXYZ0.NegZ);
		else
			instructions.add(BiEnumXYZ0.PosZ);
		counts.z++;
	}
	
	private SpanningSequence makeSpanningSequenceOpen(boolean includeStartNode, int startingNodeSize, XYZ diffs, XYZ adiffs,
			                                          int minZeroSteps,
			                                          int maxSteps) {
		SpanningSequence ss = new SpanningSequence();
		RequiredProportions rp = calcRequiredProportions(adiffs, minZeroSteps);
		XYZ counts = new XYZ();
		int zeroCounts = 0;
		List<BiEnumXYZ0> instructions = new ArrayList<BiEnumXYZ0>();
		while(countsUnfulfilled(adiffs, minZeroSteps, counts, zeroCounts)) {
			boolean changed = false;
			if (calcXProportion(counts, zeroCounts) < rp.xp && counts.x < adiffs.x) {
				addXInst(diffs, instructions, counts);
				changed = true;
			}
			if (calcYProportion(counts, zeroCounts) < rp.yp && counts.y < adiffs.y) {
				addYInst(diffs, instructions, counts);
				changed = true;
			}
			if (calcZProportion(counts, zeroCounts) < rp.zp && counts.z < adiffs.z) {
				addZInst(diffs, instructions, counts);
				changed = true;
			}
			if (calcZeroProportion(counts, zeroCounts) < rp.zerop && zeroCounts < minZeroSteps) {
				instructions.add(BiEnumXYZ0.Zero);
				zeroCounts++;
				changed = true;
			}
			if (!changed) {
				if (counts.x < adiffs.x) {
					addXInst(diffs, instructions, counts);
					changed = true;
				}
			}
			if (!changed) {
				if (counts.y < adiffs.y) {
					addYInst(diffs, instructions, counts);
					changed = true;
				}
			}
			if (!changed) {
				if (counts.z < adiffs.z) {
					addZInst(diffs, instructions, counts);
					changed = true;
				}
			}
			if (!changed) {
				if (zeroCounts < minZeroSteps) {
					instructions.add(BiEnumXYZ0.Zero);
					zeroCounts++;
					changed = true;
				}
			}
		}
		if (instructions.size() > maxSteps)
			throw new RuntimeException("too many steps");
		List<Integer> sizes = new ArrayList<Integer>();
		if (includeStartNode)
		   sizes.add(startingNodeSize);
		List<Integer> newSizes = MovementP7xxyyzz.calcNodeSizes(instructions, startingNodeSize);
		sizes.addAll(newSizes);
		ss.includeStartNode = includeStartNode;
		ss.nodeSizes = sizes;
		ss.includeEndNode = true;
		return ss;
	}
	
	private SpanningSequence distributeBridge(SpanningSequence startingSS, List<Integer> bridge) {
		int startingNodeSize = startingSS.nodeSizes.get(0);
		List<Integer> diffs = new ArrayList<Integer>();
		for (int i = 1; i < startingSS.nodeSizes.size(); i++) {
			int endSize = startingSS.nodeSizes.get(i);
			int startSize = startingSS.nodeSizes.get(i-1);
			int diff = endSize - startSize;
			diffs.add(diff);
		}
		List<Integer> newDiffs = new ArrayList<Integer>();
		int totalSize = diffs.size() + bridge.size();
		int numOrigDiffsUsed = 0;
		int numBridgeDiffsUsed = 0;
		float oReqProp = ((float)diffs.size())/((float)totalSize);
		float bRegProp = ((float)bridge.size())/((float)totalSize);
		while(numOrigDiffsUsed + numBridgeDiffsUsed < totalSize) {
			float oProp = 0;
			float bProp = 0;
			float currTotal = numOrigDiffsUsed + numBridgeDiffsUsed;
			if (currTotal > 0) {
				oProp = ((float)numOrigDiffsUsed)/currTotal;
				bProp = ((float)numBridgeDiffsUsed)/currTotal;
			}
			
			if (oProp < oReqProp && numOrigDiffsUsed < diffs.size()) {
				newDiffs.add(diffs.get(numOrigDiffsUsed));
				numOrigDiffsUsed++;
			}
			else if (bProp < bRegProp && numBridgeDiffsUsed < bridge.size()) {
				newDiffs.add(bridge.get(numBridgeDiffsUsed));
				numBridgeDiffsUsed++;
			}
			else if (numOrigDiffsUsed < diffs.size()) {
				newDiffs.add(diffs.get(numOrigDiffsUsed));
				numOrigDiffsUsed++;
			}
			else if (numBridgeDiffsUsed < bridge.size()) {
				newDiffs.add(bridge.get(numBridgeDiffsUsed));
				numBridgeDiffsUsed++;
			}			
		}
		
		List<Integer> newSizes = new ArrayList<Integer>();
		newSizes.add(startingNodeSize);
		int currSize = startingNodeSize;
		for (Integer d : newDiffs) {
			currSize += d;
			newSizes.add(currSize);
		}
		SpanningSequence ret = new SpanningSequence();
		ret.nodeSizes = newSizes;
//		ret.includeStartNode = true;
		ret.includeEndNode = true;
		
		return ret;
	}
	
	private SpanningSequence makeSpanningSequenceClosed(boolean includeStartNode, int startingNodeSize, int endingNodeSize,
			                                            XYZ diffs, XYZ adiffs,
			                                            int minZeroSteps,
			                                            int maxSteps) {
		SpanningSequence ss = makeSpanningSequenceOpen(includeStartNode, startingNodeSize, diffs, adiffs, minZeroSteps, maxSteps);
		ss.includeEndNode = false;
		int lastSequenceSize = ss.nodeSizes.get(ss.nodeSizes.size() - 1);
		if (lastSequenceSize != endingNodeSize) {
		    List<Integer> bridge = MovementP7xxyyzz.resolveDifference(lastSequenceSize, endingNodeSize, 3, 10);
		    return distributeBridge(ss, bridge);
		}
		else
		    return ss;
	}
	
	private SpanningSequence makeSpanningSequence(ApparentPoint3D p1, ApparentPoint3D p2, 
			                                      int minSteps, int maxSteps, NetworkModel nm) {
		XYZ diffs = MovementP7xxyyzz.findDiffs(p1, p2);
		XYZ adiffs = MovementP7xxyyzz.absDiffs(diffs);
		Node n1 = nm.getNode(p1.nodeId);
		Node n2 = nm.getNode(p2.nodeId);
		cantBeNull(n1, n2);
		if (areGraphSeparated(p1, p2) || areApparentSeparated(p1, p2)) {
			minSteps = Math.max(minSteps, 1);
		}
		
		int minNonZeroSteps = adiffs.x + adiffs.y + adiffs.z;
		if (minNonZeroSteps > maxSteps)
			throw new RuntimeException("too many steps");
		int minZeroSteps = minNonZeroSteps < minSteps ? (minSteps - minSteps) : 0;
		
		if (n1.getNeighbors().isEmpty()) {
			if (n2.getNeighbors().isEmpty()) {
				return this.makeSpanningSequenceOpen(true, 3, diffs, adiffs, minZeroSteps, maxSteps);
			}
			else {
				return this.makeSpanningSequenceClosed(true, 3, n2.getNeighbors().size(), diffs, adiffs, minZeroSteps, maxSteps);
			}
		}
		else {
			if (n2.getNeighbors().isEmpty()) {
				return this.makeSpanningSequenceOpen(false, n1.getNeighbors().size(), diffs, adiffs, minZeroSteps, maxSteps);
			}
			else {
				return this.makeSpanningSequenceClosed(false, n1.getNeighbors().size(), n2.getNeighbors().size(), diffs, adiffs, minZeroSteps, maxSteps);
			}
		}
	}

	@Override
	public Model createFirstNodes(NetworkController ncont, NetworkConfig ncfg,
			long currentFrame) {

		NetworkModel nm = new NetworkModel();
		NetworkModel.GenerateBuilder gb = new NetworkModel.GenerateBuilder();
		nm.setGenerateBuilder(gb);
		
		nm.setNetworkConfig(ncfg);
		
		List<Point3DConfig> things = ncfg.get3DStartPoints();
//		List<IntPoint2D> thingsT = new ArrayList<IntPoint2D>();
		List<ApparentPoint3D> appPoints = new ArrayList<ApparentPoint3D>();
		int screenWidth = 800;
		int screenHeight = 600;
		int edgeWidth = 50;
		int edgeHeight = 50;
		int arenaWidth = screenWidth - 2*edgeWidth;
		int arenaHeight = screenHeight - 2*edgeHeight;
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;
		
		for (Point3DConfig p : things) {
			if (p.getX() < xmin)
				xmin = p.getX();
			if (p.getX() > xmax)
				xmax = p.getX();
			if (p.getY() < ymin)
				ymin = p.getY();
			if (p.getY() > ymax)
				ymax = p.getY();
		}
		int xdelta = xmax - xmin;
		int ydelta = ymax - ymin;
		double pixelsPerThingX = 0.0;
		if (xdelta != 0)
			pixelsPerThingX = ((double)arenaWidth/(double)xdelta);
		double pixelsPerThingY = 0.0;
		if (ydelta != 0)
			pixelsPerThingY = ((double)arenaHeight/(double)ydelta);
		
		if (xdelta == 0) {
			int x = arenaWidth/2;
			if (ydelta == 0) {
				Point3DConfig configP = things.get(0);
				ApparentPoint3D appP = new ApparentPoint3D(x, arenaHeight/2);
				appP.apparentPoint.x = configP.getX();
				appP.apparentPoint.y = configP.getY();
				appP.apparentPoint.z = configP.getZ();
				appPoints.add(appP);
			}
			else {
				for (Point3DConfig p : things) {
					int y = (int)((p.getY() - ymin)*pixelsPerThingY + edgeHeight);
					ApparentPoint3D appP = new ApparentPoint3D(x, y, p.getX(), p.getY(), p.getZ());
					appPoints.add(appP);
				}
			}
		}
		else {
			if (ydelta == 0) {
				int y = arenaHeight/2;
				for (Point3DConfig p : things) {
					int x = (int)((p.getX() - xmin)*pixelsPerThingX + edgeWidth);
					ApparentPoint3D appP = new ApparentPoint3D(x, y, p.getX(), p.getY(), p.getZ());
					appPoints.add(appP);
				}
			}
			else {
				for (Point3DConfig p : things) {
					int x = (int)((p.getX() - xmin)*pixelsPerThingX + edgeWidth);
					int y = (int)((p.getY() - ymin)*pixelsPerThingY + edgeHeight);
					ApparentPoint3D appP = new ApparentPoint3D(x, y, p.getX(), p.getY(), p.getZ());
					appPoints.add(appP);
				}				
			}
		}
		
		for (ApparentPoint3D pt: appPoints) {
			int id = nm.getNextId();
			ApparentNode n = new ApparentNode(id, currentFrame, nm);
			n.setX(pt.x);
			n.setY(pt.y);
			n.apparentPoint.x = pt.apparentPoint.x;
			n.apparentPoint.y = pt.apparentPoint.y;
			n.apparentPoint.z = pt.apparentPoint.z;
			pt.nodeId = id;
			nm.addUnattachedNode(n);
			nm.addNewAndSourceNode(n, null);
		}
		
		List<Point2DConfig> conns = ncfg.getStartConnections();
		System.out.print("Start connections: ");
		
		double minDelta = Double.MAX_VALUE;
		for (Point2DConfig c : conns)
		{
			System.out.println(c.getX() + "---" + c.getY());
	        ApparentPoint3D p1 = appPoints.get(c.getX());
	        ApparentPoint3D p2 = appPoints.get(c.getY());
	        SpanningSequence seq = makeSpanningSequence(p1, p2, 1, 30, nm);
	        System.out.println(seq.toString());
	        double idealD = idealDelta(p1, p2, seq, nm);
	        if (idealD < minDelta)
	        	minDelta = idealD;
	        addIntermediateNodes(p1, p2, seq, currentFrame, nm);
		}
		
		equalizeUnattachedNeighbors(nm);
		pullInUnattachedNeighbors(0.8*minDelta, nm);
		
		return nm;
		
	}
	
	double idealDelta(ApparentPoint3D p1, ApparentPoint3D p2, SpanningSequence seq, NetworkModel nm) {
		Node n1 = nm.getNode(p1.nodeId);
		Node n2 = nm.getNode(p2.nodeId);
		int deltax = n2.getX() - n1.getX();
		int deltay = n2.getY() - n1.getY();
		double totaldist = Math.sqrt(deltax*deltax + deltay*deltay);
		int numSteps = seq.nodeSizes.size() + 1;
		if (!seq.includeStartNode)
			numSteps++;
		if (!seq.includeEndNode)
			numSteps++;
		if (numSteps == 0)
			throw new RuntimeException("num steps is 0!");
		return totaldist/numSteps;
	}
	
	void addTrailingNode(ApparentNode srcNode, ApparentNode newNode, int newNodeSize, 
			             long currentFrame, NetworkModel nm) {
		nm.addUnattachedNode(newNode);
		addNeighborsToNode(newNode, newNodeSize, srcNode, currentFrame, nm);
		
	}
	
	void addIntermediateNodes(ApparentPoint3D startPoint, ApparentPoint3D endPoint, 
			                  SpanningSequence ss, long currentFrame, NetworkModel nm) {
		int start = ss.includeStartNode ? 1 : 0;
		int end = ss.includeEndNode ? ss.nodeSizes.size() - 2 : ss.nodeSizes.size() - 1;
		int numArcs = end - start + 2;

		float diffx = endPoint.x - startPoint.x;
		float diffy = endPoint.y - startPoint.y;
		if (numArcs == 0)
			throw new RuntimeException("numArcs == 0");
		float arcDeltaX = diffx/numArcs;
		float arcDeltaY = diffy/numArcs;
		float x = startPoint.x;
		float y = startPoint.y;
		ApparentNode srcNode = (ApparentNode)nm.getNode(startPoint.nodeId);
		if (ss.includeStartNode) {
			if (srcNode.getNeighbors().isEmpty())
				this.addNeighborsToEmptyNode(srcNode, ss.nodeSizes.get(0), arcDeltaX, arcDeltaY, currentFrame, nm);
			else {
				if (srcNode.getNeighbors().size() != ss.nodeSizes.get(0))
					throw new RuntimeException("starting node size mismatch!");
			}
		}
		
		for (int i = start; i <= end; i++) {
			x += arcDeltaX;
			y += arcDeltaY;
			int id = nm.getNextId();
			ApparentNode newNode = new ApparentNode(id, currentFrame, nm);
			newNode.setX((int)x);
			newNode.setY((int)y);
			this.addTrailingNode(srcNode, newNode, ss.nodeSizes.get(i), currentFrame, nm);
			if (i == end) {
				if (ss.includeEndNode) {
					ApparentNode endNode = (ApparentNode)nm.getNode(endPoint.nodeId);
					double angleToSrc = Geometry.findAngle(endNode, newNode);
					if (endNode.getNeighbors().isEmpty())
						addNeighborsToEmptyNode(endNode, ss.nodeSizes.get(end+1)-1, -arcDeltaX, -arcDeltaY, currentFrame, nm);
					else {
						if (endNode.getNeighbors().size() != ss.nodeSizes.get(end+1))
							throw new RuntimeException("starting node size mismatch!");
						deleteArcFromTargetNode(endNode, angleToSrc, nm);
					}
					stealArcFromSrcNode(newNode, endNode, angleToSrc, nm);
				}
				else
				    linkLastNewNodeToEndpoint(srcNode, newNode, endPoint, nm);
			}
			srcNode = newNode;
		}
	}
	
	void linkLastNewNodeToEndpoint(ApparentNode srcNode, ApparentNode node, ApparentPoint3D endPoint,
			                       NetworkModel nm) {
		double entryAngle = Geometry.findAngle(srcNode, node);
		Node nbToLink = Geometry.findNeighborClosestToAngle(node, entryAngle, nm);
		Node endNode = nm.getNode(endPoint.nodeId);
		NetworkModel.linkNodes(endNode, nbToLink);
		nm.addNewSegment(endNode, nbToLink);
	}
	
	void stealArcFromSrcNode(Node srcNode, Node newNode, double angleToSrc, NetworkModel nm) {
		double angleFromSrc = Geometry.normalizeAngle(angleToSrc + Math.PI);
		Node srcNb = Geometry.findNeighborClosestToAngle(srcNode, angleFromSrc, nm);
		if (srcNb != null) {
			if (srcNb.getNeighbors().size() != 1)
				throw new RuntimeException("can't steal arc -- attached to other nodes");
			NetworkModel.unlinkNodes(srcNode, srcNb);			
			nm.removeNode(srcNb);
		}
		NetworkModel.linkNodes(srcNode, newNode);
		nm.addNewSegment(newNode, srcNode);
	}
	
	void deleteArcFromTargetNode(Node targetNode, double angleToSrc, NetworkModel nm) {
		Node tNb = Geometry.findNeighborClosestToAngle(targetNode, angleToSrc, nm, true);
		if (tNb == null)
			throw new RuntimeException("could not find available node");
		NetworkModel.unlinkNodes(targetNode, tNb);
		nm.removeNode(tNb);
	}
	
	void addNeighborsToEmptyNode(ApparentNode node, int nodeSize, float deltaX, float deltaY, long currentFrame, NetworkModel nm) {
		double dist = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		Geometry.Gradient2D grad = new Geometry.Gradient2D(deltaX, deltaY);
		double startAngle = Geometry.findAngle(grad);
		double angleDelta = Geometry.TwoPi/nodeSize;
		int id = nm.getNextId();
		ApparentNode firstNb = new ApparentNode(id, currentFrame, nm);
		firstNb.setX(node.getX() + (int)deltaX);
		firstNb.setY(node.getY() + (int)deltaY);
		NetworkModel.linkNodes(node, firstNb);
		nm.addNewAndSourceNode(firstNb, node);
		for (int i = 1; i < nodeSize; i++) {
			double angle = startAngle + i*angleDelta;
			int x = node.getX() + (int)(dist*Math.cos(angle));
			int y = node.getY() + (int)(dist*Math.sin(angle));
			id = nm.getNextId();
			ApparentNode newNode = new ApparentNode(id, currentFrame, nm);
			newNode.setX(x);
			newNode.setY(y);
			NetworkModel.linkNodes(node, newNode);
			nm.addNewAndSourceNode(newNode, node);
		}
	}
	
	void addNeighborsToNode(ApparentNode node, int nodeSize, ApparentNode srcNode, long currentFrame, NetworkModel nm) {
		double angle = Geometry.findAngle(node, srcNode);
		double radius = Geometry.findDistance(node, srcNode);
		double angleDelta = Geometry.TwoPi/nodeSize;
		stealArcFromSrcNode(srcNode, node, angle, nm);		
		for (int i = 1; i < nodeSize; i++) {
			angle += angleDelta;
			double nbx = node.getX() + radius*Math.cos(angle);
			double nby = node.getY() + radius*Math.sin(angle);
			int id = nm.getNextId();
			ApparentNode newNode = new ApparentNode(id, currentFrame, nm);
			newNode.setX((int)nbx);
			newNode.setY((int)nby);
			NetworkModel.linkNodes(node, newNode);
			nm.addNewAndSourceNode(newNode, node);
		}
	}
	
	static class NodeAngleAttached {
		public Node node;
		public double angle;
		public boolean attached;
	}
	
	static class NodeAngleAttachedComparator implements Comparator<NodeAngleAttached> {

		@Override
		public int compare(NodeAngleAttached o1, NodeAngleAttached o2) {
			if (o1.angle < o2.angle)
				return -1;
			if (o1.angle == o2.angle)
				return 0;
			return 1;
		}
		
	}
	
	List<NodeAngleAttached> classifyNeighbors(Node n, NetworkModel nm) {
		List<NodeAngleAttached> ret = new ArrayList<NodeAngleAttached>();
		List<Integer> nbs = n.getNeighbors();
		for (Integer i : nbs) {
			Node nb = nm.getNode(i);
			NodeAngleAttached naa = new NodeAngleAttached();
			naa.node = nb;
			naa.angle = Geometry.findAngle(n, nb);
			naa.angle = Geometry.normalizeAngle(naa.angle);
			naa.attached = nb.getNeighbors().size() > 1;
			ret.add(naa);
		}
		return ret;
	}
	
	List<NodeAngleAttached> classifyAndSortNeighbors(Node n, NetworkModel nm) {
/*
		List<NodeAngleAttached> ret = new ArrayList<NodeAngleAttached>();
		List<Integer> nbs = n.getNeighbors();
		for (Integer i : nbs) {
			Node nb = nm.getNode(i);
			NodeAngleAttached naa = new NodeAngleAttached();
			naa.node = nb;
			naa.angle = Geometry.findAngle(n, nb);
			naa.angle = Geometry.normalizeAngle(naa.angle);
			naa.attached = nb.getNeighbors().size() > 1;
			ret.add(naa);
		}
*/
		List<NodeAngleAttached> ret = classifyNeighbors(n, nm);
		Collections.sort(ret, new NodeAngleAttachedComparator());
		return ret;
	}
	
	private int inc(int index, int sz) {
		if (index == sz - 1)
			return 0;
		index++;
		return index;
	}
	
	int findNextAttachedNeighborIndex(List<NodeAngleAttached> nbs, int startIndex) {
		int index = startIndex;
		for (int i = 0; i < nbs.size(); i++) {
			NodeAngleAttached naa = nbs.get(index);
			if (naa.attached)
				return index;
			index = inc(index, nbs.size());
		}
		throw new RuntimeException("could not find attached neighbor");
	}
	
	private void equalizeBetween(Node center, List<NodeAngleAttached> nbs, int firstIndex, int secondIndex, Set<Integer> toEqualize) {
		double firstAngle = nbs.get(firstIndex).angle;
		double secondAngle = nbs.get(secondIndex).angle;
		int cx = center.getX();
		int cy = center.getY();
		double delta = 0.0;
		if (secondAngle > firstAngle)
			delta = secondAngle - firstAngle;
		else
			delta = Geometry.TwoPi - (firstAngle - secondAngle);
		int i = inc(firstIndex, nbs.size());
		int unattachedCount = 0;
		while (i != secondIndex) {
			unattachedCount++;
			i = inc(i, nbs.size());
		}
		double deltaPerU = delta/(unattachedCount + 1);
		i = inc(firstIndex, nbs.size());
		int j = 0;
		while (i != secondIndex) {
			j++;
			double angle = firstAngle + j*deltaPerU;
			NodeAngleAttached naa = nbs.get(i);
			int dx = naa.node.getX() - cx;
			int dy = naa.node.getY() - cy;
			System.out.println("..naa x = " + naa.node.getX() + ", y = " + naa.node.getY());
			System.out.println("..dx = " + dx + ", dy = " + dy);
			double dist = Math.sqrt(dx*dx + dy*dy);
			System.out.println("..dist = " + dist);
			System.out.println("..new angle = " + angle);
			int x = cx + (int)(dist*Math.cos(angle));
			int y = cy - (int)(dist*Math.sin(angle));
			System.out.println("..new x = " + x + ", y = " + y);
			naa.node.setX(x);
			naa.node.setY(y);
			toEqualize.remove(i);
			i = inc(i, nbs.size());
		}
	}
	
	void pullIn(Node center, List<NodeAngleAttached> nbs, int firstIndex, int secondIndex, Set<Integer> toPullIn, double dist) {
		int cx = center.getX();
		int cy = center.getY();
		int i = inc(firstIndex, nbs.size());
		i = inc(firstIndex, nbs.size());
		while (i != secondIndex) {
			NodeAngleAttached naa = nbs.get(i);
			double angle = naa.angle;
			int x = cx + (int)(dist*Math.cos(angle));
			int y = cy - (int)(dist*Math.sin(angle));
			naa.node.setX(x);
			naa.node.setY(y);
			toPullIn.remove(i);
			i = inc(i, nbs.size());
		}
		
	}
	
	void equalizeUnattachedNeighbors(NetworkModel nm) {
		Set<Integer> nodeKeys = nm.getNodeKeys();
		for (Integer key : nodeKeys) {
			Node node = nm.getNode(key);
			if (node.getNeighbors().size() > 1) {
				List<NodeAngleAttached> nbs = classifyAndSortNeighbors(node, nm);
				Set<Integer> toEqualize = new HashSet<Integer>();
				for (int i = 0; i < nbs.size(); i++) {
					NodeAngleAttached naa = nbs.get(i);
					if (!naa.attached)
						toEqualize.add(i);
				}
				int startIndex = 0;
				while (!toEqualize.isEmpty()) {
					int firstIndex = findNextAttachedNeighborIndex(nbs, startIndex);
					int secondIndex = findNextAttachedNeighborIndex(nbs, inc(firstIndex, nbs.size()));
					equalizeBetween(node, nbs, firstIndex, secondIndex, toEqualize);
					startIndex = secondIndex;
				}
				
			}
		}
	}
	
	void pullInUnattachedNeighbors(double dist, NetworkModel nm) {
		Set<Integer> nodeKeys = nm.getNodeKeys();
		for (Integer key : nodeKeys) {
			Node node = nm.getNode(key);
			if (node.getNeighbors().size() > 1) {
				List<NodeAngleAttached> nbs = classifyNeighbors(node, nm);
				Set<Integer> toPullIn = new HashSet<Integer>();
				for (int i = 0; i < nbs.size(); i++) {
					NodeAngleAttached naa = nbs.get(i);
					if (!naa.attached)
						toPullIn.add(i);
				}
				int startIndex = 0;
				while (!toPullIn.isEmpty()) {
					int firstIndex = findNextAttachedNeighborIndex(nbs, startIndex);
					int secondIndex = findNextAttachedNeighborIndex(nbs, inc(firstIndex, nbs.size()));
					pullIn(node, nbs, firstIndex, secondIndex, toPullIn, dist);
					startIndex = secondIndex;
				}
				
			}
		}
		
	}
	
	void printNodeAndNeighborsPosition(Node node, NetworkModel nm) {
		List<Integer> nbs = node.getNeighbors();
		System.out.println("-----------");
		System.out.println("Node: (" + node.getX() + "," + node.getY() + ")");
		System.out.println("Neighbors:");
		for (int i = 0; i < nbs.size(); i++) {
			int key = nbs.get(i);
			Node nb = nm.getNode(key);
			System.out.println(" index: " + i + ", (" + nb.getX() + "," + nb.getY() + ")");
;		}
	}
	
	void printNodeAndNeighborsPosition(Node node, List<NodeAngleAttached> naas, NetworkModel nm) {
		System.out.println("===========");
		System.out.println("Node: (" + node.getX() + "," + node.getY() + ")");
        System.out.println("NAA:");
        for (int i = 0; i < naas.size(); i++) {
        	NodeAngleAttached naa = naas.get(i);
        	System.out.println(" index: " + i + ", (" + naa.node.getX() + "," + naa.node.getY() + ") angle: " + naa.angle + " att: " + naa.attached);
        }
	}

	@Override
	public Model growNetwork(NetworkController ncont, Model model,
			long currentFrame) {

		return null;
	}

}
