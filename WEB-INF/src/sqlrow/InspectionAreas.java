package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class InspectionAreas
{
	public static List<InspectionArea> getAllForInspection(int inspectionID)
	{
		List<InspectionArea> inspectionAreas = new ArrayList<InspectionArea>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM inspectionarea WHERE inspection = " + inspectionID);
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionAreas.add(new InspectionArea(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4)));
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

		return inspectionAreas;
	}

	public static InspectionArea getInspectionArea(int inID)
	{
		InspectionArea inspectionArea = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM inspectionarea WHERE id = " + inID);
			results = stmt.executeQuery();

			while (results.next())
			{
				inspectionArea = new InspectionArea(results.getInt(1), results.getInt(2), results.getInt(3), results.getString(4));
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

		return inspectionArea;
	}

	public static int getCountForArea(int inID)
	{
		int count = 0;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT COUNT(*) FROM inspectionarea WHERE area = " + inID);
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
