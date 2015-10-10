package config;

import revsim.config.objects.ConfigObject;

public class SigmoidConfig extends ConfigObject {
	
	public double startVal;
	public double endVal;
	public long offset;
	public double steepness;
	public double pct;
	
	public SigmoidConfig() {
		startVal = -1.0;
		endVal = 1.0;
		offset = 0;
		steepness = 1.0;
		pct = 0.0;
	}

	@Override
	public ConfigObject duplicate() {
		SigmoidConfig ret = new SigmoidConfig();
		ret.startVal = startVal;
		ret.endVal = endVal;
		ret.offset = offset;
		ret.steepness = steepness;
		ret.pct = pct;
		
		return ret;
	}

	public double getStartVal() {
		return startVal;
	}

	public void setStartVal(float minVal) {
		this.startVal = minVal;
	}

	public double getEndVal() {
		return endVal;
	}

	public void setEndVal(float maxVal) {
		this.endVal = maxVal;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public double getSteepness() {
		return steepness;
	}

	public void setSteepness(float steepness) {
		this.steepness = steepness;
	}

	public double getPct() {
		return pct;
	}

	public void setPct(float pct) {
		this.pct = pct;
	}


}
