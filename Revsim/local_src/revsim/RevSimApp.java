package revsim;
/*
 * RevSimApp.java
 */



import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import revsim.config.ConfigParser;
import revsim.config.lex.Token;
import revsim.config.objects.revsim.RevsimConfig;
import revsim.config.parse.revsim.ConfigAcceptor;
import revsim.driver.Driver;
import revsim.driver.SimState;
import revsim.history.DumbModelStore;
import revsim.logging.Ingestor;
import revsim.mvc.Controller;
import revsim.mvc.View;

/**
 * The main class of the application.
 */
public class RevSimApp extends SingleFrameApplication implements CommandListener {

    private static Driver driver;

    public void rewind () {
        driver.reset();
    }

    public void dec () {
        driver.dec(true);
    }

    public void inc () {
        driver.inc(true);
    }

    public void go () {
        driver.go();
    }

    public void pause () {
        driver.pause();
    }

    public void resume () {
        driver.resume();
    }

    public void saveImage () {
        driver.saveImage();
    }

    @Override
    public void skipTo (long frame) {
        driver.skipTo(frame);
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {


        RevSimView revSimView = new RevSimView(this);
        revSimView.setCommandListener(this);
        driver.addInfoListener(revSimView);
        show(revSimView);
        revSimView.onState(SimState.Initial);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of RevSimApp
     */
    public static RevSimApp getApplication() {
        return Application.getInstance(RevSimApp.class);
    }

    public static String usage () {
        return "<prog> -d <directory> -f <filename>";
    }

    private static boolean findArg (String[] args, 
    		                        String name, 
    		                        int pos, 
    		                        String[] parsed,
    		                        String defValue) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
               if (i + 1 >= args.length) {
                   throw new Exception("missing arg to " + name + "option");
               }
               parsed[pos] = args[i+1];
               return true;
            }
        }
        if (defValue != name) {
        	parsed[pos] = defValue;
        	return false;
        }
        return false;
    }
    
    private static boolean findArg (String [] args, String name, int pos, String[] parsed) 
        throws Exception {
    	return findArg(args, name, pos, parsed, null);
    }
    
    private static final int DIR = 0;
    private static final int FILE = 1;
    private static final int PPU = 2;
    private static final int WIDTH = 3;
    private static final int HEIGHT = 4;
    private static final int ULX = 5;
    private static final int ULY = 6;
    private static final String defPpu = "400";
    private static final String defWidth = "800";
    private static final String defHeight = "600";
    

    private static String [] parse (String[] args) throws Exception {
        String[] parsed = new String[7];
        boolean found = findArg(args, "-d", DIR, parsed);
        if (!found) {
            throw new Exception("required -d argument not found");
        }
        found = findArg(args, "-f", FILE, parsed);
        if (!found) {
            throw new Exception("required -f argument not found");
        }
        findArg(args, "-ppu", PPU, parsed, defPpu);
        findArg(args, "-w", WIDTH, parsed, defWidth);
        findArg(args, "-h", HEIGHT, parsed, defHeight);
        findArg(args, "-ulx", ULX, parsed);
        findArg(args, "-uly", ULY, parsed);
        return parsed;
    }

    private static File makeDirFile (String [] info) throws Exception {
        File f = new File(info[0]);
        if (!f.exists()) {
            throw new Exception("directory " + f.getAbsolutePath() + " is not found");
        }
        return f;
    }

    private static File makeConfigFile (String [] info) throws Exception {
        File dir = new File(info[0]);
        File f = new File(dir, info[1]);
        if (!f.exists()) {
            throw new Exception("file " + f.getAbsolutePath() + " is not found");
        }
        return f;
    }







    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {

        File env = null;
        File configFile = null;
        String[] parsed;
        try {
            parsed = parse(args);
            env = makeDirFile(parsed);
            configFile = makeConfigFile(parsed);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(usage());
            return;
        }

        if (args.length < 1) {
            System.out.println("usage: <prog> filename");
            return;
        }
        
        List<Token> tokens = ConfigParser.getTokens(configFile);
        ConfigAcceptor configAcceptor = ConfigParser.getConfigAcceptor(tokens);
        RevsimConfig configObject = null;
        try {
            configObject = ConfigParser.getConfigObject(configAcceptor);
        }
        catch (Exception e) {
        	Logger.getLogger(RevSimApp.class.getName()).log(Level.SEVERE, null, e);
        	return;
        }
        
        try {
            Object [] userObjects = AppUtil.getUserObjects(env, Controller.class, View.class);
            Controller controller = (Controller)userObjects[0];
            View view = (View)userObjects[1];
            ViewDimensions vdim = new ViewDimensions();
            vdim.ppu = (float)Double.parseDouble(parsed[PPU]);
            vdim.width = Integer.parseInt(parsed[WIDTH]);
            vdim.height = Integer.parseInt(parsed[HEIGHT]);
            vdim.ulx = -vdim.width/2;
            if (parsed[ULX] != null) {
            	vdim.ulx = Integer.parseInt(parsed[ULX]);
            }
            vdim.uly = -vdim.height/2;
            if (parsed[ULY] != null) {
            	vdim.uly = Integer.parseInt(parsed[ULY]);
            }
            driver = new Driver(configFile, controller, view, new DumbModelStore(), configObject, vdim);
        }
        catch (Exception ex) {
            Logger.getLogger(RevSimApp.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        launch(RevSimApp.class, args);

    }
}
