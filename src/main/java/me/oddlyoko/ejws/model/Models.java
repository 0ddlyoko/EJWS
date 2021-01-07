package me.oddlyoko.ejws.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Models<E> {
	private final Class<E> clazz;
	private final String id;
	private final boolean stored;
	private final Map<String, Fields> fields;
	private final Set<String> primary;

	protected Models(Class<E> clazz, String id, boolean stored, Map<String, Fields> fields, Set<String> primary) {
		this.clazz = clazz;
		this.id = id;
		this.stored = stored;
		this.fields = fields;
		this.primary = primary;
	}

	public Optional<Fields> getField(String id) {
		return Optional.ofNullable(this.fields.get(id));
	}

	public Class<E> getClazz() {
		return clazz;
	}

	public String getId() {
		return id;
	}

	public boolean isStored() {
		return stored;
	}

	public Map<String, Fields> getFields() {
		return fields;
	}

	public Set<String> getPrimary() {
		return primary;
	}

	public boolean isPrimary(String key) {
		return primary.contains(key);
	}

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Model {
		String value() default "";

		String id() default "";

		boolean stored() default true;

		/**
		 * @return Primary Keys of this model
		 */
		String[] primary();
	}

	@Target({ ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Require {
		String[] value() default { "" };
	}
}
