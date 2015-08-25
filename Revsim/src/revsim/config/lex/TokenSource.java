package revsim.config.lex;

import java.util.ArrayList;
import java.util.List;

public class TokenSource {

	private final LexIter iter;
	private List<Acceptor> acceptors = new ArrayList<Acceptor>();
	
	public TokenSource (List<CharBlob> blobs, Acceptor...acceptors) {
    	iter = new LexIter(blobs);
    	for (Acceptor acc : acceptors) {
    		this.acceptors.add(acc);
    	}
	}
	
	public Token nextToken () throws LexException {
		
		if (!iter.hasNext()) {
			return null;
		}
		
		int maxLength = 0;
		List<Token> candidates = new ArrayList<Token>();
		
		for (Acceptor acc : acceptors) {
			Token tok = acc.accept(iter);
			if (tok != null) {
				if (tok.value.length() > maxLength) {
					candidates.clear();
					maxLength = tok.value.length();
					candidates.add(tok);
				}
				else if (tok.value.length() == maxLength) {
					candidates.add(tok);
				}
			}
		}
		
		if (candidates.isEmpty()) {
			throw LexException.err("unknown token", iter.getCurrentLine(), iter.getCurrChar());
		}
		
		List<String> tokenTypes = new ArrayList<String>(candidates.size());
		for (Token tok : candidates) {
			tokenTypes.add(tok.types.get(0));
		}
		
		int startingLine = iter.getCurrentLine();
		int startingChar = iter.getCurrChar();
		
		String value = candidates.get(0).value;
		
		iter.move(value.length());
		
		return new Token(value, startingLine, startingChar, tokenTypes);
		
	}
}
 