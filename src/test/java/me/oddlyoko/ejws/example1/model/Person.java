package me.oddlyoko.ejws.example1.model;

import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

@Models.Model(primary = {"id"})
public class Person {
	@Fields.Field
	private long id;
	@Fields.Field
	private String name;
	@Fields.Field(compute = "computeAge")
	private int age = 18;
	@Fields.Field(compute = "computeOld")
	public boolean old;

	@Models.Require({ "name" })
	public void computeAge() {
		age = "luc".equalsIgnoreCase(name) ? 15 : 20;
	}

	@Models.Require({ "age" })
	public void computeOld() {
		old = age >= 18;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public boolean isOld() {
		return old;
	}
}
