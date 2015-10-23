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
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import nz.ac.auckland.model.Audible;
import nz.ac.auckland.model.AudioFile;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

public class VidevoxPlayer implements Playable {

	private static final Logger logger = Logger.getLogger(VidevoxPlayer.class);

	private static VidevoxPlayer INSTANCE;

	private Map<String, Playable> _audio;

	private ObservableMap<String, Duration> _markers;

	private MediaPlayer _video;

	String _videoName;

	public VidevoxPlayer getPlayer() {
		if (INSTANCE == null) {
			INSTANCE = new VidevoxPlayer();
		}
		return INSTANCE;
	}

	public VidevoxPlayer() {
		Project project = Project.getProject();
		_videoName = project.getVideoName();
		File video = project.getVideo();
		// Retrieve audio files
		_audio = new HashMap<String, Playable>();
		// Add the video as an audio
		_audio.put(_videoName, INSTANCE);

		logger.trace("Done adding video to _audios");

		HashSet<Audible> audios = project.getAudios();
		// Add each audio file to the list of Playables
		for (Audible a : audios) {
			String name = a.getName();
			Playable player;
			try {
				player = new VidevoxMedia(a.getFile(), a.getStartOffset());
				_audio.put(name, player);
			} catch (VidevoxException e) {
				// Do nothing right now can stop crash but can't recover error
				VidevoxApplication.showExceptionDialog(e);
			}
		}

		logger.trace("Done adding audios");

		if (video != null) {
			// Only set up video if it exists
			try {
				setVideo(video);
			} catch (VidevoxException e) {
				// Do nothing right now can stop crash but can't recover error
				VidevoxApplication.showExceptionDialog(e);
				return;
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
		if (_video != null) {
			// Dump the current video if there is one
			_video.dispose();
			logger.trace("Got rid of old video");
		}
		// Load the file into the javaFX media player
		try {
			_video = new MediaPlayer(new Media(file.toURI().toString()));
		} catch (MediaException e) {
			throw new VidevoxException("Invalid file type or format");
		}
		// Set markers to trigger audio play back
		_markers = _video.getMedia().getMarkers();
		for (Entry<String, Playable> e : _audio.entrySet()) {
			_markers.put(e.getKey(), e.getValue().getStartOffset());
		}
		// Set event handlers on marker events
		_video.setOnMarker(new EventHandler<MediaMarkerEvent>() {
			@Override
			public void handle(MediaMarkerEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// Get the key for the audio that should start,
						String audioKey = event.getMarker().getKey();
						for (Entry<String, Playable> e : _audio.entrySet()) {
							if (e.getKey().equals(audioKey)) {
								// Seek to the current moment
								e.getValue().seek(event.getMarker().getValue());
								// Play the audio
								e.getValue().play();
							}
						}
					}
				});
			}
		});
		logger.trace("Done adding action listeners");
	}

	/**
	 * Only place on application side that should be used to add audio files to
	 * the project.
	 *
	 * @param audioFile
	 * @param startOffset
	 */
	public void addAudio(File audioFile, double startOffset) {
		Audible a = new AudioFile(audioFile, startOffset);
		Project.getProject().addAudio(audioFile, startOffset);
		VidevoxMedia m;
		try {
			m = new VidevoxMedia(audioFile, startOffset);
			_audio.put(a.getName(), m);
			_markers.put(a.getName(), m.getStartOffset());
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		}
	}

	/**
	 * Only place on application side that should be used to add TTS components
	 * to the project.
	 *
	 * @param name
	 * @param text
	 * @param offset
	 */
	public void addTTS(String name, String text, double offset) {
		Audible a = Project.getProject().addTTS(name, text, offset);
		VidevoxMedia m;
		try {
			m = new VidevoxMedia(a.getFile(), a.getStartOffset());
			_audio.put(name, m);
			_markers.put(name, m.getStartOffset());
		} catch (VidevoxException e) {
			VidevoxApplication.showExceptionDialog(e);
		}
	}

	public MediaPlayer getMainVideo() {
		return _video;
	}

	/**
	 *
	 * @param videoView
	 */
	public void setVideoView(MediaView videoView) {
		videoView.setMediaPlayer(_video);
	}

	@Override
	public void seek(Duration time) {
		// Go to required time
		for (Entry<String, Playable> e : _audio.entrySet()) {
			Playable p = e.getValue();
			p.seek(time);
		}
		// If the video was playing before, set everything playing
		if (_video.getStatus().equals(MediaPlayer.Status.PLAYING)) {
			play();
		}
	}

	@Override
	public void play() {
		_video.play();
		for (Entry<String, Playable> e : _audio.entrySet()) {
			Playable m = e.getValue();
			if (_video.getCurrentTime().greaterThan(m.getStartOffset())) {
				m.play();
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
			Playable m = e.getValue();
			m.stop();
		}
	}

	@Override
	public Duration getStartOffset() {
		// Return a Duration that will always be earlier thatn the current
		// duration (i.e. it will always have started)
		return new Duration(-1.0);
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		return _video;
	}

}
