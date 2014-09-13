package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectionAreaLineItems
{
	public static InspectionAreaLineItem get(int inspectionAreaID, int lineItemID)
	{
		InspectionAreaLineItem inspectionAreaLineItem = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM inspectionarealineitem WHERE inspectionarea = " + inspectionAreaID + " AND lineitem = " + lineItemID);
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItem = new InspectionAreaLineItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4), results.getString(5));
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

		return inspectionAreaLineItem;
	}

	public static InspectionAreaLineItem getByID(int id)
	{
		InspectionAreaLineItem inspectionAreaLineItem = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM inspectionarealineitem WHERE id = " + id);
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItem = new InspectionAreaLineItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4), results.getString(5));
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

		return inspectionAreaLineItem;
	}

	public static List<InspectionAreaLineItem> getAllForArea(int inspectionAreaID)
	{
		List<InspectionAreaLineItem> inspectionAreaLineItems = new ArrayList<InspectionAreaLineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM inspectionarealineitem ");
			query.append("WHERE inspectionarea = " + inspectionAreaID);
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItems.add(new InspectionAreaLineItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4), results.getString(5)));
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

		return inspectionAreaLineItems;
	}

	public static List<InspectionAreaLineItem> getAllForTopLevelLineItem(int inspectionAreaID, int topLevelID)
	{
		List<InspectionAreaLineItem> inspectionAreaLineItems = new ArrayList<InspectionAreaLineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT ili.* FROM inspectionarealineitem ili, lineitem ");
			query.append("WHERE ili.inspectionarea = " + inspectionAreaID + " AND ili.lineitem = lineitem.id AND lineitem.parent = " + topLevelID);
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItems.add(new InspectionAreaLineItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4), results.getString(5)));
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

		return inspectionAreaLineItems;
	}

	public static Map<Integer, InspectionAreaLineItem> getMapForArea(int inspectionAreaID)
	{
		Map<Integer, InspectionAreaLineItem> inspectionAreaLineItems = new HashMap<Integer, InspectionAreaLineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT lineitem.id, inspectionarealineitem.* FROM inspectionarealineitem, lineitem WHERE inspectionarealineitem.inspectionarea = " + inspectionAreaID + " AND inspectionarealineitem.lineitem = lineitem.id");
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItems.put(results.getInt(1), new InspectionAreaLineItem(results.getInt(2), results.getInt(3), results.getInt(4), results.getString(5), results.getString(6)));
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

		return inspectionAreaLineItems;
	}

	public static int getCountForArea(int inspectionAreaID)
	{
		int count = 0;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT COUNT(*) FROM inspectionarealineitem WHERE inspectionarea = " + inspectionAreaID);
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

	public static List<InspectionAreaLineItem> getFailingLineItemsForParent(int inspectionID, int parentID)
	{
		List<InspectionAreaLineItem> inspectionAreaLineItems = new ArrayList<InspectionAreaLineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT ili.* FROM inspectionarealineitem ili, lineitem, inspectionarea, inspection ");
			query.append("WHERE ili.lineitem = lineitem.id AND lineitem.parent = " + parentID + " AND ili.inspectionarea = inspectionarea.id");
			query.append(" AND inspectionarea.inspection = inspection.id AND inspection.id = " + inspectionID + " AND ili.result = 'fail'");
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItems.add(new InspectionAreaLineItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4), results.getString(5)));
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

		return inspectionAreaLineItems;
	}

	public static List<InspectionAreaLineItem> getFailingLineItemsForInspection(int inspectionID)
	{
		List<InspectionAreaLineItem> inspectionAreaLineItems = new ArrayList<InspectionAreaLineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT iali.* FROM inspectionarealineitem iali, inspectionarea ");
			query.append("WHERE iali.inspectionarea = inspectionarea.id AND inspectionarea.inspection = " + inspectionID + " AND iali.result = 'fail'");
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreaLineItems.add(new InspectionAreaLineItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4), results.getString(5)));
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

		return inspectionAreaLineItems;
	}
}
