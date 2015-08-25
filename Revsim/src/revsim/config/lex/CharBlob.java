package revsim.config.lex;

import java.util.ArrayList;
import java.util.List;

public class CharBlob {

	public char c;
	public int lineNo;
	public int charNo;
	
	public CharBlob (char c, int lineNo, int charNo) {
		this.c = c;
		this.lineNo = lineNo;
		this.charNo = charNo;
	}
	
	public static void addTo (String line, int lineNo, List<CharBlob> blobs) {
		for (int i = 0; i < line.length(); i++) {
			CharBlob blob = new CharBlob(line.charAt(i), lineNo, i+1);
			blobs.add(blob);
		}
	}
	
	public static List<CharBlob> makeBlobs (List<String> lines) {
		List<CharBlob> blobs = new ArrayList<CharBlob>();
		int lineNo = 1;
		for (String line : lines) {
			addTo(line, lineNo, blobs);
			lineNo++;
		}
		return blobs;
	}
}
