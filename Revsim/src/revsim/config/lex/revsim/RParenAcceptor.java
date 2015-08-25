package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class RParenAcceptor extends ExactAcceptor {

	public RParenAcceptor () {
		super(")", false, "rparen_t");
	}
}
