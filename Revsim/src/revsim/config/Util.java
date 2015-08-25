/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Marc
 */
public class Util {
    private static final Pattern leadingWS = Pattern.compile("^[\\s]*");
    private static final Pattern trailingWS = Pattern.compile("[\\s]*$");

    public static String trim (String str) {
        if (str == null) {
            return null;
        }
        Matcher lm = leadingWS.matcher(str);
        int left = 0;
        if (lm.find()) {
            left = lm.end();
        }
        String temp = str.substring(left);
        Matcher rm = trailingWS.matcher(temp);
        int right = temp.length();
        if (rm.find()) {
            right = rm.start();
        }
        return temp.substring(0, right);
    }

    public static boolean isEmpty (String str) {
        if (str == null) {
            return true;
        }
        return str.length() == 0;
    }

    public static String getArg (String line) {
        int left = line.indexOf("(");
        int right = line.indexOf(")");
        if (left < 0 || right < 0) {
            return null;
        }
        String raw = line.substring(left+1, right);
        return trim(raw);
    }
    
    public static boolean isAbsolute (String path) {
    	return path.startsWith("/");
    }
    
    public static boolean isUp (String path) {
    	return path.startsWith("../");
    }
    
    public static String trimOffAbsolute (String path) {
    	return path.substring(1);
    }
    
    public static String trimOffUp (String path) {
    	return path.substring(3);
    }
    
    // return first node name
    public static String splitOnFirstNode (String path, StringBuilder remainder) {
    	int firstSlash = path.indexOf("/");
    	if (firstSlash < 0) {
    		return path;
    	}
    	remainder.append(path.substring(firstSlash + 1));
    	return path.substring(0, firstSlash);
    }
    
    public static int parseInt (String n) throws ConfigException {
    	try {
    		return Integer.parseInt(n);
    	}
    	catch (NumberFormatException e) {
    		throw new ConfigException(e.getMessage());
    	}
    }
    
    public static int parseInt (String n, int lower, int upper) throws ConfigException {
    	int i = parseInt(n);
    	if (i < lower || i > upper) {
    		String istr = Integer.toString(i);
    		String lstr = Integer.toString(lower);
    		String ustr = Integer.toString(upper);
    		throw new ConfigException(istr + " is not between required limits " + lstr + " and " + ustr);
    	}
    	return i;
    }
    
    public static Float castNum (Object o) throws ConfigException {
    	if (!(o instanceof Float)) {
    		throw new ConfigException("object is not of required type Float");
    	}
    	return (Float)o;
    }
}
