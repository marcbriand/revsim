package revsim.config.lex;

public class APathAcceptor extends CompleteRegexAcceptor {

	public APathAcceptor () {
		super("[a-zA-Z0-9/]", "(/[a-zA-z][a-zA-Z0-9]*)+", "apath_t");
	}
}
