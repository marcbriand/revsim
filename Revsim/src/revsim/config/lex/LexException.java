package revsim.config.lex;

public class LexException extends Exception {

	public int lineNo;
	public int charNo;
	
	public LexException (String message) {
		super(message);
	}
	
	public static LexException err (String message, int lineNo, int charNo) {
		LexException ret = new LexException(message);
		ret.lineNo = lineNo;
		ret.charNo = charNo;
		return ret;
	}
}
