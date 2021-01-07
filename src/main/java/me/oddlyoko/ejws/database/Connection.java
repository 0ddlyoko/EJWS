package me.oddlyoko.ejws.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Fields.Type;

public class Connection implements AutoCloseable {
	private static final Logger LOG = LogManager.getLogger(Connection.class);

	private final java.sql.Connection sqlConnection;
	private final String schema;

	public Connection(java.sql.Connection sqlConnection, String schema) {
		this.sqlConnection = sqlConnection;
		this.schema = schema;
	}

	/**
	 * Check if specific table exists in database
	 *
	 * @param tableName
	 *            The name of the table
	 * @return true if the specific table exists, False otherwise
	 */
	public boolean tableExists(String tableName) {
		try (ResultSet rs = this.sqlConnection.getMetaData().getTables(null, this.schema, tableName, null)) {
			return rs.next();
		} catch (SQLException ex) {
			LOG.error(String.format("Cannot check if table %s exist !", tableName), ex);
			return false;
		}
	}

	/**
	 * Create a table without Primary / Foreign Key<br />
	 * Here is how it'll create the table:
	 * <ol>
	 * <li>Create the empty table</li>
	 * <li>For each field:
	 * 	<ol>
	 * 		<li>Create the sequence if needed</li>
	 * 		<li>Create the field with NOT NULL & UNIQUE constraints</li>
	 * 	</ol>
	 * </li>
	 * </ol>
	 * We assume that this table doesn't exist and no sequence / constraint exist for this table
	 * 
	 * @param tableName
	 *            The name of the table
	 * @param columns
	 *            Columns
	 * @param commit
	 *            Commit
	 */
	public void createTable(String tableName, List<Fields> columns, boolean commit)
			throws SQLException {
		LOG.info("Creating table {}", tableName);

		// Check if we have to create a table
		boolean create = columns.stream().anyMatch(Fields::isStored);
		if (create) {
			// First, create the empty table
			this.createEmptyTable(tableName);
			// Then, for each field, create the associated column
			for (Fields f : columns) {
				if (f.isStored()) {
					addField(tableName, f, true);
				}
			}
		}
		if (commit)
			this.commit();
	}

	/**
	 * Alter a table without adding Primary / Foreign Key<br />
	 * Here is how it'll alter the table:
	 * <ol>
	 * <li>Delete fields that have been removed</field>
	 * <li>Add missing sequences</li>
	 * <li>Delete sequences that don't belong to any new fields (or to fields that have been deleted)</li>
	 * <li>Set/Remove UNIQUE constraints for existing columns</li>
	 * <li>Create missing columns</li>
	 * <li>Alter table by adding unique constraints</li>
	 * </ol>
	 * We assume that this table doesn't exist and no sequence / constraint exist for this table
	 * 
	 * @param tableName
	 *            The name of the table
	 * @param columns
	 *            Columns
	 * @param commit
	 *            Commit
	 */
	public void editTable(String tableName, List<Fields> columns, boolean commit) throws SQLException {
		LOG.info("Altering table {}", tableName);
		List<String> columnsString = columns.stream().map(Fields::getId).collect(Collectors.toList());
		// First, delete fields that have been removed
		TableInformation ti = this.getTableInformations(tableName);
		boolean alter = false;
		StringBuilder alters = new StringBuilder("ALTER TABLE ").append(tableName).append(" ");
		// Remove removed columns
		for (String toRemove : DatabaseUtil.compareColumnInformation(ti.getColumns().keySet(), columnsString)) {
			// Remove this column
			alters.append("DROP COLUMN ").append(toRemove).append(",");
			alter = true;
		}
		// Add required columns 

		try (Statement s = this.sqlConnection.createStatement()) {
			
		}
	}

	/**
	 * Retrieves fields of a specific table
	 * 
	 * @param tableName
	 *            The name of the table
	 * @return List of Fields
	 */
	public List<Fields> getTableFields(String tableName) throws SQLException {
		try (ResultSet rs = this.sqlConnection.getMetaData().getColumns(null, this.schema, tableName, null)) {
			List<Fields> fields = new ArrayList<>();
			while (rs.next()) {
				String id = rs.getString("COLUMN_NAME");
				Fields.Type type = Fields.Type.fromSqlType(rs.getInt("DATA_TYPE"));
				boolean nullable = "YES".equals(rs.getString("IS_NULLABLE"));
				boolean autoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
				boolean unique = 
				Fields f = new Fields(type, id, null, true, null, nullable, true, autoIncrement, unique, null);
			}
			return fields;
		}
	}

	/**
	 * Create a new empty table
	 * 
	 * @param tableName The name of the table
	 * @throws SQLException If an error occurs
	 */
	public void createEmptyTable(String tableName) throws SQLException {
		try (PreparedStatement ps = this.sqlConnection.prepareStatement("CREATE TABLE ?")) {
			ps.setString(0, tableName);
			ps.execute();
		}
	}

	/**
	 * Add specific field to an existing table and create UNIQUE & NOT NULL constraints and / or sequences if needed
	 * 
	 * @param tableName The name of the table
	 * @param f The field to add
	 * @throws SQLException If an error occurs
	 */
	public void addField(String tableName, Fields f) throws SQLException {
		String sql = "ALTER TABLE ? ADD COLUMN ?";
		switch (f.getType()) {
			case BOOLEAN:
				sql += " BOOLEAN";
				break;
			case INTEGER:
				sql += " INTEGER";
				if (!f.isBlank())
					sql += " NOT NULL";
				if (f.isAutoincrement()) {
					createSequence(DatabaseUtil.columnToSequence(tableName, f.getId()));
					sql += " DEFAULT nextval(?)";
				}
				break;
			case STRING:
			default:
				sql += " VARCHAR";
				if (!f.isBlank())
					sql += " NOT NULL";
		}
		try (PreparedStatement ps = this.sqlConnection.prepareStatement(sql)) {
			ps.setString(0, tableName);
			ps.setString(1, f.getId());
			if (f.getType() == Type.INTEGER && f.isAutoincrement())
				ps.setString(2, DatabaseUtil.columnToSequence(tableName, f.getId()));
		}
		if (f.isUnique())
			this.createUnique(tableName, f.getId());
	}

	/**
	 * Remove specific field from an existing table and remove UNIQUE & NOT NULL constraints and / or sequences linked to this field
	 */
	public void removeField(String tableName, Fields f) throws SQLException {
		// First, drop sequence if it exists
		dropSequence(DatabaseUtil.columnToSequence(tableName, f.getId()));
		// Then, drop the column
		try (PreparedStatement ps = this.sqlConnection.prepareStatement("ALTER TABLE ? DROP COLUMN ?")) {
			ps.setString(0, tableName);
			ps.setString(1, f.getId());
		}
	}

	/**
	 * Create a new Sequence
	 * 
	 * @param name The name of the sequence
	 * @throws SQLException If an error occurs
	 */
	public void createSequence(String name) throws SQLException {
		try (PreparedStatement ps = this.sqlConnection.prepareStatement("CREATE SEQUENCE ?")) {
			ps.setString(0, name);
			ps.execute();
		}
	}

	/**
	 * Drop specific sequence if it exists<br />
	 * If the sequence doesn't exist, do not throw an exception
	 * 
	 * @param name the name of the sequence
	 * @throws SQLException If an error occurs
	 */
	public void dropSequence(String name) throws SQLException {
		try (PreparedStatement ps = this.sqlConnection.prepareStatement("DROP SEQUENCE IF EXISTS ?")) {
			ps.setString(0, name);
			ps.execute();
		}
	}

	/**
	 * Create UNIQUE constraint for specific row
	 * 
	 * @param tableName The name of the table
	 * @param rowName The name of the column
	 * @throws SQLException If an error occurs
	 */
	public void createUnique(String tableName, String rowName) throws SQLException {
		try (PreparedStatement ps = this.sqlConnection.prepareStatement("ALTER TABLE ? ADD CONSTRAINT ? UNIQUE (?)")) {
			ps.setString(0, tableName);
			ps.setString(1, DatabaseUtil.columnToUnique(tableName, rowName));
			ps.setString(2, rowName);
		}
	}

	/**
	 * Drop UNIQUE constraint for specific row
	 * 
	 * @param tableName The name of the table
	 * @param rowName The name of the column
	 * @throws SQLException If an error occurs
	 */
	public void dropUnique(String tableName, String rowName) throws SQLException {
		try (PreparedStatement ps = this.sqlConnection.prepareStatement("ALTER TABLE ? DROP CONSTRAINT ?")) {
			ps.setString(0, tableName);
			ps.setString(1, DatabaseUtil.columnToUnique(tableName, rowName));
		}
	}

	public void commit() throws SQLException {
		this.sqlConnection.commit();
	}

	public void rollback() throws SQLException {
		this.sqlConnection.rollback();
	}

	@Override
	public void close() throws SQLException {
		this.sqlConnection.close();
	}

	public java.sql.Connection getSqlConnection() {
		return sqlConnection;
	}
}
