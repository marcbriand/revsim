package revsim.config.lex;

import java.nio.CharBuffer;

public abstract class Acceptor {
	
	protected CharBuffer wrap (char c) {
		char [] carray = new char[1];
		carray[0] = c;
		return CharBuffer.wrap(carray);
	}

	public abstract Token accept (LexIter iter) throws LexException;
}
