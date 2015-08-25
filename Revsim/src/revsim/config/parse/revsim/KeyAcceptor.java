package revsim.config.parse.revsim;

import revsim.config.parse.Acceptor;
import revsim.config.parse.OTUAcceptor;

public class KeyAcceptor extends OTUAcceptor {

	public KeyAcceptor () {
		super("dpath_t", "alphanum_t");
	}
	
	@Override
	public Acceptor factory () {
		return new KeyAcceptor();
	}
}
