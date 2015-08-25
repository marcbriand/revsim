package revsim.config.parse;

import java.util.ArrayList;
import java.util.List;

import revsim.config.lex.Token;

public class GUAcceptor extends Acceptor {
	
	private final List<Acceptor> acceptors = new ArrayList<Acceptor>();
	private final List<String> eTypes = new ArrayList<String>();
	private List<Token> result;
	private List<String> resultTypes = new ArrayList<String>();
	
	private String resultTypesToString () {
		if (resultTypes == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		for (String rt : resultTypes) {
			sb.append("," + rt);
		}
		return sb.toString();
	}
	
	public GUAcceptor (List<Acceptor> acceptors, String...eTypes) {
		if (acceptors.size() != eTypes.length) {
			throw new IllegalArgumentException("acceptors and eTypes must be of equal length");
		}
		for (int i = 0; i < acceptors.size(); i++) {
			Acceptor acc = acceptors.get(i).factory();
			acc.setPosition(level + 1, i + 1);
			this.acceptors.add(acc);
			this.eTypes.add(eTypes[i]);
		}
	}
	
	public GUAcceptor (List<Acceptor> acceptors, List<String> eTypes) {
		if (acceptors.size() != eTypes.size()) {
			throw new IllegalArgumentException("acceptors and eTypes must be of equal length");
		}
		for (int i = 0; i < acceptors.size(); i++) {
			Acceptor acc = acceptors.get(i).factory();
			acc.setPosition(level +1, i + 1);
			this.acceptors.add(acc);
			this.eTypes.add(eTypes.get(i));

		}
		
	}

	@Override
	public boolean accept(TokenIter siter) {
		TokenIter iter = siter.dup();
		int maxWidth = 0;
		List<Acceptor> candidates = new ArrayList<Acceptor>();
		for (int i = 0; i < acceptors.size(); i++) {
			Acceptor acc = acceptors.get(i);
			if (acc.accept(iter)) {
				int length = acc.length();
				if (length > maxWidth) {
					candidates.clear();
					candidates.add(acc);
					resultTypes.clear();
					resultTypes.add(eTypes.get(i));
					maxWidth = length;
				}
				else if (length == maxWidth) {
					candidates.add(acc);
					resultTypes.add(eTypes.get(i));
				}
			}
		}
		if (candidates.isEmpty()) {
			reportFail(siter.dup(), "");
			return false;
		}
		result = candidates.get(0).getTokens();
		reportPass(resultTypesToString());
		return true;
	}

	@Override
	public List<Token> getTokens() {
		return result;
	}
	
	public List<String> getEntityTypes () {
		return resultTypes;
	}
	
	public Acceptor factory () {
		return new GUAcceptor(acceptors, eTypes);
	}
	
	public Acceptor getAcceptorForEntity (String etype) {
		int index = -1;
		for (int i = 0; i < this.eTypes.size(); i++) {
			if (etype.equals(eTypes.get(i))) {
				index = i;
				break;
			}
		}
		if (index < 0) {
			return null;
		}
		return acceptors.get(index);
	}

}
