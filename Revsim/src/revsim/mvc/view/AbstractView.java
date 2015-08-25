package revsim.mvc.view;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.objects.ArrayObject;
import revsim.config.objects.ConfigObject;
import revsim.config.objects.revsim.TransformObject;
import revsim.modeltools.objects.MColor;
import revsim.mvc.Model;
import revsim.mvc.View;
import revsim.rendering.MergeMethod;

public abstract class AbstractView implements View {
	
    protected float blipSize = 0.1f;
    protected float solar = 0.0f;
    protected MergeMethod mergeMethod = MergeMethod.Normal;
    protected List<TransformObject> transforms = new ArrayList<TransformObject>();
    protected float ppu = 280.0f;
    protected int width = 800;
    protected int height = 600;
    protected int xoffset = 0;
    protected int yoffset = 0;
    protected MColor color = new MColor();

	@Override
	public void init(File env, ConfigObject configObj, float ppu, int width,
			int height) throws ConfigException {
    	Object bsObj = configObj.getLocal("blipsize");
    	if (bsObj != null) {
    		blipSize = (Float)bsObj;
    	}
    	Object sol = configObj.getLocal("solar");
    	if (sol != null) {
    		solar = (Float)sol;
    	}
    	Object mergeObj = configObj.getLocal("merge");
    	if (mergeObj != null) {
    		String mergeMethodStr = (String)mergeObj;
    		if (mergeMethodStr.equals("average")) {
    			mergeMethod = MergeMethod.Average;
    		}
    	}
    	Object txObj = configObj.getLocal("transforms");
    	if (txObj != null) {
    		if (!(txObj instanceof ArrayObject)) {
    			throw new ConfigException("'transforms' must list of transforms");
    		}
    		ArrayObject txArr = (ArrayObject)txObj;
    		List<Object> txObjs = txArr.getObjects();
    		for (int i = 0; i < txObjs.size(); i++) {
    			Object o = txObjs.get(i);
    			if (!(o instanceof TransformObject)) {
    				throw new ConfigException("The transform at index " + i + " (0-based) is not a valid Transform");
    			}
    			transforms.add((TransformObject)o);
    		}
    	}
    	Object bkgColor = configObj.getLocal("bkgcolor");
    	if (bkgColor != null) {
    		color = (MColor)bkgColor;
    	}
    	this.ppu = ppu;
    	this.width = width;
    	this.height = height;
	}

	@Override
	public abstract Image render(Model model, int ulx, int uly);

}
