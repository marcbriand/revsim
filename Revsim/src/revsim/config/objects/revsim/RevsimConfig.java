package revsim.config.objects.revsim;

import java.util.HashMap;
import java.util.Map;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

public class RevsimConfig extends ConfigObject {
	
//	private Map<String, String> registry = null;
	private Map<String, Object> localObjects = new HashMap<String, Object>();

	@Override
	public Object getLocal(String key) {
		return localObjects.get(key);
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		localObjects.put(key, obj);
	}

	@Override
	public ConfigObject duplicate() {
		return null;
	}

}
