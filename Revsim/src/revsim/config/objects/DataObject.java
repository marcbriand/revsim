package revsim.config.objects;

import java.util.HashMap;
import java.util.Map;

import revsim.config.ConfigException;

public class DataObject extends ConfigObject {
	
	protected final Map<String, Object> properties = new HashMap<String, Object>();

	@Override
	public ConfigObject duplicate() {
		DataObject ret = new DataObject();
		for (String key : properties.keySet()) {
			Object ob = properties.get(key);
			if (ob instanceof ConfigObject) {
				ConfigObject cob = (ConfigObject)ob;
				ret.properties.put(key, cob.duplicate());
			}
			else {
				ret.properties.put(key, ob);
			}
		}
		return ret;
	}

	@Override
	public Object getLocal(String key) {
		return properties.get(key);
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		properties.put(key, obj);
	}

}
