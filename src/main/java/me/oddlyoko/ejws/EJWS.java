package me.oddlyoko.ejws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import me.oddlyoko.ejws.database.DatabaseManager;
import me.oddlyoko.ejws.model.ModelManager;

@Getter
public class EJWS {
	private static final Logger LOG = LogManager.getLogger(EJWS.class);
	private static EJWS instance;
	private DatabaseManager databaseManager;
	private ModelManager modelManager;

	public EJWS() {
		LOG.debug("DEBUG");
		LOG.info("INFO");
		LOG.warn("WARN");
		LOG.error("ERROR");
		LOG.fatal("FATAL");
		instance = this;
		this.databaseManager = new DatabaseManager("localhost", 5432, "postgres", "admin", "EJWS");
		// this.databaseManager = new DatabaseManager("localhost", 5432, "odoo",
		// "odoo", "EJWS");
		this.modelManager = new ModelManager();
	}

	public static EJWS getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		new EJWS();
	}
}
