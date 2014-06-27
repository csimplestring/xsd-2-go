package loader;

import org.junit.Assert;
import org.junit.Test;

public class TestLoader {

	@Test
	public void testLoader() {
		String folder = "/Users/yiwang/Desktop/marble-2404/generated/";
		Loader loader = new Loader(folder);

		Assert.assertTrue(loader.getClassFullNameTable().contains("FlashVariables$Value"));
		Assert.assertTrue(loader.getClassNameTable().contains("FlashVariablesValue"));

	}

}
