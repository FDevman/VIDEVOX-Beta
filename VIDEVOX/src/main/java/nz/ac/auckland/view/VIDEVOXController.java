package nz.ac.auckland.view;

import nz.ac.auckland.application.VidevoxApplication;

public abstract class VIDEVOXController {

	/**
	 * Reference to application class
	 */
	protected VidevoxApplication _application;

	public void setMainApp(VidevoxApplication app) {
		_application = app;
	}

}
