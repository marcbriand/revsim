package config;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

public class Point3DConfig extends ConfigObject {
	
	int x;
	int y;
	int z;

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
	
	public int getZ() {
		return z;
	}
	
	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public ConfigObject duplicate() {
		Point3DConfig ret = new Point3DConfig();
		ret.x = x;
		ret.y = y;
		ret.z = z;
		return ret;
	}
	
	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if (key.equals("x"))
			x = getFloatAsInt("x", obj);
		else if (key.equals("y"))
			y = getFloatAsInt("y", obj);
		else if (key.equals("z"))
			z = getFloatAsInt("z", obj);
		else {
			super.setLocal(key, obj);
		}
	}

}
