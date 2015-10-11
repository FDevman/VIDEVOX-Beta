package application.test;

import org.apache.log4j.Logger;

import org.junit.Test;

import nz.ac.auckland.application.VidevoxApplication;

public class DummyRunner {

	private static final Logger logger = Logger.getLogger(DummyRunner.class);

	@Test
	public void runApplication() {
		logger.trace("Starting!");
		VidevoxApplication.main(null);
		logger.trace("Closed!");
	}

}
