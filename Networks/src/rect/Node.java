package rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import props.DProp;

public class Node {
	private int id;
    private int x;
    private int y;
    private List<Integer> neighbors = new ArrayList<Integer>();
    private Map<String, DProp> props = new HashMap<String, DProp>();
    
    public Node duplicate() {
    	Node ret = new Node();
    	ret.id = id;
    	ret.x = x;
    	ret.y = y;
    	ret.neighbors = new ArrayList<Integer>();
    	for (int i = 0; i < neighbors.size(); i++) {
    		ret.neighbors.add(neighbors.get(i));
    	}
    	Map<String, DProp> newProps = new HashMap<String, DProp>();
    	for (String key : props.keySet()) {
    		DProp p = props.get(key);
    		newProps.put(key, p.duplicate());
    	}
    	ret.props = newProps;
    	return ret;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public List<Integer> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Integer> neighbors) {
		this.neighbors = neighbors;
	}
	
	public void addNeighbor(int neighbor) {
		neighbors.add(neighbor);
	}
	
	public boolean hasProp(String propName) {
		return props.get(propName) != null;
	}
	
	public Set<String> getPropsKeyset() {
		return props.keySet();
	}
	
	public DProp getProp(String propName) {
		return props.get(propName);
	}
	
	public void putProp(String propName, DProp prop) {
		props.put(propName, prop);
	}
	
	public void removeProp(String propName) {
		props.remove(propName);
	}
}
