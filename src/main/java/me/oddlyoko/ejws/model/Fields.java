package me.oddlyoko.ejws.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

import lombok.Getter;
import lombok.Setter;

/**
 * Represent caracteristics of a Field for a Models.
 */
@Getter
public class Fields {
	private java.lang.reflect.Field field;
	private Type type;
	private java.lang.String id;
	private java.lang.String name;
	private boolean stored;
	private ComputeMethod compute;
	@Setter
	private boolean computeWhenNeeded;

	protected Fields(Type type, java.lang.String id, java.lang.String name, boolean stored, ComputeMethod compute,
			java.lang.reflect.Field field) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.stored = stored;
		this.compute = compute;
		this.field = field;
		this.computeWhenNeeded = false;
	}

	/**
	 * Call the computed method of this field for a specific object
	 * 
	 * @param obj
	 *            The object to call
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void callComputedMethod(Object obj)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		compute.getComputeMethod().invoke(obj);
	}

	public boolean isComputable() {
		return compute != null;
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Field {
		String id() default "";

		String value() default "";

		String name() default "";

		boolean stored() default true;

		java.lang.String compute() default "";
	}

	protected static enum Type {
		STRING,
		INTEGER,
		BOOLEAN;
	}
}
