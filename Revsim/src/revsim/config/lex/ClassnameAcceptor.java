package revsim.config.lex;

public class ClassnameAcceptor extends CompleteRegexAcceptor {

	public ClassnameAcceptor () {		
		super("[a-zA-Z0-9\\.]", "[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)*", "classname_t");
	}
}
