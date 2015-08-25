package revsim.modeltools.launcher;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.modeltools.functions.Constant;
import revsim.modeltools.functions.Function;
import revsim.modeltools.functions.Linear;
import revsim.modeltools.objects.AbstractBlip;
import revsim.modeltools.objects.MColor;

public abstract class AbstractBlipCreator extends ConfigObject implements BlipCreator {
	
	protected Function xfunc;
	protected Function yfunc;
    protected long life = Long.MAX_VALUE;
    protected MColor color = new MColor();
    protected Float initX;
    protected Float initY;
    protected float scale = 1.0f;

	
	protected void endow(AbstractBlipCreator other) {
		other.xfunc = xfunc;
		other.yfunc = yfunc;
		other.life = life;
		other.color = (MColor)color.duplicate();
		other.initX = initX;
		other.initY = initY;
		other.scale = scale;
	}
	
	protected void addSuper(long currentFrame, Float x, Float y, Float xvel, Float yvel, AbstractBlip blip) {
		
		blip.setColor(color);
		blip.setLife(life);
		blip.setBirthday(currentFrame);
		blip.setScale(scale);
		
		if (x != null) {
			blip.setX(x);
			if (xvel == null) {
				blip.setXfunc(new Constant(x));
				
			}
			else {
				blip.setXfunc(new Linear(xvel, x, -Float.MAX_VALUE, Float.MAX_VALUE));
			}
		}
		else {
			if (xvel != null) {
				blip.setXfunc(new Linear(xvel, 0.0f, -Float.MAX_VALUE, Float.MAX_VALUE));
			}
		}
		
		if (y != null) {
			blip.setY(y);
			if (yvel == null) {
				blip.setYfunc(new Constant(y));
			}
			else {
				blip.setYfunc(new Linear(yvel, y, -Float.MAX_VALUE, Float.MAX_VALUE));
			}
		}
		else {
			if (yvel != null) {
			blip.setYfunc(new Linear(yvel, 0.0f, -Float.MAX_VALUE, Float.MAX_VALUE));
			}
		}
		
		if (xfunc != null) {
			blip.setXfunc(xfunc);
		}
		
		if (yfunc != null) {
			blip.setYfunc(yfunc);
		}
		
		if (initX != null) {
			blip.setX(initX);
		}
		
		if (initY != null) {
			blip.setY(initY);
		}
		
	}

	@Override
	public abstract AbstractBlip createBlip(Float x, Float y, Float xvel, Float yvel, long currentFrame);
	
	@Override
	public Object getLocal(String key) {
		if ("scale".equals(key)) {
			return scale;
		}
	    if ("xfunc".equals(key)) {
		    return xfunc;
	    }
	    if ("yfunc".equals(key)) {
		    return yfunc;
	    }
		if ("color".equals(key)) {
			return color;
		}
		if ("life".equals(key)) {
			return life;
		}
		if ("x".equals(key)) {
			return initX;
		}
		if ("y".equals(key)) {
			return initY;
		}
		if ("xvel".equals(key)) {
			if (xfunc == null) {
				return new Float(0L);
			}
			if (xfunc instanceof Linear) {
				Linear lin = (Linear)xfunc;
				return lin.getM();
			}
			return new Float(0L);
		}
		if ("yvel".equals(key)) {
			if (yfunc == null) {
				return new Float(0L);
			}
			if (yfunc instanceof Linear) {
				Linear lin = (Linear)yfunc;
				return lin.getM();
			}
			return new Float(0L);
		}
	    return null;
	}
	
	private void setLoc(boolean isX, float val) {
		if (isX) {
			initX = val;
		}
		else {
			initY = val;
		}
	}
	
	private void setFunc(boolean isX, Function func) {
		if (isX) {
			xfunc = func;
		}
		else {
			yfunc = func;
		}
	}
	
	private void setPrimitiveLoc(boolean isX, Object value) throws ConfigException {
		String name = isX ? "x" : "y";
		String funcName = isX ? "xfunc" : "yfunc";
		if (!(value instanceof Float)) {
			throw new ConfigException("'" + name + "' must be a float");
		}
		float val = (Float)value;
		setLoc(isX, val);
		Function func = isX ? xfunc : yfunc;
		if (func instanceof Linear) {
			Linear lin = (Linear)func;
			float m = lin.getM();
			func = new Linear(m, val, lin.getLl(), lin.getUl());
			setFunc(isX, func);
			return;
		}
		if (func == null) {
			func = new Constant(val);
			setFunc(isX, func);
			return;
		}
		throw new ConfigException("cannot set '" + name + "' and '" + funcName + "' in same config");
		
	}
	
	private void setPrimitiveVel(boolean isXvel, Object value) throws ConfigException {
		String name = isXvel ? "xvel" : "yvel";
		String funcName = isXvel ? "xfunc" : "yfunc";			
		if (!(value instanceof Float)) {
			throw new ConfigException("'" + name + "' must be a float");
		}
		Float xvel = (Float)value;
		Function func = isXvel ? xfunc : yfunc;
		if (func instanceof Constant) {
			Constant con = (Constant)func;
			float b = con.getValue();
			func = new Linear(xvel, b, -Float.MAX_VALUE, Float.MAX_VALUE);
			setFunc(isXvel, func);
			return;
		}
		if (func == null) {
			func = new Linear(xvel, 0, -Float.MAX_VALUE, Float.MAX_VALUE);
			setFunc(isXvel, func);
			return;
		}
		throw new ConfigException("cannot set '" + name + "' and '" + funcName + "' in same config");

		
	}
	
	@Override
	public void setLocal(String key, Object value) throws ConfigException {
		if ("scale".equals(key)) {
			if (!(value instanceof Float)) {
				throw new ConfigException("'scale' must be a float");
			}
			scale = (Float)value;
			return;
		}
		
		if ("xfunc".equals(key)) {
			if (!(value instanceof Function)) {
				throw new ConfigException("'xfunc' must be of type Functions");
			}
			xfunc = (Function)value;
			return;
		}
		
		if ("x".equals(key)) {
			setPrimitiveLoc(true, value);
			return;
		}
		if ("xvel".equals(key)) {
			this.setPrimitiveVel(true, value);
			return;
		}
		
		if ("yfunc".equals(key)) {
			if (!(value instanceof Function)) {
				throw new ConfigException("'yfunc' must be of type Functions");
			}
			yfunc = (Function)value;
			return;
		}
		
		if ("y".equals(key)) {
			setPrimitiveLoc(false, value);
			return;
		}
		if ("yvel".equals(key)) {
			setPrimitiveVel(false, value);
			return;
		}
		
		if ("color".equals(key)) {
			if (!(value instanceof MColor)) {
				throw new ConfigException("'color' must be of type MColor");
			}
			color = (MColor)value;
			return;
		}
		
		if ("life".equals(key)) {
			if (!(value instanceof Float)) {
				throw new ConfigException("'life' must be of type Float");
			}
			float temp = (Float)value;
			life = (long)temp;
			return;
		}
		
		throw new ConfigException("unknown property " + key);
		
	}


}
