package nz.ac.auckland.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RootLayoutController extends VIDEVOXController {

	public static final String PREVIEW = "preview";

	public static final String EDITOR = "editor";

	@FXML
	private Button _previewButton;

	@FXML
	private Button _editorButton;

	@FXML
	private void close() {

	}

	/**
	 *
	 * @param option
	 */
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
