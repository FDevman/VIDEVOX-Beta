package nz.ac.auckland.view;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import nz.ac.auckland.model.VidevoxException;

public class RootLayoutController extends VIDEVOXController {

	public static final String PREVIEW = "preview";

	public static final String EDITOR = "editor";

	@FXML
	private Button _previewButton;

	@FXML
	private Button _editorButton;

	@FXML
	private void close() {
		try {
			_application.saveAndClose();
		} catch (VidevoxException | IOException e) {
			System.exit(1);
		}
	}

	@FXML
	private void previewButtonAction() {
		setViewToggle(PREVIEW);
	}

	@FXML
	private void editorButtonAction() {
		setViewToggle(EDITOR);
	}

	@FXML
	private void setVideo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Please select a video file to work on");
		// Set visible extensions
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Video Files ", "*.mp4");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(_application.getStage());
		_application.setVideo(file);
	}

	public void setViewToggle(String option) {
		switch (option) {
		case PREVIEW:
			_editorButton.setDisable(false);
			_previewButton.setDisable(true);
			_application.showPlayerView();
			break;
		case EDITOR:
			_previewButton.setDisable(false);
			_editorButton.setDisable(true);
			_application.showEditView();
			break;
		default:
		}
	}
}
