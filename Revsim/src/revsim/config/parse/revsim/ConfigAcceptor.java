package revsim.config.parse.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.parse.Acceptor;
import revsim.config.parse.ListAcceptor;

public class ConfigAcceptor extends ListAcceptor {

	public ConfigAcceptor () {
		super(new KeyedStatementAcceptor());
	}
	
	public List<KeyedStatementAcceptor> getStatements () {
		List<KeyedStatementAcceptor> ret = new ArrayList<KeyedStatementAcceptor>();
		for (Acceptor acc : getMatching()) {
			KeyedStatementAcceptor kacc = (KeyedStatementAcceptor)acc;
			ret.add(kacc);
		}
		return ret;
	}
}
