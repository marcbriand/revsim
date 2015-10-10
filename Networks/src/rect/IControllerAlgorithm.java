package rect;

import config.NetworkConfig;
import revsim.mvc.Model;

public interface IControllerAlgorithm {

	Model createFirstNodes(NetworkController ncont, NetworkConfig ncfg, long currentFrame); 
	Model growNetwork(NetworkController ncont, Model model, long currentFrame);
}
