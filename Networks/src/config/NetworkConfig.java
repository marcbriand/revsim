package config;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

public class NetworkConfig extends ConfigObject {

	int maxDistance;
	float gradA;
	float gradB;
	float gradC;
	float gradD;
	Boolean useDebugWindow;
	float dbulx;
	float dbuly;
	float dblrx;
	float dblry;
	
	@Override
	public ConfigObject duplicate() {
		
		NetworkConfig ret = new NetworkConfig();
		ret.maxDistance = maxDistance;
		ret.gradA = gradA;
		ret.gradB = gradB;
		ret.gradC = gradC;
		ret.gradD = gradD;
		return ret;
	}
	
	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if (key.equals("maxDistance")) {
			if (obj == null)
				throw new ConfigException("null value passed for int 'maxDistance'");
			if (obj instanceof Float) {
				Float fobj = (Float)obj;
				maxDistance = fobj.intValue();
			}
			else
				throw new ConfigException("'maxDistance' was not a number");
		}
		else {
			super.setLocal(key, obj);
		}
	}
	
	public void setMaxDistance(int d) {
		maxDistance = d;
	}
	
	public int getMaxDistance() {
		return maxDistance;
	}
	
	public void setGradA(float ga) {
		gradA = ga;
	}
	
	public float getGradA() {
		return gradA;
	}

	public float getGradB() {
		return gradB;
	}

	public void setGradB(float gradB) {
		this.gradB = gradB;
	}

	public float getGradC() {
		return gradC;
	}

	public void setGradC(float gradC) {
		this.gradC = gradC;
	}

	public float getGradD() {
		return gradD;
	}

	public void setGradD(float gradD) {
		this.gradD = gradD;
	}

	public Boolean getUseDebugWindow() {
		return useDebugWindow;
	}

	public void setUseDebugWindow(Boolean useDebugWindow) {
		this.useDebugWindow = useDebugWindow;
	}

	public float getDbulx() {
		return dbulx;
	}

	public void setDbulx(float dbulx) {
		this.dbulx = dbulx;
	}

	public float getDbuly() {
		return dbuly;
	}

	public void setDbuly(float dbuly) {
		this.dbuly = dbuly;
	}

	public float getDblrx() {
		return dblrx;
	}

	public void setDblrx(float dblrx) {
		this.dblrx = dblrx;
	}

	public float getDblry() {
		return dblry;
	}

	public void setDblry(float dblry) {
		this.dblry = dblry;
	}

}
