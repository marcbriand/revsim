package revsim.config.parse;

import java.util.List;

import revsim.config.ConfigException;
import revsim.config.lex.Token;

public abstract class Acceptor {
	
	protected int level;
	protected int position;

	public abstract boolean accept (TokenIter iter);
	public abstract List<Token> getTokens();
	public abstract Acceptor factory();
	public int length () {
		List<Token> tokens = getTokens();
		if (tokens == null) {
			throw new IllegalStateException("null token list");
		}
		return tokens.size();
	}
	public String getText () {
		List<Token> tokens = getTokens();
		if (tokens == null) {
			throw new IllegalStateException("null token list");
		}
		StringBuilder sb = new StringBuilder();
		for (Token tok : tokens) {
			sb.append(tok.value);
		}
		return sb.toString();
	}
	public void setPosition (int level, int position) {
		this.level = level;
		this.position = position;
	}
	public int getLevel () {
		return level;
	}
	public int getPosition () {
		return position;
	}
	public void reportFail (TokenIter iter, String extra) {
		StringBuilder sb = new StringBuilder();
		if (iter.hasNext()) {
		   Token tok = iter.next();
		   sb.append("FAIL with token '" + tok.value + "' (" + tok.types + ")");
		}
		else {
		   sb.append("FAIL with no token");
		}
		sb.append(" : classname = " + this.getClass().getName());
		sb.append(" * " + extra);
		System.out.println(sb.toString());
	}
	public void reportPass (String extra) {
		StringBuilder sb = new StringBuilder();
		sb.append("PASS " + getText() + " : classname = " + this.getClass().getName());
		sb.append(" * " + extra);
		System.out.println(sb.toString());
	}
	public ConfigException addLineInfo (Exception e) {
		String message = createMessage(e);
		List<Token> tokens = getTokens();
		if (tokens == null || tokens.isEmpty()) {
			return new ConfigException(message);
		}
		Token tok = tokens.get(0);
		String lineinfo = " line: " + Integer.toString(tok.lineNo) + ", char: " + Integer.toString(tok.charNo);
		return new ConfigException(message + lineinfo);
	}
	private String createMessage (Exception e) {
		String ret = e.getMessage();
		if (ret == null || ret.length() == 0) {
			return e.getClass().getSimpleName();
		}
		if (e instanceof ClassNotFoundException) {
			return "ClassNotFoundException: " + e.getMessage();
		}
		return ret;
	}
}
