package nz.ac.auckland.application;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import nz.ac.auckland.model.Audible;
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
		HashSet<Audible> audios = project.getAudios();
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
	}

	public void addAudio(File audioFile, double startOffset, String name) throws VidevoxException {
		VidevoxMedia m = new VidevoxMedia(audioFile, startOffset);
		_audio.put(name, m);
		_markers.put(name, m.getStartOffset());
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
