package revsim.config.lex.revsim;

import revsim.config.lex.EndCapAcceptor;

public class MultiCommentAcceptor extends EndCapAcceptor {

	public MultiCommentAcceptor () {
		super("/*", "*/", "multicomment_t");
	}
}
