package symbol;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolTable {

	String jClassName;

	String gStructName;
	
	List<Node> nodes;
	
	public SymbolTable() {
		this.nodes = new ArrayList<Node>();
	}
	
	public void reset() {
		this.nodes.clear();
		this.jClassName = null;
		this.gStructName = null;
	}
	
	public void setJavaClassName(String name) {
		this.jClassName = name;
	}
	
	public void setGOStructName(String name) {
		this.gStructName = name;
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}
	
	public List<Node> getNodes() {
		return Collections.unmodifiableList(this.nodes);
	}
}
