package me.oddlyoko.example.example1.model;

import lombok.Getter;
import lombok.Setter;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

@Getter
@Models.Model(stored = true, computeWhenNeeded = true)
public class Person {
	@Fields.Field
	@Setter
	private String name;
	@Fields.Field(compute = "computeAge")
	private int age = 18;
	@Fields.Field(compute = "computeOld", stored = false)
	public boolean old;

	@Models.Require({ "name" })
	public void computeAge() {
		age = "luc".equalsIgnoreCase(name) ? 15 : 20;
	}

	@Models.Require({ "age" })
	public void computeOld() {
		old = age >= 18;
	}
}
