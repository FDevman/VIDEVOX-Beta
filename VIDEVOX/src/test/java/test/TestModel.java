package test;

import org.apache.log4j.Logger;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import nz.ac.auckland.model.VidevoxModel;

public class TestModel {

	private static final Logger logger = Logger.getLogger(TestModel.class);

	private static VidevoxModel _model;

	@Before
	public void beforeTest() {
		// Reset model to empty model
		_model = new VidevoxModel();
	}

	@Test
	public void testEquals() {

	}

	@Test
	public void testToStringBasic() {
		String str = _model.toString();
		logger.trace(str);
		VidevoxModel newModel = new VidevoxModel(str);
		if (!_model.equals(newModel)) {
			fail("Not Equal");
		}
	}

}
