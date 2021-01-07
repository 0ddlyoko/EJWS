package me.oddlyoko.ejws.example1;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.database.Connection;
import me.oddlyoko.ejws.example1.model.Person;
import me.oddlyoko.ejws.example1.model.Server;
import me.oddlyoko.ejws.exceptions.ModelLoadException;

import java.sql.SQLException;

public class Example {

	public static void main(String[] args) throws ModelLoadException {
		EJWS ejws = new EJWS();
		// Load classes
		ejws.getModelManager().loadModel(Person.class);
		ejws.getModelManager().loadModel(Server.class);

		Person p = ejws.getModelManager().newInstance(Person.class);
		p.setName("luc");
		System.out.printf("Age: %d%n", p.getAge());
		System.out.printf("Old: %b%n", p.isOld());

		try {
			ejws.getDatabaseManager().loadModels();
			try (Connection connection = ejws.getDatabaseManager().createConnection()) {
				System.out.println("Connected !");
				System.out.println("Loading Person !");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
