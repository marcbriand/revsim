package revsim.config.lex.revsim;

import revsim.config.lex.ExactAcceptor;

public class LCurlyAcceptor extends ExactAcceptor {
   
	public LCurlyAcceptor () {
		super("{", false, "lcurly_t");
	}
}
