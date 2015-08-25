package revsim.mvc.view;

import java.util.ArrayList;
import java.util.List;

import revsim.modeltools.objects.AbstractBlip;

public class GetBlipsOp implements NodeOp {
	
	private final List<AbstractBlip> blips = new ArrayList<AbstractBlip>();

	public void apply(PatternNode node) {
        blips.addAll(node.getLocalBlips());
	}

	public List<AbstractBlip> getBlips() {
		return blips;
	}
}
