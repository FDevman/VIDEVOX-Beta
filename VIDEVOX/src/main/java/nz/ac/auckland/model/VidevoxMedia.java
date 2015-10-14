package nz.ac.auckland.model;

import org.apache.log4j.Logger;

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

	private static final Logger logger = Logger.getLogger(VidevoxMedia.class);

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
	/**
	 * Switch to determine if the item is currently being included
	 */
	private boolean __active = true;

	public VidevoxMedia() {
		NAME = "Default";
	}

	/**
	 * Default skip time set to 3 seconds
	 */
	public static final Duration DEFAULT_SKIP_TIME = new Duration(3000);

	/**
	 * Represents the most basic constructor for
	 *
	 * @param fileName
	 * @throws VidevoxException
	 * @throws URISyntaxException
	 */
	VidevoxMedia(String str) {
		String[] args = str.split(":");
		_media = new MediaPlayer(new Media(args[0]));
		NAME = new File(args[0]).getName();
		_startOffset = new Duration(Double.parseDouble(args[1]));
	}

	/**
	 * Makes for one less line of code while using FileChooser which returns a
	 * File rather than a String
	 *
	 * @param mediaFile
	 * @throws VidevoxException
	 */
	VidevoxMedia(File mediaFile) throws VidevoxException {
		// Extract the basename of the file
		NAME = mediaFile.getName();
		try {
			_media = new MediaPlayer(new Media(mediaFile.toURI().toString()));
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
		if (__active && !_media.getStatus().equals(MediaPlayer.Status.PLAYING)) {
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
		return NAME;
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

	/**
	 * Converts the current media item into a string representation,
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		try {
			b.append(new URI(_media.getMedia().getSource()).toString());
		} catch (URISyntaxException e) {
			// Should never happen since URI is validated when MediaPlayer is
			// created
			logger.debug("URI Syntax error ???");
		}
		b.append(":" + _startOffset.toMillis());
		return b.toString();
	}

}
