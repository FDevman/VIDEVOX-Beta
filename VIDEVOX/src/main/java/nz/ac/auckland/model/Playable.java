package nz.ac.auckland.model;

import java.net.URISyntaxException;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public interface Playable {

	final String FILE_SEPARATOR = System.getProperty("file.separator");

	final String DEFAULT_PATH = "/tmp/";

	public String getBasename();

	public void start(Duration time);

	public Duration getStartOffset();

	public void setStartOffset(Duration offset);

	public String getName();

	public String getAbsolutePath() throws URISyntaxException;

	public MediaPlayer getMediaPlayer();

	public void play();

	public void pause();

	public void stop();

	public void seek(Duration position);

}
