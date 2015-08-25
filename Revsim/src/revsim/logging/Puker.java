package revsim.logging;

import java.util.HashMap;
import java.util.Map;

public class Puker {
	
	private static Puker instance;
	private Map<String, Object> things = new HashMap<String, Object>();
	
	private Puker () {
		
	}
	
	public static Puker puker () {
		if (instance == null) {
			instance = new Puker();
		}
		return instance;
	}
	
	public void eat (String key, Object value) {
		things.put(key, value);
	}
	
	public void puke () {
		for (String key : things.keySet()) {
			System.out.println(key + ":" + things.get(key));
		}
	}

}
