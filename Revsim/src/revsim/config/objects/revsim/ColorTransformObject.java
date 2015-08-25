package revsim.config.objects.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.objects.ArrayObject;
import revsim.config.objects.ConfigObject;
import revsim.config.objects.DataObject;
import revsim.modeltools.objects.AbstractBlip;
import revsim.modeltools.objects.SphereBlip;
import revsim.mvc.view.NodeException;

public class ColorTransformObject extends ConfigObject implements Transform {
	
	private final List<Transform> transforms = new ArrayList<Transform>();

	@Override
	public ConfigObject duplicate() {
		ColorTransformObject ret = new ColorTransformObject();
	    ret.transforms.addAll(transforms);
	    return ret;
	}

	@Override
	public Object getLocal(String key) {
		if ("transforms".equals(key)) {
			List<Transform> ret = new ArrayList<Transform>();
			ret.addAll(transforms);
			return ret;
		}
		return null;
	}
	
	private void checkTransformType (Transform obj) throws ConfigException {
		if (obj instanceof ColorRotateObject) {
			return;
		}
		throw new ConfigException("unrecognized color transform");
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		
		if ("transforms".equals(key)) {
			if (!(obj instanceof ArrayObject)) {
				throw new ConfigException("expected array of transforms");
			}
			ArrayObject ao = (ArrayObject)obj;
			List<Object> objs = ao.getObjects();
			for (Object o : objs) {
				if (!(o instanceof Transform)) {
					throw new ConfigException("color transform expected to be transform object");
				}
				Transform dato = (Transform)o;
				checkTransformType(dato);
				transforms.add(dato);
			}
		}
	}
	
	public List<Transform> getTransforms () {
		List<Transform> ret = new ArrayList<Transform>();
		ret.addAll(transforms);
		return ret;
	}

/*
	public List<List<SphereBlip>> transform(List<SphereBlip> blips) throws NodeException {
		List<SphereBlip> list = blips;
		List<List<SphereBlip>> ret = new ArrayList<List<SphereBlip>>();
		ret.add(blips);
		for (Transform tx : transforms) {
			ret = tx.transform(list);
			list = ret.get(0);
		}
		return ret;
	}
*/
	public List<List<AbstractBlip>> transform(List<AbstractBlip> blips) throws NodeException {
		List<AbstractBlip> list = blips;
		List<List<AbstractBlip>> ret = new ArrayList<List<AbstractBlip>>();
		ret.add(blips);
		for (Transform tx : transforms) {
			ret = tx.transform(list);
			list = ret.get(0);
		}
		return ret;		
	}
}
