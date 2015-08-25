package revsim.config.lex;

import java.util.List;

public class LexIter {
	
	private final List<CharBlob> blobs;
	private int cursor = 0;
	
	public LexIter (List<CharBlob> blobs) {
		this.blobs = blobs;
		
	}
	
    public boolean hasNext () {
    	return cursor < blobs.size();
    }
    
    public boolean hasPrev () {
    	return cursor > 0;
    }
        
    public char next () {
        if (!hasNext()) {
        	throw new IllegalStateException("no next char");
        }
        char ret = blobs.get(cursor).c;
        cursor++;
        return ret;
    }
    
    public char prev () {
    	if (!hasPrev()) {
    		throw new IllegalStateException("no prev char");
    	}
    	cursor--;
    	return blobs.get(cursor).c;
    }
    
    public void goTo (LexIter other) {
    	if (blobs != other.blobs) {
    		throw new IllegalStateException("incompatible iterators");
    	}
    	this.cursor = other.cursor;
    }
    
    public LexIter dup () {
    	LexIter ret = new LexIter(blobs);
    	ret.cursor = cursor;
    	return ret;
    }
    
    public int getCurrentLine () {
    	if (!hasNext()) {
    		throw new IllegalStateException("not on any line");
    	}
    	return blobs.get(cursor).lineNo;
    }
    
    public int getCurrChar () {
    	if (!hasNext()) {
    		throw new IllegalStateException("not on any char");
    	}
    	return blobs.get(cursor).charNo;
    }
    
    private boolean wrapCheck (LexIter iter, boolean wrap) {
    	if (blobs.get(iter.cursor).charNo == 1) {
    		if (!wrap) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public boolean startsWith (String str, boolean wrap) {
    	LexIter temp = this.dup();
    	for (int i = 0; i < str.length(); i++) {
    		if (temp.hasNext()) {
        		if (i > 0 && !wrapCheck(temp, wrap)) {
        			return false;
        		}
        		char c = str.charAt(i);
        		char x = temp.next();
    			if (c != x) {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	return true;	
    }
    
    public void move (int n) {
    	if (n > 0) {
    		for (int i = 0; i < n; i++){
    			next();
    		}
    		return;
    	}
    	if (n < 0) {
    		for (int i = n; i < 0; i++) {
    			prev();
    		}
    		return;
    	}
    	
    }

}
