package me.oddlyoko.ejws.database;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.oddlyoko.ejws.model.Fields;

@Getter
public class TableInformation {
	private String id;
	private Map<String, ColumnInformation> columns;

	public TableInformation(String id, Map<String, ColumnInformation> columns) {
		this.id = id;
		this.columns = columns;
	}

	public ColumnInformation getColumn(String id) {
		return this.columns.get(id);
	}

	@Getter
	@AllArgsConstructor
	public static class ColumnInformation {
		private String id;
		private Fields.Type type;
		private boolean blank;
        private boolean autoIncrement;
        
        public String toSQL() {
			StringBuilder sb = new StringBuilder();
			sb.append(id).append(" ");
			sb.append(type.getSqlKeyword()).append(" ");
			if (!blank)
				sb.append("NOT NULL");
			return sb.toString();
        }
	}
}
