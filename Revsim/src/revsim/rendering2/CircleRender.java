package revsim.rendering2;

import java.awt.Color;
import java.awt.image.BufferedImage;

import revsim.rendering.IntPoint2D;
import revsim.rendering.RenderUtil;

public class CircleRender {
    private double fillTaper;
    private double lineTaper;
    private double lineWidth;
    IRowMergeARGB merger = new AdditiveRowMergeARGB();

    public void setFillTaper(double fillTaper) {
	    this.fillTaper = fillTaper;
    }
    
    public void setLineTaper(double lineTaper) {
	    this.lineTaper = lineTaper;
    }
    
    public void setLineWidth(double lineWidth) {
	    this.lineWidth = lineWidth;
    }
    
    public IntPoint2D getMinimumPatchDimensions(double r) {
    	
    	int containingR = (int)Math.round(r + fillTaper + 1.5);
    	return new IntPoint2D(2*containingR + 1, 2*containingR + 1);
    }
    
    /**
     * 
     * @param x where in target image the circle will be placed
     * @param y "
     * @param r radius of circle in pixels
     * @param buf patch buffer
     * @param pd patch dimensions indicating number of rows and cols in supplied patch
     * @return where to apply the upper left corner of patch buffer to target image
     */
    public IntPoint2D fill(int x, int y, double r, double[][] buf, IntPoint2D pd) throws IllegalArgumentException {
    	
    	// find containing square
    	int containingR = (int)Math.round(r + fillTaper + 1.5);
    	
    	int ulx = 0;
    	int uly = 0;
    	int lrx = 2*containingR;
    	int lry = 2*containingR;
    	int cx = containingR;
    	int cy = containingR;
    	
    	if (pd.y < lry)
    		throw new IllegalArgumentException("Not enough rows in patch");
    	
    	if (pd.x < lrx)
    		throw new IllegalArgumentException("Not enough cols in patch");
    	
    	Util.RenderRegion rr = new Util.RenderRegion(ulx, uly, lrx, lry);
    	
    	int containedR = (int)Math.round(r/Util.sqrt2 -1.5);
    	
    	ulx = containingR - containedR;
    	uly = containingR - containedR;
    	lrx = ulx + 2*containedR;
    	lry = uly + 2*containedR;
    	
    	// render outer portion
    	// render upper left rect
    	for (int i = rr.uly; i < uly; i++) {
    		    for (int j = rr.ulx; j < ulx; j++) {
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
    	
    	// render right rect
    	for (int i = ulx; i <= lry; i++) {
    			for (int j = lrx + 1; j <= rr.lrx; j++) {
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
    					buf[i][j] = 1.0;
    			}
    	}
    	
    	return new IntPoint2D(x - containingR, y - containingR);
    }
    
    private int valueAndColorToARGB(double value, Color c) {
    	int[] argb = new int[4];
    	argb[0] = (int)(c.getAlpha()*value);
    	argb[1] = (int)(c.getRed()*value);
    	argb[2] = (int)(c.getGreen()*value);
    	argb[3] = (int)(c.getBlue()*value);
    	
    	return RenderUtil.combineColor4(argb);
    }
   
    public void fill(int x, int y, double r, Color c, BufferedImage bim) {
    	
    	IntPoint2D dim = getMinimumPatchDimensions(r);
    	
    	double[][] buf = new double[dim.y][dim.x];
    	
    	IntPoint2D loc = this.fill(x, y, r, buf, dim);
    	
    	for (int i = 0; i < buf.length; i++) {
    		int yt = loc.y + i;
    		if (yt < 0 || yt >= bim.getHeight())
    			continue;
    		
    		int oldColors[] = new int[dim.x];
    		int newColors[] = new int[dim.x];
    		int mergedColors[] = new int[dim.x];
    		
    		for (int j = 0; j < buf[0].length; j++) {
    			int xt = loc.x + j;
    			if (xt < 0 || xt >= bim.getWidth())
    				continue;
    			int oldColor = bim.getRGB(xt, yt);
    			oldColors[j] = oldColor;
    			
    			double newColorValue = buf[i][j];
    			int argb = valueAndColorToARGB(newColorValue, c);
    			
    			newColors[j] = argb;    			
    		}
    		merger.merge(oldColors, newColors, mergedColors);
    		
    		for (int j = 0; j < buf[0].length; j++) {
    			int xt = loc.x + j;
    			if (xt < 0 || xt >= bim.getWidth())
    				continue;
    			bim.setRGB(xt, yt, mergedColors[j]);
    		}
    		
    	}
    	
    	
    }
}
