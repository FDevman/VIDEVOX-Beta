package nz.ac.auckland.application;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import nz.ac.auckland.model.Audible;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

public class VidevoxPlayer implements Playable {

	private static final Logger logger = Logger.getLogger(VidevoxPlayer.class);

	/**
	 * The singleton instance of VidevoxPlayer.
	 */
	private static VidevoxPlayer INSTANCE;

	static final Duration SKIP_INTERVAL = new Duration(3000);

	/**
	 * A hash map linking Playables to their human readable names. Names should
	 * be unique.
	 */
	private Map<String, Playable> _audio;
	/**
	 * List of markers to start audio files, keys should match _audio field.
	 */
	private ObservableMap<String, Duration> _markers;
	/**
	 * The video that is to be played.
	 */
	private MediaPlayer _video;
	/**
	 * The human readable name of the video being played.
	 */
	String _videoName;

	private boolean _active;

	private static final String NO_VIDEO = "No Video";

	/**
	 * Singleton get instance method. Creates one if null.
	 *
	 * @return
	 */
	public static VidevoxPlayer getPlayer() {
		if (INSTANCE == null) {
			INSTANCE = new VidevoxPlayer();
		}
		return INSTANCE;
	}

	/**
	 * Default constructor to be called if no instance is present.
	 */
	private VidevoxPlayer() {
		// Retrieve project
		Project project = Project.getProject();
		// Initialize hash map
		_audio = new HashMap<String, Playable>();
		File videoFile = project.getVideo();
		if (videoFile != null) {
			logger.debug("this() - video file found at: " + videoFile.getAbsolutePath());
			try {
				_video = new MediaPlayer(new Media(videoFile.toURI().toString()));
			} catch (Exception e) {
				logger.error("this() - EXCEPTION! " + e.getMessage());
			}
			setMarkerListener();
			_videoName = project.getVideoName();
			_audio.put(_videoName, this);
			_active = project.isVideoMuted();
			_video.setMute(!_active);
		} else {
			logger.debug("this() - no video found");
			// Add a null video
			_videoName = NO_VIDEO;
			_audio.put(_videoName, null);
		}

		logger.trace("Done adding video to _audios");

		HashSet<Audible> audios = project.getAudios();
		// Add each audio file to the list of Playables
		for (Audible a : audios) {
			String name = a.getName();
			logger.debug("VidevoxPlayer - unpacking: " + a.getFile());
			Playable audioPlayer;
			try {
				audioPlayer = new VidevoxMedia(a);
				_audio.put(name, audioPlayer);
				logger.debug("Audio added: " + name + ", " + a.getStartOffset() + ", " + a.getFile());
			} catch (VidevoxException e) {
				// Do nothing right now can stop crash but can't recover error
				VidevoxApplication.showExceptionDialog(e);
			}
		}
		logger.trace("Done adding " + (_audio.size() - 1) + " audios");

		if (videoFile != null) {
			_markers = _video.getMedia().getMarkers();
			setMarkers();
		}
	}

	private void setMarkerListener() {
		// Set event handlers on marker events
		_video.setOnMarker(new EventHandler<MediaMarkerEvent>() {
			@Override
			public void handle(MediaMarkerEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						logger.debug("Marker triggered: " + event.getMarker().getKey());
						// Get the key for the audio that should start,
						String audioKey = event.getMarker().getKey();
						for (Entry<String, Playable> e : _audio.entrySet()) {
							if (e.getKey().equals(audioKey)) {
								// Seek to the current moment
								e.getValue().seek(event.getMarker().getValue());
								// Play the audio
								if (_video.getStatus().equals(Status.PLAYING)) {
									e.getValue().play();
								}
							}
						}
					}
				});
			}
		});
		logger.trace("Done adding action listeners");
	}

	private void setMarkers() {
		// Set markers to trigger audio play back
		for (Entry<String, Playable> e : _audio.entrySet()) {
			String key = e.getKey();
			logger.debug("Marker added: " + key + ", " + e.getValue().getStartOffset().toSeconds());
			if (!key.equals(_videoName)) {
				Duration value = e.getValue().getStartOffset();
				_markers.put(key, value);
				logger.debug("Marker added: " + key + ", " + value.toSeconds());
			}
		}
	}

	/**
	 * Sets the video file to be loaded into the MediaView. This file will serve
	 * as the primary video file which audio tracks can be overlayed onto.
	 *
	 * @param file
	 *            - A File object pointing to the location of the video file.
	 * @param view
	 *            - The MediaView the the video should be loaded into.
	 * @throws VidevoxException
	 */
	public void setVideo(File file) throws VidevoxException {
		// Put the new video in the Player
		_audio.remove(_videoName);
		if (_video != null) {
			// Dump the current video if there is one
			_video.dispose();
			logger.trace("Got rid of old video");
		}
		// Update the Project
		Project.getProject().setVideo(file);

		// Call the constructor to sync the player with the project
		rebuild();
	}

	/**
	 * Only place on application side that should be used to add audio files to
	 * the project.
	 *
	 * @param audioFile
	 * @param startOffset
	 */
	public void addAudio(File audioFile, double startOffset) {
		// Update the project
		Project.getProject().addAudio(audioFile, startOffset);
		// Call the constructor to sync the Player
		rebuild();
	}

	/**
	 * Adds a tts component to both the Project and the player.
	 *
	 * @param name
	 * @param text
	 * @param offset
	 * @throws VidevoxException
	 */
	public void addTTS(String name, String text, double offset) throws VidevoxException {
		logger.trace("addTTS - started");
		// Update the project
		Project.getProject().addTTS(name, text, offset);
		logger.trace("addTTS - project updated");
		// Call the constructor to sync
		rebuild();
	}

	public void openProject(File file) {
		try {
			Project.buildProject(file);
			rebuild();
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		}
	}

	public void rebuild() {
		logger.trace("reduild() - started");
		INSTANCE = new VidevoxPlayer();
	}

	@Override
	public void seek(Duration time) {
		// Go to required time
		_video.seek(time);
		for (Entry<String, Playable> e : _audio.entrySet()) {
			if (!e.getKey().equals(_videoName)) {
				Playable p = e.getValue();
				p.seek(time);
			}

		}
		// If the video was playing before, set everything playing
		if (_video.getStatus().equals(MediaPlayer.Status.PLAYING)) {
			play();
		}
	}

	@Override
	public void play() {
		if (_video == null) {
			return;
		}
		_video.play();
		for (Entry<String, Playable> e : _audio.entrySet()) {
			if (!e.getKey().equals(_videoName)) {
				Playable m = e.getValue();
				logger.debug("play() - " + e.getKey());
				if (_video.getCurrentTime().greaterThanOrEqualTo(m.getStartOffset())) {
					m.seek(_video.getCurrentTime());
					m.play();
				}
			}

		}
	}

	@Override
	public void pause() {
		_video.pause();
		for (Entry<String, Playable> e : _audio.entrySet()) {
			if (!e.getKey().equals(_videoName)) {
				Playable m = e.getValue();
				m.pause();
			}
		}
	}

	@Override
	public void stop() {
		_video.stop();
		for (Entry<String, Playable> e : _audio.entrySet()) {
			if (!e.getKey().equals(_videoName)) {
				Playable m = e.getValue();
				m.stop();
			}
		}
	}

	@Override
	public void skipForward() {
		seek(_video.getCurrentTime().add(SKIP_INTERVAL));
		logger.debug("Skipping to: " + _video.getCurrentTime().add(SKIP_INTERVAL).toSeconds());
	}

	@Override
	public void skipBack() {
		seek(_video.getCurrentTime().subtract(SKIP_INTERVAL));
	}

	@Override
	public Duration getStartOffset() {
		// Offset of Player is always zero
		return new Duration(0.0);
	}

	@Override
	public void setStartOffset(double newOffset) {
		// Can't set the start offset of this Playable
	}

	/**
	 * Returns the MediaPlayer that contains the video. May be null if no video
	 * has been loaded yet.
	 *
	 * @return
	 */
	@Override
	public MediaPlayer getMediaPlayer() {
		return _video;
	}

	public void addAudio(File file) {
		if (_video != null) {
			addAudio(file, _video.getCurrentTime().toMillis());
		} else {
			addAudio(file, 0.0);
		}
	}

	@Override
	public boolean isActive() {
		return _active;
	}

	public void setActive(String name, boolean isActive) {
		Project.getProject().setActive(name, isActive);
		if (name.equals(_videoName)) {
			_active = isActive;
			_video.setMute(!_active);
		} else {
			((VidevoxMedia) _audio.get(name)).setActive(isActive);
			if (_video != null && _video.getStatus().equals(Status.PLAYING)) {
				play();
				// _audio.get(name).seek(_video.getCurrentTime());
				// _audio.get(name).play();
			}
		}
	}

	public void setStartOffset(String name, double offset) {
		if (name.equals(_videoName)) {
			return;
		}
		Project.getProject().setOffset(name, offset);
		_audio.get(name).setStartOffset(offset);
		if (_video != null) {
			_markers.put(name, new Duration(offset));
		}
	}

	public Map<String, Playable> getPlayables() {
		return _audio;
	}

}
