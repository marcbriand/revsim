package spacetheories.relsize.p4xyzoffset1;

import revsim.rendering.IntPoint2D;
import spacetheories.relsize.XYZ;

public class ApparentPoint3D extends IntPoint2D {

   public XYZ apparentPoint = new XYZ();
   public int nodeId;
   
   public ApparentPoint3D (int graphx, int graphy)
   {
	   super(graphx, graphy);
	   nodeId = -1;
   }
   
   public ApparentPoint3D (int graphx, int graphy, int apparentx, int apparenty, int apparentz)
   {
	   super(graphx, graphy);
	   apparentPoint.x = apparentx;
	   apparentPoint.y = apparenty;
	   apparentPoint.z = apparentz;
   }
}
