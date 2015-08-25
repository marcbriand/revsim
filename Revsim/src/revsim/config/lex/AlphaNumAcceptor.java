package revsim.config.lex;

public class AlphaNumAcceptor extends CompleteRegexAcceptor {
    
	public AlphaNumAcceptor () {
		super("[a-zA-Z0-9]", "[a-zA-Z][a-zA-Z0-9]*", "alphanum_t");
	}
}
