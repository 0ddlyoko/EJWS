package me.oddlyoko.ejws.model;

import java.lang.reflect.Method;

import lombok.Getter;

@Getter
public class ComputeMethod {
	private Method computeMethod;
	private String[] required;

	public ComputeMethod(Method computeMethod, String[] required) {
		this.computeMethod = computeMethod;
		this.required = required;
	}
}
