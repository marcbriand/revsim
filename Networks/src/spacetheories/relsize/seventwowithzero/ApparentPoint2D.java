package spacetheories.relsize.seventwowithzero;

import revsim.rendering.IntPoint2D;

public class ApparentPoint2D extends IntPoint2D {

   int apparentx;
   int apparenty;
   
   public ApparentPoint2D (int graphx, int graphy)
   {
	   super(graphx, graphy);
   }
   
   public ApparentPoint2D (int graphx, int graphy, int apparentx, int apparenty)
   {
	   super(graphx, graphy);
	   this.apparentx = apparentx;
	   this.apparenty = apparenty;
   }
}
