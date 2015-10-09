package nz.ac.auckland.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class VidevoxModel {

	/**
	 * The primary video used in the
	 */
	private VidevoxMedia _mainVideo;

	private Map<String, Playable> _audio;

	public VidevoxModel() {
		_audio = new HashMap<String, Playable>();
	}

	public VidevoxModel(String savedModel) {

	}

	/**
	 *
	 * @return
	 */
	public VidevoxMedia getMainVideo() {
		return _mainVideo;
	}

	@Override
	public String toString() {
		return null;
	}

}
