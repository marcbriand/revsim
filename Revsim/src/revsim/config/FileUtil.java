package revsim.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static List<String> getLines (InputStream stream) throws IOException {
        InputStreamReader fr = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(fr);

        List<String> lines = new ArrayList<String>();
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);                
            }
        }
        finally {
            br.close();
            fr.close();
        }
        return lines;
		
	}
	
	public static String getLinesAsString (InputStream stream) throws IOException {
		List<String> lines = getLines(stream);
		StringBuilder buff = new StringBuilder();
		for (String line : lines) {
			buff.append(line);
			buff.append("\n");
		}
		return buff.toString();
	}
}
