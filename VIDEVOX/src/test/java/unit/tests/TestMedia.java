package unit.tests;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import javafx.application.Application;
import javafx.stage.Stage;
import nz.ac.auckland.model.VidevoxException;
import nz.ac.auckland.model.VidevoxMedia;
import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.model.Playable;

public class TestMedia {

	private static final Logger logger = Logger.getLogger(TestMedia.class);

	private static VidevoxMedia media;

	static String _defaultAudio1 = "\\Users\\Fraser\\Documents\\UoA Papers\\SE-206\\Eclipse Projects\\VIDEVOX-Beta\\VIDEVOX\\src\\test\\" + "resources" + Playable.FILE_SEPARATOR + "media" + Playable.FILE_SEPARATOR + "The Mercury Tale.mp3";
	static String _defaultAudio2 =Playable.FILE_SEPARATOR + "resources" + Playable.FILE_SEPARATOR + "media" + Playable.FILE_SEPARATOR + "The Mercury Tale.mp3";
	static String _defaultAudio3 = Playable.FILE_SEPARATOR + "media" + Playable.FILE_SEPARATOR + "The Mercury Tale.mp3";
	static String _defaultAudio4 = "media" + Playable.FILE_SEPARATOR + "The Mercury Tale.mp3";
	static String _defaultAudio5 =Playable.FILE_SEPARATOR + "resources" + Playable.FILE_SEPARATOR + "media" + Playable.FILE_SEPARATOR + "The Mercury Tale.mp3";
	static String _defaultAudio6 = "resources" + Playable.FILE_SEPARATOR + "media" + Playable.FILE_SEPARATOR + "The Mercury Tale.mp3";

//	@Test
//	public void getMediaTest() {
//
//		File file = new File(_defaultAudio1);
//		logger.debug(file.getAbsolutePath());
//		try {
//			media = new VidevoxMedia(new File(_defaultAudio1), 0.0);
//			logger.debug("Success: " + _defaultAudio1);
//		} catch (VidevoxException e) {
//			try {
//				media = new VidevoxMedia(new File(_defaultAudio2), 0.0);
//				logger.debug("Success: " + _defaultAudio2);
//			} catch (VidevoxException e1) {
//				try {
//					media = new VidevoxMedia(new File(_defaultAudio3), 0.0);
//					logger.debug("Success: " + _defaultAudio3);
//				} catch (VidevoxException e2) {
//					try {
//						media = new VidevoxMedia(new File(_defaultAudio4), 0.0);
//						logger.debug("Success: " + _defaultAudio4);
//					} catch (VidevoxException e3) {
//						try {
//							media = new VidevoxMedia(new File(_defaultAudio5), 0.0);
//							logger.debug("Success: " + _defaultAudio5);
//						} catch (VidevoxException e4) {
//							fail("All failed");
//						}
//					}
//				}
//			}
//		}
//	}

	@Before
	public void before() {
		try {
			media = new VidevoxMedia();
		} catch (VidevoxException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testString() {
		String str = media.toString();
		try {
			VidevoxMedia newMedia = new VidevoxMedia(str);
			if (!newMedia.equals(media)) {
				fail(str);
			}
		} catch (ParseException | URISyntaxException e) {
			fail(e.getMessage());
		}
	}

}
