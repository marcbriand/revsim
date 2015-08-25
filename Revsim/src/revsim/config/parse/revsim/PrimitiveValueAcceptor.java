package revsim.config.parse.revsim;

import revsim.config.parse.Acceptor;
import revsim.config.parse.OTUAcceptor;

public class PrimitiveValueAcceptor extends OTUAcceptor {

	public PrimitiveValueAcceptor () {
		super("apath_t", 
			  "rpath_t", 
			  "dpath_t", 
			  "num_t", 
			  "alphanum_t", 
			  "classname_t", 
			  "qstr_t",
			  "bool_t");
	}
	
	@Override
	public Acceptor factory () {
		return new PrimitiveValueAcceptor();
	}
}
