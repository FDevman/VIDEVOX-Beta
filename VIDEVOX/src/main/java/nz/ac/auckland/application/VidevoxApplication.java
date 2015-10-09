package nz.ac.auckland.application;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nz.ac.auckland.model.VidevoxModel;
import nz.ac.auckland.view.VIDEVOXController;

public class VidevoxApplication extends Application {

	/**
	 * The current model instance for the application to work with
	 */
	private VidevoxModel _model;
	/**
	 * The window for the main part of the app to be loaded into
	 */
	private Stage _primaryStage;
	/**
	 * The shell layout with menu bars etc.
	 */
	private BorderPane _rootLayout;

	public void initRootView() {
		// Load RootLayout
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(VidevoxApplication.class.getResource("view/RootLayout.fxml"));

		// Show on stage
		Scene scene = new Scene(_rootLayout);
		_primaryStage.setScene(scene);
		_primaryStage.show();

		// Give controller access to main app
		VIDEVOXController controller = loader.getController();
		controller.setMainApp(this);
		controller.setModel(_model);
	}

	@Override
	public void start(Stage primaryStage) {
		this._primaryStage = primaryStage;
		this._primaryStage.setTitle("VIDEVOX - video editor");
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
		// Implement reading from file to string and construct project from
		// string
	}
}
