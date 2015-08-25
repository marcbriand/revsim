package revsim;


import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppUtil {
	
    private static void checkUserObjects (Class [] classes, Object [] objects) throws Exception {
        if (classes.length != objects.length) {
            throw new IllegalArgumentException("classes and objects must be of equal length");
        }
        for (int i = 0; i < classes.length; i++) {
            if (objects[i] == null) {
                throw new Exception("did not find implementation of " + classes[i].getName() + " in user-supplied classes");
            }
        }
    }
	
    private static int extendsOneOf (Class cls, Class...classes) {
        for (int i = 0; i < classes.length; i++) {
            if (classes[i].isAssignableFrom(cls)) {
                return i;
            }
        }
        return -1;
    }
	
    private static Class safeGetClass (String classname) {
        try {
            return Class.forName(classname);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(RevSimApp.class.getName()).log(Level.INFO, null, ex);
            return null;
        }
    }


    public static Object [] getUserObjects (File env, Class...classes) throws Exception {

        Object [] ret = new Object[classes.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = null;
        }
        File userJar = new File(env, "usersim.jar");
        if (!userJar.exists()) {
            throw new Exception("the required user jar " + userJar.getAbsolutePath() + " could not be found");
        }
        if (!userJar.canRead()) {
            throw new Exception("the user jar " + userJar.getAbsolutePath() + " cannot be read");
        }

        ZipFile zipFile = new ZipFile(userJar);
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String name = entry.getName();
            String className = name.replaceAll("/", ".");
            if (className.endsWith(".class")) {
                className = className.substring(0, className.length() - ".class".length());
            }
            else {
                continue;
            }
            Class cls = safeGetClass(className);
            if (cls == null) {
                continue;
            }
            int index = extendsOneOf(cls, classes);
            if (index >= 0) {
                ret[index] = cls.newInstance();
            }
        }
        zipFile.close();
        checkUserObjects(classes, ret);
        return ret;
    }

}
