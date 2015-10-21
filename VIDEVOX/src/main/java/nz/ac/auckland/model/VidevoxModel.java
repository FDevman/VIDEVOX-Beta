package nz.ac.auckland.model;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

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
public class VidevoxModel extends VidevoxMedia implements Playable {

	private static final Logger logger = Logger.getLogger(VidevoxModel.class);

	private Map<String, Playable> _audio;

	private String _path;

	public VidevoxModel() throws VidevoxException {
		_audio = new HashMap<String, Playable>();
		_name = "New Project";
	}

	public VidevoxModel(String savedModel) throws VidevoxException {
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
		if (_media != null) {
			// Dump the current video if there is one
			_media.dispose();
		}
		// Load the file into the javaFX media player
		try {
			_media = new MediaPlayer(new Media(file.toURI().toString()));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format");
		}
		// Place the media player inside the supplied view
		view.setMediaPlayer(_media);
		// Set a property listener on the current time of the video, use it to
		// trigger other media players to start at the correct times
		_media.currentTimeProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable obs) {
				// Iterate over HashMap of audio components
				for (Entry<String, Playable> e : _audio.entrySet()) {
					// Get the value (Playable object)
					Playable p = e.getValue();
					// Call the start method which will handle timings on each
					// Playable
					p.start(_media.getCurrentTime());
				}
			}
		});
	}

	public MediaPlayer getMainVideo() {
		return _media;
	}

	/**
	 *
	 * @param videoView
	 */
	public void setVideoView(MediaView videoView) {
		videoView.setMediaPlayer(_media);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject obj = new JSONObject();

		obj.put("videoURI", _media.getMedia().getSource());
		obj.put("name", _name);

		JSONArray audioList = new JSONArray();
		for (Entry<String, Playable> e : _audio.entrySet()) {
			Playable p = e.getValue();
			audioList.add("audio: " + p.toString());
		}

		obj.put("audioList", audioList);

		return obj.toJSONString();

		// StringBuilder b = new StringBuilder("project-name:" + _name);
		// for (Entry<String, Playable> e : _audio.entrySet()) {
		// Playable p = e.getValue();
		// b.append(NEW_LINE + "audio:" + p.toString());
		// }
		// return b.toString();
	}

	@Override
	public void start(Duration time) {
		super.start(time);
		for (Entry<String, Playable> e: _audio.entrySet()) {
			Playable p = e.getValue();
			p.start(time);
		}
	}

	@Override
	public String getBasename() {
		return super.getBasename();
	}

	@Override
	public String getAbsolutePath() throws URISyntaxException {
		return null;
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void seek(Duration position) {
		// TODO Auto-generated method stub

	}

}
