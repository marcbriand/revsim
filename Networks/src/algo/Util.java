package algo;

import java.util.Random;

public class Util {
    public static double doubleBetween(double min, double max, Random r) {
    	double f = r.nextDouble();
    	return max*f + min*(1-f);
    }
}
