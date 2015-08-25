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
public class ModelAndFrame {
    public Model model;
    public long frame;
    public ModelAndFrame (Model model, long frame) {
        this.model = model;
        this.frame = frame;
    }
}
