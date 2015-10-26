package nz.ac.auckland.application;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javafx.concurrent.Task;
import nz.ac.auckland.model.Audible;
import nz.ac.auckland.model.ModelHelper;
import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

public class VideoCompiler extends Task<String> {

	private final File _file;
	private final Project project;
	String cmd;
	ProcessBuilder builder;
	Process process;

	public VideoCompiler(File file) {
		_file = ModelHelper.enforceFileExtension(file, ".mp4");
		project = Project.getProject();
	}

	@Override
	protected String call() throws Exception {
		int count = 0;
		cmd = "ffmpeg -y ";
		if (!project.isVideoMuted()) {
			count++;
			cmd = "ffmpeg -y -i '" + project.getVideo().getAbsolutePath() + "' -vn "
					+ VideoCompiler.class.getClassLoader().getResource("").toString() + "/exctacted.mp3";
			// run command
			updateMessage("Extracting audio from video file");
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				process = builder.start();
				int returnVal = process.waitFor();
				if (returnVal != 0) {
					throw new VidevoxException(
							"Wav file unable to be created: Check that festival is installed correctly");
				}
			} catch (IOException e) {
				throw new VidevoxException("Unknown IO Exception during WAV creation");
			} catch (InterruptedException e) {
				throw new VidevoxException("Festival thread interrupted: Unknown source");
			}
			cmd = "ffmpeg -y -i " + VideoCompiler.class.getClassLoader().getResource("").toString() + "/exctacted.mp3 ";
		}
		HashSet<Audible> audios = project.getAudios();
		for (Audible a : audios) {
			count++;
			cmd += "-i '" + a.getFile().getAbsolutePath() + "' ";
		}
		cmd += "-filter_complex amix=inputs=" + count + " "
				+ VideoCompiler.class.getClassLoader().getResource("").toString() + "/combined.mp3";
		// Run command
		updateMessage("Merging audio files");
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			process = builder.start();
			int returnVal = process.waitFor();
			if (returnVal != 0) {
				throw new VidevoxException("Wav file unable to be created: Check that festival is installed correctly");
			}
		} catch (IOException e) {
			throw new VidevoxException("Unknown IO Exception during WAV creation");
		} catch (InterruptedException e) {
			throw new VidevoxException("Festival thread interrupted: Unknown source");
		}
		cmd = "ffmpeg -y -i '" + project.getVideo().getAbsolutePath() + "' -i "
				+ VideoCompiler.class.getClassLoader().getResource("").toString() + "/combined.mp3 "
				+ "-c copy -map 0:0 -map 1:0 '" + _file.getAbsolutePath() + "'";
		// run command
		updateMessage("Compiling final video file");
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			process = builder.start();
			int returnVal = process.waitFor();
			if (returnVal != 0) {
				throw new VidevoxException("Wav file unable to be created: Check that festival is installed correctly");
			}
		} catch (IOException e) {
			throw new VidevoxException("Unknown IO Exception during WAV creation");
		} catch (InterruptedException e) {
			throw new VidevoxException("Festival thread interrupted: Unknown source");
		}
		updateMessage("Done");
		return "Success!";
	}

}
