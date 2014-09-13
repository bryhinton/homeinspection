package sqlrow;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class SQLRow
{
	public LinkedHashMap<String, Method> updateList = new LinkedHashMap<String, Method>();

	public LinkedHashMap<String, Method> loadList = new LinkedHashMap<String, Method>();

	public LinkedHashMap<String, Method> nestedObjects = new LinkedHashMap<String, Method>();

	public LinkedHashMap<String, Method> actions = new LinkedHashMap<String, Method>();

	protected int ID;

	protected String info = "";

	public List<String> includedNested = new ArrayList<String>();

	public abstract void init();

	public String getDatabase() {
		return "homeinspection";
	}

	public abstract String getTable();

	public int getID()
	{
		return ID;
	}

	public void setID(int iD)
	{
		ID = iD;
	}

	public String getInfo()
	{
		return info;
	}

	public void resetInfo(String inInfo)
	{
		if(Utils.notEmpty(inInfo))
		{
			info = inInfo;
		}
		else
		{
			info = "";
		}
	}

	public void clearInfo()
	{
		info = "";
	}

	public void setInfo(String key, String value)
	{
		removeInfo(key);
		addInfo(key, value);
	}

	public String getInfo(String inKey)
	{
		String[] sets = info.split("&");

		for (String set : sets)
		{
			String[] pair = set.split("=");

			if(pair.length > 1 && pair[0].equals(inKey))
			{
				return pair[1];
			}
		}

		return null;
	}

	public void addInfo(String key, String value)
	{
		if(info.equals(""))
		{
			info = key + "=" + value;
		}
		else
		{
			info = info + "&" + key + "=" + value;
		}
	}

	public void removeInfo(String inKey)
	{
		if(Utils.notEmpty(info) && info.contains(inKey))
		{
			String[] sets = info.split("&");

			clearInfo();

			for (String set : sets)
			{
				String[] pair = set.split("=");

				if(!pair[0].equals(inKey))
				{
					addInfo(pair[0], pair.length > 1 ? pair[1] : "");
				}
			}
		}
	}

	public boolean hasInfo()
	{
		return false;
	}

	public String getDefaultSort() {
		return "";
	}

	public void insert()
	{
		try
		{
			StringBuilder query = new StringBuilder();
			ArrayList<Object> values = new ArrayList<Object>();
			query.append("INSERT INTO " + getTable() + " (");

			boolean first = true;
			for (String column : updateList.keySet())
			{
				if(!first)
				{
					query.append(", ");
				}
				else
				{
					first = false;
				}

				query.append(column);
			}

			if(hasInfo())
			{
				query.append(", info");
			}

			query.append(") VALUES (");

			first = true;
			for (String column : updateList.keySet())
			{
				if(!first)
				{
					query.append(", ");
				}
				else
				{
					first = false;
				}

				query.append("?");

				values.add(updateList.get(column).invoke(this, new Object[]
				{}));
			}

			if(hasInfo())
			{
				query.append(", '" + getInfo() + "'");
			}

			query.append(")");

			setID((int) Utils.update(getDatabase(), query.toString(), values));
		}
		catch (Exception e)
		{
			System.out.println("Problem inserting " + getXMLName() + " record");
			System.out.println(e.getMessage());
		}
	}

	public void update()
	{
		try
		{
			StringBuilder query = new StringBuilder();
			ArrayList<Object> values = new ArrayList<Object>();
			query.append("UPDATE " + getTable() + " SET ");

			boolean first = true;
			for (String column : updateList.keySet())
			{
				if(!first)
				{
					query.append(", ");
				}
				else
				{
					first = false;
				}

				query.append(column + "=?");

				try
				{
					values.add(updateList.get(column).invoke(this, new Object[]
					{}));
				}
				catch (Exception e)
				{
					System.out.println("UPDATE ERROR " + e.getMessage().toString());
				}
			}

			if(hasInfo())
			{
				query.append(", info='" + info + "'");
			}

			query.append(", ID=" + getID());

			query.append(" WHERE ID=" + getID());

			Utils.update(getDatabase(), query.toString(), values);
		}
		catch (Exception e)
		{
			System.out.println("Problem updating " + getXMLName() + " record");
			System.out.println(e.getMessage());
		}
	}

	public void delete()
	{
		delete(null, null, 0);
	}

	public void delete(String inTable, String inColumn, int inID)
	{
		try
		{
			String table = inTable;

			if(table == null)
			{
				table = getTable();
			}

			String column = inColumn;

			if(column == null)
			{
				column = "ID";
			}

			int id = inID;

			if(id == 0)
			{
				id = getID();
			}

			String query = "DELETE FROM " + getTable() + " WHERE " + column + "=" + id;
			Utils.update(getDatabase(), query, new ArrayList<Object>());
		}
		catch (Exception e)
		{
			System.out.println("Problem deleting " + getXMLName() + "record");
			System.out.println(e.getMessage());
		}
	}

	public void load(long inID)
	{
		Utils.selectRow(getDatabase(), getTable(), inID, this);
	}

	public void load(ResultSet parameters)
	{
		try
		{
			if(parameters.next())
			{
				setID(parameters.getInt(1));

				int i = 2;
				for (String column : loadList.keySet())
				{
					if(loadList.get(column).getParameterTypes()[0].equals(Integer.TYPE))
					{
						loadList.get(column).invoke(this, new Object[]
						{ parameters.getInt(i) });
					}
					else if(loadList.get(column).getParameterTypes()[0].equals(String.class))
					{
						loadList.get(column).invoke(this, new Object[]
						{ parameters.getString(i) });
					}
					else if(loadList.get(column).getParameterTypes()[0].equals(Timestamp.class))
					{
						loadList.get(column).invoke(this, new Object[]
						{ parameters.getTimestamp(i) });
					}
					else if(loadList.get(column).getParameterTypes()[0].equals(Boolean.TYPE))
					{
						loadList.get(column).invoke(this, new Object[]
						{ parameters.getBoolean(i) });
					}

					i++;
				}

				if(hasInfo()) {
					resetInfo(parameters.getString(i));
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("LOAD ERROR " + e.getMessage());
			System.out.println(e.getMessage());
		}
	}

	public void writeXML(PrintWriter writer)
	{
		try
		{
			writer.println("\t<" + getXMLName() + ">");
			writer.println("\t\t<id>" + getID() + "</id>");

			for (String key : updateList.keySet())
			{
				writer.println("\t\t<" + key + ">" + Utils.escapeXML(updateList.get(key).invoke(this, new Object[]
				{})) + "</" + key + ">");
			}

			if(hasInfo())
			{
				writer.println("\t\t<info>" + getInfo() + "</info>");
			}

			writer.println("\t</" + getXMLName() + ">");
		}
		catch (Exception e)
		{
			System.out.println("Error writing XML for " + getXMLName());
			e.printStackTrace();
		}
	}

	public String getXMLName() {
		return getTable();
	}
}
