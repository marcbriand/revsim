/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.modeltools.objects;

import java.util.HashMap;
import java.util.Map;

import revsim.rendering.RenderUtil;

/**
 *
 * @author Marc
 */
public class SphereBlip extends AbstractBlip implements Cloneable {
    
    public SphereBlip () {
    	
    }
    
    private SphereBlip (SphereBlip other) {
    	setX(other.getX());
    	setY(other.getY());
 //   	setXvel(other.getXvel());
 //   	setYvel(other.getYvel());
    	setLife(other.getLife());
    	this.setColor((MColor)other.getColor().clone());
    	setBirthday(other.getBirthday());
    	setScale(other.getScale());

    	other.getAttributes(this.attributes);
    }

    @Override
    public Object clone () {
        return new SphereBlip(this);
    }
    
    private void initPixel (int index, int [] row) {
    	row[index++] = 0;
    	row[index++] = 0;
    	row[index++] = 0;
    	row[index] = 255;
    }
    
    private void initRow (int [] row) {
		for (int j = 0; j < row.length; j += 4) {
			initPixel(j, row);
		}    	
    }
    
    private int[][] createArray (int boxWidth) {
		int [][] rows = new int[boxWidth][];
		for (int i = 0; i < rows.length; i++) {
			int[] row = new int[4*boxWidth];
			initRow(row);
			rows[i] = row;
		}
		return rows;
    }

	public int[][] render(int boxWidth) {
		int [][] rows = createArray(boxWidth);
		
        int wover2 = boxWidth/2;
        int startx = -wover2;
        int endx = wover2;
        int starty = -wover2;
        int endy = wover2;
        float[] data = getColor().getColor().getRGBComponents(null);

        for (int i = startx; i <= endx; i++) {
        	int pi = i - startx;
        	pi = Math.min(pi, boxWidth-1);
            for (int j = starty; j <= endy; j++) {
            	int pj = j - starty;
            	pj = Math.min(pj, boxWidth-1);
                double dist = Math.sqrt(i*i + j*j);
            	
                double intensity = (wover2 - dist)/wover2;
                if (intensity < 0.0) {
                    intensity = 0.0;
                }

                int [] newColor = RenderUtil.multRGBAtoARGB(data, intensity);
                for (int k = 0; k < 4; k++) {
                	rows[pi][4*pj+k] = newColor[k];
                }
            }
        }
        return rows;

	}

	public int[] getExtent (float ppu) {
		int [] ret = new int[2];
		ret[0] = (int)(getScale()*ppu*2);
		ret[1] = ret[0];
		return ret;
	}

	@Override
	public int[][] render (float ppu) {
		int [] extent = getExtent(ppu);
		return render(extent[0]);
	}

}
