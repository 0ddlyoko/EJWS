package me.oddlyoko.ejws.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseModel {
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String database;
	private final String schema;

	public DatabaseModel(String host, int port, String username, String password, String database, String schema) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
		this.schema = schema;
		loadDatabase();
	}

	private void loadDatabase() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		String url = String.format("jdbc:postgresql://%s:%d/%s", this.host, this.port, this.database);
		Properties info = new Properties();
		info.setProperty("user", this.username);
		info.setProperty("password", this.password);
		// info.setProperty("ssl", "true");
		Connection c = DriverManager.getConnection(url, info);
		c.setAutoCommit(false);
		return c;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getDatabase() {
		return database;
	}

	public String getSchema() {
		return schema;
	}
}