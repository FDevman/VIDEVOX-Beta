package nz.ac.auckland.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import nz.ac.auckland.application.VidevoxApplication;

public class TTSViewController extends VIDEVOXController {

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
	private void validateTime() {

	}

	@FXML
	private void cancel() {

	}

	@FXML
	private void preview() {

	}

	@FXML
	private void save() {

	}

	@FXML
	private void use() {

	}

	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		// Anything else?
		_cancelButton.requestFocus();
	}

}
