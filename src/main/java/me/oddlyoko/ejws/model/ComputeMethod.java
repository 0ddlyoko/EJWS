package me.oddlyoko.ejws.model;

import java.lang.reflect.Method;

public class ComputeMethod {
	private final Method computeMethod;
	private final String[] required;

	public ComputeMethod(Method computeMethod, String[] required) {
		this.computeMethod = computeMethod;
		this.required = required;
	}

	public Method getComputeMethod() {
		return computeMethod;
	}

	public String[] getRequired() {
		return required;
	}
}
