package nz.ac.auckland.application;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import nz.ac.auckland.model.Audible;
import nz.ac.auckland.model.AudioFile;
import nz.ac.auckland.model.VidevoxException;

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
	MediaPlayer _media;
	/**
	 * The start offset for the media in milliseconds
	 */
	private Duration _startOffset;
	/**
	 * Switch to determine if the item is currently being included (default =
	 * true)
	 */
	boolean _active = true;

	static String _defaultAudio = FILE_SEPARATOR + "media" + FILE_SEPARATOR + "The Mercury Tale.mp3";

	/**
	 * Create an instance containing the default media file and a start offset
	 * of 0 seconds
	 *
	 * @throws VidevoxException
	 */
	public VidevoxMedia() throws VidevoxException {
		this(new AudioFile(new File(_defaultAudio), 0.0));
	}

	/**
	 * Default skip time set to 3 seconds
	 */
	public static final Duration DEFAULT_SKIP_TIME = new Duration(3000);

	/**
	 * Makes for one less line of code while using FileChooser which returns a
	 * File rather than a String
	 *
	 * @param Audible
	 * @throws VidevoxException
	 */
	public VidevoxMedia(Audible a) throws VidevoxException {
		try {
			_media = new MediaPlayer(new Media(a.getFile().toURI().toString()));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format: " + a.getFile().getAbsolutePath());
		}
		_startOffset = new Duration(a.getStartOffset());
	}

	// /**
	// * Makes for one less line of code while using FileChooser which returns a
	// * File rather than a String
	// *
	// * @param mediaFile
	// * @throws VidevoxException
	// */
	// public VidevoxMedia(File mediaFile, double startOffset) throws
	// VidevoxException {
	// try {
	// _media = new MediaPlayer(new Media(mediaFile.toURI().toString()));
	// } catch (MediaException e) {
	// throw new VidevoxException("Invalid file type or format");
	// }
	// _startOffset = new Duration(startOffset);
	// }

	public Duration getStartOffset() {
		return _startOffset;
	}

	public void setStartOffset(Duration offset) {
		_startOffset = offset;
		if (VidevoxPlayer.getPlayer().getMediaPlayer().getCurrentTime().greaterThanOrEqualTo(_startOffset)
				&& VidevoxPlayer.getPlayer().getMediaPlayer().getStatus().equals(Status.PLAYING)) {
			play();
		}
	}

	public String getAbsolutePath() throws URISyntaxException {
		// Extract the path as a string, not a URI. Bit of a workaround since
		// Media will only give you a URI and _uri.toString puts in strange
		// characters that FFMPEG doesn't like.
		return (new File(new URI(_media.getMedia().getSource()))).getAbsolutePath();
	}

	public void play() {
		if (_active) {
			// Start playing if active
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
		if (_active) {
			if (_startOffset.greaterThan(position)) {
				// Stop the play-back if the new position is before this media
				// should start
				_media.pause();
				_media.seek(_media.getStartTime());
			} else {
				// Go to the relative position this media should be at
				_media.seek(position.subtract(_startOffset));
			}
		}
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		return _media;
	}

	@Override
	public void skipForward() {
		_media.seek(_media.getCurrentTime().add(VidevoxPlayer.SKIP_INTERVAL));
	}

	@Override
	public void skipBack() {
		_media.seek(_media.getCurrentTime().subtract(VidevoxPlayer.SKIP_INTERVAL));
	}

	@Override
	public void setStartOffset(double newOffset) {
		_startOffset = new Duration(newOffset);
	}

	@Override
	public boolean isActive() {
		return _active;
	}

	void setActive(boolean isActive) {
		_active = isActive;
		logger.debug(_media.getMedia().getSource() + " set to " + isActive);
		if (!isActive) {
			_media.pause();
		}
	}

}
