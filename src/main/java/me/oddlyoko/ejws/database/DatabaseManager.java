package me.oddlyoko.ejws.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

public class DatabaseManager {
	private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

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

	public void loadModels() throws SQLException {
		try (Connection c = this.createConnection()) {
			for (Models<?> model : EJWS.getInstance().getModelManager().getModels().values()) {
				this.loadModel(c, model, true);
			}
		}
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
	 * @param connection
	 *            The connection
	 * @param model
	 *            The model to load
	 * @param commit
	 *            If true, commit the transaction
	 */
	private void loadModel(Connection connection, Models<?> model, boolean commit) throws SQLException {
		LOG.info("Loading model {}", model.getId());
		if (!model.isStored())
			return;
		String tableName = model.getId();
		// Check if the table exist
		TableInformation tableInformation = DatabaseUtil.getTableInformations(model);
		this.saveTableInformation(model.getId(), tableInformation);
		if (!connection.tableExists(tableName)) {
			connection.createTable(tableName, tableInformation.getColumns(), commit);
		} else {
			List<String> added = connection.alterTable(tableName, tableInformation.getColumns(), false);
			if (added.stream().map(id -> model.getField(id).get()).anyMatch(Fields::isComputable)) {
				LOG.info("Computing {} ...", model.getId());
				// We should compute
				// Get fields to compute
				List<Fields> fields = added.stream().map(id -> model.getField(id).get()).collect(Collectors.toList());
				// Compute fields
				this.computeFields(connection, model, fields);
			}
			if (commit)
				connection.commit();
		}
	}

	/**
	 * Load all data from the database, compute fields and save data to the
	 * database
	 * 
	 * @param connection
	 *            The connection
	 * @param model
	 *            The model
	 * @param fields
	 *            The fields to compute
	 */
	private void computeFields(Connection connection, Models<?> model, List<Fields> fields) throws SQLException {

	}

	public Optional<TableInformation> getTableInformation(String tableName) {
		return Optional.ofNullable(this.tableInformations.get(tableName));
	}

	public void saveTableInformation(String tableName, TableInformation tableInformation) {
		this.tableInformations.put(tableName, tableInformation);
	}
}