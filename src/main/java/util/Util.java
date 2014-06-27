package util;

public class Util {

	/**
	 * Generate xml tag
	 * 
	 * @param attr
	 * @param isRequired
	 * @return
	 */
	public static String genXmlTag(String attr, boolean isRequired) {
		return isRequired ?
				"`xml:\"" + attr + "\"`" :
				"`xml:\"" + attr + ",omitempty\"`";
	}

	/**
	 * Generate xml attribute tag
	 * 
	 * @param attr
	 * @param isRequired
	 * @return
	 */
	public static String genAttrTag(String attr, boolean isRequired) {
		return isRequired ?
				"`xml:\"" + attr + ",attr\"`" :
				"`xml:\"" + attr + ",attr,omitempty\"`";
	}
	
	public static String genCharDataTag() {
		return "`xml:\",chardata\"`";
	}

	/**
	 * Generate Go field
	 * 
	 * @param f
	 * @param isPublic
	 * @return
	 */
	public static String genGoField(String f, boolean isPublic) {

		String r = f;
		// remove underscore "_"
		if (f.contains("_")) {
			r = "";
			String[] token = f.split("_");
			for (String t : token) {
				if (!t.isEmpty()) {
					t = t.substring(0, 1).toUpperCase() + t.substring(1);
					r += t;
				}
			}
		}

		return isPublic ?
				r.substring(0, 1).toUpperCase() + r.substring(1) :
				r;
	}

	/**
	 * Generate Go Struct Name
	 * 
	 * @param name
	 * @param pkg
	 * @return
	 */
	public static String genGoStructName(String name, String pkg) {
		String r = name;
		String prefix = pkg + ".";
		if (r.startsWith(pkg)) {
			r = r.replace(prefix, "");
		}
		if (r.contains("$")) {
			r = r.replaceAll("\\$", "");
		}
		return r;
	}
	
	
	
	public static String genGoBigIntGetter(String struct, String field) {
		return null;
	}

	/**
	 * format struct definition header in Go source file. For example: type XXX
	 * struct {
	 * 
	 * @param name
	 * @return
	 */
	public static String formatGoStructHeader(String name) {
		return "type " + name + " struct {\n";
	}

	/**
	 * format XMLName field in a Go struct. For example: XMLName xml.Name
	 * `xml:"case,omitempty"`
	 * 
	 * @param tag
	 * @return
	 */
	public static String formatGoXMLNameField(String tag) {
		return "\t" + "XMLName xml.Name " + tag + "\n";
	}

	public static String formatGoField(String name, String type, String tag) {
		return "\t" + name + "\t" + type + "\t" + tag + "\n";
	}

	public static String formatGoStructFooter() {
		return "}\n";
	}
	
	
}
