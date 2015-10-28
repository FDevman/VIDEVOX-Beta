package nz.ac.auckland.model;

import java.io.File;

import org.json.simple.JSONObject;

public class AudioFile implements Audible {

	File _audioFile;
	double _startOffset = 0.0;
	boolean _active = true;

	final String DEFAULT_AUDIO = "media"+FILE_SEP+"The Mercury Tale.mp3";
	final static String FILE_SEP = System.getProperty("file.separator");
	final static String FILE = "FILE";
	final static String START = "START";
	final static String ACTIVE = "ACTIVE";

	/**
	 * Default constructor leaves default values
	 */
	public AudioFile() {
//		try {
//			_audioFile = new File(java.net.URLDecoder
//					.decode(this.getClass().getClassLoader().getResource(DEFAULT_AUDIO).getPath(), "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//
//		}
	}

	/**
	 * Full constructor specifies the file to be read and the start offset.
	 * Active is left as true.
	 *
	 * @param file
	 * @param offset
	 */
	public AudioFile(File file, double offset) {
		_audioFile = file;
		_startOffset = offset;
	}

	/**
	 * Constructs an AudioFile object given json object. Only other model
	 * classes should be able to do this
	 *
	 * @param json
	 */
	protected AudioFile(JSONObject json) {
		_audioFile = new File((String) json.get(FILE));
		_startOffset = (double) json.get(START);
		_active = (boolean) json.get(ACTIVE);
	}

	/**
	 * Marshals a JSON object from the current instance
	 *
	 * @return
	 */
// @Override
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		// Insert values
		json.put(FILE, _audioFile.getAbsolutePath());
		json.put(START, _startOffset);
		json.put(ACTIVE, _active);
		return json;
	}

// @Override
	public boolean isActive() {
		return _active;
	}

// @Override
	public void setActive(boolean active) {
		_active = active;
	}

// @Override
	public void setStartOffset(double startOffset) {
		_startOffset = startOffset;
	}

// @Override
	public double getStartOffset() {
		return _startOffset;
	}

// @Override
	public String getName() {
		return _audioFile.getName();
	}

// @Override
	public File getFile() {
		return _audioFile;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof AudioFile))
			return false;
		AudioFile other = (AudioFile) o;
		if (other._audioFile.equals(_audioFile) && other._startOffset == _startOffset) {
			return true;
		} else {
			return false;
		}
	}

}
