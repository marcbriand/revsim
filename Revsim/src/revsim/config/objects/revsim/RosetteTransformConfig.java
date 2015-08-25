package revsim.config.objects.revsim;


import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.Util;
import revsim.config.objects.ConfigObject;
import revsim.modeltools.objects.AbstractBlip;
import revsim.modeltools.objects.SphereBlip;
import revsim.mvc.view.NodeException;
import revsim.rendering.RenderUtil;

public class RosetteTransformConfig extends ConfigObject implements Transform {
	
	private int numArms;
	private float radius;
	private float scale;
	private float startAngle;

	@Override
	public ConfigObject duplicate() {
		RosetteTransformConfig ret = new RosetteTransformConfig();
		ret.numArms = numArms;
		ret.radius = radius;
		ret.scale = scale;
		ret.startAngle = startAngle;
		return ret;
	}

	@Override
	public Object getLocal(String key) {
		if ("numArms".equals(key)) {
			return new Integer(numArms);
		}
		if ("radius".equals(key)) {
			return new Float(radius);
		}
		if ("scale".equals(key)) {
			return new Float(scale);
		}
		if ("startAngle".equals(key)) {
			return new Float(startAngle);
		}
		return null;
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if ("numArms".equals(key)) {
			this.numArms = Util.castNum(obj).intValue();
			return;
		}
		if ("radius".equals(key)) {
			this.radius = Util.castNum(obj);
			return;
		}
		if ("scale".equals(key)) {
			this.scale = Util.castNum(obj);
			return;
		}
		if ("startAngle".equals(key)) {
			this.startAngle = Util.castNum(obj);
			return;
		}
		throw new ConfigException("unknown attribute '" + key + "'");
	}
/*	
	private List<SphereBlip> transformArm(List<SphereBlip> blips, double angle) {
		
		List<SphereBlip> ret = new ArrayList<SphereBlip>(blips.size());
		double rads = RenderUtil.degreesToRads(angle);
		double cx = radius*Math.cos(rads);
		double cy = radius*Math.sin(rads);
		
		int j = 1;
		for (SphereBlip blip : blips) {
			SphereBlip newBlip = (SphereBlip)blip.clone();
			newBlip.scale = scale*blip.scale;
			newBlip.x = (float)(cx + scale*blip.x);
			newBlip.y = (float)(cy + scale*blip.y);
			ret.add(newBlip);
		}
		return ret;
	}
*/	
	private List<AbstractBlip> transformArm(List<AbstractBlip> blips, double angle) {
		
		List<AbstractBlip> ret = new ArrayList<AbstractBlip>(blips.size());
		double rads = RenderUtil.degreesToRads(angle);
		double cx = radius*Math.cos(rads);
		double cy = radius*Math.sin(rads);
		
		int j = 1;
		for (AbstractBlip blip : blips) {
			AbstractBlip newBlip = (AbstractBlip)blip.clone();
			newBlip.setScale(scale*blip.getScale());
			newBlip.setX((float)(cx + scale*blip.getX()));
			newBlip.setY((float)(cy + scale*blip.getY()));
			ret.add(newBlip);
		}
		return ret;
	}
	
	
/*
	public List<List<SphereBlip>> transform(List<SphereBlip> blips) throws NodeException {
		
		List<List<SphereBlip>> ret = new ArrayList<List<SphereBlip>>();
		if (numArms < 1) {
			return ret;
		}
		double angle = startAngle;
		double spacing = 360.0/numArms;
		for (int i = 0; i < numArms; i++) {
			List<SphereBlip> arm = transformArm(blips, angle);
			ret.add(arm);
			angle += spacing;
			angle = RenderUtil.normalizeAngle(angle);
		}
		return ret;
	}
*/
	public List<List<AbstractBlip>> transform(List<AbstractBlip> blips) throws NodeException {
		
		List<List<AbstractBlip>> ret = new ArrayList<List<AbstractBlip>>();
		if (numArms < 1) {
			return ret;
		}
		double angle = startAngle;
		double spacing = 360.0/numArms;
		for (int i = 0; i < numArms; i++) {
			List<AbstractBlip> arm = transformArm(blips, angle);
			ret.add(arm);
			angle += spacing;
			angle = RenderUtil.normalizeAngle(angle);
		}
		return ret;
	}

}
