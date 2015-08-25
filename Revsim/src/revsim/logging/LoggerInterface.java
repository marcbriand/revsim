/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc
 */
public class LoggerInterface {

    public static void logDebug (String msg) {
        System.out.println(msg);
    }

    public static void logDebug (String msg, String classname) {
//        Logger.getLogger(classname).log(Level.WARNING, msg);
        System.out.println(classname + ": " + msg);
    }
}
