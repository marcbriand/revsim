/**
 * 
 */
package revsim.config.lex;

/**
 * @author marc
 *
 */
public class EndCapAcceptor extends Acceptor {
	
	private final String start;
	private final String end;
	private final String type;
	
	public EndCapAcceptor (String start, String end, String type) {
		this.start = start;
		this.end = end;
		this.type = type;
	}

	@Override
	public Token accept(LexIter siter) throws LexException {
		LexIter iter = siter.dup();
		if (!iter.startsWith(start, false)) {
			return null;
		}
		iter.move(start.length());
		StringBuilder between = new StringBuilder();
		
		while (iter.hasNext()) {
			if (iter.startsWith(end, false)) {
				String value = start + between.toString() + end;
	            return new Token(value, siter.getCurrentLine(), siter.getCurrChar(), type);			
			}
			between.append(iter.next());
		}
		throw LexException.err("incomplete token", siter.getCurrentLine(), siter.getCurrChar());
	}

}
