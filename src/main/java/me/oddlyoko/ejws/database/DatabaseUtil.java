package me.oddlyoko.ejws.database;

import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	public static Map<String, Fields> getColumnsInformation(Models<?> model) {
		Map<String, Fields> columns = new HashMap<>();
		for (Fields f : model.getFields().values()) {
			if (f.isStored()) {
				String id = f.getId();
				columns.put(id, f);
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
	 * @return List of columns id that are on left collection and not on right collection
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

	/**
	 * Returns the name of the sequence used for specific column
	 * 
	 * @param tableName The name of the model
	 * @param rowName The name of the field
	 * @return The name of the sequence used for specific column
	 */
	public static String columnToSequence(String tableName, String rowName) {
		return String.format("%s_%s_seq", tableName, rowName);
	}

	/**
	 * Returns the name of the unique used for UNIQUE constraint
	 * 
	 * @param tableName The name of the table
	 * @param rowName The name of the field
	 * @return The name of the unique used for UNIQUE constraint
	 */
	public static String columnToUnique(String tableName, String rowName) {
		return String.format("unique_%s_%s", tableName, rowName);
	}
}
