package revsim.mvc.model;

import java.util.List;

import revsim.modeltools.objects.AbstractBlip;
import revsim.mvc.Model;

public interface BlipModel extends Model {

	public List<AbstractBlip> getBlips ();
	
}
