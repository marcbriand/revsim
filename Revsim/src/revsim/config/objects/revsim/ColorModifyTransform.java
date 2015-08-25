package revsim.config.objects.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.objects.ConfigObject;
import revsim.modeltools.objects.AbstractBlip;
import revsim.modeltools.objects.MColor;
import revsim.mvc.view.NodeException;

public class ColorModifyTransform extends ConfigObject implements Transform {
	
	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

	private float brightness = 1.0f;
	
	private void applyBrightness (AbstractBlip blip) {
		MColor mcolor = blip.getColor();
		float [] rgba = mcolor.getFloat4RGBA();
		for (int i = 0; i < 3; i++) {
			float val = brightness*rgba[i];
			if (val > 1.0f) {
				val = 1.0f;
			}
			if (val < 0.0f) {
				val = 0.0f;
			}
			rgba[i] = val;
		}
		mcolor.setFloat4RGBA(rgba);
		blip.setColor(mcolor);		
	}

	@Override
	public List<List<AbstractBlip>> transform(List<AbstractBlip> blips)
			throws NodeException {
		List<List<AbstractBlip>> ret = new ArrayList<List<AbstractBlip>>();
		List<AbstractBlip> txformed = new ArrayList<AbstractBlip>();
		ret.add(txformed);
		for (AbstractBlip blip : blips) {
			AbstractBlip newBlip = (AbstractBlip)blip.clone();
			applyBrightness(newBlip);
			txformed.add(newBlip);
		}
		return ret;
	}

	@Override
	public ConfigObject duplicate() {
		ColorModifyTransform ret = new ColorModifyTransform();
		ret.brightness = brightness;
		return ret;
	}

}
