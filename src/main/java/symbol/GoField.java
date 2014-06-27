package symbol;

public class GoField {

	String name;
	String type;
	String tag;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public static GoField genGoField(String name, String type, String tag) {
		GoField g = new GoField();
		g.setName(name);
		g.setType(type);
		g.setTag(tag);
		
		return g;
	}
}
