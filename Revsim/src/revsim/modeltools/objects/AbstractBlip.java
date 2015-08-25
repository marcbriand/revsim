/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.modeltools.objects;

import java.util.HashMap;
import java.util.Map;

import revsim.modeltools.functions.Constant;
import revsim.modeltools.functions.Function;

/**
 *
 * @author Marc
 */
public abstract class AbstractBlip implements Cloneable {
    private float x;
    private float y;
//    private float xvel;
//    private float yvel;
    private Function xfunc = new Constant(0.0f);
    private Function yfunc = new Constant(0.0f);
    
	private long life = Long.MAX_VALUE;


	private MColor color = new MColor();
    private long birthday = -1;
    private float scale = 1.0f;
    
	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public long getLife() {
		return life;
	}

	public void setLife(long life) {
		this.life = life;
	}
	
	public float getX() {
		
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public void setXfunc(Function f) {
		xfunc = f;
	}
	
	public void setYfunc(Function f) {
        yfunc = f;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
/*
	public float getXvel() {
		return xvel;
	}

	public void setXvel(float xvel) {
		this.xvel = xvel;
	}

	public float getYvel() {
		return yvel;
	}

	public void setYvel(float yvel) {
		this.yvel = yvel;
	}
*/
    public Map<String, String> attributes = new HashMap<String, String>();

    @Override
    public abstract Object clone();
    
    public abstract int [][] render (float ppu);

    public boolean isDead (long currentFrame) {
        return currentFrame - birthday >= life;
    }
    
    protected void getAttributes (Map<String, String> target) {
    	for (String key : attributes.keySet()) {
    		String value = attributes.get(key);
    		String cloned = (value == null) ? null : new String(value);
    		target.put(key, cloned);
    	}
    }
    
    public void inc (long frame) {
    	x = xfunc.exec(frame, birthday);
    	y = yfunc.exec(frame, birthday);
    	color.inc(frame, birthday);
    }
    
    public MColor getColor() {
    	return color;
    }
    
    public void setColor(MColor color) {
    	this.color = (MColor)color.clone();
    }
}
