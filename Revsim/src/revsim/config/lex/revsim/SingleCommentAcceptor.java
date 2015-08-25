package revsim.config.lex.revsim;

import revsim.config.lex.Acceptor;
import revsim.config.lex.LexException;
import revsim.config.lex.LexIter;
import revsim.config.lex.Token;

public class SingleCommentAcceptor extends Acceptor {

	@Override
	public Token accept(LexIter siter) throws LexException {
		LexIter iter = siter.dup();
		if (!iter.startsWith("//", false)) {
			return null;
		}
		iter.move(2);
		StringBuilder sb = new StringBuilder();
		sb.append("//");
		while(iter.hasNext()) {
			if (iter.getCurrChar() == 1) {
				return new Token(sb.toString(), siter.getCurrentLine(), siter.getCurrChar(), "singlecomment_t");
			}
			sb.append(iter.next());
		}
		return new Token(sb.toString(), siter.getCurrentLine(), siter.getCurrChar(), "singlecomment_t");
	}

}
