/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.modeltools.launcher;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

/**
 *
 * @author Marc
 */
public class PeriodicTimer extends ConfigObject implements Timer {
    
    private long initialDelay = 0;
    private long period = 1;
    private long lastFrame = -1;
    
    public String toString () {
    	return "PeriodicTimer:\n" +
    	       " initialDelay = " + Long.toString(initialDelay) + "\n" +
    	       " period = " + Long.toString(period) + "\n" +
    	       " lastFrame = " + Long.toString(lastFrame) + "\n";
    }

    public PeriodicTimer () {
        
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public void setLastFrame (long lastFrame) {
        this.lastFrame = lastFrame;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean shouldLaunch(long currentFrame){
        if (currentFrame <= initialDelay) {
            return false;
        }

        if (lastFrame > -1 && currentFrame > lastFrame) {
            return false;
        }

        long excess = currentFrame - initialDelay - 1;
        return excess % period == 0;
    }

    public long getInitialDelay () {
        return initialDelay;
    }

    public long getPeriod () {
        return period;
    }

    public long getLastFrame () {
        return lastFrame;
    }

	@Override
	public ConfigObject duplicate() {
		PeriodicTimer ret = new PeriodicTimer();
		ret.initialDelay = initialDelay;
		ret.lastFrame = lastFrame;
		ret.period = period;
		return ret;
	}
/*
	@Override
	public Object getLocal(String key) {

		if ("delay".equals(key)) {
			return new Float(initialDelay);
		}
		if ("period".equals(key)) {
			return new Float(period);
		}
		if ("lastFrame".equals(key)) {
			return new Float(lastFrame);
		}
		return null;

	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {

		if (!(obj instanceof Float)) {
			throw new ConfigException("obj is not a Float");
		}
		System.out.println("PeriodicTimer setLocal, key = " + key + " type = " + obj.getClass().getName());
		Float fobj = (Float)obj;
		long value = (long)fobj.floatValue();
		if ("delay".equals(key)) {
			initialDelay = value;
			return;
		}
		if ("period".equals(key)) {
			period = value;
			return;
		}
		if ("lastFrame".equals(key)) {
			lastFrame = value;
			return;
		}
		throw new ConfigException("unknown property " + key);

	}
*/
}