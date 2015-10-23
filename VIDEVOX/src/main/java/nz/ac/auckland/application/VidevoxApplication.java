package nz.ac.auckland.application;

import java.io.IOException;
import java.util.Optional;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;
import nz.ac.auckland.view.PlayerViewController;
import nz.ac.auckland.view.RootLayoutController;

/**
 *
 * @author Fraser
 *
 */
public class VidevoxApplication extends Application {

	private static final Logger logger = Logger.getLogger(VidevoxApplication.class);

	/**
	 * On load, get a new default project
	 */
	Project _currentProject = Project.getProject();
	/**
	 * The window for the main part of the app to be loaded into
	 */
	private Stage _primaryStage;
	/**
	 * The shell layout with menu bars etc.
	 */
	private BorderPane _rootLayout;
	/**
	 * Controller for the root layout
	 */
	private RootLayoutController _controller;

	private ViewType _viewOnShow = ViewType.PREVIEW;

	public enum ViewType {
		PREVIEW, EDIT
	}

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

			// Set view toggle buttons
			_controller.setViewToggle(RootLayoutController.PREVIEW);
		} catch (IOException e) {
			// At this point, there is not much use trying to recover at this
			// point
			logger.error("showPlayerView()", e);
		}
	}

	private void initRootLayout() {
		try {
			// Load RootLayout
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getClassLoader().getResource("nz/ac/auckland/view/RootLayout.fxml"));
			_rootLayout = (BorderPane) loader.load();

			// Show on stage
			Scene scene = new Scene(_rootLayout);
			_primaryStage.setScene(scene);
			_primaryStage.show();
			_primaryStage.setMinHeight(550);
			_primaryStage.setMinWidth(750);

			// Set event handler on the window. Do not let it close without
			// prompting to save if unsaved
			scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent ev) {
					if (!_currentProject.isSaved()) {
						ev.consume();
						saveAndClose();
					}
				}
			});

			// Give controller access to main app
			_controller = loader.getController();
			_controller.setMainApp(this);
		} catch (IOException e) {
			logger.error("initRootLayout()", e);
		}
	}

	public void saveAndClose() {
		if (!_currentProject.isSaved()) {
			// Ask to save, exit without saving, or cancel
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Save Changes Before Exit");
			alert.setHeaderText("You Have Unsaved Changes");
			alert.setContentText("You have unsaved changes, do you want to save them now?");
			ButtonType saveButton = new ButtonType("Save Changes");
			ButtonType discardButton = new ButtonType("Discard Changes");
			ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(saveButton, discardButton, cancel);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == saveButton) {
				try {
					save();
				} catch (IOException e) {
					System.exit(1);
				}
			} else if (result.get() == discardButton) {
				Platform.exit();
			} else {
				return;
			}
		} else {
			Platform.exit();
		}
	}

	public static void showExceptionDialog(VidevoxException e) {
		// Show a generic dialog with the exception message
	}

	public void save() throws IOException {
		if (_currentProject.getLocation() != null) {
			try {
				_currentProject.toFile(_currentProject.getLocation());
			} catch (VidevoxException e) {
				showExceptionDialog(e);
			}
		} else {
			saveAs();
		}
	}

	public void saveAs() {
		// Implement
	}

	public Stage getStage() {
		return _primaryStage;
	}

	@Override
	public void start(Stage primaryStage) {
		// Set the primaryStage as the window for the application
		this._primaryStage = primaryStage;
		// Set a title to appear on the window
		this._primaryStage.setTitle("VIDEVOX - video editor");

		// Set/reset the views
		reset();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Resets the entire GUI, mostly for after a new GUI is loaded
	 */
	public void reset() {
		// Initiate the root layout of the application
		initRootLayout();

		// Decide which view to show
		switch (_viewOnShow) {
			case PREVIEW:
				showPlayerView();
				break;
			default:
				showPlayerView();
				break;
		}
	}

}
