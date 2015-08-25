package revsim.config.lex;

public class ExactAcceptor extends Acceptor {
	
	private final String matchStr;
	private final boolean wrap;
	private final String type;
	
	public ExactAcceptor (String matchStr, boolean wrap, String type) {
		this.matchStr = matchStr;
		this.wrap = wrap;
		this.type = type;
	}

	@Override
	public Token accept(LexIter iter) throws LexException {
		if (iter.startsWith(matchStr, wrap)) {
			return new Token(matchStr, iter.getCurrentLine(), iter.getCurrChar(), type);
		}
		return null;
	}

}
