package me.oddlyoko.ejws.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.javadoc.internal.doclets.formats.html.markup.Table;
import lombok.Getter;
import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.database.TableInformation.ColumnInformation;
import me.oddlyoko.ejws.model.Models;

public class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

    private EJWS ejws;
    @Getter
    private DatabaseModel databaseModel;

    public DatabaseManager(EJWS ejws, String host, int port, String username, String password, String database) {
        this.ejws = ejws;
        this.databaseModel = new DatabaseModel(host, port, username, password, database);
    }

    /**
     * Load the specific model in database<br />
     * Either the table already exist or not:
     * <ul>
     * <li>The table doesn't exist:
     * <ul>
     * <li>Create the table</li>
     * </ul>
     * </li>
     * <li>The table exists:
     * <ol>
     * <li>Update / Add required columns</li>
     * <li>Load models if one of the added column is computed</li>
     * <li>Compute models and save it</li>
     * </ol>
     * </li>
     * </ul>
     * 
     * @param model
     */
    public void loadModel(Models<?> model) {
        if (!model.isStored())
            return;
        String tableName = model.getId();
    }

    /**
     * Check if specific table exist
     * 
     * @param connection The connection
     * @param tableName  The name of the table
     * @return true if the specific table exists, False otherwise
     */
    public boolean tableExists(Connection connection, String tableName) {

    }

    /**
     * Retrieves informations about a table
     * 
     * @param connection The connection
     * @param tableName  The name of the table
     * @return
     */
    public TableInformation getTableInformations(Connection connection, String tableName) {
        DatabaseMetaData metadata;
        try {
            metadata = connection.getMetaData();
            try (ResultSet rs = metadata.getColumns(null, null, tableName.toLowerCase(), null)) {
                List<ColumnInformation> columns = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString("COLUMN_NAME");
                    int type = rs.getInt("DATA_TYPE");
                    boolean nullable = !"NO".equals(rs.getString("IS_NULLABLE"));
                    String defaultValue = rs.getString("COLUMN_DEF");
                    boolean autoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
                    columns.add(new ColumnInformation(name, type, nullable, defaultValue, autoIncrement));
                }
                return new TableInformation(tableName.toLowerCase(), columns);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Map<String, TableInformation> getTablesInformations(Connection connection, String[] tableNames) {
        Map<String, TableInformation> result = new HashMap<>();
        for (String tableName : tableNames)
            result.put(tableName.toLowerCase(), this.getTableInformations(connection, tableName));
        return result;
    }
}