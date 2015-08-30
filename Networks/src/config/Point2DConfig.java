package config;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

public class Point2DConfig extends ConfigObject {
	
	int x;
	int y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public ConfigObject duplicate() {
		Point2DConfig ret = new Point2DConfig();
		ret.x = x;
		ret.y = y;
		return ret;
	}
	
	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if (key.equals("x"))
			x = getFloatAsInt("x", obj);
		else if (key.equals("y"))
			y = getFloatAsInt("y", obj);
		else {
			super.setLocal(key, obj);
		}
	}

}
