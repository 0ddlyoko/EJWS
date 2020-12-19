package me.oddlyoko.ejws.example1;

import java.sql.Connection;
import java.sql.SQLException;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.example1.model.Person;
import me.oddlyoko.ejws.exceptions.ModelLoadException;

public class Example {

	public static void main(String[] args) throws ModelLoadException {
		EJWS ejws = new EJWS();
		// Load classes
		ejws.getModelManager().loadModel(Person.class);

		Person p = ejws.getModelManager().newInstance(Person.class);
		p.setName("luc");
		System.out.println(String.format("Age: %d", p.getAge()));
		System.out.println(String.format("Old: %b", p.isOld()));

		try (Connection connection = ejws.getDatabaseManager().newConnection()) {
			System.out.println("Connected !");
			System.out.println("Loading Person !");
			ejws.getDatabaseManager().loadModel(connection, ejws.getModelManager().getModel(Person.class));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
