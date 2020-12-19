package me.oddlyoko.ejws.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.database.TableInformation.ColumnInformation;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

public class DatabaseManager {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

	private EJWS ejws;
	@Getter
	private DatabaseModel databaseModel;
	/**
	 * Contains information about a table
	 */
	private Map<String, TableInformation> tableInformations;

	public DatabaseManager(EJWS ejws, String host, int port, String username, String password, String database) {
		this.ejws = ejws;
		this.databaseModel = new DatabaseModel(host, port, username, password, database);
		this.tableInformations = new HashMap<>();
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
	 */
	public void loadModel(Connection connection, Models<?> model) {
		if (!model.isStored())
			return;
		String tableName = model.getId();
		// Check if the table exist
		Map<String, ColumnInformation> columns = DatabaseUtil.getColumnsInformation(model);
		if (!this.tableExists(connection, tableName, false)) {
			LOG.info("Table %s not found, creating it ...", tableName);
			// The table doesn't exist, create it
			List<String> columnsList = DatabaseUtil.columnInformationToColumnSQL(columns.values());
			String columnsSQL = String.join(", ", columnsList);
			String createTable = String.format("CREATE TABLE %s (%s);", tableName, columnsSQL);
			System.out.println("createTable: " + createTable);
		} else {
			// Check if all is okay
			TableInformation ti = this.getTableInformations(connection, tableName, false);
			// Tables to remove
			List<String> toRemove = DatabaseUtil.compareColumnInformation(ti.getColumns().keySet(), columns);
			// Tables to add
			List<String> toAdd = DatabaseUtil.compareColumnInformation(columns.keySet(), ti.getColumns());
			if (!toRemove.isEmpty() || !toAdd.isEmpty()) {
				LOG.info("Table %s altered, creating it ...", tableName);
				// We have to alter the table
				StringBuilder alterSQL = new StringBuilder();
				if (!toRemove.isEmpty())
					for (String id : toRemove)
						alterSQL.append("DROP COLUMN ").append(id).append(", ");
				if (!toAdd.isEmpty()) {
					// Transform the list of columns to a SQL query
					List<String> columnsToAdd = DatabaseUtil.columnInformationToColumnSQL(
							toAdd.stream().map(id -> columns.get(id)).collect(Collectors.toList()));
					for (String id : columnsToAdd)
						alterSQL.append("ADD COLUMN ").append(id).append(", ");
				}
				String alterTable = String.format("ALTER TABLE %s %s;", tableName, alterSQL.toString());
				System.out.println("alterTable: " + alterTable);
			}
		}
	}

	/**
	 * Check if specific table exists<br />
	 * If the table has been previously loaded, the method do not check again
	 * with the database<br />
	 * If the table hasn't been loaded, retrieves information from the database
	 * and save it.
	 * 
	 * @param connection
	 *            The connection
	 * @param tableName
	 *            The name of the table
	 * @return true if the specific table exists, False otherwise
	 */
	public boolean tableExists(Connection connection, String tableName, boolean force) {
		// Check if we have information about this table
		TableInformation ti = this.tableInformations.get(tableName.toLowerCase());
		if (ti != null)
			return ti.isExist();
		return this.getTableInformations(connection, tableName, force).isExist();
	}

	/**
	 * Retrieves informations about a table and save it.<br />
	 * If force is true, search in the database even if the information is
	 * stored
	 * 
	 * @param connection
	 *            The connection
	 * @param tableName
	 *            The name of the table
	 * @param force
	 *            If true, search in database even if information is stored
	 * @return Information about a table
	 */
	public TableInformation getTableInformations(Connection connection, String tableName, boolean force) {
		if (!force) {
			// If information about the table is saved, do not retrieve it
			TableInformation ti = this.tableInformations.get(tableName);
			if (ti != null)
				return ti;
		}
		TableInformation ti = this._getTableInformations(connection, tableName);
		this.tableInformations.put(ti.getId(), ti);
		return ti;
	}

	/**
	 * Retrieves informations about a table
	 * 
	 * @param connection
	 *            The connection
	 * @param tableName
	 *            The name of the table
	 * @return Information about a table
	 */
	private TableInformation _getTableInformations(Connection connection, String tableName) {
		DatabaseMetaData metadata;
		try {
			metadata = connection.getMetaData();
			try (ResultSet rs = metadata.getColumns(null, null, tableName, null)) {
				Map<String, ColumnInformation> columns = new HashMap<>();
				boolean exist = false;
				while (rs.next()) {
					exist = true;
					String id = rs.getString("COLUMN_NAME");
					Fields.Type type = Fields.Type.fromSqlType(rs.getInt("DATA_TYPE"));
					boolean nullable = !"NO".equals(rs.getString("IS_NULLABLE"));
					boolean autoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
					columns.put(id, new ColumnInformation(id, type, nullable, autoIncrement));
				}
				if (exist)
					return new TableInformation(tableName, columns);
				else
					return new TableInformation(tableName, false);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves informations about multiple table<br />
	 * If force is true, search in the database even if the information is
	 * stored
	 * 
	 * @param connection
	 *            The connection
	 * @param tableNames
	 *            The list of table
	 * @param force
	 *            If true, search in database even if information is stored
	 * @return Information about multiple table
	 */
	public Map<String, TableInformation> getTablesInformations(Connection connection, String[] tableNames,
			boolean force) {
		Map<String, TableInformation> result = new HashMap<>();
		for (String tableName : tableNames)
			result.put(tableName.toLowerCase(), this.getTableInformations(connection, tableName, force));
		return result;
	}

	public Connection newConnection() throws SQLException {
		return databaseModel.getConnection();
	}
}