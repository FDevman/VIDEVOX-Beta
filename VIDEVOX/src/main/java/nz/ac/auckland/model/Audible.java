package nz.ac.auckland.model;

import java.io.File;

import org.json.simple.JSONObject;

public interface Audible {

	JSONObject toJSON();

	boolean isActive();

	void setActive(boolean active);

	void setStartOffset(double startOffset);

	double getStartOffset();

	String getName();

	File getFile();
}