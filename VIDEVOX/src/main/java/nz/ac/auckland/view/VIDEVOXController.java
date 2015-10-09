package nz.ac.auckland.view;

import nz.ac.auckland.model.VidevoxModel;

public abstract class VIDEVOXController {

	protected VidevoxModel _model;
	// main app reference
	
//	public void setMainApp(mainApp) {
//		set main app method
//	}
	
	public void setModel(VidevoxModel model) {
		_model = model;
	}
}
