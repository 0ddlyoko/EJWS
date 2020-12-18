package me.oddlyoko.ejws.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.oddlyoko.ejws.exceptions.ModelLoadException;

public class ModelManager {
	private static final Logger LOG = LoggerFactory.getLogger(ModelManager.class);
	private HashMap<String, Models<?>> models;

	public ModelManager() {
		this.models = new HashMap<>();
	}

	public <E> E newInstance(String id) {
		@SuppressWarnings("unchecked")
		Models<E> model = (Models<E>) this.models.get(id);
		if (model != null)
			return newInstance(model);
		return null;
	}

	public <E> E newInstance(Class<E> clazz) {
		for (Models<?> model : this.models.values()) {
			if (model.getClazz().equals(clazz)) {
				@SuppressWarnings("unchecked")
				Models<E> m = (Models<E>) model;
				return newInstance(m);
			}
		}
		return null;
	}

	private <E> E newInstance(Models<E> model) {
		try {
			// Create a proxy of this model.
			// This proxy will handle the fields updates (computes).
			return ProxiedModel.newInstance(model);
		} catch (Exception ex) {
			LOG.error(String.format("Cannot create an instance of %s", model.getId()), ex);
		}
		return null;
	}

	public <E> Models<E> getModel(String id) {
		@SuppressWarnings("unchecked")
		Models<E> model = (Models<E>) this.models.get(id);
		return model;
	}

	public <E> Models<E> getModel(Class<E> clazz) {
		for (Models<?> model : this.models.values()) {
			if (model.getClazz().equals(clazz)) {
				@SuppressWarnings("unchecked")
				Models<E> m = (Models<E>) model;
				return m;
			}
		}
		return null;
	}

	public <E> void loadModel(Class<E> modelClass) throws ModelLoadException {
		Models.Model mod = modelClass.getAnnotation(Models.Model.class);
		if (mod != null) {
			// Load the class
			String id = mod.value();
			if (id == null || "".equalsIgnoreCase(id.trim())) {
				id = mod.id();
				if (id == null || "".equalsIgnoreCase(id.trim())) {
					// Still null, use the id of the class
					id = modelClass.getSimpleName();
				}
			}
			boolean stored = mod.stored();
			// Load fields
			List<Fields> fields = this.loadFields(modelClass);
			Models<E> model = new Models<E>(modelClass, id, stored, fields);
			this.models.put(id, model);
		}
	}

	private List<Fields> loadFields(Class<?> modelClass) throws ModelLoadException {
		List<Fields> lst = new ArrayList<>();
		for (java.lang.reflect.Field f : modelClass.getDeclaredFields()) {
			// Check if an annotation exists
			// String
			Fields.Field annotation = f.getAnnotation(Fields.Field.class);
			if (annotation != null) {
				// Load this field
				Fields field;
				try {
					// Set accessible (if it's private)
					f.setAccessible(true);
					field = loadField(modelClass, f, annotation);
				} catch (NoSuchMethodException ex) {
					throw new ModelLoadException(
							String.format("Cannot load field %s of class %s", f.getName(), modelClass.getSimpleName()),
							ex);
				}
				lst.add(field);
			}
		}
		return lst;
	}

	private Fields loadField(Class<?> modelClass, java.lang.reflect.Field field, Fields.Field annotation)
			throws NoSuchMethodException {
		// Id
		String id = annotation.id();
		if (id == null || "".equalsIgnoreCase(id.trim())) {
			id = annotation.value();
			if (id == null || "".equalsIgnoreCase(id.trim())) {
				// Still null, use the name of the variable
				id = field.getName();
			}
		}
		// Name
		String name = annotation.name();
		if (name == null || "".equalsIgnoreCase(name.trim())) {
			name = annotation.value();
			if (name == null || "".equalsIgnoreCase(name.trim())) {
				// Still null, use the id
				name = id;
			}
		}
		Fields.Type type = getFieldType(field);
		boolean stored = annotation.stored();
		String compute = annotation.compute();
		// Check if this method exists
		ComputeMethod computeMethod = null;
		if (compute != null && !"".equalsIgnoreCase(compute.trim())) {
			try {
				Method method = modelClass.getMethod(compute);
				String[] required = null;
				Models.Require require = method.getAnnotation(Models.Require.class);
				if (require != null)
					required = require.value();
				computeMethod = new ComputeMethod(method, required);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new NoSuchMethodException(
						String.format("Method %s not found for computed value of %s", compute, name));
			}
		}
		return new Fields(type, id, name, stored, computeMethod, field);
	}

	private Fields.Type getFieldType(java.lang.reflect.Field f) {
		Class<?> clazz = f.getType();
		if (clazz.equals(Integer.class) || clazz.equals(int.class))
			return Fields.Type.INTEGER;
		else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class))
			return Fields.Type.BOOLEAN;
		return Fields.Type.STRING;
	}
}
