package me.oddlyoko.ejws;

import me.oddlyoko.ejws.database.DatabaseManager;
import me.oddlyoko.ejws.model.ModelManager;

public class EJWS {
    private DatabaseManager databaseManager;
    private ModelManager modelManager;

    private EJWS() {
        this.databaseManager = new DatabaseManager(this, "localhost", 432, "root", "", "database");
        this.modelManager = new ModelManager(this);
    }

    public static void main(String[] args) {
        new EJWS();
    }
}
