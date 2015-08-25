package revsim.modeltools.functions;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

public class Linear extends AbstractFunction implements Function {
	
	private float m;
	private float b;
	private float ll;
	private float ul;
	
	public Linear () {
		ul = Float.MAX_VALUE;
	}
	
	public Linear (float m, float b, float ll, float ul) {
		this.m = m;
		this.b = b;
		this.ll = ll;
		this.ul = ul;
	}

	@Override
	public float exec(long frame, long birthday) {
		
		float r = m*(frame + offset - birthday) + b;
		if (r < ll) {
			return ll;
		}
		if (r > ul) {
			return ul;
		}
		return r;
	}

	public float getM() {
		return m;
	}

	public void setM(float m) {
		this.m = m;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
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
		Linear ret = new Linear(m, b, ll, ul);
		endow(ret);
		return ret;
	}

	public float getLl() {
		return ll;
	}

	public void setLl(float ll) {
		this.ll = ll;
	}

	public float getUl() {
		return ul;
	}

	public void setUl(float ul) {
		this.ul = ul;
	}

}
