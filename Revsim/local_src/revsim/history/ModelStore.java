/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.history;

import revsim.mvc.Model;

/**
 *
 * @author Marc
 */
public interface ModelStore {

    public void offer (Model model, long frame);
    public ModelAndFrame getAtOrBefore (long frame);
    public void clear();
}
