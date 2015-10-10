package algo;

import config.SigmoidConfig;
import revsim.rendering.DoublePoint2D;

public class Sigmoid {
	
	public static double sigmoidNormalized(double x, double steepness) {
		double steepX = steepness*x;
		double dsq = 1 + steepX*steepX;
		double denom = Math.sqrt(dsq);
		return steepness*x/denom;
	}
	
	public static double sigmoid(double minVal, double maxVal, double steepness, long offset, long count) {
		double nml = sigmoidNormalized((count - offset), steepness);
		double zToOne = 0.5*nml + 0.5;
		return zToOne*maxVal + (1.0 - zToOne)*minVal;
	}
	
	public static double sigmoid(SigmoidConfig config, long count) {
		return sigmoid(config.startVal, config.endVal, config.steepness, config.offset, count);
	}
	
	public static DoublePoint2D sigmoidRange(SigmoidConfig config, long count) {
		double center = sigmoid(config, count);
		if (config.endVal >= config.startVal) { 
		    double range = config.pct*(config.endVal - config.startVal);
		    double low = center - 0.5*range;
		    low = Math.max(config.startVal, low);
		    double high = center + 0.5*range;
		    high = Math.min(config.endVal, high);
		    return new DoublePoint2D(low, high);
		} else {
			double range = config.pct*(config.startVal - config.endVal);
			double low = center - 0.5*range;
			low = Math.max(config.endVal, low);
			double high = center + 0.5*range;
			high = Math.min(config.startVal, high);
			return new DoublePoint2D(low, high);
		}
	}

}
