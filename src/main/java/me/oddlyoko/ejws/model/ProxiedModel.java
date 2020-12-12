package me.oddlyoko.ejws.model;

import java.lang.reflect.Method;
import java.util.HashMap;

import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

@Getter
public class ProxiedModel<E> implements MethodInterceptor {
	private Models<E> model;
	private HashMap<String, FieldInstance> fields;

	private ProxiedModel(Models<E> model) {
		this.model = model;
		this.fields = new HashMap<>();
		// Initialize fields for computed values
		for (Fields f : model.getFields()) {
			FieldInstance fi = new FieldInstance(this, f);
			this.fields.put(f.getId(), fi);
		}
		// Initialize hash field
		for (FieldInstance fi : this.fields.values())
			fi.initializeHash();
	}

	public FieldInstance getFieldInstance(String id) {
		return fields.get(id);
	}

	public FieldInstance getFieldFromName(String name) {
		for (FieldInstance fi : this.fields.values())
			if (fi.getField().getField().getName().equals(name))
				return fi;
		return null;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		String name = method.getName();
		if ((name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2)) {
			// Get variable
			char propName[] = name.substring(name.startsWith("get") ? 3 : 2).toCharArray();
			propName[0] = Character.toLowerCase(propName[0]);
			// Get field
			FieldInstance fi = getFieldFromName(new String(propName));
			// Compute if needed
			if (fi != null)
				fi.computeIfNeeded(obj);
		}
		return proxy.invokeSuper(obj, args);
	}

	public static <E> E newInstance(Models<E> model) {
		Class<E> clazz = model.getClazz();
		Enhancer e = new Enhancer();
		e.setSuperclass(clazz);
		e.setCallback(new ProxiedModel<E>(model));
		@SuppressWarnings("unchecked")
		E bean = (E) e.create();
		return (E) bean;
	}
}
