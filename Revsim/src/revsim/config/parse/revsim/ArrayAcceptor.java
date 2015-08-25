package revsim.config.parse.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.lex.Token;
import revsim.config.parse.Acceptor;
import revsim.config.parse.ListAcceptor;
import revsim.config.parse.OTAcceptor;
import revsim.config.parse.SequenceAcceptor;
import revsim.config.parse.TokenIter;

public class ArrayAcceptor extends Acceptor {
	
	private SequenceAcceptor innerAcceptor;

	@Override
	public boolean accept(TokenIter iter) {
		innerAcceptor = new SequenceAcceptor(new OTAcceptor("lbrace_t"),
				                             new ListAcceptor(new ValueAcceptor()),
				                             new OTAcceptor("rbrace_t"));
		innerAcceptor.setPosition(level, position);
		return innerAcceptor.accept(iter);
	}

	@Override
	public Acceptor factory() {
		return new ArrayAcceptor();
	}

	@Override
	public List<Token> getTokens() {
		if (innerAcceptor == null) {
			throw new IllegalStateException("inner accepter is null");
		}
		return innerAcceptor.getTokens();
	}
	
	public List<ValueAcceptor> getValueAcceptors () {
		List<ValueAcceptor> ret = new ArrayList<ValueAcceptor>();
		ListAcceptor lacc = (ListAcceptor)innerAcceptor.getAcceptor(1);
		List<Acceptor> acceptors = lacc.getMatching();
		for (Acceptor acc : acceptors) {
			ret.add((ValueAcceptor)acc);
		}
		return ret;
	}
}

