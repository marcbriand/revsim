package revsim.rendering2;

import java.awt.Color;
import java.awt.image.BufferedImage;

import revsim.rendering.DoublePoint2D;
import revsim.rendering.IntPoint2D;

public class LineRender extends BaseRender {
    private double taper;
    private double lineWidthHalf;
    IRowMergeARGB merger = new AdditiveRowMergeRGB();
    
	public void setTaper(double taper) {
		this.taper = taper;
	}
	public void setLineWidth(double lineWidth) {
		this.lineWidthHalf = 0.5*lineWidth;
	}
	
	@Override
	protected IRowMergeARGB getMerger() {
		return merger;
	}
	
	private IntPoint2D getEndSlopPadded(int diffx, int diffy, double cos, double sin, int minPadX, int minPadY) {
		int hSlop, vSlop;
		double wid = lineWidthHalf+taper;
		if (Math.abs(diffy) <= Math.abs(diffx)) {
			hSlop = (int)Math.round(wid*sin + 1.5);
			vSlop = (int)Math.round(wid*cos + 1.5);
			
		}
		else {
			hSlop = (int)Math.round(wid*cos + 1.5);
			vSlop = (int)Math.round(wid*sin + 1.5);						
		}
		
		hSlop = Math.max(minPadX, hSlop);
		vSlop = Math.max(minPadY, vSlop);
		
		return new IntPoint2D(hSlop, vSlop);
	}
	
	private DoublePoint2D getEndSlop(int diffx, int diffy, double cos, double sin) {
		double wid = lineWidthHalf+taper;
		if (Math.abs(diffy) <= Math.abs(diffx)) {
			return new DoublePoint2D(wid*sin, wid*cos);			
		}
		else {
			return new DoublePoint2D(wid*cos, wid*sin);
		}
		
	}
	
	public static class LineInfo {
		public final double hyp;
		public final DoublePoint2D endSlop;
		public final IntPoint2D endSlopPadded;
		public final IntPoint2D minDimensions;
		public final double cos;
		public final double sin;
		
		public LineInfo(int diffx, int diffy, LineRender lr, int minPadX, int minPadY) {
			hyp = Math.sqrt(diffx*diffx + diffy*diffy);
			
			if (hyp == 0) {
				endSlopPadded = new IntPoint2D(0, 0);
				endSlop = new DoublePoint2D(0, 0);
				minDimensions = new IntPoint2D(1, 1);
				cos = 0.0;
				sin = 0.0;
				return;
			}
			
			if (Math.abs(diffy) <= Math.abs(diffx)) {
				sin = Math.abs(diffy/hyp);
				cos = Math.abs(diffx/hyp);
			}
			else {
				sin = Math.abs(diffx/hyp);
				cos = Math.abs(diffy/hyp);
			}
			endSlopPadded = lr.getEndSlopPadded(diffx, diffy, cos, sin, minPadX, minPadY);
			endSlop = lr.getEndSlop(diffx, diffy, cos, sin);
			int hextent = 2*endSlopPadded.x + Math.abs(diffx) + 1;
			int vextent = 2*endSlopPadded.y + Math.abs(diffy) + 1;
			minDimensions = new IntPoint2D(hextent, vextent);

		}
	}
	
	public LineInfo getLineInfo(int x1, int y1, int x2, int y2, int minPadX, int minPadY) {
		int diffx = x2 - x1;
		int diffy = y2 - y1;
        return new LineInfo(diffx, diffy, this, minPadX, minPadY);		
	}
	
	private IntPoint2D drawHorzLinePosSlope(LineInfo inf, int startLineX, int startLineY,
			                                int endX, int endY, int endLineX, int endLineY,
			                                double M, double B, double[][] buf) {
		// ledgeX = Me*y + Bel
		// Me = -edgeSlop.x/edgeSlop.y
		// edgeSlopPadded.x = Me*edgeSlopPadded.y + Bel
		// Bel = edgeSlopPadded.x - Me*edgeSlopPadded.y
		
		// endlineX = Me*endLineY + Ber
		// Ber = endLineX - Me*endLineY
		double Me = -inf.endSlop.x/inf.endSlop.y;
		double Bel = inf.endSlopPadded.x - Me*inf.endSlopPadded.y;
		double Ber = endLineX - Me*endLineY;
		
		for (int i = 0; i <= endY; i++) {
			double leftEdgeX = Me*i + Bel;
			double rightEdgeX = Me*i + Ber;
			
			for (int j = 0; j < endX; j++) {
				if (j >= leftEdgeX && j <= rightEdgeX) {
					double y = M*j + B;
					double deltaY = Math.abs(y - i);
					double dist = deltaY*inf.cos;
					if (dist <= lineWidthHalf)
						buf[i][j] = 1.0;
					else if (dist <= taper + lineWidthHalf) {
						double p = (dist - lineWidthHalf)/(taper);
						buf[i][j] = 1.0 - p;
					}
				}
				
			}
		}
		
		return new IntPoint2D(-startLineX, -startLineY);
		
	}
	
	private IntPoint2D drawLineHorzNegSlope(LineInfo inf, int endX, int endY, int startLineX, int startLineY, int endLineX, int endLineY,
			                                double M, double B, double[][] buf) {
		// ledgex = Me*y + Bel
		// Me = endSlop.x/endSlop.y
		// startLineX = Me*startLineY + Bel
		// Bel = startLineX - Me*startLineY
		
		
		double Me = (double)inf.endSlop.x/(double)inf.endSlop.y;
		double Bel = startLineX - Me*startLineY;
		double Ber = endLineX - Me*endLineY;
		
		for (int i = 0; i <= startLineY + inf.endSlopPadded.y; i++) {
			double leftEdgeX = Me*i + Bel;
			double rightEdgeX = Me*i + Ber;
			
			for (int j = 0; j < endX; j++) {
				if (j >= leftEdgeX && j <= rightEdgeX) {
					double y = M*j + B;
					double deltaY = Math.abs(y - i);
					double dist = deltaY*inf.cos;
					if (dist <= lineWidthHalf)
						buf[i][j] = 1.0;
					else if (dist <= taper + lineWidthHalf) {
						double p = (dist - lineWidthHalf)/taper;
						buf[i][j] = 1.0 - p;
					}
				}
				
			}
		}
		
        return new IntPoint2D(-startLineX, -endLineY);
	}
	
	public IntPoint2D drawVertLinePosSlope(LineInfo inf, int endX, int endY, int startLineX, int startLineY, 
			                               int endLineX, int endLineY,
			                               double M, double B, double[][] buf) {
		
		// topY = Me*x + Bet
		// Me = -endSlop.y/endSlop.x
		// endSlopPadded.y = Me*endSlopPadded.x + Bet
		// Bet = endSlopPadded.y - Me*endSlopPadded.x
		
		// endLineY = Me*endLineX + Beb
		// Beb = endLineY - Me*endLineX
		double Me = -inf.endSlop.y/inf.endSlop.x;
		double Bet = inf.endSlopPadded.y - Me*inf.endSlopPadded.x;
	    double Beb = endLineY - Me*endLineX;
	    
	    for (int i = 0; i < endY; i++) {
	    	for (int j = 0; j < endX; j++) {
	    		double topEdgeY = Me*j + Bet;
	    		double botEdgeY = Me*j + Beb;
	    		if (i >= topEdgeY && i <= botEdgeY) {
	    			double x = M*i + B;
	    			double deltaX = Math.abs(x - j);
	    			double dist = deltaX*inf.cos;
	    			if (dist <= lineWidthHalf)
	    				buf[i][j] = 1.0;
	    			else if (dist <= taper + lineWidthHalf) {
	    				double p = (dist - lineWidthHalf)/taper;
	    				buf[i][j] = 1.0 - p;
	    			}
	    			
	    		}
	    	}
	    }
	    
	    return new IntPoint2D(-startLineX, -startLineY);
	}
	
	public IntPoint2D drawVertLineNegSlope(LineInfo inf, int endX, int endY, int startLineX, int startLineY, 
			                               int endLineX, int endLineY, double M, double B, double[][] buf)
	{
		// topY = Me*x + Bet
		// Me = endSlop.x/endSlop.y
		// startLineY = Me*startLineX + Bet
		// Bet = startLineY - Me*startLineX
		// Beb = endLineY - Me*endLineX
		double Me = inf.endSlop.y/inf.endSlop.x;
		double Bet = startLineY - Me*startLineX;
	    double Beb = endLineY - Me*endLineX;
	    for (int i = 0; i < endY; i++) {
	    	for (int j = endX + 1; j <= startLineX + inf.endSlopPadded.x; j++) {
	    		if (j == startLineX)
	    			System.out.println("ugh");

	    		double topEdgeY = Me*j + Bet;
	    		double botEdgeY = Me*j + Beb;
	    		if (i >= topEdgeY && i <= botEdgeY) {
	    			double x = M*i + B;
	    			double deltaX = Math.abs(x - j);
	    			double dist = deltaX*inf.cos;
	    			if (dist <= lineWidthHalf)
	    				buf[i][j] = 1.0;
	    			else if (dist <= taper + lineWidthHalf) {
	    				double p = (dist - lineWidthHalf)/taper;
	    				buf[i][j] = 1.0 - p;
	    			}
	    			
	    		}
	    	}
	    }
	    return new IntPoint2D(-endLineX, -startLineY);
	}
	
	private static class LineOffsets {
		public IntPoint2D targetxy;
		public IntPoint2D cap1Offset;
		public IntPoint2D cap2Offset;
	}
	
	public LineOffsets drawLine(int x1, int y1, int x2, int y2, LineInfo inf, double[][] buf) 
	   throws IllegalArgumentException {
		
		int numBufRows = buf.length;
		if (numBufRows == 0)
			throw new IllegalArgumentException("buf has zero rows");
		
		int numBufCols = buf[0].length;
		
		if (numBufCols < inf.minDimensions.x)
			throw new IllegalArgumentException("not enough columns in patch");
		
		if (numBufRows < inf.minDimensions.y)
			throw new IllegalArgumentException("not enough rows in patch");
		
		int diffx = x2 - x1;
		int diffy = y2 - y1;
		
//		double wid = lineWidthHalf + taper;
		
		if (Math.abs(diffy) <= Math.abs(diffx)) {
						
			double M = (double)diffy/(double)diffx;

			int minx;
			int y1Virtual = y1;
			int y2Virtual = y2;
			if (x1 < x2)
				minx = x1;
			else {
				minx = x2;
				diffy = -diffy;
				y1Virtual = y2;
				y2Virtual = y1;
			}
			
			// dist = deltay*cosA
			// y1 = Mx1 + B
			// B = y1 - Mx1
						
	        int startLineX = inf.endSlopPadded.x;
			int endLineX = startLineX + Math.abs(diffx) + 1;
			int endX = endLineX + inf.endSlopPadded.x;

			if (diffy >= 0) {
				int startLineY = inf.endSlopPadded.y;
				int endLineY = startLineY + diffy;
				int endY = endLineY + inf.endSlopPadded.y;
					
				double B = startLineY - M*startLineX;

				this.drawHorzLinePosSlope(inf, startLineX, startLineY, endX, endY, endLineX, endLineY, M, B, buf);
				LineOffsets offsets = new LineOffsets();
				offsets.targetxy = new IntPoint2D(minx - inf.endSlopPadded.x, y1Virtual - inf.endSlopPadded.y);
				offsets.cap1Offset = new IntPoint2D(startLineX, startLineY);
				offsets.cap2Offset = new IntPoint2D(endLineX, endLineY);
				return offsets;
			}
			else {
				// startLineY is at lower left
				int endY = 0;
				int endLineY = inf.endSlopPadded.y;
				int startLineY = endLineY - diffy;
					
				double B = startLineY - M*startLineX;
				this.drawLineHorzNegSlope(inf, endX, endY, startLineX, startLineY, endLineX, endLineY, M, B, buf);
				LineOffsets offsets = new LineOffsets();
				offsets.targetxy = new IntPoint2D(minx - inf.endSlopPadded.x, y2Virtual - inf.endSlopPadded.y);
				offsets.cap1Offset = new IntPoint2D(startLineX, startLineY);
				offsets.cap2Offset = new IntPoint2D(endLineX, endLineY);
				return offsets;
			}
		}
		else {
			
			double M = (double)diffx/(double)diffy;
			
			int miny;
			int x1Virtual = x1;
			int x2Virtual = x2;
			if (y1 < y2)
				miny = y1;
			else {
				miny = y2;
				x1Virtual = x2;
				x2Virtual = x1;
				diffx = -diffx;
			}
			
			int startLineY = inf.endSlopPadded.y;
			int endLineY = startLineY + Math.abs(diffy) + 1;
			int endY = endLineY + inf.endSlopPadded.y;
			
			if (diffx >= 0) {
				int startLineX = inf.endSlopPadded.x;
				int endLineX = startLineX + diffx;
				int endX = endLineX + inf.endSlopPadded.x;
				
				double B = startLineX - M*startLineY;
				this.drawVertLinePosSlope(inf, endX, endY, startLineX, startLineY, endLineX, endLineY, M, B, buf);
				LineOffsets offsets = new LineOffsets();
				offsets.targetxy = new IntPoint2D(x1Virtual - inf.endSlopPadded.x, miny - inf.endSlopPadded.y);
				offsets.cap1Offset = new IntPoint2D(startLineX, startLineY);
				offsets.cap2Offset = new IntPoint2D(endLineX, endLineY);
				return offsets;
			}
			else {
				int endX = 0;
				int endLineX = inf.endSlopPadded.x;
				int startLineX = endLineX - diffx;
				
				double B = startLineX - M*startLineY;
				this.drawVertLineNegSlope(inf, endX, endY, startLineX, startLineY, endLineX, endLineY, M, B, buf);
				LineOffsets offsets = new LineOffsets();
				offsets.targetxy = new IntPoint2D(x2Virtual - inf.endSlopPadded.x, miny - inf.endSlopPadded.y);
				offsets.cap1Offset = new IntPoint2D(startLineX, startLineY);
				offsets.cap2Offset = new IntPoint2D(endLineX, endLineY);
				return offsets;
			}
		}
	}
	
	public IntPoint2D drawLine(int x1, int y1, int x2, int y2, Color c, BufferedImage bim) {
		return drawLine(x1, y1, x2, y2, c, bim, 0, 0);
	}
	
	public IntPoint2D drawLine(int x1, int y1, int x2, int y2, Color c, BufferedImage bim, int minPadX, int minPadY) {
		
		LineInfo inf = getLineInfo(x1, y1, x2, y2, minPadX, minPadY);
		double[][] buf = new double[inf.minDimensions.y][inf.minDimensions.x];
		
		LineOffsets offsets = this.drawLine(x1, y1, x2, y2, inf, buf);
		
		this.merge(offsets.targetxy, inf.minDimensions, buf, c, bim);
		
		return offsets.targetxy;
	}
	
	public IntPoint2D drawLineWithCaps(int x1, int y1, int x2, int y2, Color c, BufferedImage bim) {
		
		CircleRender cr = new CircleRender();
		cr.setMerger(new ExclusiveRowMergeRGB());
		cr.setFillTaper(taper);
		cr.setUseCentering(true);
		cr.setDontStomp(true);
		IntPoint2D circlePatchSize = cr.getMinimumPatchDimensions(lineWidthHalf);
		int minCircleXPad = circlePatchSize.x/2 + 1;
		int minCircleYPad = circlePatchSize.y/2 + 1;
		
		LineInfo inf = getLineInfo(x1, y1, x2, y2, minCircleXPad, minCircleYPad);
		double[][] buf = new double[inf.minDimensions.y][inf.minDimensions.x];

		LineOffsets offsets = this.drawLine(x1, y1, x2, y2, inf, buf);
        cr.fill(offsets.cap1Offset.x, offsets.cap1Offset.y, lineWidthHalf, buf, 
        		offsets.cap1Offset.x, offsets.cap1Offset.y);
        cr.fill(offsets.cap2Offset.x, offsets.cap2Offset.y, lineWidthHalf, buf,
        		offsets.cap2Offset.x, offsets.cap2Offset.y);
        
        this.merge(offsets.targetxy, inf.minDimensions, buf, c, bim);
        
        return offsets.targetxy;
		
	}

}
