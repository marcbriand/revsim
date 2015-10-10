package spacetheories.relsize.seventwowithzero;

import rect.NetworkModel;
import rect.Node;

public class ApparentNode extends Node {
  
	private int apparentX;
	private int apparentY;
	
	public int getApparentX() {
		return apparentX;
	}

	public void setApparentX(int graphX) {
		this.apparentX = graphX;
	}

	public int getApparentY() {
		return apparentY;
	}

	public void setApparentY(int graphY) {
		this.apparentY = graphY;
	}
	
	ApparentNode(int id, long birthday, NetworkModel nm)
	{
		super(id, birthday, nm);
	}

}
