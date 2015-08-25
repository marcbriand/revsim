package revsim.config.objects;

import revsim.config.ConfigException;

public class TemplateObject extends ConfigObject {
	
	private ConfigObject object;

	@Override
	public Object getLocal(String key) {
		if (object == null) {
			return null;
		}
		return object.getLocal(key);
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if (object == null) {
			throw new ConfigException("cannot set property on null object");
		}
		if (!(object instanceof ConfigObject)) {
			throw new ConfigException("cannot set property on object -- it is not a ConfigObject");
		}
		object.setLocal(key, obj);
	}
	
	@Override
	public Object get(String key) {
		if (object == null) {
			return null;
		}
		if (!(object instanceof ConfigObject)) {
			return null;
		}
		return ((ConfigObject)object).get(key);
	}
	
	@Override
	public void set(String key, Object obj) throws ConfigException {
		if (object == null) {
			throw new ConfigException("cannot set property on null object");
		}
		object.set(key, obj);
	}
	
	@Override
	public ConfigObject duplicate () {
		TemplateObject ret = new TemplateObject();
		ret.setObject(object.duplicate());
		return ret;
	}
	
	public ConfigObject getObject () {
		return object;
	}
	
	public void setObject (ConfigObject obj) {
		object = obj;
	}

}
