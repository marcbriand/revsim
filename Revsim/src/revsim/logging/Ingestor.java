/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc
 */
public class Ingestor {

    private static Ingestor instance;
    private static boolean enabled = false;
    private static File dumpFile;
    private List<String> records = new LinkedList<String>();
    private IngestorPolicy policy;
    public Map<String, Object> bag = new HashMap<String, Object>();

    private Ingestor (String dumpFileName) {
        if (dumpFileName == null) {
            return;
        }
        dumpFile = new File(dumpFileName);
        dumpFile.delete();
    }

    public static Ingestor dsink () {
        if (instance == null) {
            throw new IllegalStateException("Ingestor was not properly initialized");
        }
        return instance;
    }

    public static Ingestor dsink (String dumpFileName) {
        if (instance == null) {
            instance = new Ingestor(dumpFileName);
        }
        return instance;
    }

    public void enable (boolean value) {
        enabled = value;
    }

    public void eat (String msg) {
        if (policy == null) {
            return;
        }
        if (policy.shouldLog()) {
            LoggerInterface.logDebug(msg);
        }
        if (enabled) {
            records.add(msg);
        }
    }

    public void eat (Object condition, String msg) {
        if (policy == null) {
            return;
        }
        policy.ate(condition);
        if (policy.shouldLog()) {
            LoggerInterface.logDebug(msg);
        }
        if (!enabled) {
            if (policy.shouldStart()) {
                enable(true);
                records.add(msg);
            }
        }
        else {
            records.add(msg);
            if (policy.shouldStop()) {
                enable(false);
                dump();
            }
            if (policy.shouldThrow()) {
                throw new IllegalStateException("termination by ingestor policy");
            }
        }
    }

    public void dump () {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(dumpFile);
            for (String record : records) {
                writer.println(record);
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(Ingestor.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            writer.close();
        }

    }

    public void setPolicy (IngestorPolicy policy) {
        this.policy = policy;
    }

    public static String i2Str (int i) {
        return Integer.toString(i);
    }

    public static String f2Str (float f) {
        return Float.toString(f);
    }
}
