package revsim.config.parse;

import java.util.List;

import revsim.config.lex.Token;

public class TokenIter {
	
	private final List<Token> tokens;
	private int index = 0;
	
	public TokenIter (List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public boolean hasNext () {
		return index < tokens.size();
	}
	
	public Token next () {
		if (!hasNext()) {
			throw new IllegalStateException("out of tokens");
		}
		Token ret = tokens.get(index);
		index++;
		return ret;
	}
	
	public TokenIter dup () {
		TokenIter ret = new TokenIter(tokens);
		ret.index = index;
		return ret;
	}
	
	public void move (int n) {
		index += n;
	}
	
	public boolean startsWith (String type) {
		if (!hasNext()) {
			return false;
		}
		return tokens.get(index).types.contains(type);
	}

}
