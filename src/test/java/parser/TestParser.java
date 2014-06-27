package parser;

import loader.Loader;

import org.junit.Test;

public class TestParser {

	@Test
	public void testParser() throws ClassNotFoundException {
		String pkg = "generated";
		String folder = "/Users/yiwang/Desktop/marble-2404/generated";
		Loader loader = new Loader(folder);
		
		Parse parse = new Parse(loader, pkg);
		parse.init();
		
		Class cls = loader.load("generated.Config");
		String src = parse.parse(cls);
		System.out.print(src);
	}
}
