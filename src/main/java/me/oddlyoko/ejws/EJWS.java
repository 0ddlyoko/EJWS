package me.oddlyoko.ejws;

import lombok.Getter;
import me.oddlyoko.ejws.database.DatabaseManager;
import me.oddlyoko.ejws.model.ModelManager;

@Getter
public class EJWS {
	private DatabaseManager databaseManager;
	private ModelManager modelManager;

	public EJWS() {
		this.databaseManager = new DatabaseManager(this, "localhost", 5432, "postgres", "admin", "EJWS");
		this.modelManager = new ModelManager(this);
	}

	public static void main(String[] args) {
		new EJWS();
	}
}
