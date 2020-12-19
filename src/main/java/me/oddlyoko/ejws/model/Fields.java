package me.oddlyoko.ejws.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.sql.Types;

import lombok.Getter;
import lombok.Setter;

/**
 * Represent caracteristics of a Field for a Models.
 */
@Getter
public class Fields {
	private java.lang.reflect.Field field;
	private Type type;
	private String id;
	private String name;
	private boolean stored;
	private ComputeMethod compute;
	@Setter
	private boolean computeWhenNeeded;
	private boolean blank;
	private boolean empty;

	protected Fields(Type type, String id, String name, boolean stored, ComputeMethod compute, boolean blank,
			boolean empty, java.lang.reflect.Field field) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.stored = stored;
		this.compute = compute;
		this.blank = blank;
		this.empty = empty;
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

		String compute() default "";

		/**
		 * Could the field be null in database ?
		 */
		boolean blank() default false;

		/**
		 * Could the field be empty in database ?
		 */
		boolean empty() default false;
	}

	public static enum Type {
		STRING(Types.BOOLEAN, "VARCHAR"),
		INTEGER(Types.INTEGER, "INT"),
		BOOLEAN(Types.VARCHAR, "BOOLEAN");

		private int sqlType;
		private String sqlKeyword;

		private Type(int sqlType, String sqlKeyword) {
			this.sqlType = sqlType;
			this.sqlKeyword = sqlKeyword;
		}

		public int getSqlType() {
			return sqlType;
		}

		public String getSqlKeyword() {
			return sqlKeyword;
		}

		public static Type fromSqlType(int sqlType) {
			for (Type t : values())
				if (t.sqlType == sqlType)
					return t;
			return STRING;
		}
	}
}
