package revsim.config.parse.revsim;

import revsim.config.parse.Acceptor;
import revsim.config.parse.OTAcceptor;
import revsim.config.parse.SequenceAcceptor;

public class CreateAcceptor extends SequenceAcceptor {

	public CreateAcceptor () {
		super(new OTAcceptor("create_t"),
			  new OTAcceptor("lparen_t"),
			  new OTAcceptor("alphanum_t"),
			  new OTAcceptor("rparen_t"),
			  new DataAcceptor());
	}
	
	@Override
	public Acceptor factory () {
		return new CreateAcceptor();
	}
	
	public String getAlias () {
		return getAcceptor(2).getText();
	}
	
	public DataAcceptor getDataAcceptor () {
		return (DataAcceptor)getAcceptor(4);
	}
}
