package revsim.config.lex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Token {
	
	public String value;
	public int lineNo;
	public int charNo;
	public final List<String> types = new ArrayList<String>();
	
	public Token (String value, int lineNo, int charNo, List<String> types) {
		this.value = value;
		this.lineNo = lineNo;
		this.charNo = charNo;
		for (String type : types) {
			this.types.add(type);
		}
	}
	
	public Token (String value, int lineNo, int charNo, String type) {
		this(value, lineNo, charNo, Arrays.asList(type));
	}
	
	public String getTypesStr () {
		boolean needComma = false;
		StringBuilder sb = new StringBuilder();
		for (String type : types) {
			if (needComma) {
				sb.append(", ");
			}
			needComma = true;
			sb.append(type);
		}
		return sb.toString();
	}

}
