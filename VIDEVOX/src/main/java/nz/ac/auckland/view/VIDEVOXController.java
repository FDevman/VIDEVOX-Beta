package nz.ac.auckland.view;

import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.model.VidevoxModel;

public abstract class VIDEVOXController {

	/**
	 * Reference to current model instance
	 */
	protected VidevoxModel _model;
	/**
	 * Reference to application class
	 */
	protected VidevoxApplication _application;

	public void setMainApp(VidevoxApplication app) {
		_application = app;
	}

	public void setModel(VidevoxModel model) {
		_model = model;
	}
}
