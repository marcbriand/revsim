package algo;

import revsim.rendering.DoublePoint2D;

public class AsymptoticRange {
	
    public static double asymptotic(AsymptoticRangeConfig config, long count) {
    	double p = 0.0;
    	if (count > 0)
    		p = 1.0 - 1.0/(1.0 + config.bias + config.scale*count);
    	return p*config.maxVal + (1.0 - p)*config.minVal;
    }
    
    public static DoublePoint2D asymptoticRange(AsymptoticRangeConfig config, long count) {
    	double center = asymptotic(config, count);
    	double range = (config.maxVal - config.minVal)*config.percent;
    	double low = center - 0.5*range;
    	low = Math.max(config.minVal, low);
    	double high = center + 0.5*range;
    	high = Math.min(config.maxVal, high);
    	return new DoublePoint2D(low, high);
    }

}
