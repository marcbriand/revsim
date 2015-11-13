package spacetheories.relsize.p4xyzoffset1;

import rect.NetworkModel;
import rect.Node;
import spacetheories.relsize.XYZ;

public class ApparentNode extends Node {
  
	public XYZ apparentPoint = new XYZ();
	
	ApparentNode(int id, long birthday, NetworkModel nm)
	{
		super(id, birthday, nm);
	}

}
