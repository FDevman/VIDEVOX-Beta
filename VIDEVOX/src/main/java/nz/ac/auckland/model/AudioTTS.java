package nz.ac.auckland.model;

import java.io.File;
import java.io.IOException;

/**
 * This class represents an audio clip that has been created "on-the-fly". Upon
 * creation, it will actually create a temporary file with the TTS audio in it,
 * point there, and then act as any other AudioFile would. This file is not
 * meant to persist however and so AudioTTS must know how to recreate it next
 * time the project is loaded. If the audio is to be persisted, it should be
 * saved as an audio file and referred to as any other AudioFile.
 *
 * @author Fraser
 *
 */
public class AudioTTS extends AudioFile {

	/**
	 * Text that should be given to Festival
	 */
	String _speech;
	/**
	 * Unlike, the Audio File, the TTS does not need the file extension in its
	 * name
	 */
	String _name;

	final String SPEECH = "SPEECH";
	final String FILE_SEP = System.getProperty("file.separator");

	public AudioTTS(String name, String speech, double offset) throws VidevoxException {
		// Initialize fields
		_name = name;
		_startOffset = offset;
		_speech = speech;
		_audioFile = new File(System.getProperty("java.io.tmpdir") + FILE_SEP + name);
		ModelHelper.ensureFileExtension(_audioFile, ".mp3");

		textToMP3(_audioFile, speech);
	}

	private boolean textToMP3(File destination, String speech) throws VidevoxException {
		// Create a pointer to a temporary wav file
		File tempWAV = new File(System.getProperty("java.io.tmpdir") + destination.getName());
		ModelHelper.ensureFileExtension(tempWAV, ".wav");

		// Make sure the destination is a .mp3 file
		ModelHelper.ensureFileExtension(destination, ".mp3");

		// Create the audio file at designated location (surround file names
		// with quotes in case of spaces)
		String cmd = "echo '" + speech + "' | text2wave " + "-o " + "\"" + tempWAV.getAbsolutePath() + "\"";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			int returnVal = process.waitFor();
			if (returnVal != 0) {
				throw new VidevoxException("Wav file unable to be created: Check that festival is installed correctly");
			}
		} catch (IOException e) {
			throw new VidevoxException("Unknown IO Exception during WAV creation");
		} catch (InterruptedException e) {
			throw new VidevoxException("Festival thread interrupted: Unknown source");
		}
		// Convert the wav to mp3
		cmd = "ffmpeg -i " + "\"" + tempWAV.getAbsolutePath() + "\"" + " -y -f mp3 " + "\"" + destination.getAbsolutePath()
				+ "\"";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			int returnVal = process.waitFor();
			if (returnVal != 0) {
				throw new VidevoxException("Unable to convert to mp3: Check that festival is installed correctly");
			}
		} catch (IOException e) {
			throw new VidevoxException("Unknown IO Exception during mp3 conversion");
		} catch (InterruptedException e) {
			throw new VidevoxException("Festival thread interrupted: Unknown source");
		}
		// Delete the temporary file
		tempWAV.delete();
		return true;
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
