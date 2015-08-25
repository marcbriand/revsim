/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.modeltools.objects;

import java.awt.Color;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.modeltools.functions.Constant;
import revsim.modeltools.functions.Function;


/**
 *
 * @author Marc
 */
public class MColor extends ConfigObject implements Cloneable {

    private Color color = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    private Function rfunc = new Constant(0.0f);
    private Function gfunc = new Constant(0.0f);
    private Function bfunc = new Constant(0.0f);
    private Function afunc = new Constant(1.0f);
    
    public String toString () {
    	float [] comps = color.getComponents(null);
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < comps.length; i++) {
    		sb.append(Float.toString(comps[i]) + " ");
    	}
    	return "MColor:\n" +
    	       " " + sb.toString();
    }

    public MColor () {
        
    }
    
    public MColor (float r, float g, float b) {
    	color = new Color(r, g, b, 1.0f);
    	rfunc = new Constant(r);
    	gfunc = new Constant(g);
    	bfunc = new Constant(b);
    	afunc = new Constant(1.0f);
    }


    @Override
    public Object clone () {

        MColor ret = new MColor();
        ret.rfunc = rfunc;
        ret.gfunc = gfunc;
        ret.bfunc = bfunc;
        ret.afunc = afunc;
        if (color == null) {
            return ret;
        }
        float [] comps = color.getRGBComponents(null);
        ret.color = new Color(comps[0], comps[1], comps[2], comps[3]);
        return ret;
    }
    
    @Override
    public ConfigObject duplicate () {

        MColor ret = new MColor();
        ret.rfunc = rfunc;
        ret.gfunc = gfunc;
        ret.bfunc = bfunc;
        ret.afunc = afunc;
        if (color == null) {
            return ret;
        }
        float [] comps = color.getRGBComponents(null);
        ret.color = new Color(comps[0], comps[1], comps[2], comps[3]);
        return ret;
    	
    }

    public Color getColor() {
        return color;
    }

    public void setColor (Color color) {
        this.color = color;
    }
    
    public float[] getFloat4RGBA() {
    	return color.getComponents(null);
    }
    
    public void setFloat4RGBA(float[] rgba) {
    	color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
    	rfunc = new Constant(rgba[0]);
    	gfunc = new Constant(rgba[1]);
    	bfunc = new Constant(rgba[2]);
    	afunc = new Constant(rgba[3]);
    }
    
    public int[] getInt4RGBA() {
    	int[] ret = new int[4];
    	float[] farray = color.getComponents(null);
    	for (int i = 0; i < ret.length; i++) {
    		ret[i] = (int)(255*farray[i]);
    	}
    	return ret;
    }
    
    public int[] getInt4ARGB() {
    	int[] ret = new int[4];
    	float[] farray = color.getComponents(null);
    	ret[0] = (int)(255*farray[3]);
    	for (int i = 1; i < 4; i++) {
    		ret[i] = (int)(255*farray[i-1]);
    	}
    	return ret;
    }

	@Override
	public Object getLocal(String key) {
		float[] farray = color.getComponents(null);
		if ("r".equals(key)) {
			return farray[0];
		}
		if ("g".equals(key)) {
			return farray[1];
		}
		if ("b".equals(key)) {
			return farray[2];
		}
		if ("a".equals(key)) {
			return farray[3];
		}
		if ("rfunc".equals(key)) {
			return rfunc;
		}
		if ("gfunc".equals(key)) {
			return gfunc;
		}
		if ("bfunc".equals(key)) {
			return bfunc;
		}
		if ("afunc".equals(key)) {
			return afunc;
		}
		
		return null;
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		
		if ("rfunc".equals(key)) {
			if (!(obj instanceof Function)) {
				throw new ConfigException("object is not a Function");
			}
			rfunc = (Function)obj;
			return;
		}
		
		if ("gfunc".equals(key)) {
			if (!(obj instanceof Function)) {
				throw new ConfigException("object is not a Function");
			}
			gfunc = (Function)obj;
			return;
		}
		
		if ("bfunc".equals(key)) {
			if (!(obj instanceof Function)) {
				throw new ConfigException("object is not a Function");
			}
			bfunc = (Function)obj;
			return;
		}
		
		if ("afunc".equals(key)) {
			if (!(obj instanceof Function)) {
				throw new ConfigException("object is not a Function");
			}
			afunc = (Function)obj;
			return;
		}
		
		float[] farray = color.getComponents(null);
		if (!(obj instanceof Float)) {
			throw new ConfigException ("object is not a Float");
		}
		float f = (Float)obj;
		System.out.println("MColor setting " + key + " to " + f);
		
		if ("r".equals(key)) {
			farray[0] = f;
			rfunc = new Constant(f);
		}
		else if ("g".equals(key)) {
			farray[1] = f;
			gfunc = new Constant(f);
		}
		else if ("b".equals(key)) {
			farray[2] = f;
			bfunc = new Constant(f);
		}
		else if ("a".equals(key)) {
			farray[3] = f;
			afunc = new Constant(f);
		}
		else {
			throw new ConfigException("unknown property " + key);
		}
		
		System.out.print("MColor.setLocal() setting color to " + farray[0] + ", " + farray[1] + ", " + farray[2] + ", " + farray[3]);
		color = new Color(farray[0], farray[1], farray[2], farray[3]);
		
	}
	
	public void inc(long frame, long birthday) {

		float r = rfunc.exec(frame, birthday);
		float g = gfunc.exec(frame, birthday);
		float b = bfunc.exec(frame, birthday);
		float a = afunc.exec(frame, birthday);
		color = new Color(r, g, b, a);

	}
};