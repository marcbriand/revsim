package algo;

import revsim.config.objects.ConfigObject;

public class AsymptoticRangeConfig extends ConfigObject {

	public void setMinVal(double minVal) {
		this.minVal = minVal;
	}

	public void setMaxVal(double maxVal) {
		this.maxVal = maxVal;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double minVal;
	public double maxVal;
	public double bias;
	public double scale;
	public double percent;
	
	@Override
	public ConfigObject duplicate() {
		AsymptoticRangeConfig ret = new AsymptoticRangeConfig();
		ret.minVal = minVal;
		ret.maxVal = maxVal;
		ret.bias = bias;
		ret.scale = scale;
		ret.percent = percent;
		
		return ret;
	}

}
