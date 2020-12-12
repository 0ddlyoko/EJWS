package me.oddlyoko.ejws.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import lombok.Getter;
import me.oddlyoko.ejws.exceptions.ComputeException;

/**
 * Represent caracteristics of a Field for a Models.<br />
 * This class is linked to an instance of a Models.
 */
@Getter
public class FieldInstance {
	private ProxiedModel<?> model;
	private Fields field;
	// Hash containing an hash of the value of specific field.
	// This is used to check if a field has been updated or not without having a
	// reference to the specific value
	// The key is the id of the field
	// The value is the hash of the variable
	private HashMap<FieldInstance, Integer> hash;

	public FieldInstance(ProxiedModel<?> model, Fields field) {
		this.model = model;
		this.field = field;
		this.hash = new HashMap<>();
	}

	/**
	 * Initialize the hash field
	 */
	public void initializeHash() {
		if (this.field.isComputable()) {
			String[] required = field.getCompute().getRequired();
			for (String id : required) {
				FieldInstance fi = this.model.getFieldInstance(id);
				this.hash.put(fi, 0);
			}
		}
	}

	/**
	 * Compute this field if needed.<br />
	 * This method can call compute on other fields !
	 * 
	 * @return true if this field has been computed, false it not
	 */
	public boolean computeIfNeeded(Object instance) throws ComputeException {
		// First, check if this is computable
		if (!this.field.isComputable()) {
			// This field is not computable so we don't have to compute it
			return false;
		}
		boolean shouldCompute = false;
		// Check for each children if we need to compute it
		for (Entry<FieldInstance, Integer> h : this.hash.entrySet()) {
			h.getKey().computeIfNeeded(instance);
			// Get the hash
			Object o = null;
			try {
				o = h.getKey().getField().getField().get(instance);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				throw new ComputeException(String.format("Cannot compute field %s that is required to compute %s",
						h.getKey().getField().getId(), this.field.getId()), ex);
			}
			Integer hash = o == null ? 0 : o.hashCode();
			if (h.getValue().intValue() != hash.intValue()) {
				// Hashes are differents
				shouldCompute = true;
				h.setValue(hash);
			}
		}
		if (shouldCompute) {
			// Compute this field, call the appropriated method
			Method compute = field.getCompute().getComputeMethod();
			try {
				compute.invoke(instance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new ComputeException(String.format("Cannot compute field %s", this.field.getId()), ex);
			}
		}
		return shouldCompute;
	}

	public void setHash(FieldInstance fi, Integer hash) {
		this.hash.put(fi, hash);
	}
}
