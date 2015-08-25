package revsim.config.objects.revsim;

import java.util.List;

import revsim.modeltools.objects.AbstractBlip;
import revsim.modeltools.objects.SphereBlip;
import revsim.mvc.view.NodeException;

public interface Transform {

	public List<List<AbstractBlip>> transform (List<AbstractBlip> blips) throws NodeException;
}
