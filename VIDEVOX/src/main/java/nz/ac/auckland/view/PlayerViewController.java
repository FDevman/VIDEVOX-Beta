package nz.ac.auckland.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import nz.ac.auckland.application.VidevoxModel;

/**
 *
 * @author Fraser
 *
 */
public class PlayerViewController extends VIDEVOXController {

	@FXML
	private MediaView _mainPlayerView;

	@FXML
	private Button _playPauseButton;

	@FXML
	private Button _skipBackButton;

	@FXML
	private Button _skipForwardButton;

	@FXML
	private VBox _scrollBox;

	@Override
	public void setModel(VidevoxModel model) {
		super.setModel(model);
		_model.setVideoView(_mainPlayerView);
		// Load audio into audio views
	}

}
