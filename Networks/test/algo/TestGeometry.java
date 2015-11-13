package algo;

import static org.junit.Assert.*;

import org.junit.Test;

import rect.NetworkModel;
import rect.Node;

public class TestGeometry {

    @Test
    public void test() {
    	
    	NetworkModel nm = new NetworkModel();
    	
    	Node a1 = new Node(13, 0, nm);
    	a1.setX(520);
    	a1.setY(279);
    	
    	Node a2 = new Node(88, 0, nm);
    	a2.setX(516);
    	a2.setY(308);
    	
        Node b1 = new Node(1, 0, nm);
        b1.setX(500);
        b1.setY(300);
        
        Node b2 = new Node(6, 0, nm);
        b2.setX(529);
        b2.setY(300);
        
        boolean di = Geometry.doIntersect2(a1, a2, b1, b2);
        di = Geometry.doIntersect2(a1, a2, b2, b1);
        di = Geometry.doIntersect2(a2, a1, b1, b2);
        di = Geometry.doIntersect2(a2, a1, b2, b1);
	
    }

}
