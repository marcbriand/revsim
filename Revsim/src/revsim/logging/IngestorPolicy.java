/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.logging;

/**
 *
 * @author Marc
 */
public interface IngestorPolicy {

    public void ate (Object obj);
    public boolean shouldStart ();
    public boolean shouldStop ();
    public boolean shouldLog ();
    public boolean shouldThrow ();
}
