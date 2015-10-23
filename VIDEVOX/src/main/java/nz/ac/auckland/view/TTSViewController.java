package nz.ac.auckland.view;

import java.io.File;

import org.apache.log4j.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.application.VidevoxPlayer;
import nz.ac.auckland.model.Audible;
import nz.ac.auckland.model.AudioFile;
import nz.ac.auckland.model.AudioTTS;
import nz.ac.auckland.model.ModelHelper;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

public class TTSViewController extends VIDEVOXController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TTSViewController.class);

	@FXML
	private Button _previewButton;

	@FXML
	private VBox _container;

	@FXML
	private HBox _buttonBox;

	@FXML
	private TextField _nameField;

	@FXML
	private TextArea _content;

	@FXML
	private TextField _offset;

	@FXML
	private Button _cancelButton;

	@FXML
	private void cancel() {
		((Stage) _container.getScene().getWindow()).close();
	}

	@FXML
	private void preview() {
		_previewButton.setDisable(true);
		try {
			AudioTTS.preview(_content.getText());
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		} finally {
			_previewButton.setDisable(false);
		}
	}

	@FXML
	private void save() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Text-to-Speech as mp3");
		// Set extension filter to only see .mp4 files
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 Audio", "*.mp3");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog((Stage) _container.getScene().getWindow());
		// If no file was made (user canceled) then return
		if (file == null) {
			return;
		}
		ModelHelper.enforceFileExtension(file, ".mp3");

		try {
			AudioTTS.textToMP3(file, _content.getText());
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
			return;
		}
		AudioFile audio = Project.getProject().addAudio(file, Double.parseDouble(_offset.getText()));
		VidevoxPlayer.getPlayer().addAudio(audio);

		cancel();
	}

	@FXML
	private void use() {
		try {
			Audible audio = Project.getProject().addTTS(_nameField.getText(), _content.getText(),
					Double.parseDouble(_offset.getText()));
			VidevoxPlayer.getPlayer().addAudio(audio);
		} catch (NumberFormatException e) {
			logger.error(_offset.getText() + " : not valid double : " + e.getMessage());
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		}

		cancel();
	}

	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		// Add a change listener to the combined number of chars in the name and
		// text fields
		NumberBinding buttonsActive = Bindings.add(_nameField.lengthProperty(), _content.lengthProperty());
		buttonsActive.addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				// If either field is null, disable the buttons
				if (_content.getLength() == 0 || _nameField.getLength() == 0) {
					_buttonBox.setDisable(true);
				} else {
					_buttonBox.setDisable(false);
				}
			}
		});
		// Enforce offset field to be a valid double
		_offset.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				logger.debug("Offset text changed");
				// Check format, if invalid set it back to the old value
				if (newValue.equals("")) {
					_offset.setText("0");
					return;
				}
				try {
					Double.parseDouble(newValue);
				} catch (NumberFormatException e) {
					_offset.setText(oldValue);
				}
			}
		});
	}

}
