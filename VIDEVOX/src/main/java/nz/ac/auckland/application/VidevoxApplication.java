package nz.ac.auckland.application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nz.ac.auckland.model.VidevoxModel;
import nz.ac.auckland.view.PlayerViewController;
import nz.ac.auckland.view.RootLayoutController;
import nz.ac.auckland.view.VIDEVOXController;

/**
 *
 * @author Fraser
 *
 */
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
	/**
	 *
	 */
	private RootLayoutController _controller;

	/**
	 *
	 */
	public void showPlayerView() {
		try {
			// Load PlayerView
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(VidevoxApplication.class.getResource("/nz/ac/auckland/view/PlayerView.fxml"));
			AnchorPane playerView = (AnchorPane) loader.load();

			// Place it inside the root layout
			_rootLayout.setCenter(playerView);

			// Give the controller class the references it wants
			PlayerViewController controller = loader.getController();
			controller.setMainApp(this);
			controller.setModel(_model);

			// Set view toggle buttons
			_controller.setViewToggle(RootLayoutController.PREVIEW);
		} catch (IOException e) {
			// At this point, there is not much use trying to recover at this
			// point
			e.printStackTrace();
		}
	}

	private void initRootLayout() {
		try {
			// Load RootLayout
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(VidevoxApplication.class.getResource("/nz/ac/auckland/view/RootLayout.fxml"));
			_rootLayout = (BorderPane) loader.load();

			// Show on stage
			Scene scene = new Scene(_rootLayout);
			_primaryStage.setScene(scene);
			_primaryStage.show();

			// Give controller access to main app
			_controller = loader.getController();
			_controller.setMainApp(this);
			_controller.setModel(_model);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		System.out.println("stopping!");
		return;
	}

	@Override
	public void start(Stage primaryStage) {
		// Set the primaryStage as the window for the application
		this._primaryStage = primaryStage;
		// Set a title to appear on the window
		this._primaryStage.setTitle("VIDEVOX - video editor");

		// Create a blank project so that _model is not null
		_model = new VidevoxModel();

		// Initiate the root layout of the application
		initRootLayout();

		// Show the player view as default
		showPlayerView();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 *
	 */
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
