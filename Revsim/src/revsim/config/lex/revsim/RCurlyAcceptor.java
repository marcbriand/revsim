package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class RCurlyAcceptor extends ExactAcceptor {

	public RCurlyAcceptor () {
		super("}", false, "rcurly_t");
	}
}
