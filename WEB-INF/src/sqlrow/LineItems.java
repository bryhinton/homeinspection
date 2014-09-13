package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class LineItems
{
	public static List<LineItem> getTopLevelLineItems(int areaID, boolean includeInactive)
	{
		List<LineItem> lineItems = new ArrayList<LineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM lineitem WHERE area = " + areaID + " AND (parent IS NULL OR parent = 0)");

			if(!includeInactive) {
				query.append(" AND isactive = TRUE");
			}

			query.append(" ORDER BY isactive DESC, name");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				lineItems.add(new LineItem(results.getInt(1), results.getString(2), results.getInt(3), results.getInt(4), results.getBoolean(5)));
			}
		}
		catch (Exception e)
		{
			Utils.rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			Utils.commitAndClose(db);
		}

		return lineItems;
	}

	public static List<LineItem> getChildren(int parentID, boolean includeInactive)
	{
		List<LineItem> lineItems = new ArrayList<LineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM lineitem WHERE parent = " + parentID);

			if(!includeInactive) {
				query.append(" AND isactive = TRUE");
			}

			query.append(" ORDER BY isactive DESC, name");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				lineItems.add(new LineItem(results.getInt(1), results.getString(2), results.getInt(3), results.getInt(4), results.getBoolean(5)));
			}
		}
		catch (Exception e)
		{
			Utils.rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			Utils.commitAndClose(db);
		}

		return lineItems;
	}

	public static LineItem getLineItem(int lineItemID)
	{
		LineItem lineItem = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM lineitem WHERE id = " + lineItemID);

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				lineItem = new LineItem(results.getInt(1), results.getString(2), results.getInt(3), results.getInt(4), results.getBoolean(5));
			}
		}
		catch (Exception e)
		{
			Utils.rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			Utils.commitAndClose(db);
		}

		return lineItem;
	}

	public static int getCountForInspectionAreaTopLevel(int inspectionAreaID, int topLevelID)
	{
		int count = 0;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT COUNT(*) FROM lineitem, area, inspectionarea ");
			query.append("WHERE inspectionarea.id = " + inspectionAreaID + " AND inspectionarea.area = area.id AND lineItem.area = area.id AND lineitem.parent = " + topLevelID);
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				count = results.getInt(1);
			}
		}
		catch (Exception e)
		{
			Utils.rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			Utils.commitAndClose(db);
		}

		return count;
	}

	public static int getCountForLineItem(int inID)
	{
		int count = 0;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT COUNT(*) FROM lineitem WHERE id = " + inID + " OR parent = " + inID);
			results = stmt.executeQuery();

			while (results.next())
			{
				count = results.getInt(1);
			}
		}
		catch (Exception e)
		{
			Utils.rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			Utils.commitAndClose(db);
		}

		return count;
	}
}
