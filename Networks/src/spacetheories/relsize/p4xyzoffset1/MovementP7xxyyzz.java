package spacetheories.relsize.p4xyzoffset1;

import java.util.ArrayList;
import java.util.List;

import spacetheories.relsize.XYZ;

// This recognizes dimensional movement with a major period of 7
// 0    z   -z    y   -y   x   -x   0    z    -z    y    -y    x   -x    0    x    -x    y    -y    z    -z    0    x    -x    -y    y    z    -z    0
//-14 -13  -12  -11  -10  -9   -8  -7   -6    -5   -4    -3   -2   -1    0    1     2    3     4    5     6    7    8     9    10

public class MovementP7xxyyzz {
	
	public static XYZ findDiffs(ApparentPoint3D p1, ApparentPoint3D p2) {
		XYZ ret = new XYZ();
		ret.x = p2.apparentPoint.x - p1.apparentPoint.x;
		ret.y = p2.apparentPoint.y - p1.apparentPoint.y;
		ret.z = p2.apparentPoint.z - p1.apparentPoint.z;
		return ret;
	}
	
	public static XYZ absDiffs(XYZ xyz) {
		XYZ ret = new XYZ();
		ret.x = Math.abs(xyz.x);
		ret.y = Math.abs(xyz.y);
		ret.z = Math.abs(xyz.z);
		return ret;
	}
	
	
	private static int findNodeSizeForZero(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		int distToMax = Math.abs(maxNodeSize - startingNodeSize);
		int distToMin = Math.abs(minNodeSize - startingNodeSize);
		if (distToMin > distToMax) {
			int newSize = startingNodeSize - 7;
			if (newSize < minNodeSize)
				throw new RuntimeException("cannot find node size for 0");
			return newSize;
		}
		else {
			int newSize = startingNodeSize + 7;
			if (newSize > maxNodeSize)
				throw new RuntimeException("cannot find node size for 0");
			return newSize;
		}
	}
	
	private static int findNodeSize(int startingNodeSize, int startDelta, int crossZeroDelta,
			                        int minNodeSize, int maxNodeSize) {
		int delta = startDelta;
		while (startingNodeSize + delta > maxNodeSize) {
			if (delta == startDelta)
				delta = crossZeroDelta;
			else
				delta -= 7;
			if (startingNodeSize + delta < minNodeSize)
				break;
		}
		while(startingNodeSize + delta < minNodeSize) {
			if (delta == -startDelta)
				delta = -crossZeroDelta;
			else
				delta += 7;
			if (startingNodeSize + delta > maxNodeSize)
				throw new RuntimeException("can't size node");
		}
		return startingNodeSize + delta;		
	}
	
	private static int findNodeSizeForPosX(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		return findNodeSize(startingNodeSize, 1, -2, minNodeSize, maxNodeSize);
	}
	
	private static int findNodeSizeForNegX(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		return findNodeSize(startingNodeSize, -1, 2, minNodeSize, maxNodeSize);
	}
	
	private static int findNodeSizeForPosY(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		return findNodeSize(startingNodeSize, 3, -4, minNodeSize, maxNodeSize);
	}
	
	private static int findNodeSizeForNegY(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		return findNodeSize(startingNodeSize, -3, 4, minNodeSize, maxNodeSize);
	}
	
	private static int findNodeSizeForPosZ(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		return findNodeSize(startingNodeSize, 5, -6, minNodeSize, maxNodeSize);
	}

	private static int findNodeSizeForNegZ(int startingNodeSize, int minNodeSize, int maxNodeSize) {
		return findNodeSize(startingNodeSize, -5, 6, minNodeSize, maxNodeSize);
	}
	
	public static int calcNodeSizeForMovement(BiEnumXYZ0 dir, int startingNodeSize, int minNodeSize, int maxNodeSize) {
		switch (dir) {
		    case PosX: return findNodeSizeForPosX(startingNodeSize, minNodeSize, maxNodeSize);
		    case NegX: return findNodeSizeForNegX(startingNodeSize, minNodeSize, maxNodeSize);
		    case PosY: return findNodeSizeForPosY(startingNodeSize, minNodeSize, maxNodeSize);
		    case NegY: return findNodeSizeForNegY(startingNodeSize, minNodeSize, maxNodeSize);
		    case PosZ: return findNodeSizeForPosZ(startingNodeSize, minNodeSize, maxNodeSize);
		    case NegZ: return findNodeSizeForNegZ(startingNodeSize, minNodeSize, maxNodeSize);
		    case Zero: return findNodeSizeForZero(startingNodeSize, minNodeSize, maxNodeSize);
		    default: throw new RuntimeException("wtf");
		}
	}
	
	public static List<Integer> calcNodeSizes(List<BiEnumXYZ0> inst, int startingNodeSize) {
		List<Integer> ret = new ArrayList<Integer>();
		for (BiEnumXYZ0 e : inst) {
			int temp = calcNodeSizeForMovement(e, startingNodeSize, 3, 10);
			startingNodeSize = temp;
			ret.add(temp);
		}
		return ret;
	}
	
	private static void addForNeg1(List<Integer> deltas) {
		addFor3(deltas);
		addFor3(deltas);
		addForNeg7(deltas);
	}
	
	private static void addForNeg7(List<Integer> deltas) {
		deltas.add(-4);
		deltas.add(-3);
	}
	
	private static void addForNeg3(List<Integer> deltas) {
		deltas.add(-2);
		deltas.add(-1);
	}
	
	private static void addForNeg2(List<Integer> deltas) {
		addForNeg1(deltas);
		addForNeg1(deltas);
	}
	
	private static void addFor1(List<Integer> deltas) {
		addForNeg3(deltas);
		addForNeg3(deltas);
		addFor7(deltas);
	}
	
	private static void addFor2(List<Integer> deltas) {
		addFor1(deltas);
		addFor1(deltas);
	}
	
	private static void addFor3(List<Integer> deltas) {
		deltas.add(2);
		deltas.add(1);
	}
	
	private static void addFor7(List<Integer> deltas) {
		deltas.add(4);
		deltas.add(3);
	}
	
	/**
	 * return the sequence in-between, not the end points
	 * @param startSize
	 * @param endSize
	 * @param minNodeSize
	 * @param maxNodeSize
	 * @return
	 */
	public static List<Integer> resolveDifference(int startSize, int endSize, int minNodeSize, int maxNodeSize) {
		/*
		 * -1': 3,  3, -7 :                       (2, 1), (2, 1), (-4, -3)
         * 1': -3, -3, 7  :                       (-2, -1), (-2, -1), (4, 3)
         *-2'': -1', -1'  : 3, 3, -7, 3, 3, -7 :  (2, 1), (2, 1), (-4, -3), (2, 1), (2, 1), (-4, -3)
         *-2'': -3, 1'    : (-2, -1), -3, -3, 7 : (-2, -1) (-2, -1) (-2 -1) 7
         *-4': 3, -7      :                       (2 1) (-4 -3)
         *4': -3, 7       :                       (-2 -1) (4 3)
         *-5''': -2'', -3 : -1' -1' (-2 -1) :     (2 1) (2 1) (-4 -3) (2 1) (2 1) (-4 -3) (-2 -1)
         *5''': 2'', 3    : 1'  1' (2 1)    :     (-2 -1) (-2 -1) (4 3) (-2 -1) (-2 -1) (4 3) (2 1)
         *-6: -3, -3      :                       (-2 -1) (-2 -1)
         *6': 3, 3        :                       (2 1) (2 1)
         *-8': -7, -1'    :                       (-4 -3) (2 1) (2 1) (-4 -3)
         *8': 7, 1'       :                       (4 3) (-2 -1) (-2 -1) (4 3)
         *-9': -3, -3, -3 :                       (-2 -1) (-2 -1) (-2 -1)
         *9': 3, 3, 3     :                       (2 1) (2 1) (2 1)
		 */
		List<Integer> ret = new ArrayList<Integer>();
		int diff = endSize - startSize;
        if (diff < -9 || diff > 9)
        	throw new RuntimeException("cannot resolve difference");
        switch (diff) {
            case -9:
            	addForNeg3(ret);
            	addForNeg3(ret);
            	addForNeg3(ret);
            	return ret;
            case -8:
            	addForNeg7(ret);
            	addForNeg1(ret);
            	return ret;
            case -7:
            	addForNeg7(ret);
            	return ret;
            case -6:
            	addForNeg3(ret);
            	addForNeg3(ret);
            	return ret;
            case -5:
            	addForNeg3(ret);
            	addForNeg2(ret);
            	return ret;
            case -4:
            	addFor3(ret);
            	addForNeg7(ret);
            	return ret;
            case -3:
            	addForNeg3(ret);
            	return ret;
            case -2:
            	addForNeg2(ret);
            	return ret;
            case -1:
            	addForNeg1(ret);
            	return ret;
            case 1:
            	addFor1(ret);
            	return ret;
            case 2:
            	addFor2(ret);
            	return ret;
            case 3:
            	addFor3(ret);
            	return ret;
            case 4:
            	addForNeg3(ret);
            	addFor7(ret);
            	return ret;
            case 5:
            	addFor3(ret);
            	addFor2(ret);
            	return ret;
            case 6:
            	addFor3(ret);
            	addFor3(ret);
            	return ret;
            case 7:
            	addFor7(ret);
            	return ret;
            case 8:
            	addFor7(ret);
            	addFor1(ret);
            	return ret;
            case 9:
            	addFor3(ret);
            	addFor3(ret);
            	addFor3(ret);
            	return ret;
            default: throw new RuntimeException("cannot resolve difference!");
        }
        
	}
}
