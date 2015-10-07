package nz.ac.auckland.model;

import java.net.URISyntaxException;

import javafx.scene.media.MediaPlayer;

public interface Playable {

	final String FILE_SEPARATOR = System.getProperty("file.separator");

	final String DEFAULT_PATH = "/tmp/";

	public int getStartOffset();

	public void setStartOffset(int offset);

	public String getName();

	public String getAbsolutePath() throws URISyntaxException;

	public MediaPlayer getMediaPlayer();

}
