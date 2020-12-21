package me.oddlyoko.ejws;

import lombok.Getter;
import me.oddlyoko.ejws.database.DatabaseManager;
import me.oddlyoko.ejws.model.ModelManager;

@Getter
public class EJWS {
	private static EJWS instance;
	private DatabaseManager databaseManager;
	private ModelManager modelManager;

	public EJWS() {
		instance = this;
		// this.databaseManager = new DatabaseManager("localhost", 5432, "postgres", "admin", "EJWS");
		this.databaseManager = new DatabaseManager("localhost", 5432, "odoo", "odoo", "EJWS");
		this.modelManager = new ModelManager(this);
	}

	public static EJWS getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		new EJWS();
	}
}
