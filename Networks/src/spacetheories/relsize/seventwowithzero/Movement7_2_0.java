package spacetheories.relsize.seventwowithzero;

import java.util.ArrayList;
import java.util.List;

import spacetheories.relsize.XYZ;

// This recognizes dimensional movement with a major period of 7, a negating period of 2,
// and includes zeros:
// ...(-7, 0), (-6, z), (-5, -y), (-4, x), (-3, -z), (-2, y), (-1, -x), (0, 0), (1, x), (2, -y), (3, z), (4, -x), (5, y), (6, -z), (7, 0), (8, x)...
public class Movement7_2_0 {
	
	private static int findFirstNegDiffForX(boolean pos, int minLimit) {
		int i = pos ? -4 : -1;
		while (i > minLimit) {
			i -= 7;
		}
		return i == minLimit ? i : i + 7;
	}

	/**
	 * Returns a list of differences, sorted from most negative to most positive, that
	 * correspond to unit movement in the x direction
	 * 
	 * @param pos true if movement is in positive x direction, neg if movement is in negative x direction
	 * @param minLimit the minimum difference to return
	 * @param maxLimit the maximum difference to return
	 * @return the list of differences
	 */
	public static List<Integer> findDiffsForX(boolean pos, int minLimit, int maxLimit) {
		List<Integer> ret = new ArrayList<Integer>();
		if (minLimit < 0) {
			int i = findFirstNegDiffForX(pos, minLimit);
			while(i < 0 && i <= maxLimit) {
				ret.add(i);
				i += 7;
			}
			i = pos ? 1 : 4;
			while (i <= maxLimit) {
				ret.add(i);
			}
			return ret;
		}
		
		int i = pos ? 1 : 4;
		while (i < minLimit) {
			i += 7;
		}
		while (i <= maxLimit) {
			ret.add(i);
			i += 7;
		}
		return ret;		
	}
	
	private static XYZ findMovementForPosDiff(int diff) {
		XYZ ret = new XYZ();
		int rem = diff % 7;
		switch (rem) {
		   case 0: return ret;
		   case 1: ret.x = 1;
		           return ret;
		   case 2: ret.y = -1;
		           return ret;
		   case 3: ret.z = 1;
		           return ret;
		   case 4: ret.x = -1;
		           return ret;
		   case 5: ret.y = 1;
		           return ret;
		   case 6: ret.z = -1;
		           return ret;
		}
		return ret;
	}
	
	/**
	 * Determine a movement vector corresponding to a difference in number
	 * of neighbors
	 * 
	 * @param diff the difference in number of neighbors
	 * @return the movement vector
	 */
	public static XYZ findMovementForDiff(int diff) {
		if (diff < 0) {
			diff = -diff;
			XYZ m = findMovementForPosDiff(diff);
			m.reverse();
			return m;
		}
		return findMovementForPosDiff(diff);
	}
}
