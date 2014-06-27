package symbol;

import java.lang.reflect.Field;

public class JavaField {
	
	Field field;
	
	Class cls;

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
	
	public Class getFieldClass() {
		return this.cls;
	}
	
	public void setFieldClass(Class c) {
		this.cls = c;
	}
	
	public static JavaField genJavaField(Field f, Class c) {
		JavaField j = new JavaField();
		j.setField(f);
		j.setFieldClass(c);
		
		return j;
	}
}
