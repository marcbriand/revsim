/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package revsim.modeltools.launcher;

import revsim.modeltools.objects.AbstractBlip;

/**
 *
 * @author Marc
 */
public interface BlipCreator {

	public AbstractBlip createBlip (Float x, Float y, Float xvel, Float yvel, long currentFrame);
	
}
