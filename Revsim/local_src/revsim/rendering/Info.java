/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.rendering;

import java.awt.Image;
import revsim.driver.SimState;

/**
 *
 * @author Marc
 */
public class Info {
    public Image image;
    public Long frame;
    public SimState state;

    public Info (Image image, Long frame, SimState state) {
        this.image = image;
        this.frame = frame;
        this.state = state;
    }
}
