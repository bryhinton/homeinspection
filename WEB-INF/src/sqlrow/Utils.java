package sqlrow;

import api.SQLRowAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Utils
{
	public static final String HOME_INSPECTION = "homeinspection";
	private static Gson gson = null;

	// Loads the DB driver class. By doing it this way, the compiler
	// doesn't need access to the driver's jar file.
	static
	{
		try
		{
			// Class.forName("org.sqlite.JDBC");
		}
		catch (Exception e)
		{
		}
	}

	// This opens a connection to the database, starts a new transaction,
	// and returns the connection.

	public static Connection openConnection(String database) throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		Connection db = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, "mainuser", "m41nus3r");
		db.setAutoCommit(false);

		return db;
	}

	// Commits the transaction and closes the connection.

	public static void commitAndClose(Connection db)
	{
		try
		{
			db.commit();
			db.close();
		}
		catch (Exception e)
		{
		}
	}

	// Rolls back (cancels) the transaction and closes the connection.

	public static void rollbackAndClose(Connection db)
	{
		try
		{
			db.rollback();
			db.close();
		}
		catch (Exception e)
		{
		}
	}

	public static PrintWriter getWriter(HttpServletResponse rsp)
	{
		PrintWriter writer = null;

		try
		{
			writer = rsp.getWriter();
		}
		catch (IOException e)
		{
			return null;
		}

		return writer;
	}

	public static void PrepareUpdate(String database, String table, int ID, HashMap<String, Object> parameters) throws Exception
	{
		StringBuilder query = new StringBuilder();

		query.append("UPDATE " + table + " SET ");

		boolean first = true;

		for (String key : parameters.keySet())
		{
			if(!first)
			{
				query.append(", ");
			}
			else
			{
				first = false;
			}

			query.append(key + "=" + parameters.get(key));
		}

		query.append(" WHERE ID=" + ID);

		update(database, query.toString(), new ArrayList<Object>());
	}

	public static long update(String database, String query, ArrayList<Object> inValues) throws Exception
	{
		Connection db = null;
		ResultSet results = null;
		long id = 0;

		try
		{
			db = openConnection(database);

			PreparedStatement stmt = db.prepareStatement(query);

			int i = 1;
			for (Object value : inValues)
			{
				if(value instanceof Integer)
				{
					stmt.setInt(i, (Integer) value);
				}
				else if(value instanceof Double)
				{
					stmt.setDouble(i, (Double) value);
				}
				else if(value instanceof String)
				{
					stmt.setString(i, (String) value);
				}
				else if(value instanceof Timestamp)
				{
					stmt.setTimestamp(i, (Timestamp) value);
				}
				else if(value instanceof Boolean)
				{
					stmt.setBoolean(i, (Boolean) value);
				}
				else if(value == null) {
					stmt.setString(i, null);
				}

				i++;
			}
			//System.out.println(stmt.toString());

			stmt.executeUpdate();

			results = stmt.executeQuery("SELECT LAST_INSERT_ID()");

			if(results.next())
			{
				id = results.getInt(1);
			}

			results.close();
		}
		catch (Exception e)
		{
			rollbackAndClose(db);
			System.out.println(e.getMessage());
			throw e;
		}
		finally
		{
			commitAndClose(db);
		}

		return id;
	}

	public static void selectRow(String database, String table, long inID, SQLRow object)
	{
		Connection db = null;
		ResultSet results = null;

		try
		{
			db = openConnection(database);

			String query = "SELECT * FROM " + table + " WHERE ID=" + inID;
			PreparedStatement stmt = db.prepareStatement(query);
			results = stmt.executeQuery();

			object.load(results);
		}
		catch (Exception e)
		{
			rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			commitAndClose(db);
		}
	}

	public static int getLastInsertID(String database)
	{
		Connection db = null;
		ResultSet results = null;
		int lastID = -1;

		try
		{
			db = openConnection(database);
			PreparedStatement stmt = db.prepareStatement("SELECT LAST_INSERT_ID();");
			results = stmt.executeQuery();

			while (results.next())
			{
				lastID = results.getInt(1);
			}
		}
		catch (Exception e)
		{
			rollbackAndClose(db);
			System.out.println(e.getMessage().toString());
		}
		finally
		{
			commitAndClose(db);
		}

		return lastID;
	}

	public static boolean notEmpty(String inString)
	{
		if(inString == null)
		{
			return false;
		}
		else if(inString.equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static boolean isEmpty(String inString)
	{
		return !notEmpty(inString);
	}

	public static boolean daysEqual(Calendar date1, Calendar date2)
	{
		if(date1 == null || date2 == null)
		{
			return false;
		}

		if(date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR))
		{
			return false;
		}

		if(date1.get(Calendar.MONTH) != date2.get(Calendar.MONTH))
		{
			return false;
		}

		if(date1.get(Calendar.DAY_OF_MONTH) != date2.get(Calendar.DAY_OF_MONTH))
		{
			return false;
		}

		return true;
	}

	public static String formatPhoneNumber(String rawNumber)
	{
		if(notEmpty(rawNumber))
		{
			if(rawNumber.length() == 7)
			{
				StringBuilder formattedNumber = new StringBuilder();
				formattedNumber.append(rawNumber.substring(0, 3));
				formattedNumber.append("-");
				formattedNumber.append(rawNumber.substring(3));

				return formattedNumber.toString();

			}
			else if(rawNumber.length() == 10)
			{
				StringBuilder formattedNumber = new StringBuilder();
				formattedNumber.append("(");
				formattedNumber.append(rawNumber.substring(0, 3));
				formattedNumber.append(") ");
				formattedNumber.append(rawNumber.substring(3, 6));
				formattedNumber.append("-");
				formattedNumber.append(rawNumber.substring(6));

				return formattedNumber.toString();
			}
		}

		return rawNumber;
	}

	public static void drawComment(PrintWriter writer, String inMessage, StackTraceElement[] inStackTrace)
	{
		writer.println("<!-- ");
		writer.println(inMessage);

		for (StackTraceElement element : inStackTrace)
		{
			writer.println(element.getClassName() + " " + element.getLineNumber() + " " + element.getMethodName());
		}

		writer.println("-->");
	}

	public static int parseInt(String inValue, int defaultValue)
	{
		if(inValue == null)
		{
			return defaultValue;
		}

		int value;

		try
		{
			value = Integer.parseInt(inValue);
		}
		catch (NumberFormatException e)
		{
			value = defaultValue;
		}

		return value;
	}

	public static double parseDouble(String inValue, double defaultValue)
	{
		if(inValue == null)
		{
			return defaultValue;
		}

		double value;

		try
		{
			value = Double.parseDouble(inValue);
		}
		catch (NumberFormatException e)
		{
			value = defaultValue;
		}

		return value;
	}

	public static long parseLong(String inValue, long defaultValue)
	{
		if(inValue == null)
		{
			return defaultValue;
		}

		long value;

		try
		{
			value = Long.parseLong(inValue);
		}
		catch (NumberFormatException e)
		{
			value = defaultValue;
		}

		return value;
	}

	@SuppressWarnings("all")
	public static LinkedHashMap sortMapByValue(Map<?, ?> unsortedMap, boolean inIsAscending)
	{
		final boolean isAscending = inIsAscending;
		LinkedList<Map.Entry<?, ?>> list = new LinkedList<Map.Entry<?, ?>>(unsortedMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<?, ?>>()
		{

			public int compare(Map.Entry<?, ?> arg0, Map.Entry<?, ?> arg1)
			{
				if(!isAscending)
				{
					return -((Comparable) ((Map.Entry<?, ?>) (arg0)).getValue()).compareTo(((Map.Entry<?, ?>) (arg1)).getValue());
				}

				return ((Comparable) ((Map.Entry<?, ?>) (arg0)).getValue()).compareTo(((Map.Entry<?, ?>) (arg1)).getValue());
			}
		});

		LinkedHashMap newMap = new LinkedHashMap();
		for (Iterator<Map.Entry<?, ?>> it = list.iterator(); it.hasNext();)
		{
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
			newMap.put(entry.getKey(), entry.getValue());
		}

		return newMap;
	}

	public static String truncateString(String inValue, int limit)
	{
		StringBuilder truncatedString = new StringBuilder();

		if(inValue.length() <= limit)
		{
			return inValue;
		}

		truncatedString.append(inValue.substring(0, limit));

		int location = limit;
		char i = inValue.charAt(location);

		while (!Character.isWhitespace(i))
		{
			truncatedString.append(i);

			location++;
			i = inValue.charAt(location);
		}

		return truncatedString.toString();
	}

	public static void drawStatesMenu(PrintWriter writer, String name, String selection)
	{
		writer.println("<select name='" + name + "' id='" + name + "'>");

		writer.println("<option>Choose...</option>");
		writer.println("<option " + (selection.equals("Alabama") ? "selected " : "") + "value='Alabama'>Alabama</option>");
		writer.println("<option " + (selection.equals("Alaska") ? "selected " : "") + "value='Alaska'>Alaska</option>");
		writer.println("<option " + (selection.equals("Arizona") ? "selected " : "") + "value='Arizona'>Arizona</option>");
		writer.println("<option " + (selection.equals("Arkansas") ? "selected " : "") + "value='Arkansas'>Arkansas</option>");
		writer.println("<option " + (selection.equals("California") ? "selected " : "") + "value='California'>California</option>");
		writer.println("<option " + (selection.equals("Colorado") ? "selected " : "") + "value='Colorado'>Colorado</option>");
		writer.println("<option " + (selection.equals("Connecticut") ? "selected " : "") + "value='Connecticut'>Connecticut</option>");
		writer.println("<option " + (selection.equals("Delaware") ? "selected " : "") + "value='Delaware'>Delaware</option>");
		writer.println("<option " + (selection.equals("Florida") ? "selected " : "") + "value='Florida'>Florida</option>");
		writer.println("<option " + (selection.equals("Georgia") ? "selected " : "") + "value='Georgia'>Georgia</option>");
		writer.println("<option " + (selection.equals("Hawaii") ? "selected " : "") + "value='Hawaii'>Hawaii</option>");
		writer.println("<option " + (selection.equals("Idaho") ? "selected " : "") + "value='Idaho'>Idaho</option>");
		writer.println("<option " + (selection.equals("Illinois") ? "selected " : "") + "value='Illinois'>Illinois</option>");
		writer.println("<option " + (selection.equals("Indiana") ? "selected " : "") + "value='Indiana'>Indiana</option>");
		writer.println("<option " + (selection.equals("Iowa") ? "selected " : "") + "value='Iowa'>Iowa</option>");
		writer.println("<option " + (selection.equals("Kansas") ? "selected " : "") + "value='Kansas'>Kansas</option>");
		writer.println("<option " + (selection.equals("Kentucky") ? "selected " : "") + "value='Kentucky'>Kentucky</option>");
		writer.println("<option " + (selection.equals("Louisiana") ? "selected " : "") + "value='Louisiana'>Louisiana</option>");
		writer.println("<option " + (selection.equals("Maine") ? "selected " : "") + "value='Maine'>Maine</option>");
		writer.println("<option " + (selection.equals("Maryland") ? "selected " : "") + "value='Maryland'>Maryland</option>");
		writer.println("<option " + (selection.equals("Massachusetts") ? "selected " : "") + "value='Massachusetts'>Massachusetts</option>");
		writer.println("<option " + (selection.equals("Michigan") ? "selected " : "") + "value='Michigan'>Michigan</option>");
		writer.println("<option " + (selection.equals("Minnesota") ? "selected " : "") + "value='Minnesota'>Minnesota</option>");
		writer.println("<option " + (selection.equals("Mississippi") ? "selected " : "") + "value='Mississippi'>Mississippi</option>");
		writer.println("<option " + (selection.equals("Missouri") ? "selected " : "") + "value='Missouri'>Missouri</option>");
		writer.println("<option " + (selection.equals("Montana") ? "selected " : "") + "value='Montana'>Montana</option>");
		writer.println("<option " + (selection.equals("Nebraska") ? "selected " : "") + "value='Nebraska'>Nebraska</option>");
		writer.println("<option " + (selection.equals("Nevada") ? "selected " : "") + "value='Nevada'>Nevada</option>");
		writer.println("<option " + (selection.equals("New Hampshire") ? "selected " : "") + "value='New Hampshire'>New Hampshire</option>");
		writer.println("<option " + (selection.equals("New Jersey") ? "selected " : "") + "value='New Jersey'>New Jersey</option>");
		writer.println("<option " + (selection.equals("New Mexico") ? "selected " : "") + "value='New Mexico'>New Mexico</option>");
		writer.println("<option " + (selection.equals("New York") ? "selected " : "") + "value='New York'>New York</option>");
		writer.println("<option " + (selection.equals("North Carolina") ? "selected " : "") + "value='North Carolina'>North Carolina</option>");
		writer.println("<option " + (selection.equals("North Dakota") ? "selected " : "") + "value='North Dakota'>North Dakota</option>");
		writer.println("<option " + (selection.equals("Ohio") ? "selected " : "") + "value='Ohio'>Ohio</option>");
		writer.println("<option " + (selection.equals("Oklahoma") ? "selected " : "") + "value='Oklahoma'>Oklahoma</option>");
		writer.println("<option " + (selection.equals("Oregon") ? "selected " : "") + "value='Oregon'>Oregon</option>");
		writer.println("<option " + (selection.equals("Pennsylvania") ? "selected " : "") + "value='Pennsylvania'>Pennsylvania</option>");
		writer.println("<option " + (selection.equals("Rhode Island") ? "selected " : "") + "value='Rhode Island'>Rhode Island</option>");
		writer.println("<option " + (selection.equals("South Carolina") ? "selected " : "") + "value='South Carolina'>South Carolina</option>");
		writer.println("<option " + (selection.equals("South Dakota") ? "selected " : "") + "value='South Dakota'>South Dakota</option>");
		writer.println("<option " + (selection.equals("Tennessee") ? "selected " : "") + "value='Tennessee'>Tennessee</option>");
		writer.println("<option " + (selection.equals("Texas") ? "selected " : "") + "value='Texas'>Texas</option>");
		writer.println("<option " + (selection.equals("Utah") ? "selected " : "") + "value='Utah'>Utah</option>");
		writer.println("<option " + (selection.equals("Vermont") ? "selected " : "") + "value='Vermont'>Vermont</option>");
		writer.println("<option " + (selection.equals("Virginia") ? "selected " : "") + "value='Virginia'>Virginia</option>");
		writer.println("<option " + (selection.equals("Washington") ? "selected " : "") + "value='Washington'>Washington</option>");
		writer.println("<option " + (selection.equals("West Virginia") ? "selected " : "") + "value='West Virginia'>West Virginia</option>");
		writer.println("<option " + (selection.equals("Wisconsin") ? "selected " : "") + "value='Wisconsin'>Wisconsin</option>");
		writer.println("<option " + (selection.equals("Wyoming") ? "selected " : "") + "value='Wyoming'>Wyoming</option>");

		writer.println("</select>");
	}

	public static String getThreeCharacterMonthFromInt(int month)
	{
		if(month == 0)
		{
			return "Jan";
		}
		else if(month == 1)
		{
			return "Feb";
		}
		else if(month == 2)
		{
			return "Mar";
		}
		else if(month == 3)
		{
			return "Apr";
		}
		else if(month == 4)
		{
			return "May";
		}
		else if(month == 5)
		{
			return "Jun";
		}
		else if(month == 6)
		{
			return "Jul";
		}
		else if(month == 7)
		{
			return "Aug";
		}
		else if(month == 8)
		{
			return "Sep";
		}
		else if(month == 9)
		{
			return "Oct";
		}
		else if(month == 10)
		{
			return "Nov";
		}
		else
		{
			return "Dec";
		}
	}

	public static String escapeXML(Object xml) {
		if(xml instanceof String) {
			return escapeXML((String) xml);
		}

		return String.valueOf(xml);
	}

	public static String escapeXML(String xml) {
		if(xml.contains("&")) {
			xml = xml.replaceAll("&", "&amp;");
		}
		xml = xml.replaceAll("<", "&lt;");
		xml = xml.replaceAll(">", "&gt;");

		return xml;
	}

	public static String getCookieValue(HttpServletRequest req, String name) {
		if(req.getCookies() != null) {
			for(Cookie cookie : req.getCookies()) {
				if(name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static Gson getGson() {
		if(gson == null) {
			gson = new GsonBuilder().registerTypeHierarchyAdapter(SQLRow.class, new SQLRowAdapter()).create();
		}

		return gson;
	}
}
