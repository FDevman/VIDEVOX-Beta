package nz.ac.auckland.model;

import java.io.File;

public class AudioTTS extends AudioFile {

	/**
	 *
	 */
	String _speech;

	final String SPEECH = "SPEECH";
	final String FILE_SEP = System.getProperty("file.separator");

	public AudioTTS() {
	}

	public AudioTTS(String name, String speech, double offset) {
		_audioFile = new File(System.getProperty("java.io.tmpdir" + FILE_SEP + name));

	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof AudioTTS))
			return false;
		AudioTTS other = (AudioTTS) o;
		if (other._audioFile.equals(_audioFile) && other._startOffset == _startOffset) {
			return true;
		} else {
			return false;
		}
	}

}
