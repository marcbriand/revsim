package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class EqualsAcceptor extends ExactAcceptor {

	public EqualsAcceptor () {
		super("=", false, "equals_t");
	}
}
