package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class RBraceAcceptor extends ExactAcceptor {

	public RBraceAcceptor () {
		super("]", false, "rbrace_t");
	}
}
