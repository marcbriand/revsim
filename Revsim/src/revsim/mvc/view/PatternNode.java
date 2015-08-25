package revsim.mvc.view;

import java.util.ArrayList;
import java.util.List;

import revsim.config.objects.revsim.TransformDestOption;
import revsim.config.objects.revsim.TransformObject;
import revsim.config.objects.revsim.TransformSourceOption;
import revsim.modeltools.objects.AbstractBlip;

public class PatternNode {

	private PatternNode parent = null;
	private final List<PatternNode> children = new ArrayList<PatternNode>();
	private final List<AbstractBlip> blips = new ArrayList<AbstractBlip>();
	
	public List<AbstractBlip> getLocalBlips () {
		return blips;
	}

	public List<AbstractBlip> gather (int depth) {
		List<AbstractBlip> ret = new ArrayList<AbstractBlip>();
		for (AbstractBlip blip : blips) {
			ret.add((AbstractBlip)blip.clone());
		}
		if (depth > 0) {
			for (PatternNode pn : children) {
				ret.addAll(pn.gather(depth-1));
			}
		}
		return ret;
	}

	public List<AbstractBlip> gatherFromSubnode (List<Integer> path, int depth) throws NodeException {
		if (path.isEmpty()) {
			return gather(depth);
		}
		int child = path.get(0);
		if (child >= children.size())
		   throw NodeException.make(child, children.size());
		PatternNode childNode = children.get(child);
		path.remove(0);
		return childNode.gatherFromSubnode(path, depth);
	}
	
	public void addNode (PatternNode node) {
		node.parent = this;
		children.add(node);
	}
	
	public void mergeNode (PatternNode node) {
		blips.addAll(node.blips);
	}
	
	public PatternNode getParent() {
		return parent;
	}
	
	public int getNumChildren () {
		return children.size();
	}
	
	public PatternNode getChild (int i) {
		return children.get(i);
	}
	
	public void addBlip (AbstractBlip b) {
		blips.add(b);
	}
	
	public static void apply (PatternNode root, NodeOp op) {
		
		op.apply(root);
		for (int i = 0; i < root.getNumChildren(); i++) {
			PatternNode child = root.getChild(i);
			apply(child, op);
		}
	}
	
	private static void applyTransform (TransformObject tx, PatternNode tree) throws NodeException {
		List<Integer> path = tx.getSrcPath();
		PatternNode cursor = tree;
		for (Integer i : path) {
			if (i >= cursor.getNumChildren()) {
				throw new NodeException("No child at index " + Integer.toString(i));
			}
			cursor = cursor.getChild(i);
		}
		List<AbstractBlip> blips = cursor.gather(tx.getSrcDepth());
		if (tx.getSrcOption() == TransformSourceOption.Remove) {
			cursor.blips.clear();
		}
		List<List<AbstractBlip>> txFormed = tx.getOperation().transform(blips);
		List<Integer> destPath = tx.getDestPath();
		cursor = tree;
		for (Integer i : destPath) {
			while(i >= cursor.getNumChildren()) {
				cursor.addNode(new PatternNode());
			}
			cursor = cursor.getChild(i);
		}
		if (tx.getDestOption() == TransformDestOption.Add) {
			for (List<AbstractBlip> list : txFormed) {
				PatternNode node = new PatternNode();
				node.blips.addAll(list);
				cursor.addNode(node);
			}
		}
		else {
			for (List<AbstractBlip> list : txFormed) {
				cursor.blips.addAll(list);
			}
		}
		System.out.println(TransformUtil.showTree(tree, 0, 0));
	}
	
	public static void applyTransforms (List<TransformObject> transforms, PatternNode tree) throws NodeException {
		System.out.println(TransformUtil.showTree(tree, 0, 0));
		int count = 1;
		for (TransformObject tO : transforms) {
			applyTransform(tO, tree);
			count++;
		}
		
	}
}
