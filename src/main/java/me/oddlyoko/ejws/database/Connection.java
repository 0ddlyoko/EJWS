package me.oddlyoko.ejws.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class Connection implements AutoCloseable {
	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
	@Getter
	private java.sql.Connection sqlConnection;

	public Connection(java.sql.Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}

	/**
	 * Check if specific table exists in database
	 * 
	 * @param connection The connection
	 * @param tableName  The name of the table
	 * @return true if the specific table exists, False otherwise
	 */
	public boolean tableExists(String tableName) {
		try (ResultSet rs = this.sqlConnection.getMetaData().getTables(null, null, tableName, null)) {
			return rs.next();
		} catch (SQLException ex) {
			LOG.error(String.format("Cannot check if table %s exist !", tableName), ex);
			return false;
		}
	}

	/**
	 * Create a table
	 * 
	 * @param connection The connection
	 * @param tableName  The name of the table
	 * @param columns    Columns
	 */
	public void createTable(String tableName, Map<String, ColumnInformation> columns, boolean commit)
			throws SQLException {
		LOG.info("Creating table %s", tableName);
		// Retrive columns
		List<String> columnsList = columns.values().stream().map(c -> c.toSQL()).collect(Collectors.toList());
		String columnsSQL = String.join(", ", columnsList);
		String createTable = String.format("CREATE TABLE %s (%s);", tableName, columnsSQL);
		LOG.info("SQL: %s", createTable);
		try (Statement s = this.sqlConnection.createStatement()) {
			s.execute(createTable);
			if (commit)
				this.commit();
		}
	}

	/**
	 * Alter a table by adding or removing columns and updating fields
	 * 
	 * @param connection The connection
	 * @param tableName  The name of the table
	 * @param columns    Columns
	 */
	public void alterTable(String tableName, Map<String, ColumnInformation> columns, boolean commit)
			throws SQLException {
		LOG.info("Altering table %s", tableName);
		// Check if all is okay
		TableInformation ti = EJWS.getInstance().getDatabaseManager().getTableInformation(tableName).get();
		// Tables to remove
		List<String> toRemove = DatabaseUtil.compareColumnInformation(ti.getColumns().keySet(), columns.keySet());
		// Tables to add
		List<String> toAdd = DatabaseUtil.compareColumnInformation(columns.keySet(), ti.getColumns().keySet());
		if (!toRemove.isEmpty() || !toAdd.isEmpty()) {
			LOG.info("Table %s is not the same as model, please wait", tableName);
			// We have to alter the table
			StringBuilder alterSQL = new StringBuilder();
			if (!toRemove.isEmpty())
				toRemove.stream().map(id -> String.format("DROP COLUMN %s, ", id)).forEach(alterSQL::append);
			if (!toAdd.isEmpty())
				toAdd.stream().map(id -> String.format("ADD COLUMN %s, ", columns.get(id).toSQL()))
						.forEach(alterSQL::append);
			String alterTable = String.format("ALTER TABLE %s %s;", tableName, alterSQL.toString());
			LOG.info("SQL: %s", alterTable);
			try (Statement s = this.sqlConnection.createStatement()) {
				s.execute(alterTable);
			}
		}
	}

	/**
	 * Retrieves informations about a table from the database
	 * 
	 * @param connection The connection
	 * @param tableName  The name of the table
	 * @return Information about a table
	 */
	public TableInformation getTableInformations(String tableName) throws SQLException {
		try (ResultSet rs = this.sqlConnection.getMetaData().getColumns(null, null, tableName, null)) {
			Map<String, ColumnInformation> columns = new HashMap<>();
			while (rs.next()) {
				String id = rs.getString("COLUMN_NAME");
				Fields.Type type = Fields.Type.fromSqlType(rs.getInt("DATA_TYPE"));
				boolean nullable = !"NO".equals(rs.getString("IS_NULLABLE"));
				boolean autoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
				columns.put(id, new ColumnInformation(id, type, nullable, autoIncrement));
			}
			return new TableInformation(tableName, columns);
		}
	}

	public void commit() throws SQLException {
		this.sqlConnection.commit();
	}

	@Override
	public void close() throws SQLException {
		this.sqlConnection.close();
	}
}
