package nz.ac.auckland.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This is the primary model class, it contains all information required to save
 * a project to file and to reconstruct a project from a file.
 *
 * @author Fraser
 *
 */
public class Project {

	private static final Logger logger = Logger.getLogger(Project.class);

	/**
	 * Singleton instance of the project
	 */
	private static Project INSTANCE;
	/**
	 * The name of the project to be displayed. Should also be the basename of
	 * the file it is saved to.
	 */
	String _name;
	/**
	 * The location the project was loaded from if any.
	 */
	File _location;
	/**
	 * A file object containing the location of the video to be played.
	 */
	File _videoFile;
	/**
	 * A set of objects representing audio files to be included.
	 */
	Set<AudioFile> _audios;
	/**
	 * A set of objects representing tts files to be created/included.
	 */
	Set<Object> _tts;
	/**
	 * A switch to tell the application whether the video's audio should be
	 * included
	 */
	boolean _videoMuted = false;
	/**
	 * A switch to say if the project has been modified since the last save/load
	 */
	boolean _saved = false;

	// Keys to be used in JSON document
	public static final String NAME = "NAME";
	public static final String VIDEO = "VIDEO";
	public static final String AUDIOS = "AUDIOS";
	public static final String TTS = "TTS";
	public static final String MUTED = "MUTED";

	/**
	 * Singleton instantiation method.
	 *
	 * @return
	 */
	public static Project getProject() {
		if (INSTANCE == null) {
			INSTANCE = new Project();
		}
		return INSTANCE;
	}

	/**
	 * Default project
	 */
	private Project() {
		_name = "New Project";
		_audios = new HashSet<AudioFile>();
		_tts = new HashSet<Object>();
		_videoFile = new File("no video");
	}

	public void addAudio(File file, double offset) {
		AudioFile audio = new AudioFile(file, offset);
		if (!_audios.contains(audio)){
			_audios.add(audio);
		}
	}

	/**
	 * Constructs a project back up from a file so long at the file contains a
	 * valid JSON document. Places it as the singleton instance.
	 *
	 * @param file
	 * @throws VidevoxException
	 */
	@SuppressWarnings("unchecked")
	public static boolean buildProject(File file) throws VidevoxException {
		// Start off with a default project
		Project p = new Project();

		logger.debug("File at: " + file.getAbsolutePath());

		// The name of the project is defined by the file name it was saved as
		p._name = file.getName();
		logger.debug("Project name: " + p._name);

		// Set up json object to read from
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(file));
			// Messages in VidevoxException s will likely be shown to user
		} catch (FileNotFoundException e) {
			logger.error("Could not find file at: " + file.getAbsolutePath());
			throw new VidevoxException("Could not find file");
		} catch (IOException e) {
			logger.error(
					"IOException Parsing JSON at: " + file.getAbsolutePath() + ": error message - " + e.getMessage());
			throw new VidevoxException("Unknown IO Exception parsing JSON: " + e.getMessage());
		} catch (ParseException e) {
			logger.error("Invalid JSON format at: " + file.getAbsolutePath());
			throw new VidevoxException("Invalid JSON format");
		}
		JSONObject json = (JSONObject) obj;

		// Read off values for fields
		p._videoFile = new File((String) json.get(VIDEO));
		p._videoMuted = (boolean) json.get(MUTED);

		// Retrieve audio objects
		JSONArray audios = (JSONArray) json.get(AUDIOS);
		Iterator<JSONObject> iterator = audios.iterator();
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			AudioFile a = new AudioFile(next);
			p._audios.add(a);
		}

		// Retrieve tts objects
		JSONArray tts = (JSONArray) json.get(TTS);
		iterator = tts.iterator();
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			// Construct the tts object
		}

		// If all went well set the file location
		p._location = file;

		p._saved = true;

		// If no exceptions were thrown, return true to signal that the project
		// has finished building successfully
		return true;
	}

	/**
	 * Generates a json object and writes it to the designated file
	 *
	 * @param destination
	 * @throws VidevoxException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void toFile(File destination) throws VidevoxException, IOException {
		// The project name should always be the same as the file name.
		_name = destination.getName();
		logger.debug("Destination: " + destination.getAbsolutePath());
		JSONObject json = new JSONObject();

		// Insert values
		json.put(VIDEO, _videoFile.getAbsolutePath());
		json.put(MUTED, _videoMuted);

		// Iterate over audio files to fill json array
		JSONArray audios = new JSONArray();
		for (AudioFile a : _audios) {
			audios.add(a.toJSON());
		}
		json.put(AUDIOS, audios);

		// Iterate over tts objects
		JSONArray tts = new JSONArray();
		for (Object o : _tts) {
			tts.add(o);
		}
		json.put(TTS, tts);

		// Write to file
		FileWriter writer = new FileWriter(destination);
		try {
			writer.write(json.toJSONString());
			logger.debug("JSON: " + json.toJSONString());
		} catch (IOException e) {
			logger.error("IOException writing JSON to: " + destination.getAbsolutePath());
			throw new VidevoxException("Unknown IOException writing json to file");
		} finally {
			writer.flush();
			writer.close();
		}

		_saved = true;
	}

	/**
	 * Resets the INSTANCE back to the default project
	 */
	public static void reset() {
		INSTANCE = new Project();
	}

	public String getName() {
		return _name;
	}

	public boolean isSaved() {
		return _saved;
	}

}
