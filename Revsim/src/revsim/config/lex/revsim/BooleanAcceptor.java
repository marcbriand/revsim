package revsim.config.lex.revsim;

import revsim.config.lex.CompleteRegexAcceptor;

public class BooleanAcceptor extends CompleteRegexAcceptor {

	public BooleanAcceptor () {
		super("[truefals]", "true|false", "bool_t");
	}
}
