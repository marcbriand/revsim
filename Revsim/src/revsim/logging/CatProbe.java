package revsim.logging;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import revsim.rendering.RenderUtil;

public class CatProbe {
	
	private final int y;
	private final int sectionSize;
	private static CatProbe instance;
	
	private CatProbe (int sectionSize, int y) {
		
		this.y = y;
		this.sectionSize = sectionSize;
		
		if (sectionSize <= 0) {
			throw new IllegalArgumentException("sectionSize must be > 0");
		}

		if (sectionSize > 255) {
			throw new IllegalArgumentException("sectionSize must be <= 255");
		}
		
	}
	
	public static CatProbe getInstance (int sectionSize, int y) {
		if (instance == null) {
			instance = new CatProbe(sectionSize, y);
		}
		return instance;
	}
	
	private int findBin (double avgColor) {
		
		double sec = avgColor/sectionSize;
		int bin = (int)Math.floor(sec);
		return bin;
	}
	
	private void report (int lastBin, int count) {
		System.out.print("|" + lastBin + ":" + count);
	}
	
	public void probe (BufferedImage bim) {
		int lastBin = -1;
		int count = 0;
		for (int i = 0; i < bim.getWidth(); i++) {
			int[] color = RenderUtil.splitColor4(bim.getRGB(i, y));
			double avgColor = (color[1] + color[2] + color[3])/3.0;
			avgColor = (color[0]/255.0)*avgColor;
			int bin = findBin(avgColor);
			if (bin != lastBin) {
				report(lastBin, count);
				lastBin = bin;
				count = 0;
			}
			else {
				count++;
			}			
		}
		report(lastBin, count);
		System.out.println();
	}
	
	

}
