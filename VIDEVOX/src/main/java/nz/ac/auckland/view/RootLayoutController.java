package nz.ac.auckland.view;

import java.io.File;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class RootLayoutController extends VIDEVOXController {
	/**
	* Logger for this class
	*/
	private static final Logger logger = Logger.getLogger(RootLayoutController.class);

	public static final String PREVIEW = "preview";

	public static final String EDITOR = "editor";

	@FXML
	private Button _previewButton;

	@FXML
	private Button _editorButton;

	@FXML
	private void selectVideo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Please select a video file to work on");
		// Set visible extensions
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Video Files ", "*.mp4");
		fileChooser.getExtensionFilters().add(extFilter);
		File file;
		file = fileChooser.showOpenDialog(_application.getStage());
		if (file == null) {
			// User canceled selecting video
			return;
		}
		logger.debug("Video name: " + file.getName());
		// set the video
	}

	@FXML
	private void close() {
		_application.saveAndClose();
	}

	@FXML
	private void editorButtonAction() {
		setViewToggle(EDITOR);
	}

	@FXML
	private void previewButtonAction() {
		setViewToggle(PREVIEW);
	}

	public void setViewToggle(String option) {
		switch (option) {
		case PREVIEW:
			_editorButton.setDisable(false);
			_previewButton.setDisable(true);
			break;
		case EDITOR:
			_previewButton.setDisable(false);
			_editorButton.setDisable(true);
			break;
		default:
		}
	}
}
