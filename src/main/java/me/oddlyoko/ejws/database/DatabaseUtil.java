package me.oddlyoko.ejws.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.oddlyoko.ejws.database.TableInformation.ColumnInformation;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

/**
 * A list of utilities methods used by the Database part
 */
public class DatabaseUtil {

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
	 * Compare two Collection of String and returns ids that are on left and not
	 * on right
	 * 
	 * @param left
	 *            The collection to compare
	 * @param right
	 *            The second collection to compare
	 * @return
	 */
	public static List<String> compareColumnInformation(Collection<String> left, Collection<String> right) {
		return left.stream().filter(id -> !right.contains(id)).collect(Collectors.toList());
	}

	/**
	 * Retrieves informations about a table from a model
	 * 
	 * @param model
	 *            The model
	 * @return Information about a table
	 */
	public static TableInformation getTableInformations(Models<?> model) {
		return new TableInformation(model.getId(), DatabaseUtil.getColumnsInformation(model));
	}
}
