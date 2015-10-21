package nz.ac.auckland.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Project {

	private static final Logger logger = Logger.getLogger(Project.class);

	/**
	 * The name of the project to be displayed. Should also be the basename of
	 * the file it is saved to.
	 */
	String _name;
	/**
	 * A file object containing the location of the video to be played.
	 */
	File _videoFile;
	/**
	 * A set of objects representing audio files to be included.
	 */
	Set<Object> _audios;
	/**
	 * A set of objects representing tts files to be created/included.
	 */
	Set<Object> _tts;
	/**
	 * A switch to tell the application whether the video's audio should be
	 * included
	 */
	boolean _videoMuted = false;

	public static final String NAME = "NAME";
	public static final String VIDEO = "VIDEO";
	public static final String AUDIOS = "AUDIOS";
	public static final String TTS = "TTS";
	public static final String MUTED = "MUTED";


	/**
	 * Keys to be used when marshaling/un-marshaling a json object
	 *
	 * @author Fraser
	 *
	 */
	public enum JSONkeys {
		NAME, VIDEO, AUDIOS, TTS, MUTED
	}

	/**
	 * Default project
	 */
	public Project() {
		_name = "New Project";
		_audios = new HashSet<Object>();
		_tts = new HashSet<Object>();
		_videoFile = new File("none.txt");
	}

	/**
	 * Constructs a project back up from a file so long at the file contains a
	 * valid JSON document.
	 *
	 * @param file
	 * @throws VidevoxException
	 */
	public Project(File file) throws VidevoxException {
		logger.debug("File at: " + file.getAbsolutePath());
		// The name of the project is defined by the file name it was saved as
		_name = file.getName();
		logger.debug("Project name: " + _name);
		// Set up json object to read from
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(file));
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
		String videoPath = (String) json.get(VIDEO);
		logger.debug("Video at: " + videoPath);
		_videoFile = new File(videoPath);

//		_videoFile = new File((String) json.get(JSONkeys.VIDEO));

		_videoMuted = (boolean) json.get(MUTED);
		// Retrieve audio objects
		JSONArray audios = (JSONArray) json.get(AUDIOS);
		Iterator<JSONObject> iterator = audios.iterator();
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			// Construct the audio object
		}
		// Retrieve tts objects
		JSONArray tts = (JSONArray) json.get(TTS);
		iterator = tts.iterator();
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			// Construct the tts object
		}
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
		json.put(NAME, _name);
		json.put(VIDEO, _videoFile.getAbsolutePath());
		json.put(MUTED, _videoMuted);
		// Iterate over sets to fill json arrays
		JSONArray audios = new JSONArray();
		for (Object o : _audios) {
			audios.add(o);
		}
		json.put(AUDIOS, audios);
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
	}

	public Object getName() {
		return _name;
	}

}
