package revsim.config.parse;

import java.util.ArrayList;
import java.util.List;

import revsim.config.lex.Token;
import revsim.config.parse.revsim.KeyedStatementAcceptor;
import revsim.config.parse.revsim.MakeAcceptor;

public class SequenceAcceptor extends Acceptor {
	
	protected final List<Acceptor> acceptors = new ArrayList<Acceptor>();
	
	public SequenceAcceptor (Acceptor...acceptors) {
		int pos = 1;
		for (Acceptor acc : acceptors) {
			Acceptor newAcc = acc.factory();
			newAcc.setPosition(level + 1, pos);
			pos++;
			this.acceptors.add(newAcc);
		}
	}
	
	public SequenceAcceptor (List<Acceptor> acceptors) {
		int pos = 1;
		for (Acceptor acc : acceptors) {
			Acceptor newAcc = acc.factory();
			newAcc.setPosition(level + 1, pos);
			pos++;
			this.acceptors.add(newAcc);
		}		
	}

	public boolean accept(TokenIter siter) {
		TokenIter iter = siter.dup();
		for (Acceptor acc : acceptors) {
			if (acc.accept(iter)) {
				iter.move(acc.length());
			}
			else {
				reportFail(iter.dup(), "");
				return false;
			}
		}
		reportPass("");
		if (this instanceof MakeAcceptor) {
			System.out.println("foo");
		}
		return true;
	}

	@Override
	public List<Token> getTokens() {
		List<Token> ret = new ArrayList<Token>();
		for (Acceptor acc : acceptors) {
			ret.addAll(acc.getTokens());
		}
		return ret;
	}

	@Override
	public Acceptor factory() {
		return new SequenceAcceptor(acceptors);
	}
	
	@Override
	public void setPosition(int level, int position) {
		super.setPosition(level, position);
		int pos = position;
		for (Acceptor acc : acceptors) {
			acc.setPosition(level+1, pos);
		}
	}
	
	public Acceptor getAcceptor (int i) {
		if (i >= acceptors.size()) {
			throw new IllegalArgumentException("i outside range of acceptors");
		}
		return acceptors.get(i);
	}

}
