package revsim.config.lex;

public class NumAcceptor extends CompleteRegexAcceptor {

	public NumAcceptor () {
		super("[-\\.0-9]", "-?[0-9]+(\\.[0-9]+)?", "num_t");
	}
}
