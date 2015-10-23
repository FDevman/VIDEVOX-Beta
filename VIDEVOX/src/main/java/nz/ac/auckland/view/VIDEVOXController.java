package nz.ac.auckland.view;

import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.application.VidevoxPlayer;

public abstract class VIDEVOXController {

	/**
	 * Reference to current model instance
	 */
	protected VidevoxPlayer _model;
	/**
	 * Reference to application class
	 */
	protected VidevoxApplication _application;

	public void setMainApp(VidevoxApplication app) {
		_application = app;
	}

	public void setModel(VidevoxPlayer model) {
		_model = model;
	}
}
