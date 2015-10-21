package unit.tests;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import nz.ac.auckland.model.Project;
import nz.ac.auckland.model.VidevoxException;

public class TestProject {

	static Project project;
	static File file;

	@BeforeClass
	public static void setUp() {
		file = new File("test.txt");
		FileWriter w;
		try {
			w = new FileWriter(file);
			w.write("");
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void cleanUp() {
		file.delete();
	}

	@Before
	public void beforeTest() {
		project = new Project();
		FileWriter w;
		try {
			w = new FileWriter(file);
			w.write("");
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testConstructor() {
		if (!project.getName().equals("New Project")) {
			fail("Wrong name: " + project.getName());
		}
	}

	@Test
	public void testJSON() {
		try {
			project.toFile(file);
			Project newProject = new Project(file);
			// check they are the same
			if (!project.getName().equals(newProject.getName())) {
				fail("Names not the same");
			}
		} catch (VidevoxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
