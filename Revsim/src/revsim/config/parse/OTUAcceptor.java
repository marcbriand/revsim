package revsim.config.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import revsim.config.lex.Token;
import revsim.config.parse.revsim.PrimitiveValueAcceptor;

public class OTUAcceptor extends Acceptor {
	
	private final List<String> types = new ArrayList<String>();
	private List<Token> result;
	private String tokTypesToString (List<String> tokTypes) {
		if (tokTypes == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		for (String tt : tokTypes) {
			sb.append("," + tt);
		}
		return sb.toString();
	}
	
	public OTUAcceptor (String...types) {
		for (String type : types) {
			this.types.add(type);
		}
	}
	
	public OTUAcceptor (List<String> types) {
		for (String type : types) {
			this.types.add(type);
		}		
	}

	public boolean accept(TokenIter siter) {
		TokenIter iter = siter.dup();
		int maxWidth = 0;
		List<Token> candidates = new ArrayList<Token>();
		int pos = 1;
		for (String type : types) {
			OTAcceptor acc = new OTAcceptor(type);
			acc.setPosition(level + 1, pos);
			pos++;
			if (acc.accept(iter)) {
				Token tok = acc.getTokens().get(0);
				if (tok.value.length() > maxWidth) {
					candidates.clear();
					candidates.add(tok);
					maxWidth = tok.value.length();
				}
				else if (tok.value.length() == maxWidth) {
					candidates.add(tok);
				}
			}
		}
		if (candidates.isEmpty()) {
			reportFail(siter.dup(), "");
			return false;
		}
		Set<String> tokTypes = new HashSet<String>();
		for (Token cand : candidates) {
			tokTypes.addAll(cand.types);
		}
		List<String> tokTypeList = new ArrayList<String>();
		tokTypeList.addAll(tokTypes);
		Token firstCand = candidates.get(0);
		result = Arrays.asList(new Token(firstCand.value, firstCand.lineNo, firstCand.lineNo, tokTypeList));
        reportPass(tokTypesToString(tokTypeList));
		return true;
	}

	@Override
	public List<Token> getTokens() {
		return result;
	}

	@Override
	public Acceptor factory() {
		return new OTUAcceptor(types);
	}

}
