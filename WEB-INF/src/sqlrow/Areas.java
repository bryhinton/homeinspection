package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class Areas
{
	public static Area getArea(int id)
	{
		Area area = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM area WHERE id = " + id);
			results = stmt.executeQuery();

			while (results.next())
			{
				area = new Area(results.getInt(1), results.getString(2), results.getInt(3), results.getBoolean(4));
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

		return area;
	}

	public static Area getAreaFromInspectionArea(int id)
	{
		Area area = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT area.* FROM area, inspectionarea WHERE area.id = inspectionarea.area AND inspectionarea.id = " + id);
			results = stmt.executeQuery();

			while (results.next())
			{
				area = new Area(results.getInt(1), results.getString(2), results.getInt(3), results.getBoolean(4));
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

		return area;
	}

	public static List<Area> getAllForCompany(int companyID, boolean includeInactive)
	{
		List<Area> areas = new ArrayList<Area>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM area WHERE company = " + companyID);

			if(!includeInactive) {
				query.append(" AND isactive = TRUE");
			}

			query.append(" ORDER BY isactive DESC, name");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				areas.add(new Area(results.getInt(1), results.getString(2), results.getInt(3), results.getBoolean(4)));
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

		return areas;
	}

	public static List<Area> getAllForInspection(int inspectionID, boolean includeInactive)
	{
		List<Area> areas = new ArrayList<Area>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT area.* FROM area, inspectionarea WHERE area.id = inspectionarea.area AND inspectionarea.inspection = " + inspectionID);

			if(!includeInactive) {
				query.append(" AND isactive = TRUE");
			}

			query.append(" ORDER BY isactive DESC, name");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				areas.add(new Area(results.getInt(1), results.getString(2), results.getInt(3), results.getBoolean(4)));
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

		return areas;
	}
}
