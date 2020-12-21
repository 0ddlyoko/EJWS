package me.oddlyoko.ejws.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import lombok.Getter;

public class DatabaseModel {
	@Getter
	private String host;
	@Getter
	private int port;
	@Getter
	private String username;
	private String password;
	@Getter
	private String database;

	public DatabaseModel(String host, int port, String username, String password, String database) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
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
}