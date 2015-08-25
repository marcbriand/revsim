/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.rendering;

import revsim.driver.SimState;

/**
 *
 * @author Marc
 */
public interface InfoListener {
    public void onInfo (Info iaf);
    public void onState (SimState state);
}
