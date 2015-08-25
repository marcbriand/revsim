package revsim.config.parse.revsim;

import revsim.config.parse.Acceptor;
import revsim.config.parse.OTAcceptor;
import revsim.config.parse.SequenceAcceptor;

public class KeyedStatementAcceptor extends SequenceAcceptor {

	public KeyedStatementAcceptor () {
		super(new KeyAcceptor(),
			  new OTAcceptor("equals_t"),
			  new ValueAcceptor());
	}
	
	@Override
	public Acceptor factory () {
		return new KeyedStatementAcceptor();
	}
	
	public String getKey () {
		KeyAcceptor kacc = (KeyAcceptor)acceptors.get(0);
		return kacc.getText();
	}
	
	public ValueAcceptor getValueAcceptor () {
		return (ValueAcceptor)acceptors.get(2);
	}
}
