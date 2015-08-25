/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.driver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import revsim.ViewDimensions;
import revsim.config.ConfigException;
import revsim.config.objects.ConfigObject;
import revsim.mvc.Controller;
import revsim.mvc.Model;
import revsim.mvc.View;

public class CLDriver {
    private final int ulx;
    private final int uly;
    private final Controller controller;
    private final View view;
    private final long targetFrame;
    private final File outputFile;
    private final String outputFormat;
    private final ConfigObject controllerConfig;
    private final ConfigObject viewConfig;
    private boolean verbose = true;
    

    public CLDriver (File env,
    		         Controller controller,
                     View view,
                     ConfigObject config,
                     ViewDimensions vdim,
                     long targetFrame,
                     File outputFile,
                     String outputFormat) throws ConfigException {

    	if (config == null) {
    		throw new ConfigException("config is null");
    	}
        this.controller = controller;
        this.view = view;
        ulx = vdim.ulx;
        uly = vdim.uly;
        this.targetFrame = targetFrame;
        this.outputFile = outputFile;
        this.outputFormat = outputFormat.toLowerCase();
        controllerConfig = (ConfigObject)config.getLocal("controller");
        viewConfig = (ConfigObject)config.getLocal("view");
        controller.init(env, controllerConfig);
        view.init(env, viewConfig, vdim.ppu, vdim.width, vdim.height);
    }

    public void saveImage (Model currentModel) {
    	writeIfVerbose("Saving image to " + outputFile);
        BufferedImage img = (BufferedImage)view.render(currentModel, ulx, uly);
        try {
            ImageIO.write(img, outputFormat, outputFile);
            System.out.println("Image saved at " + outputFile);
        } catch (IOException ex) {
            Logger.getLogger(CLDriver.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }
    
    public void go () {
    	long currentFrame = 0;
    	Model currentModel = null;
    	writeIfVerbose("Starting...target frame = " + targetFrame);
    	while (currentFrame < targetFrame) {
    		currentFrame++;
    		writeIfVerbose("calculating frame " + currentFrame);
    		currentModel = controller.inc(currentModel, currentFrame);
    	}
    	saveImage(currentModel);
    }
    
    public void setVerbose(boolean v) {
    	verbose = v;
    }
    
    private void writeIfVerbose (String msg) {
    	if (verbose) {
    		System.out.println(msg);
    	}
    }


    
}
