package me.oddlyoko.ejws.database;

import me.oddlyoko.ejws.model.Fields;

import java.util.Map;

public class TableInformation {
	private final String id;
	private final Map<String, Fields> columns;

	public TableInformation(String id, Map<String, Fields> columns) {
		this.id = id;
		this.columns = columns;
	}

	public Fields getColumn(String id) {
		return this.columns.get(id);
	}

	public String toSQL(Fields f) {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(" ");
		switch (f.getType()) {
		case BOOLEAN:
			sb.append("BOOLEAN");
			break;
		case INTEGER:
			sb.append("INTEGER");
			break;
		case STRING:
			sb.append("VARCHAR");
			break;
		}
		if (!f.isBlank())
			sb.append(" NOT NULL");
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public Map<String, Fields> getColumns() {
		return columns;
	}
}
