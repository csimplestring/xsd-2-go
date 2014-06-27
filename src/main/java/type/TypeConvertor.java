package type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeConvertor {
	/**
	 * Type mapping xsd -> Java -> Go
	 * see: http://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding#Default_data_type_bindings
	 */
	static final Map<String, String> TYPE_MAP;
	static {
		TYPE_MAP = new HashMap<String, String>();
		TYPE_MAP.put("java.lang.Boolean", "bool");
		TYPE_MAP.put("java.lang.Byte", "int8");// -128 ~ 127
		TYPE_MAP.put("java.lang.Character", "uint16");// 0 ~ 65535
		TYPE_MAP.put("java.lang.Double", "float64");// 64bit IEEE 
		TYPE_MAP.put("java.lang.Float", "float32");// 32bit IEEE 
		TYPE_MAP.put("java.lang.Integer", "int32"); // -2^31 ~ 2^31-1
		TYPE_MAP.put("java.lang.Long", "int64"); // -2^63 ~ 2^63-1
		TYPE_MAP.put("java.lang.Short", "int16"); // -2^15 ~ 2^15-1
		TYPE_MAP.put("java.lang.String", "string"); // builtin type
		
		TYPE_MAP.put("java.math.BigInteger", "string"); // stored as string, big.Int
		TYPE_MAP.put("java.math.BigDecimal", "string"); // stored as string, big.Rat

		TYPE_MAP.put("byte", "int8");
		TYPE_MAP.put("short", "int16");
		TYPE_MAP.put("int", "int32");
		TYPE_MAP.put("long", "int64");
		TYPE_MAP.put("float", "float32");
		TYPE_MAP.put("double", "float64");
		TYPE_MAP.put("boolean", "bool");
		TYPE_MAP.put("char", "uint16");
	}

	String packageName;
	Set<String> classNameTable;
	
	public boolean isPrimitive(String t) {
		return TYPE_MAP.containsKey(t);
	}
	
	public String getGoType(String t) {
		
		// builtin type
		if (TYPE_MAP.containsKey(t)) {
			return TYPE_MAP.get(t);
		}
		
		String goType = removePkg(t);
		
		// nested struct type: like "XX$YY$ZZ"
		if (goType.contains("$")) {
			goType = goType.replaceAll("\\$", "");
		}

		// created struct type
		if (classNameTable.contains(goType)) {
			return goType;
		}
		
		return null;
	}

	String removePkg(String t) {
		String prefix = this.packageName + ".";
		return t.startsWith(prefix) ?
				t.replaceFirst(prefix, "") :
				t;
	}

	public void setClassNameTable(Set<String> t) {
		this.classNameTable = t;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String pkg) {
		this.packageName = pkg;
	}
}
