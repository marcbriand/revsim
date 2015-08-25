/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.driver;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import revsim.ViewDimensions;
import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.history.ModelAndFrame;
import revsim.history.ModelStore;
import revsim.mvc.Controller;
import revsim.mvc.Model;
import revsim.mvc.View;
import revsim.rendering.Info;
import revsim.rendering.InfoListener;

/**
 *
 * @author Marc
 * running, pause, stop states
 *
 * 
 *
 *
 */
public class Driver implements InfoListener {
    private File env;
    private int width = 800, height = 600;
    private int ulx = -width/2;
    private int uly = -height/2;
    private float ppu = 400.0f;
    private Controller controller;
    private View view;
    private List<InfoListener> listeners = new ArrayList<InfoListener>();
    long currentFrame = 0;
    Model currentModel;
    ModelStore store;
    final Object pauseMutex = new Object();
    volatile boolean pause;
    RunThread runThread;
    ConfigObject controllerConfig;
    ConfigObject viewConfig;

    volatile SimState simState = SimState.Initial;

    class RunThread extends Thread {
        private long lastFrame = -1;
        private boolean show;
        private boolean keepRunning = true;
        private List<InfoListener> listeners = new ArrayList<InfoListener>();

        public RunThread (long lastFrame, 
                          boolean show,
                          InfoListener...listeners) {
            this.lastFrame = lastFrame;
            this.show = show;
            for (InfoListener listener : listeners) {
                this.listeners.add(listener);
            }
        }

        private void publish (SimState sState) {
            for (InfoListener listener : listeners) {
                listener.onState(sState);
            }
        }

        @Override
        public void run () {
            publish(SimState.Running);
            while (keepRunning) {
                if (pause) {
                    publish(SimState.Paused);
                    waitOn(pauseMutex);
                    publish(SimState.Running);
                }
                if (!keepRunning) {
                    publish(SimState.Idle);
                    return;
                }
                if (lastFrame > -1 && currentFrame >= lastFrame) {
                    publish(SimState.Idle);
                    return;
                }
                privateInc(show);
            }
            publish(SimState.Idle);
        }

        public void stopThread () {
            keepRunning = false;
        }
    }

    public static void sleep (long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Driver (File env, 
                   Controller controller,
                   View view,
                   ModelStore store,
                   ConfigObject config,
                   ViewDimensions vdim) throws ConfigException {
        this.env = env;
        this.controller = controller;
        this.view = view;
        this.store = store;
        ulx = vdim.ulx;
        uly = vdim.uly;
        if (config != null) {
           controllerConfig = (ConfigObject)config.getLocal("controller");
           viewConfig = (ConfigObject)config.getLocal("view");
        }
        controller.init(env, controllerConfig);
        view.init(env, viewConfig, vdim.ppu, vdim.width, vdim.height);
    }

    public void onInfo (Info info) {
        setState(info.state);
    }

    public void onState (SimState state) {
        setState(state);
    }

    void setState (SimState state) {
        this.simState = state;
        publishState();
    }

    public void addInfoListener (InfoListener listener) {
        this.listeners.add(listener);
    }

    // called from UI only. Should really only be called if thread is not running.
    public void reset () {
        try {
            if (runThread != null) {
                stop();
            }
            currentFrame = 0;
            currentModel = null;
            controller.init(env, controllerConfig);
            store.clear();
            setState(SimState.Initial);
        } catch (ConfigException ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // called both from UI, and from RunThread, hence no checks of 'running' flag
    void privateInc (boolean show) {
        currentFrame++;
        currentModel = controller.inc(currentModel, currentFrame);
        store.offer(currentModel, currentFrame);
        if (simState == SimState.Initial) {
            setState(SimState.Idle);
        }
        publish(show);
//        sleep(10);
    }

    public void inc (boolean show) {
        checkState("inc", SimState.Initial, SimState.Idle, SimState.Paused);
        privateInc(show);
    }

    public void skipTo (long frame) {
        checkState("skipTo", SimState.Initial, SimState.Idle, SimState.Paused);
        ModelAndFrame mf = store.getAtOrBefore(frame);
        if (mf == null) {
            currentModel = null;
            currentFrame = 0;
            runTo(frame, false);
        }
        else {
            currentModel = mf.model;
            currentFrame = mf.frame;
            if (mf.frame < frame) {
                runTo(frame, false);
            }
        }
        publish(true);
    }

    // called only from UI. Should really only be called if thread is not running
    public void dec (boolean show) {
        checkState("dec", SimState.Idle, SimState.Paused);
        if (runThread != null) {
            stop();
        }
        long frame = currentFrame - 1;
        if (frame < 1) {
            return;
        }
        ModelAndFrame mf = store.getAtOrBefore(frame);
        if (mf == null) {
            currentModel = null;
            currentFrame = 0;
            runTo(frame, false);
        }
        else {
            currentModel = mf.model;
            currentFrame = mf.frame;
            if (mf.frame < frame) {
                runTo(frame, false);
            }
        }
        publish(show);
    }

    // called from UI only, should only be called if thread is not running
    public void go () {
        if (runThread != null) {
            return;
        }
        runThread = new RunThread(-1, true, this);
        pause = false;
        runThread.start();
    }

    // called from UI only, should only be called if thread is running
    public void pause () {
        if (runThread == null) {
            return;
        }
        pause = true;
    }

    // called from UI only, should only be called if thread is paused
    public void resume () {
        if (runThread == null) {
            return;
        }
        pause = false;
        synchronized (pauseMutex) {
            pauseMutex.notify();
        }
    }

    // called from reset or dec
    void stop () {
        if (runThread == null) {
            return;
        }
        runThread.stopThread();

        if (pause) {
            resume();
        }
        pause = false;
        joinThread(runThread);
        runThread = null;
    }

    public void saveImage () {
        BufferedImage img = (BufferedImage)view.render(currentModel, ulx, uly);
        String filename = "img_f" + Long.toString(currentFrame) +
                          "_d" + Integer.toString(width) + "x" + Integer.toString(height) +
                          ".png";
        File ofile = new File(filename);
        try {
            ImageIO.write(img, "png", ofile);
        } catch (IOException ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // only called from dec() and skipTo(). Should only be called if thread is not running
    private void runTo (long lastFrame, boolean show) {
        if (runThread != null) {
            throw new IllegalStateException("runTo called with non-null runThread");
        }
        runThread = new RunThread(lastFrame, show, this);
        pause = false;
        runThread.start();
        joinThread(runThread);
        runThread = null;
    }

    private void publish (boolean show) {
        Image image = show ? view.render(currentModel, ulx, uly) : null;
        for (InfoListener listener : listeners) {
            listener.onInfo(new Info(image, currentFrame, simState));
        }
    }

    private void publishState () {
        for (InfoListener listener : listeners) {
            listener.onState(simState);
        }
    }

    private void waitOn (Object mutex) {
        synchronized (mutex) {
            try {
                mutex.wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void joinThread (Thread thread) {
        try {
            thread.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkState (String methodName, SimState...allowed) {
        
        for (SimState as : allowed) {
            if (as == simState) {
                return;
            }
        }
        throw new IllegalStateException("entering method " + methodName + " in illegal state " + simState.toString());
    }
    
}
