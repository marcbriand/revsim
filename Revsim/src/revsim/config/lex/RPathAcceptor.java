package revsim.config.lex;

public class RPathAcceptor extends CompleteRegexAcceptor {

	public RPathAcceptor () {
		super("[a-zA-Z0-9/\\.]", "\\.\\.(/[a-zA-Z][a-zA-Z0-9]*)+", "rpath_t");
	}
}
