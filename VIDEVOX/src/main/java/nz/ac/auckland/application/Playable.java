package nz.ac.auckland.application;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public interface Playable {

	public final String FILE_SEPARATOR = System.getProperty("file.separator");

	public void play();

	public void pause();

	public void stop();

	public void seek(Duration position);

	public Duration getStartOffset();

	public MediaPlayer getMediaPlayer();

}
