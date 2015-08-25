package revsim.config.parse.revsim;

import revsim.config.parse.Acceptor;
import revsim.config.parse.OTAcceptor;
import revsim.config.parse.OTUAcceptor;
import revsim.config.parse.SequenceAcceptor;

public class MakeAcceptor extends SequenceAcceptor {

	public MakeAcceptor () {
		super(new OTAcceptor("make_t"),
			  new OTAcceptor("lparen_t"),
			  new OTUAcceptor("alphanum_t", "apath_t", "rpath_t", "dpath_t"),
			  new OTAcceptor("rparen_t"),
			  new DataAcceptor());
	}
	
	@Override
	public Acceptor factory () {
		return new MakeAcceptor();
	}
	
	public String getTemplatePath () {
		return getAcceptor(2).getText();
	}
	
	public DataAcceptor getDataAcceptor () {
		return (DataAcceptor)getAcceptor(4);
	}
}
