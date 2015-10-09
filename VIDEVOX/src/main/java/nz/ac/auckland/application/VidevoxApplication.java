package nz.ac.auckland.application;

import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;
import nz.ac.auckland.model.VidevoxModel;

public class VidevoxApplication extends Application {

	private VidevoxModel model;
	
	@Override
	public void start(Stage primaryStage) {
		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void findModel() {
		// Display file chooser
		// pass file to loadMoedl();
	}
	
	/**
	 * Loads a new project from file
	 */
	private void loadModel(File modelFile) {
		// Implement reading from file to string and construct project from string
	}
}
