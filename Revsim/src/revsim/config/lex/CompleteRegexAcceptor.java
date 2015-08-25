package revsim.config.lex;

import java.util.regex.Pattern;

public class CompleteRegexAcceptor extends Acceptor {
	
	private final Pattern acceptSet;
	private final Pattern pattern;
	private final String tokenType;
	
	public CompleteRegexAcceptor (String acceptSet, String pattern, String tokenType) {
		this.acceptSet = Pattern.compile(acceptSet);
		this.pattern = Pattern.compile(pattern);
		this.tokenType = tokenType;
	}

	@Override
	public Token accept(LexIter siter) throws LexException {
		LexIter iter = siter.dup();
		StringBuilder collected = new StringBuilder();
		
		while (iter.hasNext()) {
			char c = iter.next();
			if (acceptSet.matcher(wrap(c)).matches()) {
				collected.append(c);
			}
			else {
				break;
			}
		}
		
		String value = collected.toString();
		if (pattern.matcher(value).matches()) {
			return new Token(value, siter.getCurrentLine(), siter.getCurrChar(), tokenType);
		}
		
		return null;
		
	}

}
