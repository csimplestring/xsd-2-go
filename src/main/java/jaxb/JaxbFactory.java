package jaxb;


import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlElementDecl;


public class JaxbFactory {

	Class factoryClass;

	Map<String, Class> QNameMap;
	
	public JaxbFactory(Class objectFactory) throws IllegalArgumentException, IllegalAccessException {
		this.factoryClass = objectFactory;
		this.init();
	}
	
	public Class getJaxbElementType(String name) {
		return this.QNameMap.get(name);
	}

	private void init() throws IllegalArgumentException, IllegalAccessException {
		this.QNameMap = new HashMap<String, Class>();
		
		Field[] fields = this.factoryClass.getDeclaredFields();
		for (Field field : fields) {
			String type = field.getType().getName();
			// this field is QName
			if (type.equals("javax.xml.namespace.QName")) {
				field.setAccessible(true);
				QName qName = new QName("");
				qName =(QName) field.get(qName);
				
				String localPart = qName.getLocalPart();
				QNameMap.put(localPart, null);
			}
		}
		
		Method[] methods = this.factoryClass.getDeclaredMethods();
		for(Method method : methods) {
			XmlElementDecl decl = method.getAnnotation(XmlElementDecl.class);
			
			// this method is to create jaxb element
			if (decl != null && QNameMap.containsKey(decl.name())) {
				
				if (!method.getReturnType().getName().equals("javax.xml.bind.JAXBElement")) {
					System.err.println("this method does not return JaxbElement");
				}
				
				ParameterizedType param = (ParameterizedType) method.getGenericReturnType();
				Type paramType = param.getActualTypeArguments()[0];
				Class typeClass = (Class) paramType;
				
				this.QNameMap.put(decl.name(), typeClass);
			}
		}
	}
}
