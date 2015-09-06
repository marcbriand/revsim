package revsim.rendering2;

import java.awt.Color;
import java.awt.image.BufferedImage;

import revsim.rendering.IntPoint2D;
import revsim.rendering.RenderUtil;

public class BaseRender {

	protected IRowMergeARGB getMerger() {
		return null;
	}
	
    private int valueAndColorToARGB(double value, Color c) {
    	int[] argb = new int[4];
    	argb[0] = (int)(c.getAlpha()*value);
    	argb[1] = (int)(c.getRed()*value);
    	argb[2] = (int)(c.getGreen()*value);
    	argb[3] = (int)(c.getBlue()*value);
    	
    	return RenderUtil.combineColor4(argb);
    }
	
	protected void merge(IntPoint2D loc, IntPoint2D dim, double[][] buf, Color c, BufferedImage bim) {
    	for (int i = 0; i < buf.length; i++) {
    		int yt = loc.y + i;
    		if (yt < 0 || yt >= bim.getHeight())
    			continue;
    		
    		if (yt == 300)
    			System.out.println("hoo");
    		
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
    		getMerger().merge(oldColors, newColors, mergedColors);
    		
    		for (int j = 0; j < buf[0].length; j++) {
    			int xt = loc.x + j;
    			if (xt < 0 || xt >= bim.getWidth())
    				continue;
    			bim.setRGB(xt, yt, mergedColors[j]);
    		}
    		
    	}
		
	}
}
