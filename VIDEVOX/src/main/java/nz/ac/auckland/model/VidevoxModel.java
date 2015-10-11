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

/**
 * <p>
 * Each instance represents a project in VIDEVOX. Each project contains a main
 * video file which will dictate the length of the final product. Each project
 * contains any number of audio files to be played alongside the video with a
 * start offset. Audio clips must be in .mp3 format and video must be in .mp4
 * for the moment.
 * </p>
 *
 * <p>
 * This class will handle the synchronizing of play-back between media files so
 * that they behave as if they were all one file. It also supplies
 * implementation for compiling (exporting) all the media into a single video
 * file, although this takes too long to be done on-the-fly.
 * </p>
 *
 * <p>
 * This class has implementation for converting the object to a string format
 * and building it back up from a string with the correct format.
 * </p>
 *
 * <p>
 * Also note, this class only contains locations of the media files in the
 * native file system. Moving those files will therefore break the play-back of
 * that specific media.
 * </p>
 *
 * @author Fraser
 *
 */
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

	/**
	 * Sets the video file to be loaded into the supplied MediaView. This file
	 * will serve as the primary video file which audio tracks can be overlayed
	 * onto.
	 *
	 * @param file
	 *            - A File object pointing to the location of the video file.
	 * @param view
	 *            - The MediaView the the video should be loaded into.
	 * @throws VidevoxException
	 */
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

	/**
	 *
	 * @param videoView
	 */
	public void setVideoView(MediaView videoView) {
		videoView.setMediaPlayer(_mainVideo);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("project-name:" + _name);
		for (Entry<String, Playable> e : _audio.entrySet()) {
			Playable p = e.getValue();
			b.append(NEW_LINE + "audio:" + p.toString());
		}
		return b.toString();
	}

}
