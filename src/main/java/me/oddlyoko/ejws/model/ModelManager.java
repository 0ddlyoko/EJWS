package me.oddlyoko.ejws.model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.oddlyoko.ejws.exceptions.ModelLoadException;

public class ModelManager {
	private static final Logger LOG = LogManager.getLogger(ModelManager.class);

	private final Map<String, Models<?>> models;

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
			try {
				// Load the class
				String id = mod.value();
				if ("".equalsIgnoreCase(id.trim())) {
					id = mod.id();
					if ("".equalsIgnoreCase(id.trim())) {
						// Still null, use the id of the class
						id = modelClass.getSimpleName();
					}
				}
				id = id.toLowerCase();
				LOG.info("Loading {}", id);
				boolean stored = mod.stored();
				// Load fields
				Map<String, Fields> fields = this.loadFields(modelClass);
				String[] primary = mod.primary();
				if (primary.length == 0)
					throw new ModelLoadException("Model should have at least one primary key");
				Set<String> primarySet = Arrays.stream(primary).filter(p -> fields.get(p) != null)
						.collect(Collectors.toSet());
				if (primarySet.size() != primary.length) {
					throw new ModelLoadException("Invalid Primary Key, it should contains non-repeat fields variables");
				}
				Models<E> model = new Models<>(modelClass, id, stored, fields, primarySet);
				this.models.put(id, model);
			} catch (ModelLoadException ex) {
				throw new ModelLoadException(String.format("Cannot load model at %s", modelClass.getName()), ex);
			}
		}
	}

	private Map<String, Fields> loadFields(Class<?> modelClass) throws ModelLoadException {
		Map<String, Fields> lst = new HashMap<>();
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
					throw new ModelLoadException(String.format("Cannot load field %s:", f.getName()), ex);
				}
				lst.put(field.getId(), field);
			}
		}
		return lst;
	}

	private Fields loadField(Class<?> modelClass, java.lang.reflect.Field field, Fields.Field annotation)
			throws NoSuchMethodException, ModelLoadException {
		// Id
		String id = annotation.id();
		if ("".equalsIgnoreCase(id.trim())) {
			id = annotation.value();
			if ("".equalsIgnoreCase(id.trim())) {
				// Still null, use the name of the variable
				id = field.getName();
			}
		}
		// Name
		String name = annotation.name();
		if ("".equalsIgnoreCase(name.trim())) {
			name = annotation.value();
			if ("".equalsIgnoreCase(name.trim())) {
				// Still null, use the id
				name = id;
			}
		}
		Fields.Type type = Fields.Type.fromClass(modelClass);
		if (type == null) {
			// Unknown type
			throw new ModelLoadException("Type not supported !");
		}
		boolean stored = annotation.stored();
		String compute = annotation.compute();
		boolean blank = annotation.blank();
		boolean empty = annotation.empty();
		boolean autoincrement = annotation.autoincrement();
		// Check if this method exists
		ComputeMethod computeMethod = null;
		if (!"".equalsIgnoreCase(compute.trim())) {
			try {
				Method method = modelClass.getMethod(compute);
				String[] required = null;
				Models.Require require = method.getAnnotation(Models.Require.class);
				if (require != null)
					required = require.value();
				computeMethod = new ComputeMethod(method, required);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new ModelLoadException(
						String.format("Method %s not found", compute));
			}
		}
		return new Fields(type, id, name, stored, computeMethod, blank, empty, autoincrement, field);
	}

	public Map<String, Models<?>> getModels() {
		return models;
	}
}
