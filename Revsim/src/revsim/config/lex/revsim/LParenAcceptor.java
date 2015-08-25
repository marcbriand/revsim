package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class LParenAcceptor extends ExactAcceptor {

	public LParenAcceptor () {
		super("(", false, "lparen_t");
	}
}
