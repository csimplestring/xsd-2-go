package parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.*;
import javax.xml.bind.JAXBElement;

import jaxb.JaxbContext;
import jaxb.JaxbFactory;
import symbol.GoField;
import symbol.JavaField;
import symbol.Node;
import symbol.SymbolTable;
import type.TypeConvertor;
import type.TypeGen;
import util.Util;
import loader.Loader;

public class Parse {

	String pkg;
	Loader loader;
	TypeConvertor typeConvertor;
	TypeGen typeGen;
	JaxbFactory jaxbFactory;
	StringBuilder buffer;
	SymbolTable symbolTable;

	int level = 0;
	boolean verbose = false;

	public Parse(Loader ld, String pkg) {
		this.loader = ld;
		this.pkg = pkg;
		this.buffer = new StringBuilder();
		this.symbolTable = new SymbolTable();
	}

	public void init() {
		this.typeConvertor = new TypeConvertor();
		this.typeConvertor.setPackageName(this.pkg);
		this.typeConvertor.setClassNameTable(this.loader.getClassNameTable());

		this.typeGen = new TypeGen();
		this.typeGen.setPkg(this.pkg);
		this.typeGen.setTypeConv(this.typeConvertor);

		try {
			Class factory = this.loader.load(this.pkg+".ObjectFactory");
			this.jaxbFactory = new JaxbFactory(factory);

			this.findEnumClass();

			this.typeGen.setEnumTypes(this.loader.getEnumClassTable());

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void findEnumClass() throws ClassNotFoundException {

		for (String cls : this.loader.getClassFullNameTable()) {
			Class c = this.loader.load(this.pkg + "." + cls);

			// this class is XmlEnum
			XmlEnum xmlEnum = (XmlEnum) c.getAnnotation(XmlEnum.class);
			if (xmlEnum != null) {
				this.loader.getEnumClassTable().add(Util.genGoStructName(cls, this.pkg));
			}
		}
	}

	public void print(String str) {
		if (this.verbose) {
			for (int i = 0; i < level; i++) {
				System.out.print("\t");
			}
			System.out.print(str);
		}
	}

	public void println(String str) {
		if (this.verbose) {
			for (int i = 0; i < level; i++) {
				System.out.print("\t");
			}
			System.out.println(str);
		}
	}

	/**
	 * Pre filter class: 1 Xml Enum Class
	 * 
	 * @param cls
	 * @return
	 */
	public boolean preprocess(Class cls) {
		String structName = Util.genGoStructName(cls.getName(), this.pkg);
		if (this.loader.getEnumClassTable().contains(structName)) {
			return false;
		}

		if (structName.equals("ObjectFactory")) {
			return false;
		}

		return true;
	}

	public String parse(Class cls) throws ClassNotFoundException {

		this.buffer.delete(0, this.buffer.length());

		if (!this.preprocess(cls)) {
			return finalizeParse();
		}

		// Struct Name
		parseStructName(cls);

		// Fields
		Field[] fields = cls.getDeclaredFields();
		Set<Field> xmlAttrFields = new HashSet<Field>();
		Set<Field> xmlElementFields = new HashSet<Field>();
		Set<Field> xmlElementRefsFields = new HashSet<Field>();
		Set<Field> xmlElementsFields = new HashSet<Field>();
		Set<Field> xmlValueFields = new HashSet<Field>();

		for (Field f : fields) {
			Annotation[] annotations = f.getAnnotations();

			// no annotation
			if (annotations.length == 0) {
				xmlElementFields.add(f);
			}

			// has annotation
			else {
				for (Annotation ann : annotations) {
					if (ann instanceof XmlAttribute) {
						xmlAttrFields.add(f);
					} else if (ann instanceof XmlElement) {
						xmlElementFields.add(f);
					} else if (ann instanceof XmlElementRefs) {
						xmlElementRefsFields.add(f);
					} else if (ann instanceof XmlElements) {
						xmlElementsFields.add(f);
					} else if (ann instanceof XmlValue) {
						xmlValueFields.add(f);
					} else if (ann instanceof XmlTransient) {

					}
				}
			}
		}

		JaxbContext ctx = new JaxbContext();
		ctx.setScope(cls);

		// attribute
		for (Field f : xmlAttrFields) {
			parseXmlAttribute(f);
		}

		// xml element
		for (Field f : xmlElementFields) {
			parseXmlElement(f);
		}

		// xml elements
		for (Field f : xmlElementsFields) {
			parseXmlElements(f);
		}

		// xml element refs
		for (Field f : xmlElementRefsFields) {
			parseXmlElementRefs(f, ctx);
		}

		// xml value
		for (Field f : xmlValueFields) {
			parseXmlValue(f);
		}

		return finalizeParse();
	}

	private String finalizeParse() {
		if (this.buffer.length() != 0) {
			this.buffer.append(Util.formatGoStructFooter());
		}
		this.symbolTable.reset();
		return this.buffer.toString();
	}

	private void regNodeInfo(JavaField j, GoField g) {
		// node info
		Node node = Node.genNode(j, g);
		this.symbolTable.addNode(node);
	}

	/**
	 * Parse struct name & xml name
	 * 
	 * @param cls
	 */
	public String parseStructName(Class cls) {
		// Struct name
		String structName = Util.genGoStructName(cls.getName(), this.pkg);
		this.println("struct name: " + "XML" + structName);

		this.buffer.append(Util.formatGoStructHeader("XML" + structName));
		this.symbolTable.setJavaClassName(cls.getName());
		this.symbolTable.setGOStructName(structName);

		// XMLName
		Annotation annotation = cls.getAnnotation(XmlRootElement.class);
		if (annotation != null) {
			XmlRootElement xmlRootElement = (XmlRootElement) annotation;
			String xmlName = xmlRootElement.name();
			this.println("\t" + "XMLName xml.Name " + Util.genXmlTag(xmlName, true));
			this.buffer.append(Util.formatGoXMLNameField(Util.genXmlTag(xmlName, true)));
		}

		return structName;
	}

	/**
	 * parse XmlAttribute field
	 * 
	 * @param attributeFileds
	 */
	public void parseXmlAttribute(Field f) {

		XmlAttribute attr = f.getAnnotation(XmlAttribute.class);

		// attribute name
		String attrName = attr.name() == null ? f.getName() : attr.name();
		String goFieldName = Util.genGoField(attrName, true);

		// attribute type
		String type = f.getType().getName();
		String goType = this.typeConvertor.getGoType(type);
		if (goType == null) {
			System.err.println("Can not map to Golang type: " + type);
			System.exit(1);
		}

		// attribute tag
		String goXmlTag = Util.genAttrTag(attrName, attr.required());

		// node info
		JavaField jf = JavaField.genJavaField(f, null);
		GoField gf = GoField.genGoField(goFieldName, goType, goXmlTag);
		this.regNodeInfo(jf, gf);

		this.print("\t" + goFieldName + "\t" + goType + "\t" + goXmlTag + "\n");
		this.buffer.append(Util.formatGoField(goFieldName, goType, goXmlTag));

	}

	/**
	 * parse XmlElement field
	 * 
	 * @param f
	 */
	public void parseXmlElement(Field f) {
		// element name
		String elemName = f.getName();
		String goFieldName = Util.genGoField(elemName, true);

		// element type
		String goType = this.typeGen.typeOf(f);

		// element tag
		String goTag = null;
		XmlElement elementAnn = f.getAnnotation(XmlElement.class);

		if (elementAnn == null) {
			goTag = Util.genXmlTag(f.getName(), false);
		} else {
			String tag = elementAnn.name() == null | elementAnn.name().equals("##default") ? f.getName() : elementAnn.name();
			goTag = Util.genXmlTag(tag, elementAnn.required());
		}

		this.print("\t" + goFieldName + "\t" + goType + "\t" + goTag + "\n");

		// node info
		JavaField javaField = JavaField.genJavaField(f, null);
		GoField goField = GoField.genGoField(goFieldName, goType, goTag);
		this.regNodeInfo(javaField, goField);

		this.buffer.append(Util.formatGoField(goFieldName, goType, goTag));
	}

	/**
	 * parse XmlElements field
	 * 
	 * @param field
	 */
	public void parseXmlElements(Field field) {
		XmlElements xmlElements = field.getAnnotation(XmlElements.class);

		if (xmlElements == null) {
			return;
		}

		for (XmlElement element : xmlElements.value()) {
			// element name
			String goName = Util.genGoField(element.name(), true);

			// element type
			String goType = this.typeGen.typeOf(element, this.jaxbFactory);

			// element tag
			String goTag = Util.genXmlTag(element.name(), false);

			this.print("\t" + goName + "\t" + goType + "\t" + goTag + "\n");

			// node info
			JavaField javaField = JavaField.genJavaField(null, element.type());
			GoField goField = GoField.genGoField(goName, goType, goTag);
			this.regNodeInfo(javaField, goField);

			this.buffer.append(Util.formatGoField(goName, goType, goTag));
		}
	}

	/**
	 * parse XmlElementRefs field
	 * 
	 * @param xmlElementRefs
	 * @param ctx
	 */
	public void parseXmlElementRefs(Field field, JaxbContext ctx) {
		XmlElementRefs xmlElementRefs = field.getAnnotation(XmlElementRefs.class);

		if (xmlElementRefs == null) {
			return;
		}

		for (XmlElementRef ref : xmlElementRefs.value()) {

			// ref name
			String goName = Util.genGoField(ref.name(), true);

			// ref tag
			String goTag = Util.genXmlTag(ref.name(), false);

			// ref type
			String goType = this.typeGen.typeOf(ref, ctx, this.jaxbFactory);

			// node info
			JavaField j = JavaField.genJavaField(null, ref.type());
			GoField g = GoField.genGoField(goName, goType, goTag);
			this.regNodeInfo(j, g);

			this.print("\t" + goName + "\t" + goType + "\t" + goTag + "\n");
			this.buffer.append(Util.formatGoField(goName, goType, goTag));
		}
	}

	/**
	 * parse XmlValue field
	 * 
	 * @param field
	 */
	public void parseXmlValue(Field field) {
		// for XmlValue field, the name is 'Value'
		String goName = "Value";

		// type
		String goType = this.typeGen.typeOf(field);

		// for XmlValue field, the tag is 'chardata'
		String goTag = Util.genCharDataTag();

		// node info
		JavaField j = JavaField.genJavaField(field, null);
		GoField g = GoField.genGoField(goName, goType, goTag);
		this.regNodeInfo(j, g);

		this.print("\t" + goName + "\t" + goType + "\t" + goTag + "\n");
		this.buffer.append(Util.formatGoField(goName, goType, goTag));
	}
}
