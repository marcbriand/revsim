package revsim.config.lex;

public class DPathAcceptor extends CompleteRegexAcceptor {
	
	public static final String kAcceptSet = "[a-zA-Z0-9/]";
	public static final String kPattern = "[a-zA-Z][a-zA-Z0-9]*(/[a-zA-Z][a-zA-Z0-9]*)*";
	
	public DPathAcceptor () {
		super(kAcceptSet, kPattern, "dpath_t");
	}

}
