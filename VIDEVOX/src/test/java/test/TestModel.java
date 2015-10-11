package test;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import nz.ac.auckland.model.VidevoxModel;

public class TestModel {

	private static VidevoxModel _model;

	@Before
	public static void beforeTest() {
		// Reset model to empty model
		_model = new VidevoxModel();
	}

	@Test
	public void testToStringBasic() {
		String str = _model.toString();
		VidevoxModel newModel = new VidevoxModel(str);
		if (!_model.equals(newModel)) {

			fail("");
		}
	}

}
