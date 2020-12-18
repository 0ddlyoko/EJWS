package me.oddlyoko.ejws.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import lombok.Getter;

@Getter
public class Models<E> {
	private Class<E> clazz;
	private String id;
	private boolean stored;
	private List<Fields> fields;

	protected Models(Class<E> clazz, String id, boolean stored, List<Fields> fields) {
		this.clazz = clazz;
		this.id = id;
		this.stored = stored;
		this.fields = fields;
	}

	public Fields getField(String id) {
		for (Fields f : this.fields)
			if (f.getId().equals(id))
				return f;
		return null;
	}

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Model {
		String value() default "";

		String id() default "";

		boolean stored() default true;
	}

	@Target({ ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Require {
		String[] value() default { "" };
	}
}
