package me.oddlyoko.ejws.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

/**
 * Manage the Database, create model tables, create, retrieve, edit & delete data (CRUD)
 * <br />
 * Once calling {@link #loadDatabase()}, load the database, create required tables and alter old ones.
 */
public class DatabaseManager {
	private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

	private final DatabaseModel databaseModel;
	/**
	 * Contains information about a table
	 */
	private final Map<String, TableInformation> tableInformation;

	public DatabaseManager(String host, int port, String username, String password, String database, String schema) {
		this.databaseModel = new DatabaseModel(host, port, username, password, database, schema);
		this.tableInformation = new HashMap<>();
	}

	public Connection createConnection() throws SQLException {
		return new Connection(this.databaseModel.getConnection(), this.databaseModel.getSchema());
	}

	/**
	 * Load the database by:
	 * <ol>
	 * <li>Creating tables of all models if it doesn't exist with correct columns</li>
	 * <li>If table exists, check if table should be altered (add / remove / edit columns)</li>
	 * <li>Create / Delete Primary Keys</li>
	 * <li>Create / Delete Foreign Keys</li>
	 * </ol>
	 * 
	 * @param connection The connection
	 * @throws SQLException If exception occurs
	 */
	public void loadDatabase(Connection connection) throws SQLException {
		this.loadDatabase(connection, EJWS.getInstance().getModelManager().getModels().values());
	}

	/**
	 * Load the database by:
	 * <ol>
	 * <li>Creating tables of all given models if it doesn't exist with correct columns</li>
	 * <li>If table exists, check if table should be altered (add / remove / edit columns)</li>
	 * <li>Create / Delete Primary Keys</li>
	 * <li>Create / Delete Foreign Keys</li>
	 * </ol>
	 * 
	 * @param connection The connection
	 * @param models A collection of models
	 * @throws SQLException If exception occurs
	 */
	public void loadDatabase(Connection connection, Collection<Models<?>> models) throws SQLException {
		// First: Create tables that aren't created
		try {
			this.createOrEditTables(connection, models);
			connection.commit();
		} catch (Exception ex) {
			// Rollback
			try {
				connection.rollback();
			} catch (SQLException ex2) {
				LOG.error("Rollback error:", ex2);
			}
			throw new SQLException("An error has occured while loading database", ex);
		}
	}

	/**
	 * Create tables or edit columns for specific models<br />
	 * Do not create / edit Primary / Foreign Keys
	 * 
	 * @param connection The connection
	 * @param models A collection of models
	 * @throws SQLException If exception occurs
	 */
	private void createOrEditTables(Connection connection, Collection<Models<?>> models) throws SQLException {
		for (Models<?> model : models)
			this.createOrEditTable(connection, model);
	}

	/**
	 * Create table or edit columns for specific model<br />
	 * Do not create / edit Primary / Foreign Keys
	 * 
	 * @param connection The connection
	 * @param model The model
	 * @throws SQLException If exception occurs
	 */
	private void createOrEditTable(Connection connection, Models<?> model) throws SQLException {
		// First: retrieve data for specific model
		TableInformation tableInformation = DatabaseUtil.getTableInformations(model);
		// Then check if table exist
		String tableName = model.getId();
		List<Fields> fields = model.getFields().values().stream().filter(Fields::isStored).collect(Collectors.toList());

		if (!connection.tableExists(tableName)) {
			// Create the table
			connection.createTable(tableName, fields, false);
		} else {
			// Edit table (Add / Delete / Alter columns)
			connection.editTable(tableName, fields, false);
		}
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
		return Optional.ofNullable(this.tableInformation.get(tableName));
	}

	public void saveTableInformation(String tableName, TableInformation tableInformation) {
		this.tableInformation.put(tableName, tableInformation);
	}

	public DatabaseModel getDatabaseModel() {
		return databaseModel;
	}
}