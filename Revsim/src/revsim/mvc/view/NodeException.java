package revsim.mvc.view;

public class NodeException extends Exception {

	public NodeException (String msg) {
		super(msg);
	}
	
	public static NodeException make (int requestedIndex, int numChildren) {
		return new NodeException("attempt to reference non-existent node at index " +
				                 Integer.toString(requestedIndex) + " -- number of nodes = " + 
				                 Integer.toString(numChildren));
	}
}
