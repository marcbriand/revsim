package revsim.modeltools.functions;

import revsim.config.objects.ConfigObject;

public class Sawtooth extends AbstractFunction {
	
	private float m;
	private float b;
	private float ll;
	private float ul;
	private float span;

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

	public float getLl() {
		return ll;
	}

	public void setLl(float ll) {
		this.ll = ll;
		span = ul - ll;
	}

	public float getUl() {
		return ul;
	}

	public void setUl(float ul) {
		this.ul = ul;
		span = ul - ll;
	}
	
	private float constrain(float value) {
		if (value < ll) {
			return ll;
		}
		if (value > ul) {
			return ul;
		}
		return value;
	}
	
	private boolean isEven(double numF) {
		int i = (int)numF;
		return i % 2 == 0;
	}

	
	@Override
	public float exec(long frame, long birthday) {
		if (absolute) {
			return execPrivate(frame, 0);
		}
		return execPrivate(frame, birthday);
	}
	
	public float execPrivate(long frame, long birthday) {
		if (frame + offset < birthday) {
			return constrain(b);
		}
		float d = (frame + offset - birthday)*m + b;
		double numF = 0;
		double r = 0;
		if (d >= 0) {
			numF = Math.floor(d/span);
			r = d - numF*span;
			if (isEven(numF)) {
				return (float)r + ll;
			}
			else {
				return ul - (float)r;
			}
		}
		else {
			numF = Math.floor(-d/span);
			r = -(-d - numF*span);
			if (isEven(numF)) {
				return (ll - (float)r);
			} 
			else {
				return (ul + (float)r);
			}
		}
	}

	@Override
	public ConfigObject duplicate() {
		Sawtooth ret = new Sawtooth();
		ret.m = m;
		ret.b = b;
		ret.setLl(ll);
		ret.setUl(ul);
		endow(ret);
		return ret;
	}

}
