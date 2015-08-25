package revsim.modeltools.objects;

import java.util.HashMap;
import java.util.Map;

import revsim.rendering.RenderUtil;
import static revsim.rendering.RenderUtil.*;

public class PillowBlip extends AbstractBlip {
	
	private static class Specs {
		private int cRadius;
		private int cRSquared;
		private float taperRadius;
		private float taperRadiusSquared;
		public int leftBar;
		public int rightBar;
		public int topBar;
		public int botBar;
		
		public String toString () {
			StringBuilder sb = new StringBuilder();
			sb.append("Specs:\n");
			sb.append("cRadius = " + Integer.toString(cRadius) + "\n");
			sb.append("cRadiusSquared = " + Integer.toString(cRSquared) + "\n");
			sb.append("taperRadius = " + Float.toString(taperRadius) + "\n");
			sb.append("taperRadiusSquared = " + Float.toString(taperRadiusSquared) + "\n");
			sb.append("leftBar = " + Integer.toString(leftBar) + "\n");
			sb.append("rightBar = " + Integer.toString(rightBar) + "\n");
			sb.append("topBar = " + Integer.toString(topBar) + "\n");
			sb.append("botBar = " + Integer.toString(botBar) + "\n");
			
			return sb.toString();
		}
		
		public void setCRadius (int cRadius) {
			this.cRadius = cRadius;
			this.cRSquared = cRadius*cRadius;
		}
		
		public int getCRadius () {
			return cRadius;
		}
		
		public int getCRSquared () {
			return cRSquared;
		}
		
		public void setTaperRadius (int taperRadius) {
			this.taperRadius = taperRadius;
			this.taperRadiusSquared = taperRadius*taperRadius;
		}
		
		public float getTaperRadius () {
			return taperRadius;
		}
		
		public float getTaperRadiusSquared () {
			return taperRadiusSquared;
		}
	}
	
	private float width = 1.0f;
	private float height = 1.0f;
	private float cornerRadius = 0.05f;
	private float taperRadius = 0.04f;
	private static Map<Integer, Map<Integer, double[][]>> cornerTaperCache = new HashMap<Integer, Map<Integer, double[][]>>();
	private static Map<Integer, Map<Integer, double[]>> linearTaperCache = new HashMap<Integer, Map<Integer, double[]>>();
	
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(float cornerRadius) {
		this.cornerRadius = cornerRadius;
	}
	
	public float getTaperRadius() {
		return taperRadius;
	}
	
	public void setTaperRadius (float taperRadius) {
		this.taperRadius = taperRadius;
	}

	@Override
	public Object clone() {
		PillowBlip ret = new PillowBlip();
		getAttributes(ret.attributes);
		ret.setBirthday(getBirthday());
		ret.setColor((MColor)getColor().clone());
		ret.cornerRadius = cornerRadius;
		ret.taperRadius = taperRadius;
		ret.height = height;
		ret.setLife(getLife());
		ret.setScale(getScale());
		ret.width = width;
		ret.setX(getX());
//		ret.setXvel(getXvel());
		ret.setY(getY());
//		ret.setYvel(getYvel());
		
		return ret;
	}
	
	private Specs createSpecH (int w, int h) {
		Specs ret = new Specs();
		int[] hsides = RenderUtil.splitByRatioPreserveRight(w, 0.5);
		int[] leftSide = RenderUtil.splitByRatioPreserveLeft(hsides[0], cornerRadius*2.0);
		ret.setCRadius(zeroLimit(leftSide[0]));
		ret.setTaperRadius((int)(zeroLimit(hsides[0])*taperRadius*2.0));
		ret.leftBar = zeroLimit(leftSide[1]);
		ret.rightBar = zeroLimit(hsides[1] - ret.cRadius);
		
		int[] vsides = RenderUtil.splitByRatioPreserveRight(h, 0.5);
		ret.topBar = zeroLimit(vsides[0] - ret.cRadius);
		ret.botBar = zeroLimit(vsides[1] - ret.cRadius);
		
		return ret;
	}
	
	private Specs createSpecV (int w, int h) {
		Specs ret = new Specs();
		int[] vsides = RenderUtil.splitByRatioPreserveRight(h, 0.5);
		int[] topSide = RenderUtil.splitByRatioPreserveLeft(vsides[0], cornerRadius*2.0);
		ret.setCRadius(zeroLimit(topSide[0]));
		ret.setTaperRadius((int)(zeroLimit(vsides[0])*taperRadius*2.0));
		ret.topBar = zeroLimit(topSide[1]);
		ret.botBar = zeroLimit(vsides[1] - ret.cRadius);
		
		int[] hsides = RenderUtil.splitByRatioPreserveRight(w, 0.5);
		ret.leftBar = zeroLimit(hsides[0] - ret.cRadius);
		ret.rightBar = zeroLimit(hsides[1] - ret.cRadius);
		
		return ret;
	}
	
	private double calcTaperIntensity (Specs specs, double dist) {
		float denom = specs.cRadius - specs.taperRadius;
		double ratio = (dist - specs.taperRadius)/denom;
		
		if (ratio > 1.0) {
			return 0.0;
		}
		if (ratio < 0.0) {
			return 1.0;
		}
		return 1.0 - ratio;
		
	}
	
	private double calcTaperIntensity (Specs specs, int dSq) {
        if (specs.taperRadius <= 0 && specs.cRadius <= 0) {
        	return 1.0;
        }
        if (specs.taperRadius > specs.cRadius) {
        	return 1.0;
        }
		if (dSq <= specs.taperRadiusSquared) {
			return 1.0;
		}
		double dist = Math.sqrt(dSq);
		return this.calcTaperIntensity(specs, dist);
	}
	
	private Integer getTaperKey (Specs specs) {
		return (int)(10000.0*specs.taperRadius);
	}
	
	private double[] getLinearTaperFromCache (Specs specs) {
		Map<Integer, double[]> taperMap = linearTaperCache.get(specs.cRadius);
		if (taperMap == null) {
			return null;
		}
		return taperMap.get(getTaperKey(specs));
	}
	
	private void putLinearTaperInCache (Specs specs, double[] array) {
		Map<Integer, double[]> taperMap = linearTaperCache.get(specs.cRadius);
		if (taperMap == null) {
			taperMap = new HashMap<Integer, double[]>();
			linearTaperCache.put(specs.cRadius, taperMap);
		}
		taperMap.put(getTaperKey(specs), array);
	}
	
	private double[] getLinearTaper (Specs specs) {
		double[] ret = getLinearTaperFromCache(specs);
		if (ret == null) {
			ret = calcLinearTaper(specs);
			putLinearTaperInCache(specs, ret);
		}
		return ret;
	}
	
	private double[] calcLinearTaper (Specs specs) {
		double [] ret = new double[specs.cRadius];
		for (int i = 0; i < specs.cRadius; i++) {
			if (i+1 > specs.cRadius) {
				ret[i] = 0.0;
				continue;
			}
			ret[i] = this.calcTaperIntensity(specs, (double)(i+1));
		}
		return ret;
	}
	
	private double[][] getCornerTaperFromCache (Specs specs) {
		Map<Integer, double[][]> taperMap = cornerTaperCache.get(specs.cRadius);
		if (taperMap == null) {
			return null;
		}
		Integer taperKey = (int)(10000.0*specs.taperRadius);
		return taperMap.get(taperKey);
	}
	
	private void putCornerTaperInCache (Specs specs, double[][] taper) {
		Map<Integer, double[][]> taperMap = cornerTaperCache.get(specs.cRadius);
		if (taperMap == null) {
			taperMap = new HashMap<Integer, double[][]>();
			cornerTaperCache.put(specs.cRadius, taperMap);
		}
		Integer taperKey = (int)(10000.0*specs.taperRadius);
		if (taperMap.containsKey(taperKey)) {
			throw new IllegalStateException("corner taper list is already cached");
		}
		taperMap.put(taperKey, taper);
	}
	
	private double[][] calcCornerTaper (Specs specs) {
	    double[][] ret = new double[specs.cRadius][specs.cRadius];
	    for (int i = 0; i < specs.cRadius; i++) {
			int rowWidth = specs.cRadius;
			for (int j = 0; j < rowWidth; j++) {
				int dSq = (i+1)*(i+1) + (j+1)*(j+1);
				if (dSq > specs.cRSquared) {
					ret[i][j] = 0.0;
					continue;
				}
				double intensity = calcTaperIntensity(specs, dSq);
				ret[i][j] = intensity;
			}
	    }
	    return ret;
	}
	
	private double[][] getCornerTaper (Specs specs) {
		double[][] taper = getCornerTaperFromCache(specs);
		if (taper == null) {
			taper = calcCornerTaper(specs);
			putCornerTaperInCache(specs, taper);
		}
		return taper;
	}
	
	private int[][] renderLRCorner (Specs specs) {
		int ret[][] = RenderUtil.createArray4ARGB(specs.cRadius, specs.cRadius);
		double[][] taper = getCornerTaper(specs);
		for (int i = 0; i < ret.length; i++) {
			int rowWidth = ret[0].length;
			int jd = 0;
			for (int j = 0; j < rowWidth; j += 4) {
				int dSq = (i+1)*(i+1) + (jd+1)*(jd+1);
				if (dSq > specs.cRSquared) {
					continue;
				}
				float[] data = getColor().getColor().getRGBComponents(null);
				double intensity = taper[i][jd];
				int[] pixel = RenderUtil.multRGBAtoARGB(data, intensity);
				
				for (int k = 0; k < 4; k++) {
					ret[i][j+k] = pixel[k];
				}
				
				jd++;
			}
		}
		return ret;
	}
	
	private int[][] renderHBar (Specs specs, int w, int h) {
		int ret[][] = RenderUtil.createArray4ARGB(w, h);
		float[] data = getColor().getColor().getRGBComponents(null);
		double[] taper = getLinearTaper(specs);
		for (int i = 0; i < h; i++) {
			double intensity = taper[i];
			int[] pixel = RenderUtil.multRGBAtoARGB(data, intensity);
			for (int j = 0; j < w; j++) {
				for (int k = 0; k < 4; k++) {
					ret[i][4*j+k] = pixel[k];
				}
			}
		}
		return ret;
		
	}
	
	private int[][] renderVBar (Specs specs, int w, int h) {
		int ret[][] = RenderUtil.createArray4ARGB(w, h);
		float[] data = getColor().getColor().getRGBComponents(null);
		double[] taper = getLinearTaper(specs);
		// make single-row patch
		int[][] rowPatch = RenderUtil.createArray4ARGB(w, 1);
		for (int j = 0; j < w; j++) {
			double intensity = taper[j];
			int[] pixel = RenderUtil.multRGBAtoARGB(data, intensity);
			for (int k = 0; k < 4; k++) {
				rowPatch[0][4*j+k] = pixel[k];
			}
		}
		for (int i = 0; i < h; i++) {
			RenderUtil.putSubpatch(rowPatch, 0, i, ret);
		}
		return ret;
	}
	
	private int[][] renderBottomSide (Specs specs) {
		int[][] ret = RenderUtil.createArray4ARGB(2*specs.cRadius + specs.leftBar + specs.rightBar, specs.cRadius);
        int[][] corner = renderLRCorner(specs);
        RenderUtil.putSubpatch(corner, specs.cRadius + specs.leftBar + specs.rightBar, 0, ret);
        RenderUtil.flipHorizontal4(corner, 0, 0, specs.cRadius, specs.cRadius);
        int[][] lbar = this.renderHBar(specs, specs.leftBar, specs.cRadius);
        int[][] rbar = this.renderHBar(specs, specs.rightBar, specs.cRadius);
        RenderUtil.putSubpatch(corner, 0, 0, ret);
        RenderUtil.putSubpatch(lbar, specs.cRadius, 0, ret);  
        RenderUtil.putSubpatch(rbar,  specs.cRadius + specs.leftBar,  0, ret);
		return ret;		
	}

	@Override
	public int[][] render(float ppu) {
		int w = (int)(getScale()*width*ppu);
		int h = (int)(getScale()*height*ppu);
//		int[][] ret = RenderUtil.createArray4ARGB(w, h);
		
        Specs specs = null;
        if (width > height) {
        	specs = this.createSpecH(w, h);
        }
        else {
        	specs = this.createSpecV(w, h);
        }
        int[][] ret = RenderUtil.createArray4ARGB(specs.cRadius + specs.leftBar + specs.rightBar + specs.cRadius, 
        		                                  specs.cRadius + specs.topBar + specs.botBar + specs.cRadius);
        
        int[][] hSide = renderBottomSide(specs);
        int[][] vSide = renderVBar(specs, specs.cRadius, specs.topBar + specs.botBar);
        RenderUtil.putSubpatch(vSide, 
        		               specs.cRadius + specs.leftBar + specs.rightBar, 
        		               specs.cRadius, ret);
        RenderUtil.putSubpatch(hSide, 
        		               0, 
        		               specs.cRadius + specs.topBar + specs.botBar, ret);
        RenderUtil.flipHorizontal4(vSide, 0, 0, vSide[0].length/4, vSide.length);
        RenderUtil.flipVertical4(hSide, 0, 0, hSide[0].length/4, hSide.length);
        RenderUtil.putSubpatch(hSide, 0, 0, ret);
        RenderUtil.putSubpatch(vSide, 0, specs.cRadius, ret);
        
        float[] pixelRGBA = getColor().getColor().getRGBComponents(null);
        int[] pixelARGB = RenderUtil.multRGBAtoARGB(pixelRGBA, 1.0);
        int[][] rect = RenderUtil.makeRectOfPixel(pixelARGB, 
        		                                  specs.leftBar + specs.rightBar,
        		                                  specs.topBar + specs.botBar);
        RenderUtil.putSubpatch(rect, specs.cRadius, specs.cRadius, ret);
		return ret;
	}

}
