package revsim.config.parse.revsim;

import revsim.config.parse.Acceptor;
import revsim.config.parse.OTAcceptor;
import revsim.config.parse.SequenceAcceptor;

public class RefAcceptor extends SequenceAcceptor {

	public RefAcceptor () {
		super(new OTAcceptor("ref_t"),
			  new OTAcceptor("lparen_t"),
			  new OTAcceptor("alphanum_t"),
			  new OTAcceptor("rparen_t"));
	}
	
	@Override
	public Acceptor factory () {
		return new RefAcceptor();
	}
}
