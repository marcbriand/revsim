/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.mvc;

import java.awt.Image;
import java.io.File;

import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;

/**
 *
 * @author Marc
 */
public interface View {
    public void init (File env, ConfigObject config,
    		          float ppu, int width, int height) throws ConfigException;
    public Image render (Model model, int ulx, int uly);
}
