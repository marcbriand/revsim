package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class LBraceAcceptor extends ExactAcceptor {

	public LBraceAcceptor () {
		super("[", false, "lbrace_t");
	}
}
