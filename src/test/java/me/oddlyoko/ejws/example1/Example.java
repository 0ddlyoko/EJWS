package me.oddlyoko.ejws.example1;

import me.oddlyoko.ejws.exceptions.ModelLoadException;
import me.oddlyoko.ejws.model.ModelManager;
import me.oddlyoko.ejws.example1.model.Person;

public class Example {

	public static void main(String[] args) throws ModelLoadException {
		ModelManager mm = new ModelManager();
		// Load classes
		mm.loadModel(Person.class);

		Person p = mm.newInstance(Person.class);
		p.setName("jean");
		System.out.println(String.format("Age: %d", p.getAge()));
		System.out.println(String.format("Old: %b", p.isOld()));
		p.setName("luc");
		System.out.println(String.format("Age: %d", p.getAge()));
		System.out.println(String.format("Old: %b", p.isOld()));
		long before = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			p.isOld();
		}
		long after = System.currentTimeMillis();
		System.out.println(String.format("Time: %dms", after - before));
	}
}
