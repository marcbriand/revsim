package revsim.mvc.view;

import java.util.List;

import revsim.modeltools.objects.AbstractBlip;

public class TransformUtil {
	
	public static final String TreeBreak = "===================================\n";
	public static final String LevelBreak = "-----------------------------------\n";
	public static final String ChildBreak = "---\n";
	
	public static String showTree(PatternNode node, int level, int childNo) {
		StringBuilder sb = new StringBuilder();
		if (level == 0 && childNo == 0) {
			sb.append(TreeBreak);
		}
		sb.append("Level: " + Integer.toString(level));
		sb.append(", Child No: " + Integer.toString(childNo));
		sb.append(", No blips: " + Integer.toString(node.getLocalBlips().size()));
		sb.append(", No children: " + Integer.toString(node.getNumChildren()));
		sb.append("\n");
		
		for (int i = 0; i < node.getNumChildren(); i++) {
			String subTreeStr = showTree(node.getChild(i), level+1, i);
			sb.append(subTreeStr);
		}
		return sb.toString();
	}
	
	public static String showTransformedCounts(List<List<AbstractBlip>> blips) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean first = true;
		for (List<AbstractBlip> blipList : blips) {
			if (!first) {
				sb.append(",");
			}
			first = false;
			sb.append(Integer.toString(blipList.size()));
		}
		sb.append("]");
		return sb.toString();
		
	}

}
