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
    private long birthday;
    private List<Integer> neighbors = new ArrayList<Integer>();
    private Map<String, DProp> props = new HashMap<String, DProp>();
    private double density;
    NetworkModel nm;
    
    public Node (int id, long birthday, NetworkModel nm) {
    	this.id = id;
    	this.birthday = birthday;
    	this.nm = nm;
    	
    	density = 1.0;
    }
    
    public Node duplicate() {
    	Node ret = new Node(id, birthday, nm);
    	ret.x = x;
    	ret.y = y;
    	ret.density = density;
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
    
    void calcDensity() {
    	double d = 1.0;
    	for (Integer i : neighbors) {
    		Node nb = nm.getNode(i);
    		if (nb != null) {
    			int diffx = nb.getX() - x;
    			int diffy = nb.getY() - y;
    			double dsq = diffx*diffx + diffy*diffy;
                double factor = 1.0 - dsq/nm.getGenerateBuilder().maxDistanceSquared;
                factor = Math.max(0.0, factor);
                d += factor;
    		}
    	}
    	density = d;
    }
    
    void calcDensity2() {
    	double d = 1.0;
    	for (Integer i : neighbors) {
    		Node nb = nm.getNode(i);
    		if (nb != null) {
    		    d += 0.5;
    		    int numNeighborNeighbors = nb.getNeighbors().size() - 1; // don't count self
    		    d += 0.25*numNeighborNeighbors;
    		}
    	}
    	density = d;
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
		calcDensity2();
	}
	
	public void removeNeighbor(int neighbor) {
		List<Integer> temp = new ArrayList<Integer>();
		for (Integer i : neighbors) {
			if (i != neighbor)
				temp.add(i);
		}
		neighbors = temp;
		calcDensity2();
	}
	
	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
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

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}
	
	public int[] findNeighborsAroundArc (int entryId, double fraction) {
		if (fraction == 0.0)
			throw new IllegalArgumentException("fraction must be > 0 and < 1");
		int entryIndex = -1;
		int nsize = neighbors.size();
		for (int i = 0; i < nsize; i++) {
			if (entryId == neighbors.get(i)) {
				entryIndex = i;
				break;
			}
		}
		if (entryIndex < 0)
			throw new IllegalArgumentException("entryId " + entryId + " does not represent a neighbor");
        int[] ret;
        if (nsize == 1)
        	return new int[0];
		int sector = (int)Math.floor(fraction*nsize);
		if (sector/fraction == nsize) {
			// evenly divided
			ret = new int[1];
            ret[0] = (entryIndex + sector) % nsize;
			
		}
		else {
			ret = new int[2];
			ret[0] = (entryIndex + sector) % nsize;
			ret[1] = (entryIndex + sector + 1) % nsize;
		}
		return ret;
	}
}
