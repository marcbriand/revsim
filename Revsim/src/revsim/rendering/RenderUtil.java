/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.rendering;

import java.awt.image.BufferedImage;

import revsim.logging.CatProbe;
import revsim.modeltools.objects.AbstractBlip;

/**
 *
 * @author Marc
 */
public class RenderUtil {
    public static final int kByte1 = 256;
    public static final int kByte2 = kByte1*256;
    public static final int kByte3 = kByte2*256;

    public static double degreesToRads (double degs) {
        return 2*Math.PI*degs/360.0;
    }

    public static double normalizeAngle (double degs) {
        long wraps = Math.round(degs/360.0);
        return degs - wraps*360;
    }

    public static int [] splitColor (int c) {
        int [] retVal = new int[3];
        retVal[0] = (c & 0x00ff0000)/65536;
        retVal[1] = (c & 0x0000ff00)/256;
        retVal[2] = c & 0x000000ff;
        return retVal;
    }

    public static int [] splitColor4 (int c) {
        int [] retVal = new int[4];
        String cHex = Integer.toHexString(c);
        long topVal = Long.parseLong(cHex, 16);
        topVal = topVal & 0xff000000;
        retVal[0] = (int)(topVal/kByte3);
        retVal[1] = (c & 0x00ff0000)/kByte2;
        retVal[2] = (c & 0x0000ff00)/kByte1;
        retVal[3] = c & 0x000000ff;
        return retVal;
    }
    
    public static int [] multRGBAtoARGB (float[] data, double intensity) {
        int [] retVal = new int[4];
        retVal[0] = (int)(255*data[3]);
        for (int i = 1; i < 4; i++) {
            retVal[i] = (int)(255*data[i-1]*intensity);
        }
        return retVal;
    }

    public static int combineColor (int [] color) {
        return 65536*color[0] + 256*color[1] + color[2];
    }

    public static int combineColor4 (int [] color) {
        return kByte3*color[0] + kByte2*color[1] + kByte1*color[2] + color[3];
    }
    
    public static double toAngle (double x, double y) {
        return Math.atan2(y, x);    	
    }
    
    public static int rangeLimit (int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 255) {
            return 255;
        }
        return n;
    }
    
    public static int zeroLimit (int n) {
    	if (n < 0) {
    		return 0;
    	}
    	return n;
    }
  
    public static int[] averageMerge4ARGB (int [] oldColor, int [] newColor) {
        int [] retVal = new int[oldColor.length];
        retVal[0] = 255;
        for (int i = 1; i < 4; i++) {
            int sum = newColor[i] > 0 ? (oldColor[i] + newColor[i])/2 : oldColor[i];
            sum = rangeLimit(sum);
            retVal[i] = sum;
        }
        return retVal;

    }
    
    public static int [] normalMerge4ARGB (int [] oldColor, int [] newColor, float solar) {
        int [] retVal = new int[oldColor.length];
        int max = 0;
        retVal[0] = 255;
        for (int i = 1; i < 4; i++) {
            int sum = oldColor[i] + newColor[i];
            if (sum > max) {
                max = sum;
            }
            if (sum < 0) {
                sum = 0;
            }
            if (sum > 255) {
                int excess = sum - 255;
                sum = 255 - (int)(solar*excess);
                if (sum < 0) {
                    sum = 0;
                }
            }
            retVal[i] = sum;
        }
        return retVal;
    }

    public static int [] merge4ARGB (int [] oldColor, int [] newColor, MergeMethod mergeMethod, float solar) {
        if (mergeMethod == MergeMethod.Normal) {
            return RenderUtil.normalMerge4ARGB(oldColor, newColor, solar);
        }
        if (mergeMethod == MergeMethod.Average) {
            return RenderUtil.averageMerge4ARGB(oldColor, newColor);
        }
        return RenderUtil.normalMerge4ARGB(oldColor, newColor, solar);

    }
    
    public static void printIntArray (int[] array) {
    	for (int i = 0; i < array.length; i++) {
    		System.out.print(array[i] + ":");
    	}
    	System.out.println();
    }
    
    public static void printPatch (int x, int y, int[][] patch) {
    	System.out.println("------------------------- patch ---------------------------");
    	System.out.println("x = " + x + ", y = " + y);
    	for (int i = 0; i < patch.length; i++) {
    		System.out.println("*** row " + i + " ***");
    		for (int j = 0; j < patch[0].length; j += 4) {
    			for (int k = 0; k < 4; k++) {
    				System.out.print(":" + patch[i][j+k]);
    			}
    			System.out.println();
    		}
    	}
    }
    
    public static void printPatchCompare (int x, int y, int[][] pa, int[][] pb) {
    	System.out.println("-------------------- patch compare ----------------------------");
    	System.out.println("x = " + x + ", y = " + y);
    	if (pa.length != pb.length) {
    		throw new IllegalArgumentException("patch heights not equal");
    	}
    	if (pa[0].length != pb[0].length) {
    		throw new IllegalArgumentException("patch widths not equal");
    	}
    	for (int i = 0; i < pa.length; i++) {
    		for (int j = 0; j < pb.length; j++) {
    			System.out.println(i + "," + j + ":");
    			for (int k = 0; k < 4; k++) {
    				System.out.print(":" + pa[i][4*j+k]);
    			}
    			System.out.println();
    			for (int k = 0; k < 4; k++) {
    				System.out.print(":" + pb[i][4*j+k]);
    			}
    			System.out.println();
    		}
    	}
    }

    private static void initPixelARGB (int index, int [] row) {
    	row[index++] = 255;
    	row[index++] = 0;
    	row[index++] = 0;
    	row[index] = 0;    	
    }
    
    private static void initRow4ARGB (int [] row) {
		for (int j = 0; j < row.length; j += 4) {
			initPixelARGB(j, row);
		}    	
    }

    public static int[][] createArray4ARGB (int w, int h) {
		int [][] rows = new int[h][];
		for (int i = 0; i < rows.length; i++) {
			int[] row = new int[4*w];
			initRow4ARGB(row);
			rows[i] = row;
		}
		return rows;
    }
    
    public static int[][] createEncodedArray (int w, int h) {
    	int [][] rows = new int[h][];
    	for (int i = 0; i < rows.length; i++) {
    		int[] row = new int[w];
    		for (int j = 0; j < row.length; j++) {
    			row[j] = 0;
    		}
    		rows[i] = row;
    	}
    	return rows;
    }

    public static int[][] merge4PatchARGB (int[][] oldPatch, int[][] newPatch, MergeMethod mergeMethod, float solar) {
    	int[][] ret = RenderUtil.createArray4ARGB(oldPatch[0].length/4, oldPatch.length);
    	for (int i = 0; i < oldPatch.length; i++) {
    		int rowLength = oldPatch[0].length;
    		for (int j = 0; j < rowLength; j += 4) {
    			int[] oldColor = new int[4];
    			int[] newColor = new int[4];
    			for (int k = 0; k < 4; k++) {
    				oldColor[k] = oldPatch[i][j+k];
    				newColor[k] = newPatch[i][j+k];
    			}
    			int[] merged = RenderUtil.merge4ARGB(oldColor, newColor, mergeMethod, solar);
    			for (int k = 0; k < 4; k++) {
    				ret[i][j+k] = merged[k];
    			}
    		}
    	}
    	return ret;
    }

    public static int[][] encodePatch4 (int[][] patch) {
    	int[][] ret = RenderUtil.createEncodedArray(patch[0].length/4, patch.length);
    	for (int i = 0; i < patch.length; i++) {
    		int rowLength = patch[0].length;
    		int column = 0;
    		for (int j = 0; j < rowLength; j += 4) {
    			int[] color = new int[4];
    			for (int k = 0; k < 4; k++) {
    				color[k] = patch[i][j+k];
    			}
				int combined = RenderUtil.combineColor4(color);
				ret[i][column] = combined;
    			column++;
    		}
    	}
    	return ret;
    }
    
    public static void applyPatchToBitmapOld (int[][] patch, int i, int j, BufferedImage bim) {
    	int patchHeight = patch.length;
    	int patchWidth = patch[0].length;
    	int centerx = patchHeight/2;
    	int centery = patchWidth/2;
    	
    	for (int pi = 0; pi < patchHeight; pi++) {
    		int x = i + pi - centerx;
    		if (x < 0 || x >= bim.getWidth()) {
    			continue;
    		}
    		for (int pj = 0; pj < patchWidth; pj++) {
    			int y = j + pj - centery;
    			if (y < 0 || y >= bim.getHeight()) {
    				continue;
    			}
    			int color = patch[pi][pj];
    			bim.setRGB(x, y, color);
    		}
    	}
    	
    }
    
    public static void applyPatchToBitmap (int[][] patch, int i, int j, BufferedImage bim) {
    	int patchHeight = patch.length;
    	int patchWidth = patch[0].length;
    	int centery = patchHeight/2;
    	int centerx = patchWidth/2;
    	
    	for (int pi = 0; pi < patchHeight; pi++) {
    		int y = j + pi - centery;
    		if (y < 0 || y >= bim.getHeight()) {
    			continue;
    		}
    		for (int pj = 0; pj < patchWidth; pj++) {
    			int x = i + pj - centerx;
    			if (x < 0 || x >= bim.getWidth()) {
    				continue;
    			}
    			int color = patch[pi][pj];
    			bim.setRGB(x, y, color);
    		}
    	}
    }
    
    public static void initImageWithColor (BufferedImage bim, int c) {
        int width = bim.getWidth();
        int height = bim.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bim.setRGB(i, j, c);
            }
        }
    }
    
    public static void renderBlip (int x, int y, float ppu, AbstractBlip blip, MergeMethod mergeMethod, float solar, 
    		BufferedImage bim) {
    	
    	int bimho2 = bim.getHeight()/2;
    	
//    	CatProbe.getInstance(10, bimho2).probe(bim);
    	
    	int[][] newPatch = blip.render(ppu);
    	
    	int h = newPatch.length;
    	int w = newPatch[0].length/4;
    	
        int wover2 = w/2;
        int startx = x - wover2;
        int endx = x + wover2;
        
        int hover2 = h/2;
        int starty = y - hover2;
        int endy = y + hover2;
        
        int[][] oldPatch = RenderUtil.createArray4ARGB(w, h);
        
        for (int i = starty; i <= endy; i++) {
        	int pi = i - starty;
        	pi = Math.min(pi, h-1);
            if (i < 0 || i >= bim.getHeight()) {
                continue;
            }
            for (int j = startx; j <= endx; j++) {
            	int pj = j - startx;
            	pj = Math.min(pj, w-1);
                if (j < 0 || j >= bim.getWidth()) {
                    continue;
                }

                int [] oldColor = RenderUtil.splitColor4(bim.getRGB(j, i));
                for (int k = 0; k < 4; k++) {
                	oldPatch[pi][4*pj+k] = oldColor[k];
//                	oldPatch[pj][4*pi+k] = oldColor[k];
                }
                
            }
        }
        
        int[][] mergedPatch = RenderUtil.merge4PatchARGB(oldPatch, newPatch, mergeMethod, solar);
        int[][] encodedPatch = RenderUtil.encodePatch4(mergedPatch);
        RenderUtil.applyPatchToBitmap(encodedPatch, x, y, bim);
        
    }

    public static int[] splitByRatioPreserveLeft (int src, double ratio) {
        if (ratio < 0.0 || ratio > 1.0) {
        	throw new IllegalArgumentException("ratio must be between 0.0 and 1.0");
        }
        int[] ret = new int[2];
        ret[0] = (int)(src*ratio);
        ret[1] = src - ret[0];
        return ret;
    }
    
    public static int[] splitByRatioPreserveRight (int src, double ratio) {
        if (ratio < 0.0 || ratio > 1.0) {
        	throw new IllegalArgumentException("ratio must be between 0.0 and 1.0");
        }
        int[] ret = new int[2];
        ret[1] = (int)(src*(1.0-ratio));
        ret[0] = src - ret[1];
        return ret;
    }
    
    public static int[] copyPixelFromPatch4 (int[][] patch, int srcX, int srcY) {
    	int ret[] = new int[4];
    	if (patch.length == 0) {
    		throw new IllegalArgumentException("empty patch");
    	}
    	int pRowWidth = patch[0].length/4;
    	if (srcX < 0 || srcX >= pRowWidth || srcY < 0 || srcY  >= patch.length) {
    		throw new IllegalArgumentException("srcX or srcY out of bounds");
    	}
    	for (int k = 0; k < 4; k++) {
    		ret[k] = patch[srcY][4*srcX+k];
    	}
    	return ret;
    }
    
    public static void copyPixelToPatch4 (int[] pixel, int destX, int destY, int[][] patch) {
    	if (patch.length == 0) {
    		throw new IllegalArgumentException("patch is empty");
    	}
    	int pRowWidth = patch[0].length/4;
    	if (destX < 0 || destX >= pRowWidth || destY < 0 || destY  >= patch.length) {
    		throw new IllegalArgumentException("destX or destY out bounds");
    	}
    	for (int k = 0; k < 4; k++) {
    		patch[destY][4*destX + k] = pixel[k];
    	}
    }
    
    public static void flipHorizontal4 (int[][] patch, int x, int y, int w, int h) {
    	if (patch.length == 0) {
    		return;
    	}
    	int pRowWidth = patch[0].length/4;
    	if (x < 0 || x >= pRowWidth || y < 0 || y >= patch.length) {
    		throw new IllegalArgumentException("x or y out of bounds");
    	} 
    	if (w < 0 || x + w > pRowWidth || h < 0 || y + h > patch.length) {
    		throw new IllegalArgumentException("h or w out of bounds");
    	}
    	int halfW = w/2;
    	for (int i = y; i < y + h; i++) {
    		int lj = 0; int rj = w-1;
    		for (int j = 0; j < halfW; j++) {
    			int[] lSrc = copyPixelFromPatch4(patch, x + lj, i);
    			int[] rSrc = copyPixelFromPatch4(patch, x + rj, i);
    			copyPixelToPatch4(lSrc, x + rj, i, patch);
    			copyPixelToPatch4(rSrc, x + lj, i, patch);
    			lj ++;
    			rj --;
    		}
    	}
    }
    
    public static void flipVertical4 (int[][] patch, int x, int y, int w, int h) {
    	if (patch.length == 0) {
    		return;
    	}
    	int pRowWidth = patch[0].length/4;
    	if (x < 0 || x >= pRowWidth || y < 0 || y >= patch.length) {
    		throw new IllegalArgumentException("x or y out of bounds");
    	} 
    	if (w < 0 || x + w > pRowWidth || h < 0 || y + h > patch.length) {
    		throw new IllegalArgumentException("h or w out of bounds");
    	}
    	int[][] topPatch = RenderUtil.createArray4ARGB(w, 1);
    	int[][] botPatch = RenderUtil.createArray4ARGB(w, 1);
    	int halfh = h/2;
    	int ti = 0; int bi = h-1;
    	for (int i = 0; i < halfh; i++) {
    		topPatch = getSubpatch(patch, x, ti + y, w, 1);
    		botPatch = getSubpatch(patch, x, bi + y, w, 1);
    		putSubpatch(topPatch, x, bi + y, patch);
    		putSubpatch(botPatch, x, ti + y, patch);
    		ti++;
    		bi--;
    	} 
    	
    }
    
    public static int[][] getSubpatch (int[][] patch, int x, int y, int w, int h) {
    	if (patch.length == 0) {
    		throw new IllegalArgumentException("empty patch");
    	}
    	int pRowWidth = patch[0].length/4;
    	if (x < 0 || x >= pRowWidth || y < 0 || y >= patch.length) {
    		throw new IllegalArgumentException("x or y out of bounds");
    	} 
    	if (w < 0 || x + w > pRowWidth || h < 0 || y + h > patch.length) {
    		throw new IllegalArgumentException("h or w out of bounds");
    	}
    	int [][] ret = createArray4ARGB(w, h);
    	int di = 0;
    	for (int i = y; i < y + h; i++) {
    		int dj = 0;
    		for (int j = x; j < x + w; j++) {
    			int[] pixel = copyPixelFromPatch4(patch, j, i);
    			copyPixelToPatch4(pixel, dj, di, ret);
    			dj++;
    		}
    		di++;
    	}
    	return ret;
    }
    
    public static void putSubpatch (int[][] srcPatch, int x, int y, int[][]destPatch) {
    	if (srcPatch.length == 0) {
    		throw new IllegalArgumentException("empty src patch");
    	}
    	if (destPatch.length == 0) {
    		throw new IllegalArgumentException("empty dest patch");
    	}
    	int h = srcPatch.length;
    	int w = srcPatch[0].length/4;
    	int dRowLength = destPatch[0].length/4;
    	if (x < 0 || x + w > dRowLength || y < 0 || y + h > destPatch.length) {
    		throw new IllegalArgumentException("x or y out of bounds");
    	}
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			int[] pixel = copyPixelFromPatch4(srcPatch, j, i);
    			copyPixelToPatch4(pixel, x + j, y + i, destPatch);
    		}
    	}
    }
    
   public static int[] makePixelARGB (int a, int r, int g, int b) {
	   int[] ret = new int[4];
	   ret[0] = a;
	   ret[1] = r;
	   ret[2] = g;
	   ret[3] = b;
	   return ret;
   }
   
   public static int[][] makeRectOfPixel (int[] pixel, int w, int h) {
	   int[][] ret = RenderUtil.createArray4ARGB(w, h);
	   for (int i = 0; i < h; i++) {
		   for (int j = 0; j < w; j++) {
			   for (int k = 0; k < 4; k++) {
				   ret[i][4*j + k] = pixel[k];
			   }
		   }
	   }
	   return ret;
   }


}
