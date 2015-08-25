package revsim.config.parse;

import java.util.ArrayList;
import java.util.List;

import revsim.config.lex.Token;

public class ListAcceptor extends Acceptor {
	
	private final Acceptor prototype;
	private final List<Acceptor> matched = new ArrayList<Acceptor>();
	
	public ListAcceptor (Acceptor proto) {
		prototype = proto.factory();
	}

	@Override
	public boolean accept(TokenIter siter) {
		TokenIter iter = siter.dup();
		while (true) {
			Acceptor temp = prototype.factory();
			temp.setPosition(level + 1, matched.size() + 1);
			if (temp.accept(iter)) {
				matched.add(temp);
				iter.move(temp.length());
			}
			else {
				reportPass("");
				return true;
			}
		}
	}

	@Override
	public List<Token> getTokens() {
		List<Token> ret = new ArrayList<Token>();
		for (Acceptor acc : matched) {
			ret.addAll(acc.getTokens());
		}
		return ret;
	}

	@Override
	public Acceptor factory() {
		return new ListAcceptor(prototype);
	}
	
	public List<Acceptor> getMatching () {
		return matched;
	}

}
