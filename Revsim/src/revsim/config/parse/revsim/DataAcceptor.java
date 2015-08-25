package revsim.config.parse.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.lex.Token;
import revsim.config.parse.Acceptor;
import revsim.config.parse.ListAcceptor;
import revsim.config.parse.OTAcceptor;
import revsim.config.parse.SequenceAcceptor;
import revsim.config.parse.TokenIter;

public class DataAcceptor extends Acceptor {
	
	private SequenceAcceptor innerAcceptor;

	@Override
	public boolean accept(TokenIter iter) {
		innerAcceptor = new SequenceAcceptor(new OTAcceptor("lcurly_t"),
				                             new ListAcceptor(new KeyedStatementAcceptor()),
				                             new OTAcceptor("rcurly_t"));
		innerAcceptor.setPosition(level, position);
		return innerAcceptor.accept(iter);
	}

	@Override
	public Acceptor factory() {
		return new DataAcceptor();
	}

	@Override
	public List<Token> getTokens() {
		if (innerAcceptor == null) {
			throw new IllegalStateException ("inner acceptor is null");
		}
		return innerAcceptor.getTokens();
	}
	
	public List<KeyedStatementAcceptor> getStatements () {
		if (innerAcceptor == null) {
			return null;
		}
		ListAcceptor lacc = (ListAcceptor)innerAcceptor.getAcceptor(1);
		List<Acceptor> acceptors = lacc.getMatching();
		List<KeyedStatementAcceptor> ret = new ArrayList<KeyedStatementAcceptor>();
		for (Acceptor acc : acceptors) {
			ret.add((KeyedStatementAcceptor)acc);
		}
		return ret;
		
	}

}
