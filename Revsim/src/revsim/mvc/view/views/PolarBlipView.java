/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.mvc.view.views;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.objects.ArrayObject;
import revsim.config.objects.ConfigObject;
import revsim.config.objects.revsim.TransformObject;
import revsim.modeltools.objects.AbstractBlip;
import revsim.mvc.Model;
import revsim.mvc.View;
import revsim.mvc.model.BlipModel;
import revsim.mvc.view.AbstractView;
import revsim.mvc.view.GetBlipsOp;
import revsim.mvc.view.NodeException;
import revsim.mvc.view.PatternNode;
import revsim.rendering.MergeMethod;
import revsim.rendering.RenderUtil;

/**
 *
 * @author Marc
 */
public class PolarBlipView extends AbstractView {
    // ulx and uly are in terms of distance from center
    // for example, for a rectangle 400 x 200 centered on
    // the virtual point 0,0, ulx is -200 and uly is -100
    @Override
    public Image render (Model model, int ulx, int uly) {
    	BlipModel blipModel = (BlipModel)model;

        int radius = (int)(ppu*2) - 20;
        
        float whalf = ((float)width)/2.0f;
        float hhalf = ((float)height)/2.0f;
        
        float xoffset = -whalf - ulx;
        float yoffset = -hhalf - uly;
        
        int w = (int)(blipSize*radius);
        
        BufferedImage bim = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//        int background = Integer.parseInt("ff0000", 16);
//        background <<= 8;
        int[] bkgARGB = color.getInt4ARGB();
        int background = RenderUtil.combineColor4(bkgARGB);
        RenderUtil.initImageWithColor(bim, background);

        Graphics g = bim.getGraphics();
        
        List<AbstractBlip> newBlips = new ArrayList<AbstractBlip>(blipModel.getBlips().size());
        for (AbstractBlip blip : blipModel.getBlips()) {
        	
            double angle = RenderUtil.normalizeAngle(blip.getX()*360.0);
            double rads = RenderUtil.degreesToRads(angle);

            double xprime = blip.getY()*Math.cos(rads);
            double yprime = blip.getY()*Math.sin(rads);
            
            AbstractBlip newBlip = (AbstractBlip)blip.clone();
            newBlip.setX((float)xprime);
            newBlip.setY((float)yprime);
            newBlip.setColor(blip.getColor());
            newBlips.add(newBlip);

        }
        
        PatternNode tree = new PatternNode();
        for (AbstractBlip blip : newBlips) {
        	tree.addBlip(blip);
        }
        try {
			PatternNode.applyTransforms(transforms, tree);
		} 
        catch (NodeException e) {
			e.printStackTrace();
			return null;
		}
        
        GetBlipsOp op = new GetBlipsOp();
        PatternNode.apply(tree, op);
        List<AbstractBlip> txFormed = op.getBlips();

        for (AbstractBlip blip : txFormed) {
        	int x = (int)(whalf + (blip.getX()*ppu) + xoffset);
        	int y = (int)(hhalf + (blip.getY()*ppu) + yoffset);
        	g.setColor(blip.getColor().getColor());
        	int dotWidth = (int)(w*blip.getScale());
            RenderUtil.renderBlip(x, y, 
            		              blipSize*ppu,
            		              blip, mergeMethod, solar, bim);
        }
                
        return bim;
    }
}
