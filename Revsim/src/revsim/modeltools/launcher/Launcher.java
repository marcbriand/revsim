/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.modeltools.launcher;

import revsim.modeltools.objects.AbstractBlip;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

/**
 *
 * @author Marc
 */
public class Launcher extends ConfigObject {

    protected Float x;
    protected Float y;
    protected Float xvel;
    protected Float yvel;
    protected float angle;
    protected float velocity;
    protected Timer timer;
    protected BlipCreator creator;
    
    private String floatToString(Float f) {
    	return f == null ? "null" : f.toString();
    }
    
    private Float dupFloat(Float f) {
    	return f == null ? f : new Float(f.floatValue());
    }
    
    public String toString () {
    	return "Launcher:\n" +
    	         "x = " + floatToString(x) + "\n" +
    	         "y = " + floatToString(y) + "\n" +
    	         "xvel = " + floatToString(xvel) + "\n" +
    	         "yvel = " + floatToString(yvel) + "\n" +
    	         "angle = " + Float.toString(angle) + "\n" +
    	         "velocity = " + Float.toString(velocity) + "\n" + 
    	         "timer = " + timer + "\n" +
    	         "creator = " + creator + "\n";
    }

    public Launcher () {
        
    }
    
    public void tryLaunch (long currentFrame, List<AbstractBlip> blipList) {
    	if (timer == null || creator == null) {
    		return;
    	}
    	
    	if (timer.shouldLaunch(currentFrame)) {
    		AbstractBlip blip = creator.createBlip(x, y, xvel, yvel, currentFrame);
    		blipList.add(blip);
    	}
    }
    public void setTimer (Timer t) {
        timer = t;
    }

    public void setCreator (BlipCreator c) {
        creator = c;
    }

	@Override
	public ConfigObject duplicate() {
		Launcher ret = new Launcher();
		ret.x = dupFloat(x);
		ret.y = dupFloat(y);
		ret.xvel = dupFloat(xvel);
		ret.yvel = dupFloat(yvel);
		ret.angle = angle;
		ret.velocity = velocity;
		ConfigObject ctimer = (ConfigObject)timer;
		ret.timer = (Timer)ctimer.duplicate();
		ConfigObject ccreator = (ConfigObject)creator;
		ret.creator = (BlipCreator)ccreator.duplicate();
		return ret;
	}

	@Override
	public Object getLocal(String key) {
		if ("x".equals(key)) {
			return x;
		}
		if ("y".equals(key)) {
			return y;
		}
		if ("xvel".equals(key)) {
			return xvel;
		}
		if ("yvel".equals(key)) {
			return yvel;
		}
		if ("angle".equals(key)) {
			return new Float(angle);
		}
		if ("velocity".equals(key)) {
			return new Float(velocity);
		}
		if ("timer".equals(key)) {
			ConfigObject ctimer = (ConfigObject)timer;
			return ctimer;
		}
		if ("creator".equals(key)) {
			ConfigObject temp = (ConfigObject)creator;
			return temp;
		}
		return null;
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if ("timer".equals(key)) {
			if (!(obj instanceof ConfigObject)) {
				throw new ConfigException("timer must be a ConfigObject");
			}
			if (!(obj instanceof Timer)) {
				throw new ConfigException("timer must be an instance of Timer");
			}
			timer = (Timer)obj;
			return;
		}
		if ("creator".equals(key)) {
			if (!(obj instanceof ConfigObject)) {
				throw new ConfigException("creator must be ConfigObject");
			}
			if (!(obj instanceof BlipCreator)) {
				throw new ConfigException("creator must be BlipCreator");
			}
			creator = (BlipCreator)obj;
			return;
		}
		if (!(obj instanceof Float)) {
			throw new ConfigException("object must be a Float");
		}
		Float fobj = (Float)obj;
		if ("x".equals(key)) {
			x = fobj;
			return;
		}
		if ("y".equals(key)) {
			y = fobj;
			return;
		}
		if ("xvel".equals(key)) {
			xvel = fobj;
			return;
		}
		if ("yvel".equals(key)) {
			yvel = fobj;
			return;
		}
		if ("angle".equals(key)) {
			angle = fobj;
			double rads = 2.0*angle*Math.PI/360.0;
	        xvel = (float)(velocity*Math.cos(rads));
	        yvel = (float)(velocity*Math.sin(rads));
			return;
		}
		if ("velocity".equals(key)) {
			velocity = fobj;
			double rads = 2.0*angle*Math.PI/360.0;
	        xvel = (float)(velocity*Math.cos(rads));
	        yvel = (float)(velocity*Math.sin(rads));
	        return;
		}
		
		throw new ConfigException("unknown property " + key);
		
	}
}
