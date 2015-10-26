package nz.ac.auckland.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * Class containing static helper methods that are used in multiple locations
 * and don't belong in any other specific class
 *
 * @author Fraser
 *
 */
public class ModelHelper {

	private static final Logger logger = Logger.getLogger(ModelHelper.class);

	/**
	 * Checks to see if a file name ends with the specified extension, if it
	 * doesn't appends the extension to the existing file name
	 *
	 * @param file
	 *            - The file you want to potentially rename
	 * @param extension
	 *            - The extension you want to enforce e.g. ".mp4"
	 */
	public static File enforceFileExtension(File file, String extension) {
		String path = file.getAbsolutePath();
		path = FilenameUtils.removeExtension(path);
		path = path + extension;
		file.renameTo(new File(path));
		logger.debug("Changed file to: " + path);
		return new File(path);
	}

	/**
	 * Converts any file with URL/URI characters in its path to a regular path
	 * name.
	 *
	 * @param file
	 */
	public static void decodeURL(File file) {
		try {
			String path = new URL(file.getAbsolutePath()).getPath();
			path = URLDecoder.decode(path, "UTF-8");
			file.renameTo(new File(path));
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
