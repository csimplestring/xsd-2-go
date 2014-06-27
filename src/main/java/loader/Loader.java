package loader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;

public class Loader {

	String folder;
	ClassLoader classLoader;
	Set<String> classNameTable = new HashSet<String>();
	Set<String> classFullNameTable = new HashSet<String>();
	Set<String> enumClassTable = new HashSet<String>();

	public Loader(String folder) {
		File file = new File(folder);

		File packageDir = new File(file.getAbsolutePath());
		this.buildClassNameTable(packageDir);

		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			classLoader = new URLClassLoader(urls);
			URLClassLoader f = new URLClassLoader(urls);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * find all the java classes in dir folder
	 * 
	 * @param dir
	 */
	private void buildClassNameTable(File dir) {
		FilenameFilter classFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".class")) {
					return true;
				} else {
					return false;
				}
			}
		};

		String[] clsNames = dir.list(classFilter);
		for (String c : clsNames) {
			if (c.endsWith(".class")) {
				c = c.substring(0, c.length() - 6);
			}

			this.classFullNameTable.add(c);

			if (c.contains("$")) {
				c = c.replaceAll("\\$", "");
			}

			this.classNameTable.add(c);
		}
	}

	public Set<String> getClassNameTable() {
		return this.classNameTable;
	}

	public Set<String> getClassFullNameTable() {
		return classFullNameTable;
	}

	public Class load(String className) throws ClassNotFoundException {
		return this.classLoader.loadClass(className);
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public Set<String> getEnumClassTable() {
		return enumClassTable;
	}

	public void setEnumClassTable(Set<String> enumClassTable) {
		this.enumClassTable = enumClassTable;
	}

}
