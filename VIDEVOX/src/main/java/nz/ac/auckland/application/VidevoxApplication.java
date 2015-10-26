package nz.ac.auckland.application;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.model.ModelHelper;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;
import nz.ac.auckland.view.PlayerViewController;
import nz.ac.auckland.view.RootLayoutController;
import nz.ac.auckland.view.TTSViewController;

/**
 *
 * @author Fraser
 *
 */
public class VidevoxApplication extends Application {

	private static final Logger logger = Logger.getLogger(VidevoxApplication.class);

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
					if (!Project.getProject().isSaved()) {
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
		if (!Project.getProject().isSaved()) {
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
				Platform.exit();
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
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setHeaderText("An Error has Occurred");
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}

	public void save() throws IOException {
		Project project = Project.getProject();
		if (project.getLocation() != null) {
			try {
				project.toFile(project.getLocation());
			} catch (VidevoxException e) {
				showExceptionDialog(e);
			}
		} else {
			saveAs();
		}
	}

	public void saveAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save the project");
		// Set extension filter to only see .vvox project files
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Project file", "*.vvox");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(_primaryStage);
		ModelHelper.enforceFileExtension(file, ".vvox");
		if (file == null) {
			return;
		}
		try {
			Project.getProject().toFile(file);
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		// Initiate the root layout of the application
		initRootLayout();

		// Set/reset the views
		reset();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Resets the entire GUI from the root layout down, mostly for after a new
	 * GUI is loaded
	 */
	public void reset() {
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

	public void showTTS() {
		try {
			logger.trace("entered showTTS");

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getClassLoader().getResource("nz/ac/auckland/view/TTSView.fxml"));
			logger.debug(
					"location: " + this.getClass().getClassLoader().getResource("nz/ac/auckland/view/TTSView.fxml"));
			VBox ttsView = (VBox) loader.load();

			logger.trace("Loaded ttsView from fxml");

			Stage stage = new Stage();
			stage.setTitle("VIDEVOX Text-to-Speech");
			stage.setScene(new Scene(ttsView));

			// Keep a pointer to the Primary Stage's Event Dispatcher for later
			EventDispatcher ev = _primaryStage.getEventDispatcher();

			// Put in a new Event Dispatcher while the TTS view is open
			_primaryStage.setEventDispatcher(new EventDispatcher() {
				@Override
				public Event dispatchEvent(Event event, EventDispatchChain tail) {
					stage.requestFocus();
					return null;
				}
			});

			logger.trace("Showing ttsView");

			TTSViewController controller = loader.getController();
			controller.setMainApp(this);

			stage.showAndWait();

			// Put the Event Dispatcher back and reset the app in case TTS was
			// added
			_primaryStage.setEventDispatcher(ev);
			reset();

		} catch (IOException e) {
			logger.debug("error: " + e.getMessage());
			e.printStackTrace();
			VidevoxApplication.showExceptionDialog(new VidevoxException(e.getMessage()));
		}
	}

	public void addAudio() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select an Audio file to use");
		// Set visible extensions
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Audio Files", "*.mp3");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(_primaryStage);
		if (file != null) {
			VidevoxPlayer.getPlayer().addAudio(file);
			reset();
		}
	}

}
