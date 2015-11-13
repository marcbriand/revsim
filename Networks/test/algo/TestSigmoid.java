package algo;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSigmoid {

	@Test
	public void test() {
		
		double steepness = 0.05;
		
		for (int i = -5; i <= 5; i++) {
			double y = Sigmoid.sigmoidNormalized(i, steepness);
			System.out.println("x = " + i + ", y = " + y);
		}
	}

}
