package nz.ac.auckland.model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class is a wrapper on the JavaFX MediaPlayer to support starting after
 * an offset.
 *
 * @author Fraser
 *
 */
public class VidevoxMedia implements Playable {

	/**
	 * A MediaPlayer object for the Playable. Should be a JavaFX supported
	 * format
	 */
	private MediaPlayer _media;
	/**
	 * The start offset for the media in milliseconds
	 */
	private Duration _startOffset;
	/**
	 * The base name of the media. Used to identify it
	 */
	private final String NAME;

	public VidevoxMedia() {
		NAME = "Default";
	}

	private boolean _isActive = true;

	/**
	 * Default skip time set to 3 seconds
	 */
	public static final Duration DEFAULT_SKIP_TIME = new Duration(3000);

	/**
	 * Represents the most basic constructor for
	 * 
	 * @param fileName
	 * @throws VidevoxException
	 */
	VidevoxMedia(String fileName) throws VidevoxException {
		// Extract the base name of the file
		NAME = (new File(fileName)).getName();
		try {
			_media = new MediaPlayer(new Media(fileName));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format");
		}
	}

	/**
	 * Makes for one less line of code while using FileChooser which returns a
	 * File rather than a String
	 *
	 * @param mediaFile
	 * @throws VidevoxException
	 */
	VidevoxMedia(File mediaFile) throws VidevoxException {
		NAME = mediaFile.getName();
		try {
			_media = new MediaPlayer(new Media(mediaFile.getAbsolutePath()));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format");
		}
	}

	@Override
	public Duration getStartOffset() {
		return _startOffset;
	}

	@Override
	public void setStartOffset(Duration offset) {
		_startOffset = offset;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getAbsolutePath() throws URISyntaxException {
		// Extract the path as a string, not a URI. Bit of a workaround since
		// Media will only give you a URI and _uri.toString puts in strange
		// characters that FFMPEG doesn't like.
		return (new File(new URI(_media.getMedia().getSource()))).getAbsolutePath();
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		return _media;
	}

	@Override
	public void play() {
		if (_isActive && !_media.getStatus().equals(MediaPlayer.Status.PLAYING)) {
			// Start playing if not already playing and it is set to active
			_media.play();
		}
	}

	@Override
	public void pause() {
		_media.pause();
	}

	@Override
	public void stop() {
		_media.stop();
	}

	@Override
	public void seek(Duration position) {
		if (position.greaterThanOrEqualTo(_startOffset)) {
			// If the media should have started already at the new position,
			// start at that position minus the offset
			_media.seek(position.subtract(_startOffset));
			play();
		}
	}

	@Override
	public String getBasename() {

		return null;
	}

	/**
	 * To be called to que the starting of the media
	 * 
	 * @param time
	 */
	@Override
	public void start(Duration time) {
		// Current implementation is identical to seek
		seek(time);
	}

	@Override
	public String toString() {
		return null;
	}

}
