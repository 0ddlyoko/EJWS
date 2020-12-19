package me.oddlyoko.ejws.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.oddlyoko.ejws.database.TableInformation.ColumnInformation;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

/**
 * A list of utilities methods used by the Database part
 */
public class DatabaseUtil {

	/**
	 * Transform a list of ColumnInformation to a list of PostgreSQL column
	 * syntax
	 * 
	 * @param columns
	 *            Columns
	 * @return
	 */
	public static List<String> columnInformationToColumnSQL(Collection<ColumnInformation> columns) {
		List<String> result = new ArrayList<>();
		for (ColumnInformation ci : columns) {
			String id = ci.getId();
			Fields.Type type = ci.getType();
			boolean nullable = ci.isBlank();
			StringBuilder sb = new StringBuilder();
			sb.append(id).append(" ");
			sb.append(type.getSqlKeyword()).append(" ");
			if (!nullable)
				sb.append("NOT NULL");
			result.add(sb.toString());
		}
		return result;
	}

	/**
	 * Load column information of a model<br />
	 * If a column has stored to false, do not add it
	 * 
	 * @param model
	 *            The model
	 * @return A list containing all columns of a model
	 */
	public static Map<String, ColumnInformation> getColumnsInformation(Models<?> model) {
		Map<String, ColumnInformation> columns = new HashMap<>();
		for (Fields f : model.getFields()) {
			if (f.isStored()) {
				String id = f.getId();
				Fields.Type type = f.getType();
				boolean nullable = f.isBlank();
				ColumnInformation ci = new ColumnInformation(id, type, nullable, false);
				columns.put(id, ci);
			}
		}
		return columns;
	}

	/**
	 * Compare two HashMap of {@link ColumnInformation} and returns ids that are
	 * on left and not on right
	 * 
	 * @param left
	 *            The collection to compare
	 * @param right
	 *            The second collection to compare
	 * @return
	 */
	public static List<String> compareColumnInformation(Collection<String> left, Map<String, ColumnInformation> right) {
		List<String> result = new ArrayList<>();
		for (String id : left)
			if (!right.containsKey(id))
				result.add(id);
		return result;
	}
}
