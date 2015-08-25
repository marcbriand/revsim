package revsim;


import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import revsim.config.ConfigParser;
import revsim.config.lex.Token;
import revsim.config.objects.revsim.RevsimConfig;
import revsim.config.parse.revsim.ConfigAcceptor;
import revsim.driver.CLDriver;
import revsim.driver.Driver;
import revsim.history.DumbModelStore;
import revsim.mvc.Controller;
import revsim.mvc.View;

import cl.options.ArgParser;
import cl.options.BoolOption;

public class CLApp {
	
	private static boolean isSupportedFormat (String format) {
		if ("png".equalsIgnoreCase(format)) {
			return true;
		}
		if ("jpg".equalsIgnoreCase(format)) {
			return true;
		}
		return false;
	}
	
	private static void validateFormat (String filename, String format) throws Exception {
		if (!isSupportedFormat(format)) {
			throw new Exception("'" + format + "' is not a supported image file format");
		}
		int lastDot = filename.lastIndexOf(".");
		if (lastDot < 0) {
			throw new Exception("filename '" + filename + "' has no extension");
		}
		String ext = filename.substring(lastDot + 1);
		if (!format.equalsIgnoreCase(ext)) {
			throw new Exception("The filename extension '" + ext + "' does not match the specified format '" + format + "'");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		ArgParser parser = new ArgParser();
		parser.addBoolOption("-verbose", "bool", "verbose mode", BoolOption.ArgOp.HasArg, true);
		parser.addIntOption("-frame", "frame", "frame to print", 1);
		parser.addStringOption("-o", "imgfile", "path to image file", "image.png");
		parser.addStringOption("-format", "format", "image file format", "png");
		parser.addFloatOption("-ppu", "ppu", "pixels per unit", 400);
		parser.addIntOption("-w", "width", "width in pixels", 800);
		parser.addIntOption("-h", "height", "height in pixels", 600);
		parser.addIntOption("-ulx", "ulx", "left corner in pixels", -400);
		parser.addIntOption("-uly", "uly", "upper corner in pixels", -300);
		parser.addRequiredString("dir"); // dir
		parser.addRequiredString("file"); // file
		
		try {
			parser.parse(args);
		}
		catch (Exception e) {
			String usage = parser.getUsage("<prog>");
			System.out.println(usage);
		}
		
		String inputFolder = parser.getRequiredString(0);
		String inputFile = parser.getRequiredString(1);
		float ppu = parser.getFloatOption("-ppu");
		int w = parser.getIntOption("-w");
		int h = parser.getIntOption("-h");
		int ulx = parser.getIntOption("-ulx");
		int uly = parser.getIntOption("-uly");
		int frame = parser.getIntOption("-frame");
		boolean verbose = parser.getBoolOption("-verbose");
		String outputFileName = parser.getStringOption("-o");
		String outputFormat = parser.getStringOption("-format");
		
		System.out.println("Revsim command-line mode");
		System.out.println("Input folder: " + parser.getRequiredString(0));
		System.out.println("Input file: " + parser.getRequiredString(1));
		System.out.println("Pixels per unit: " + ppu);
		System.out.println("Width in pixels: " + w);
		System.out.println("Height in pixels: " + h);
		System.out.println("Ulx in pixels: " + ulx);
		System.out.println("Uly in pixels: " + uly);
		System.out.println("Target frame: " + frame);
		System.out.println("Output file: " + outputFileName);
		System.out.println("Output format: " + outputFormat);
		
		try {
			validateFormat(outputFileName, outputFormat);
		} 
		catch (Exception e1) {
			System.out.println(e1.getMessage());
			return;
		}
		
		File env = new File(parser.getRequiredString(0));
		if (!env.exists()) {
			System.out.println("cannot read from env folder " + inputFolder);
			return;
		}
		
		File configFile = new File(env, parser.getRequiredString(1));
		if (!configFile.exists() || !configFile.canRead()) {
			System.out.println("Cannot read config file " + parser.getRequiredString(1));
			return;
		}
		
		File outputFile = new File(outputFileName);
		
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
            vdim.ppu = ppu;
            vdim.width = w;
            vdim.height = h;
            vdim.ulx = ulx;
            vdim.uly = uly;
            CLDriver driver = 
            	new CLDriver(env, controller, view, configObject, vdim, (long)frame, outputFile, outputFormat);
            driver.setVerbose(verbose);
            driver.go();
        }
        catch (Exception ex) {
            Logger.getLogger(RevSimApp.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

	}

}
