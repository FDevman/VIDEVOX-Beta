package nz.ac.auckland.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;

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
	* Logger for this class
	*/
	private static final Logger logger = Logger.getLogger(AudioTTS.class);

	/**
	 * Text that should be given to Festival
	 */
	String _speech;
	/**
	 * Unlike, the Audio File, the TTS does not need the file extension in its
	 * name
	 */
	String _name;

	final static String SPEECH = "SPEECH";
	final static String NAME = "NAME";

	public AudioTTS(JSONObject json) throws VidevoxException {
		// Build TTS objects
		this((String) json.get(NAME), (String) json.get(SPEECH), (double) json.get(START));
		_active = (boolean) json.get(ACTIVE);
	}

	public AudioTTS(String name, String speech, double offset) throws VidevoxException {
		// Initialize fields
		_name = name;
		_startOffset = offset;
		_speech = speech;
		String path = AudioTTS.class.getClassLoader().getResource("temp").toString().substring(5);
		logger.debug("this(String, String, double) - path found at: " + path);
		_audioFile = new File(path + "/" + name);

		textToMP3(_audioFile, _speech);
		_audioFile = ModelHelper.enforceFileExtension(_audioFile, ".mp3");
		logger.debug("AudioTTS(String, String, double) - New TTS file: " + _audioFile.getAbsolutePath());
	}

	public static boolean preview(String text) throws VidevoxException {
		String cmd = "echo \"" + text + "\" | festival --tts";
		logger.debug("command is: " + cmd);
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start().waitFor();
		} catch (IOException | InterruptedException e) {
			throw new VidevoxException("Text to speech preview is not working at the moment");
		}
		return true;
	}

	/**
	 * Creates wav and mp3 files to be used from the temp directory. Any name
	 * conflicts will be overwritten without prompt.
	 *
	 * @param destination
	 * @param speech
	 * @return
	 * @throws VidevoxException
	 */
	public static boolean textToMP3(File destination, String speech) throws VidevoxException {
		// Create a pointer to a temporary wav file
		File tempWAV = ModelHelper.enforceFileExtension(destination, ".wav");
		logger.debug("File name is actually: " + tempWAV.getAbsolutePath());

		// Make sure the destination is a .mp3 file
		destination = ModelHelper.enforceFileExtension(destination, ".mp3");

		// Create the audio file at designated location (surround file names
		// with quotes in case of spaces)
		String cmd = "echo '" + speech + "' | text2wave " + "-o " + "\"" + tempWAV.getAbsolutePath() + "\"";
		logger.debug("textToMP3 - cmd = " + cmd);
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			int returnVal = process.waitFor();
			if (returnVal != 0) {
				throw new VidevoxException("Wav file unable to be created: Check that festival is installed correctly");
			}
			logger.trace("created wav at: " + tempWAV.getAbsolutePath());
		} catch (IOException e) {
			throw new VidevoxException("Unknown IO Exception during WAV creation");
		} catch (InterruptedException e) {
			throw new VidevoxException("Festival thread interrupted: Unknown source");
		}
		// Convert the wav to mp3
		cmd = "ffmpeg -i " + "\"" + tempWAV.getAbsolutePath() + "\"" + " -y -f mp3 " + "\""
				+ destination.getAbsolutePath() + "\"";
		logger.debug("cmd = " + cmd);
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			int returnVal = process.waitFor();
			if (returnVal != 0) {
				throw new VidevoxException("Unable to convert to mp3: Check that festival is installed correctly");
			}
			logger.debug("Created mp3 at: " + destination.getAbsolutePath());
		} catch (IOException e) {
			throw new VidevoxException("Unknown IO Exception during mp3 conversion");
		} catch (InterruptedException e) {
			throw new VidevoxException("Festival thread interrupted: Unknown source");
		}
		// Delete the temporary file
//		tempWAV.delete();
		logger.trace("Done with textToMP3");
		return true;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		// Insert values
		json.put(FILE, _audioFile.getAbsolutePath());
		json.put(START, _startOffset);
		json.put(ACTIVE, _active);
		json.put(SPEECH, _speech);
		json.put(NAME, _name);
		return json;
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
