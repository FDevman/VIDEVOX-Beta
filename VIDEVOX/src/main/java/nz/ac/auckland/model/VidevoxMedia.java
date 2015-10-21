package nz.ac.auckland.model;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	MediaPlayer _media;
	/**
	 * The start offset for the media in milliseconds
	 */
	Duration _startOffset;
	/**
	 * The base name of the media. Used to identify it
	 */
	String _name;
	/**
	 * Switch to determine if the item is currently being included (default =
	 * true)
	 */
	boolean _active = true;

	static String _defaultAudio = "resources" + FILE_SEPARATOR + "media" + FILE_SEPARATOR + "The Mercury Tale.mp3";
//	static String _defaultAudio = "C:\\Users\\Fraser\\Documents\\UoA Papers\\SE-206\\Eclipse Projects\\VIDEVOX-Beta\\VIDEVOX\\src\\test\\resources\\media";

	/**
	 * Create an instance containing the default media file and a start offset
	 * of 0 seconds
	 *
	 * @throws VidevoxException
	 */
	public VidevoxMedia() throws VidevoxException {
		this(new File(_defaultAudio), 0.0);
	}

	/**
	 * Default skip time set to 3 seconds
	 */
	public static final Duration DEFAULT_SKIP_TIME = new Duration(3000);

	/**
	 * Is the constructor called when recreating the object based on a JSON
	 * formatted string
	 *
	 * @param fileName
	 * @throws ParseException
	 * @throws VidevoxException
	 * @throws URISyntaxException
	 */
	public VidevoxMedia(String str) throws ParseException, URISyntaxException {

		// Create a JSON object from the string
		JSONObject obj = (JSONObject) new JSONParser().parse(str);
		// Extract the URL field to create the MediaPlayer object
		_media = new MediaPlayer(new Media((String) obj.get("URI")));
		// Extracts the basename of the file from the URI by using the
		// java.io.File class
		_name = new File(new URI((String) obj.get("URI"))).getName();
		// Parse the string from JSON as a double and use it to create a
		// Duration
		_startOffset = new Duration(Double.parseDouble((String) obj.get("startOffset")));
		// Retrieve a boolean from the JSON string
		_active = Boolean.parseBoolean((String) obj.get("active"));

		// String[] args = str.split(":");
		// _media = new MediaPlayer(new Media(args[0]));
		// NAME = new File(args[0]).getName();
		// _startOffset = new Duration(Double.parseDouble(args[1]));

	}

	/**
	 * Makes for one less line of code while using FileChooser which returns a
	 * File rather than a String
	 *
	 * @param mediaFile
	 * @throws VidevoxException
	 */
	public VidevoxMedia(File mediaFile, double startOffset) throws VidevoxException {
		// Extract the basename of the file
		_name = mediaFile.getName();
		try {
			_media = new MediaPlayer(new Media(mediaFile.toURI().toString()));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format");
		}
		_startOffset = new Duration(startOffset);
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
		if (_active && !_media.getStatus().equals(MediaPlayer.Status.PLAYING)) {
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
		return _name;
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
	 * Converts the current media item into a JSON formatted string
	 * representation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("URI", _media.getMedia().getSource());
		obj.put("startOffset", Double.toString(_startOffset.toMillis()));
		obj.put("active", Boolean.toString(_active));
		return obj.toJSONString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof VidevoxMedia)) {
			return false;
		}
		VidevoxMedia vm = (VidevoxMedia) obj;
		try {
			if (vm.getAbsolutePath().equals(this.getAbsolutePath())) {
				if (vm._startOffset.equals(_startOffset)) {
					if (vm._active == _active) {
						return true;
					}
				}
			}
		} catch (URISyntaxException e) {
			return false;
		}
		return false;
	}

}
