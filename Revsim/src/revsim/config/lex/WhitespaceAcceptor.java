package revsim.config.lex;

public class WhitespaceAcceptor extends CompleteRegexAcceptor {

	public WhitespaceAcceptor () {
		super("\\s", "\\s+", "whitespace_t");
		
	}
}
