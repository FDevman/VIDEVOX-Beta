package nz.ac.auckland.view;

import java.io.File;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.application.VidevoxPlayer;
import nz.ac.auckland.model.Project;

/**
 *
 * @author Fraser
 *
 */
public class PlayerViewController extends VIDEVOXController {
	/**
	* Logger for this class
	*/
	private static final Logger logger = Logger.getLogger(PlayerViewController.class);

	@FXML
	private MediaView _mainPlayerView;

	@FXML
	private ImageView _playButton;

	@FXML
	private ImageView _pauseButton;

	@FXML
	private ImageView _skipBackButton;

	@FXML
	private ImageView _skipForwardButton;

	@FXML
	private VBox _scrollBox;

	@FXML
	private HBox _mediaControls;

	@FXML
	private HBox _videoContainer;

	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		logger.debug(getClass().getClassLoader().getResource("icons/play-icon.png").toString());
		// Set images for buttons
		_playButton.setImage(new Image(getClass().getClassLoader().getResource("icons/play-icon.png").toString()));
		_pauseButton.setImage(new Image(getClass().getClassLoader().getResource("icons/pause-icon.png").toString()));
		_skipForwardButton.setImage(new Image(getClass().getClassLoader().getResource("icons/last-track-icon.png").toString()));
		_skipBackButton.setImage(new Image(getClass().getClassLoader().getResource("icons/first-track-icon.png").toString()));
		MediaPlayer p = VidevoxPlayer.getPlayer().getMediaPlayer();
		if (p != null) {
			_mainPlayerView.setMediaPlayer(p);
			// Figure out scale
			double mediaHeight = _mainPlayerView.getFitHeight();
			double mediaWidth = _mainPlayerView.getFitWidth();
			double fitHeight = _videoContainer.getHeight();
			double fitWidth = _videoContainer.getWidth();
			if (true) {

			}
			double scale = 1.0;
			_mainPlayerView.setScaleX(scale);
			_mainPlayerView.setScaleY(scale);
		} else {
			_mediaControls.setDisable(true);
		}
	}

	@FXML
	private void play() {
		VidevoxPlayer.getPlayer().play();
	}

	@FXML
	private void pause() {
		VidevoxPlayer.getPlayer().pause();
	}

	@FXML
	private void skipBack() {
		VidevoxPlayer.getPlayer().skipBack();
	}

	@FXML
	private void skipForward() {
		VidevoxPlayer.getPlayer().skipForward();
	}

}
