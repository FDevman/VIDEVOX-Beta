package nz.ac.auckland.view;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.application.VidevoxPlayer;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

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
	private Label _projectName;

	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		_projectName.setText(Project.getProject().getName());
	}

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
		try {
			VidevoxPlayer.getPlayer().setVideo(file);
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		}
		_application.reset();
	}
	
	@FXML
	private void open() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Please select a project file to work on");
		// Set visible extensions
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Project Files", "*.vvox");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(_application.getStage());
		if (file == null) {
			return;
		}
		try {
			Project.buildProject(file);
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		}
		_application.reset();
	}
	
	@FXML
	private void newProject() {
		Project.reset();
		_application.reset();
	}
	
	@FXML
	private void save() {
		try {
			_application.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void saveAs() {
		_application.saveAs();
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
