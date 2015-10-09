package nz.ac.auckland.model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class was really just supposed to extend javafx.scene.Media or
 * javafx.scene.MediaPlayer and add a start offset and a robust way to get the
 * basename of the media file.
 *
 * @author Fraser
 *
 */
public abstract class VidevoxMedia implements Playable {

	/**
	 * A media object for the Playable. Should be a JavaFX supported format
	 */
	private Media _media;
	/**
	 * The start offset for the media in milliseconds
	 */
	private int _startOffset;
	/**
	 * The base name of the media. Used to identify it
	 */
	private final String NAME;

	public VidevoxMedia() {
		NAME = "Default";
	}

	/**
	 * Represents the most basic constructor for
	 * @param fileName
	 */
	VidevoxMedia(String fileName) {
		// Extract the base name of the file
		NAME = (new File(fileName)).getName();
		_media = new Media(fileName);
	}

	/**
	 * Makes for one less line of code while using FileChooser which returns a
	 * File rather than a String
	 *
	 * @param mediaFile
	 */
	VidevoxMedia(File mediaFile) {
		NAME = mediaFile.getName();
		_media = new Media(mediaFile.getAbsolutePath());
	}

	@Override
	public int getStartOffset() {
		return _startOffset;
	}

	@Override
	public void setStartOffset(int offset) {
		_startOffset = offset;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getAbsolutePath() throws URISyntaxException {
		// Extract the path as a string, not a URI. Bit of a workaround since
		// Media will only give you a URI and _uri.toString puts in strange
		// characters that FFMPEG doesn't like.
		return (new File(new URI(_media.getSource()))).getAbsolutePath();
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		return new MediaPlayer(_media);
	}

	private String basename(String fullName) {
		// Get basename
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
	public void skipAhead() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skipBack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seek(Duration position) {
		// TODO Auto-generated method stub
		
	}

}
