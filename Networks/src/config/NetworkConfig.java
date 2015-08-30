package config;

import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.objects.ArrayObject;
import revsim.config.objects.ConfigObject;

public class NetworkConfig extends ConfigObject {

	int minDistance;
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
	float maxDensity = Float.MAX_VALUE;
	int minNeighbors = 0;
	int maxNeighbors = Integer.MAX_VALUE;
	ArrayObject startPoints = new ArrayObject();
	
	@Override
	public ConfigObject duplicate() {
		
		NetworkConfig ret = new NetworkConfig();
		ret.minDistance = minDistance;
		ret.maxDistance = maxDistance;
		ret.gradA = gradA;
		ret.gradB = gradB;
		ret.gradC = gradC;
		ret.gradD = gradD;
		ret.maxDensity = maxDensity;
		ret.minNeighbors = minNeighbors;
		ret.maxNeighbors = maxNeighbors;
		ret.startPoints = (ArrayObject)startPoints.duplicate();
		return ret;
	}
	
	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if (key.equals("maxDistance")) {
			maxDistance = getFloatAsInt("maxDistance", obj);
		}
		else if (key.equals("minDistance")) {
			minDistance = getFloatAsInt("minDistance", obj);
		}
		else if (key.equals("minNeighbors")) {
			minNeighbors = getFloatAsInt("minNeighbors", obj);
		}
		else if (key.equals("maxNeighbors")) {
			maxNeighbors = getFloatAsInt("maxNeighbors", obj);
		}
		else {
			super.setLocal(key, obj);
		}
	}
	
	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
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

	public float getMaxDensity() {
		return maxDensity;
	}

	public void setMaxDensity(float maxDensity) {
		this.maxDensity = maxDensity;
	}

	public int getMinNeighbors() {
		return minNeighbors;
	}

	public int getMaxNeighbors() {
		return maxNeighbors;
	}

	public List<Point2DConfig> getStartPoints() {
		List<Point2DConfig> ret = new ArrayList<Point2DConfig>();
		List<Object> obs = startPoints.getObjects();
		for (Object o : obs) {
			Point2DConfig pc = (Point2DConfig)o;
			ret.add(pc);
		}
		return ret;
	}

	public void setStartPoints(ArrayObject startPoints) {
		this.startPoints = startPoints;
	}
	
	

}
