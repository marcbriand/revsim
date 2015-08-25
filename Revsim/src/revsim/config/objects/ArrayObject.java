package revsim.config.objects;

import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;

public class ArrayObject extends ConfigObject {
	
	private final List<Object> objects = new ArrayList<Object>();

	@Override
	public ConfigObject duplicate() {
		ArrayObject ret = new ArrayObject();
		for (Object o : objects) {
			if (o instanceof ConfigObject) {
				ConfigObject cob = (ConfigObject)o;
				ret.objects.add(cob.duplicate());
			}
			else {
				ret.objects.add(o);
			}
		}
		return ret;
	}

	//TODO: add array access [i]
	@Override
	public Object getLocal(String key) {
		return null;
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		throw new ConfigException("not supported");
	}
	
	public void addObject (Object o) {
		objects.add(o);
	}
	
	public List<Object> getObjects () {
		List<Object> ret = new ArrayList<Object>();
		System.out.println("ArrayObject returning " + objects.size() + " objects");
		ret.addAll(objects);
		return ret;
	}

}
