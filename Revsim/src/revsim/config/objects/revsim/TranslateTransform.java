package revsim.config.objects.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.Util;
import revsim.config.objects.ConfigObject;
import revsim.modeltools.objects.AbstractBlip;
import revsim.mvc.view.NodeException;

public class TranslateTransform extends ConfigObject implements Transform {
	
	private float dx;
	private float dy;

	@Override
	public List<List<AbstractBlip>> transform(List<AbstractBlip> blips)
			throws NodeException {
		List<List<AbstractBlip>> ret = new ArrayList<List<AbstractBlip>>();
		List<AbstractBlip> newList = new ArrayList<AbstractBlip>(blips.size());
		for (AbstractBlip blip : blips) {
			AbstractBlip newBlip = (AbstractBlip)blip.clone();
			newBlip.setX(blip.getX() + dx);
			newBlip.setY(blip.getY() + dy);
			newList.add(newBlip);
		}
		ret.add(newList);
		return ret;
	}

	@Override
	public Object getLocal(String key) {
		if ("dx".equals(key)) {
			return new Float(dx);
		}
		if ("dy".equals(key)) {
			return new Float(dy);
		}
		return null;
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if ("dx".equals(key)) {
			dx = Util.castNum(obj);
			return;
		}
		if ("dy".equals(key)) {
			dy = Util.castNum(obj);
			return;
		}
		throw new ConfigException("Unknown attribute: '" + key + "'");
	}

	@Override
	public ConfigObject duplicate() {
		TranslateTransform ret = new TranslateTransform();
		ret.dx = dx;
		ret.dy = dy;
		return ret;
	}

}
