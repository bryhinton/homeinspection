package api;

import com.google.gson.*;
import sqlrow.SQLRow;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 7/18/13
 * Time: 7:41 PM
 */
public class SQLRowAdapter implements JsonSerializer<SQLRow> {

	@Override
	public JsonElement serialize(SQLRow sqlRow, Type type, JsonSerializationContext jsonSerializationContext) {
		if(sqlRow != null) {
			return serialzeSQLRow(sqlRow);
		}
		else {
			return getErrorJSON("OBJECT IS NULL");
		}
	}

	public JsonObject serialzeSQLRow(SQLRow sqlRow) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("id", sqlRow.getID());

		for(Map.Entry<String, Method> field : sqlRow.updateList.entrySet()) {
			try {
				Object value = field.getValue().invoke(sqlRow, null);

				if(value instanceof Integer || value instanceof Double) {
					jsonObject.addProperty(field.getKey(), (Number) value);
				}
				else if(value instanceof Boolean) {
					jsonObject.addProperty(field.getKey(), (Boolean) value);
				}
				else {
					jsonObject.addProperty(field.getKey(), value != null ? value.toString() : "");
				}

			}
			catch(Exception e) {
				System.out.println("Could not serialize field: " + field.getKey() + ", on object: " + sqlRow.getTable());
				return getErrorJSON("Could not serialize field: " + field.getKey() + ", on object: " + sqlRow.getTable());
			}
		}

		for(Map.Entry<String, Method> field : sqlRow.nestedObjects.entrySet()) {
			if(sqlRow.includedNested.contains(field.getKey()) || sqlRow.includedNested.contains("*")) {
				try {
					Object value = field.getValue().invoke(sqlRow, null);

					if(value instanceof SQLRow) {
						if(sqlRow.includedNested.contains("*")) {
							((SQLRow) value).includedNested.add("*");
						}

						jsonObject.add(field.getKey(), serialzeSQLRow((SQLRow) value));
					}
					else if(value instanceof List) {
						JsonObject list = new JsonObject();

						int i = 0;
						for(Object object : (List) value) {
							if(object instanceof SQLRow) {
								if(sqlRow.includedNested.contains("*")) {
									((SQLRow) object).includedNested.add("*");
								}

								list.add(String.valueOf(((SQLRow) object).getID()), serialzeSQLRow((SQLRow) object));
								i++;
							}
						}

						if(i == 0) {
							list.addProperty("empty", true);
						}

						jsonObject.add(field.getKey(), list);
					}
					else if(value instanceof Integer || value instanceof Double) {
						jsonObject.addProperty(field.getKey(), (Number) value);
					}
					else if(value instanceof Boolean) {
						jsonObject.addProperty(field.getKey(), (Boolean) value);
					}
					else {
						jsonObject.addProperty(field.getKey(), value != null ? value.toString() : "");
					}
				}
				catch(Exception e) {
					System.out.println("Could not serialize field: " + field.getKey() + ", on object: " + sqlRow.getTable());
					return getErrorJSON("Could not serialize field: " + field.getKey() + ", on object: " + sqlRow.getTable());
				}
			}
		}

		if(sqlRow.hasInfo()) {
			jsonObject.addProperty("info", sqlRow.getInfo());
		}

		return jsonObject;
	}

	public JsonObject getErrorJSON(String message) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("error", message);

		return jsonObject;
	}
}
