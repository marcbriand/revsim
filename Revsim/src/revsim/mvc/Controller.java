/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.mvc;

import java.io.File;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

/**
 *
 * @author Marc
 */
public interface Controller {
    public void init (File env, ConfigObject config) throws ConfigException;
    public Model inc (Model model, long currentFrame);
}
