package revsim;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Marc
 */
public interface CommandListener {
    public void rewind ();
    public void inc();
    public void dec();
    public void go();
    public void pause();
    public void resume();
    public void saveImage();
    public void skipTo(long frame);
}
