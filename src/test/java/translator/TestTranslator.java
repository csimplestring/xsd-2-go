package translator;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.junit.Test;

public class TestTranslator {

	@Test
	public void testTranslator() throws ClassNotFoundException {

		String pkg = "generated";
		String folder = "/Users/yiwang/Desktop/marble-2404/generated";
		Translator t = new Translator(folder, pkg);

		String src = t.go("generated.Config");
		System.out.println(src);

	}

}
