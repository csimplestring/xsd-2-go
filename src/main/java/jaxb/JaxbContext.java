package jaxb;

import javax.xml.bind.JAXBElement;

public class JaxbContext {
	
	JAXBElement jaxbElement;
	
	Class scope;
	
	public JaxbContext(){
		
	}

	public JAXBElement getJaxbElement() {
		return jaxbElement;
	}

	public void setJaxbElement(JAXBElement jaxbElement) {
		this.jaxbElement = jaxbElement;
	}

	public Class getScope() {
		return scope;
	}

	public void setScope(Class scope) {
		this.scope = scope;
	}
	
	public boolean isRecursive(String s) {
		return this.scope.getSimpleName().equals(s);
	}
}
