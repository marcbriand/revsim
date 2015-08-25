package revsim.modeltools.functions;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

public class Constant extends ConfigObject implements Function {
	
	private float value;
	
	public Constant () {
		
	}
	
	public Constant (float value) {
		this.value = value;
	}

	@Override
	public float exec(long frame, long birthday) {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	@Override
	public Object getLocal(String key) {
		return getLocalR(key);
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		setLocalR(key, obj);
	}

	@Override
	public ConfigObject duplicate() {
		return new Constant(value);
	}
	

}
