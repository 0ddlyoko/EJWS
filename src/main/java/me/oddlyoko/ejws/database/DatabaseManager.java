package me.oddlyoko.ejws.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import me.oddlyoko.ejws.database.TableInformation.ColumnInformation;
import me.oddlyoko.ejws.model.Models;

public class DatabaseManager {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

	@Getter
	private DatabaseModel databaseModel;
	/**
	 * Contains information about a table
	 */
	private Map<String, TableInformation> tableInformations;

	public DatabaseManager(String host, int port, String username, String password, String database) {
		this.databaseModel = new DatabaseModel(host, port, username, password, database);
		this.tableInformations = new HashMap<>();
	}

	public Connection createConnection() throws SQLException {
		return new Connection(this.databaseModel.getConnection());
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
	 * <li>The table exists:</li>
	 * <ol>
	 * <li>Update / Add required columns</li>
	 * <li>Load models if one of the added column is computed</li>
	 * <li>Compute models and save it</li>
	 * </ol>
	 * </li>
	 * </ul>
	 * 
	 * @param connection The connection
	 * @param model      The model to load
	 */
	public void loadModel(Connection connection, Models<?> model) throws SQLException {
		if (!model.isStored())
			return;
		String tableName = model.getId();
		// Check if the table exist
		Map<String, ColumnInformation> columns = DatabaseUtil.getColumnsInformation(model);
		if (!connection.tableExists(tableName)) {
			connection.createTable(tableName, columns, true);
		} else {
			connection.alterTable(tableName, columns, true);
		}
	}

	public Optional<TableInformation> getTableInformation(String tableName) {
		return Optional.ofNullable(this.tableInformations.get(tableName));
	}

	public void saveTableInformation(String tableName, TableInformation tableInformation) {
		this.tableInformations.put(tableName, tableInformation);
	}
}