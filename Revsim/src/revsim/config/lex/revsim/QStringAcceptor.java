package revsim.config.lex.revsim;

import revsim.config.lex.EndCapAcceptor;

public class QStringAcceptor extends EndCapAcceptor {

	public QStringAcceptor () {
		super("\"", "\"", "qstr_t");
	}
}
