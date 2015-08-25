package revsim.config.objects.revsim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.modeltools.objects.AbstractBlip;
import revsim.modeltools.objects.MColor;
import revsim.mvc.view.NodeException;

public class ColorRotateObject extends ConfigObject implements Transform {
	
	public float getPct() {
		return pct;
	}

	public void setPct(float pct) {
		this.pct = pct;
	}

	private float pct;

	@Override
	public ConfigObject duplicate() {
		ColorRotateObject ret = new ColorRotateObject();
		ret.pct = pct;
		return ret;
	}

	@Override
	public Object getLocal(String key) {
/*
		if ("pct".equals(key)) {
			return (Float)pct;
		}
		return null;
*/
		return getLocalR(key);
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
/*
		if (!("pct".equals(key))) {
			throw new ConfigException("unrecognized property '" + key + "'");
		}
		if (!(obj instanceof Float)) {
			throw new ConfigException("pct property must be a number");
		}
		pct = (Float)obj;
*/
		setLocalR(key, obj);
	}
	
	private float fit (float val) {
		if (val < 0) {
			return 0;
		}
		if (val > 1.0f) {
			return 1.0f;
		}
		return val;
	}
	
	private void rotateColor (AbstractBlip blip) {
		float [] comps = blip.getColor().getColor().getComponents(null);
		float redShift = pct*comps[0];
		float greenShift = pct*comps[1];
		float blueShift = pct*comps[2];
		
		if (pct >= 0) {
			comps[0] = fit(comps[0] - redShift);
			comps[1] = fit(comps[1] - greenShift);
			comps[2] = fit(comps[2] - blueShift);

			comps[1] = fit(comps[1] + redShift);
			comps[2] = fit(comps[2] + greenShift);
			comps[0] = fit(comps[0] + blueShift);
		}
		else {
			comps[0] = fit(comps[0] + redShift);
			comps[1] = fit(comps[1] + greenShift);
			comps[2] = fit(comps[2] + blueShift);
			
			comps[1] = fit(comps[1] - blueShift);
			comps[0] = fit(comps[0] - greenShift);
			comps[2] = fit(comps[2] - redShift);
		}
		Color newColor = new Color(comps[0], comps[1], comps[2], comps[3]);
		MColor mcolor = new MColor();
		mcolor.setColor(newColor);
		blip.setColor(mcolor);
		
	}

	public List<List<AbstractBlip>> transform(List<AbstractBlip> blips) throws NodeException {
		List<AbstractBlip> list = new ArrayList<AbstractBlip>();
		for (AbstractBlip blip : blips) {
			AbstractBlip newBlip = (AbstractBlip)blip.clone();
            rotateColor(newBlip);
            list.add(newBlip);
		}
		List<List<AbstractBlip>> ret = new ArrayList<List<AbstractBlip>>();
		ret.add(list);
		return ret;
	}

}
