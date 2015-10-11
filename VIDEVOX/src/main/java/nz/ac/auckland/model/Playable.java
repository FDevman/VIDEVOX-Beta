package nz.ac.auckland.model;

import java.net.URISyntaxException;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public interface Playable {

	public final String FILE_SEPARATOR = System.getProperty("file.separator");

	public final String DEFAULT_PATH = "/tmp/";

	public void start(Duration time);

	public Duration getStartOffset();

	public void setStartOffset(Duration offset);

	public String getBasename();

	public String getAbsolutePath() throws URISyntaxException;

	public MediaPlayer getMediaPlayer();

	public void play();

	public void pause();

	public void stop();

	public void seek(Duration position);

}
