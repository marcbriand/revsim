package revsim.rendering2;

import java.awt.Color;
import java.awt.image.BufferedImage;

import revsim.rendering.IntPoint2D;
import revsim.rendering.RenderUtil;

public class CircleRender extends BaseRender {
    private double fillTaper;
    private double lineTaper;
    private double lineWidth;
    private boolean useCentering;
    private boolean dontStomp;
    IRowMergeARGB merger = new AdditiveRowMergeRGB();

    public void setFillTaper(double fillTaper) {
	    this.fillTaper = fillTaper;
    }
    
    public void setLineTaper(double lineTaper) {
	    this.lineTaper = lineTaper;
    }
    
    public void setLineWidth(double lineWidth) {
	    this.lineWidth = lineWidth;
    }
    
    public void setUseCentering(boolean useCentering) {
		this.useCentering = useCentering;
	}

	public void setDontStomp(boolean dontStomp) {
		this.dontStomp = dontStomp;
	}

	public IntPoint2D getMinimumPatchDimensions(double r) {
    	
    	int containingR = (int)Math.round(r + fillTaper + 1.5);
    	return new IntPoint2D(2*containingR + 1, 2*containingR + 1);
    }
    
    public IntPoint2D fill(int x, int y, double r, double[][] buf) throws IllegalArgumentException {
    	return fill(x, y, r, buf, 0, 0);
    }
    
    /**
     * 
     * @param x where in target image the circle will be placed
     * @param y "
     * @param r radius of circle in pixels
     * @param buf patch buffer
     * @return where to apply the upper left corner of patch buffer to target image
     */
    public IntPoint2D fill(int x, int y, double r, double[][] buf, 
    		               int xcenter, int ycenter) throws IllegalArgumentException {
    	
    	// find containing square
    	int containingR = (int)Math.round(r + fillTaper + 1.5);
    	
    	int ulx = 0;
    	int uly = 0;
    	int lrx = 2*containingR;
    	int lry = 2*containingR;
    	int cx = containingR;
    	int cy = containingR;
    	
    	if (useCentering) {
    		ulx = xcenter - containingR;
    		if (ulx < 0)
    			throw new IllegalArgumentException("extending neg past 0 column");
    		uly = ycenter - containingR;
    		if (uly < 0)
    			throw new IllegalArgumentException("extending neg past 0 row");
    		lrx = ulx + 2*containingR;
    		lry = uly + 2*containingR;
    		cx = xcenter;
    		cy = ycenter;
    	}
    	
    	int numBufRows = buf.length;
    	if (numBufRows == 0)
    		throw new IllegalArgumentException("buf has no rows");
    	int numBufCols = buf[0].length;
    	
    	if (numBufRows < lry)
    		throw new IllegalArgumentException("Not enough rows in patch");
    	
    	if (numBufCols < lrx)
    		throw new IllegalArgumentException("Not enough cols in patch");
    	
    	Util.RenderRegion rr = new Util.RenderRegion(ulx, uly, lrx, lry);
    	
    	int containedR = (int)Math.round(r/Util.sqrt2 -1.5);
    	
    	ulx = containingR - containedR;
    	uly = containingR - containedR;
    	lrx = ulx + 2*containedR;
    	lry = uly + 2*containedR;
    	
    	if (useCentering) {
    		ulx += rr.ulx;
    		uly += rr.uly;
    		lrx += rr.ulx;
    		lry += rr.uly;
    	}
    	
    	// render outer portion
    	// render upper left rect
    	for (int i = rr.uly; i < uly; i++) {
    		    for (int j = rr.ulx; j < ulx; j++) {
    		    	    if (dontStomp && buf[i][j] > 0.0)
    		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }
    		    }
    	}
    	
    	// render top rect
    	for (int i = rr.uly; i < uly; i++) {
    		    for (int j = ulx; j <= lrx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }
    		    }    			
    	}
    	
    	// render upper right rect
    	for (int i = rr.uly; i < uly; i++) {
    		    for (int j = lrx + 1; j <= rr.lrx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }
    		    }    			
    	}
    	
    	// render left rect
    	for (int i = uly; i <= lry; i++) {
    			for (int j = rr.ulx; j < ulx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }
    			}
    	}
    	
    	System.out.println("befo rr");
    	
    	// render right rect
    	for (int i = uly; i <= lry; i++) {
    			for (int j = lrx + 1; j <= rr.lrx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }
    			}
    	}
    	
    	// render lower left rect
    	for (int i = lry + 1; i <= rr.lry; i++) {
    			for (int j = rr.ulx; j < ulx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }    					
    			}
    	}
    	
    	// render lower rect
    	for (int i = lry + 1; i <= rr.lry; i++) {
    			for (int j = ulx; j <= lrx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }        					
    			}
    	}
    	
    	// render lower right rect
    	for (int i = lry + 1; i < rr.lry; i++) {
    			for (int j = lrx + 1; j <= rr.lrx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    			        int diffx = j - cx;
    			        int diffy = i - cy;
    			        double dist = Math.sqrt(diffx*diffx + diffy*diffy);
    			        if (dist < r)
    			        	buf[i][j] = 1.0;
    			        else if (dist < r + fillTaper) {
    			        	double p = (dist - r)/fillTaper;
    			        	buf[i][j] = 1.0 - p;
    			        }        					    					
    			}
    	}
    	
        // render inner rect
    	for (int i = uly; i <= lry; i++) {
    			for (int j = ulx; j <= lrx; j++) {
		    	    if (dontStomp && buf[i][j] > 0.0)
		    	    	continue;
    					buf[i][j] = 1.0;
    			}
    	}
    	
    	return new IntPoint2D(x - containingR, y - containingR);
    }
    
    @Override
    protected IRowMergeARGB getMerger() {
    	return merger;
    }
    
    void setMerger(IRowMergeARGB m) {
    	merger = m;
    }
    
    public void fill(int x, int y, double r, Color c, BufferedImage bim) {
    	
    	IntPoint2D dim = getMinimumPatchDimensions(r);
    	
    	double[][] buf = new double[dim.y][dim.x];
    	
    	IntPoint2D loc = this.fill(x, y, r, buf);
    	
    	this.merge(loc, dim, buf, c, bim);
    	
    }
}
