package revsim.config.parse;

import java.util.Arrays;
import java.util.List;

import revsim.config.lex.Token;

public class OTAcceptor extends Acceptor {
	
	private final String type;
	private List<Token> tokens;
	
	public OTAcceptor (String type) {
		this.type = type;
	}

	public boolean accept(TokenIter siter) {
		TokenIter iter = siter.dup();
		tokens = null;
		if (iter.startsWith(type)) {
			Token tok = iter.next();
			tokens = Arrays.asList(tok);
			reportPass(type);
			return true;
		}
		reportFail(siter.dup(), type);
		return false;		
	}

	@Override
	public List<Token> getTokens() {
		return tokens;
	}

	@Override
	public Acceptor factory() {
		return new OTAcceptor(type);
	}

}
