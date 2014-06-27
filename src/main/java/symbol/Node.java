package symbol;

public class Node {
	JavaField javaField;
	GoField goField;

	public JavaField getJavaField() {
		return javaField;
	}

	public void setJavaField(JavaField javaField) {
		this.javaField = javaField;
	}

	public GoField getGoField() {
		return goField;
	}

	public void setGoField(GoField goField) {
		this.goField = goField;
	}
	
	public static Node genNode(JavaField j, GoField g) {
		Node n = new Node();
		n.setGoField(g);
		n.setJavaField(j);
		
		return n;
	}
}
