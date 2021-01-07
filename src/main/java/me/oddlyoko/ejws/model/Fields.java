package me.oddlyoko.ejws.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.sql.Types;

/**
 * Represent caracteristics of a Field for a Models.
 */
public class Fields {
	private final java.lang.reflect.Field field;
	private final Type type;
	private final String id;
	private final String name;
	private final boolean stored;
	private final ComputeMethod compute;
	private final boolean blank;
	private final boolean empty;
	private final boolean autoincrement;
	private final boolean unique;

	public Fields(Type type, String id, String name, boolean stored, ComputeMethod compute, boolean blank,
			boolean empty, boolean autoincrement, boolean unique, java.lang.reflect.Field field) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.stored = stored;
		this.compute = compute;
		this.blank = blank;
		this.empty = empty;
		this.field = field;
		this.autoincrement = autoincrement;
		this.unique = unique;
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

	public java.lang.reflect.Field getField() {
		return field;
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isStored() {
		return stored;
	}

	public ComputeMethod getCompute() {
		return compute;
	}

	public boolean isBlank() {
		return blank;
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean isAutoincrement() {
		return autoincrement;
	}

	public boolean isUnique() {
		return unique;
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Field {
		String id() default "";

		String value() default "";

		String name() default "";

		boolean stored() default true;

		String compute() default "";

		/**
		 * Could the field be null in database ?
		 */
		boolean blank() default false;

		/**
		 * Could the field be empty in database ?
		 */
		boolean empty() default false;

		/**
		 * Should this field autoincrement if type is INTEGER ?
		 */
		boolean autoincrement() default false;

		/**
		 * Is this field unique ? (Only check for unique constraint in Database)
		 */
		boolean unique() default false;
	}

	public enum PrimaryType {
		AUTOINCREMENT(),
		MANUAL();
	}

	public enum Type {
		STRING(String.class),
		INTEGER(Integer.class, int.class),
		BOOLEAN(Boolean.class, boolean.class);

		private final Class<?>[] classes;

		Type(Class<?>... classes) {
			this.classes = classes;
		}

		public Class<?>[] getClasses() {
			return classes;
		}

		public int toSqlType() {
			switch (this) {
			case INTEGER:
				return Types.INTEGER;
			case BOOLEAN:
				return Types.BOOLEAN;
			default:
				return Types.VARCHAR;
			}
		}

		public static Type fromSqlType(int sqlType) {
			switch (sqlType) {
			case Types.INTEGER:
				return INTEGER;
			case Types.BOOLEAN:
				return BOOLEAN;
			default:
				return STRING;
			}
		}

		public static Type fromClass(Class<?> clazz) {
			for (Type t : values())
				for (Class<?> claz : t.getClasses())
					if (clazz == claz)
						return t;
			return null;
		}
	}
}
