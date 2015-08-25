/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.mvc;

/**
 *
 * @author Marc
 */
public interface Model {
    public String serialize ();
    public void deserialize (String str);
    public Model duplicate ();
}
