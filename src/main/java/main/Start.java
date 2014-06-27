package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import translator.Translator;

public class Start {

	public void run(String folder, String pkg) throws ClassNotFoundException {
		Translator t = new Translator(folder, pkg);
		File dir = new File(folder);

		for (String fname : dir.list()) {
			if (fname.endsWith(".class")) {
				fname = fname.replaceAll("\\.class", "");
				String src = t.go(pkg + "." + fname);
				System.out.println(src);
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {

		String classDir = args[0];
		String pkg = args[1];
		
		Start s = new Start();
		s.run(classDir, pkg);
		
	}

}
