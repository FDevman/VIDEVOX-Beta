package nz.ac.auckland.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import nz.ac.auckland.application.Playable;
import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.application.VidevoxPlayer;
import nz.ac.auckland.model.VidevoxException;

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
	private Slider _timeSlider;

	@FXML
	private Label _timeLabel;

	/**
	 * At this point, everything else should be ready to go so it can be used as
	 * trigger for setting up the view.
	 */
	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		logger.debug(getClass().getClassLoader().getResource("icons/play-icon.png").toString());
		// Set images for buttons
		_playButton.setImage(new Image(getClass().getClassLoader().getResource("icons/play-icon.png").toString()));
		_pauseButton.setImage(new Image(getClass().getClassLoader().getResource("icons/pause-icon.png").toString()));
		_skipForwardButton
				.setImage(new Image(getClass().getClassLoader().getResource("icons/last-track-icon.png").toString()));
		_skipBackButton
				.setImage(new Image(getClass().getClassLoader().getResource("icons/first-track-icon.png").toString()));
		MediaPlayer player = VidevoxPlayer.getPlayer().getMediaPlayer();
		if (player != null) {
			_mainPlayerView.setMediaPlayer(player);
			logger.trace("Video in view");
			_mediaControls.setDisable(false);
			resize();
			// Add listeners for window size
			_application.getStage().heightProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable o) {
					resize();
				}
			});
			_application.getStage().widthProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable o) {
					resize();
				}
			});
			player.currentTimeProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					_timeSlider.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis());
					String current = String.format("%1$,.2f", player.getCurrentTime().toSeconds());
					String total = String.format("%1$,.2f", player.getTotalDuration().toSeconds());
					_timeLabel.setText(current + "/" + total);
					if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
						logger.debug("end of video");
						VidevoxPlayer.getPlayer().pause();
					}
				}
			});

		} else {
			_mediaControls.setDisable(true);
		}
		// Populate audio list
		Map<String, Playable> media = VidevoxPlayer.getPlayer().getPlayables();
		for (Entry<String, Playable> e : media.entrySet()) {
			try {
				// Load RootLayout
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(
						this.getClass().getClassLoader().getResource("nz/ac/auckland/view/AudioListBox.fxml"));
				VBox listItem = (VBox) loader.load();
				_scrollBox.getChildren().add(listItem);
				AudioListBoxController controller = loader.getController();
				controller.setMainApp(_application);
				controller.setPlayable(e.getKey(), e.getValue());
			} catch (IOException e1) {
				VidevoxApplication
						.showExceptionDialog(new VidevoxException("Could not load " + e.getKey() + "into audio list"));
			}
		}
	}

	private void resize() {
		// Figure out scale
		double mediaHeight = _mainPlayerView.getFitHeight();
		double mediaWidth = _mainPlayerView.getFitWidth();
		double fitHeight = _application.getStage().getHeight() - 160;
		double fitWidth = _application.getStage().getWidth() - 15;
		double scale;
		if (fitHeight / mediaHeight < fitWidth / mediaWidth) {
			scale = fitHeight / mediaHeight;
		} else {
			scale = fitWidth / mediaWidth;
		}
		logger.debug("Height scale = " + fitHeight / mediaHeight + ", fitHeight = " + fitHeight + ", mediaHeight"
				+ mediaHeight);
		logger.debug(
				"Width scale = " + fitWidth / mediaWidth + ", fitWidth = " + fitWidth + ", mediaWidth" + mediaWidth);

		// _mainPlayerView.heigh
		_mainPlayerView.setScaleX(scale);
		_mainPlayerView.setScaleY(scale);
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

	@FXML
	private void tts() {
		_application.showTTS();
	}

	@FXML
	private void audio() {
		_application.addAudio();
	}

	@FXML
	private void seek() {
		double totalTime = VidevoxPlayer.getPlayer().getMediaPlayer().getTotalDuration().toMillis();
		VidevoxPlayer.getPlayer().seek(Duration.millis(_timeSlider.getValue() * totalTime));
	}

}
