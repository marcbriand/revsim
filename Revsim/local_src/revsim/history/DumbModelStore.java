/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import revsim.mvc.Model;

/**
 *
 * @author Marc
 */
public class DumbModelStore implements ModelStore {
    private List<ModelAndFrame> models = new Vector<ModelAndFrame>();

    public void clear() {
        models.clear();
    }

    public List<ModelAndFrame> getModels () {
        return models;
    }

    public void offer (Model model, long frame) {
        for (int i = models.size() - 1; i >= 0; i--) {
            ModelAndFrame mf = models.get(i);
            if (mf.frame > frame) {
                continue;
            }
            else if (mf.frame == frame) {
                models.set(i, new ModelAndFrame(model.duplicate(), frame));
                return;
            }
            else {
                models.add(new ModelAndFrame(model.duplicate(), frame));
                return;
            }
        }
        models.add(new ModelAndFrame(model.duplicate(), frame));
    }
    public ModelAndFrame getAtOrBefore (long frame) {
        for (int i = models.size() - 1; i >= 0; i--) {
            ModelAndFrame mf = models.get(i);
            if (mf.frame == frame) {
                return mf;
            }
            if (mf.frame < frame) {
                return mf;
            }
        }
        return null;
    }

}
