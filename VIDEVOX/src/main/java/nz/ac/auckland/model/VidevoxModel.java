package nz.ac.auckland.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VidevoxModel {

	/**
	 * The primary video used in the
	 */
	private MediaPlayer _mainVideo;

	private Map<String, Playable> _audio;

	private String _name;

	public static final String NEW_LINE = System.getProperty("line.separator");

	public static final String AUDIO_TO_FOLLOW = "AUDIO";

	public static final String VIDEO_TO_FOLLOW = "VIDEO";

	public VidevoxModel() {
		_audio = new HashMap<String, Playable>();
		_name = "New Project";
	}

	public VidevoxModel(String savedModel) {
		this(); // Initializes the
		// Implement from string
	}

	public void setMainVideo(File file, MediaView view) throws VidevoxException {
		if (_mainVideo != null) {
			// Dump the current video if there is one
			_mainVideo.dispose();
		}
		// Load the file into the javaFX media player
		try {
			_mainVideo = new MediaPlayer(new Media(file.toURI().toString()));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format");
		}
		// Place the media player inside the supplied view
		view.setMediaPlayer(_mainVideo);
		// Set a property listener on the current time of the video, use it to
		// trigger other media players to start at the correct times
		_mainVideo.currentTimeProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable obs) {
				// Iterate over HashMap of audio components
				for (Entry<String, Playable> e : _audio.entrySet()) {
					// Get the value (Playable object)
					Playable p = e.getValue();
					// Call the start method which will handle timings on each
					// Playable
					p.start(_mainVideo.getCurrentTime());
				}
			}
		});
	}
	
	public MediaPlayer getMainVideo() {
		return _mainVideo;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(_name);
		b.append(NEW_LINE + "audio:");
		return null;
	}

}
