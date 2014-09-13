package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class Inspections
{
	public static Inspection getInspection(int ID)
	{
		Inspection inspection = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM inspection WHERE id = " + ID);
			results = stmt.executeQuery();

			while (results.next())
			{
				inspection = new Inspection(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7), results.getTimestamp(8), results.getString(9), results.getString(10), results.getInt(11), results.getInt(12), results.getBoolean(13), results.getBoolean(14), results.getString(15));
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

		return inspection;
	}

	public static List<Inspection> getInspectionsForCompany(int companyID, Map<String, String> options) {
		List<Inspection> inspections = new ArrayList<Inspection>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM inspection WHERE company = " + companyID);

			if(options.containsKey("date") && Utils.notEmpty(options.get("date")) && !"all".equals(options.get("date"))) {
				query.append(" AND date > ");

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());

				if("30days".equals(options.get("date"))) {
					calendar.set(Calendar.DAY_OF_MONTH, -30);
				}
				else if("3months".equals(options.get("date"))) {
					calendar.set(Calendar.MONTH, -1);
				}
				else if("lastyear".equals(options.get("date"))) {
					calendar.set(Calendar.YEAR, -1);
				}
				else if("3months".equals(options.get("date"))) {
					calendar.set(Calendar.MONTH, -1);
				}

				query.append("'" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "'");
			}

			if(options.containsKey("tech") && Utils.notEmpty(options.get("tech")) && !"all".equals(options.get("tech"))) {
				query.append(" AND tech = " + options.get("tech"));
			}

			if(options.containsKey("option") && Utils.notEmpty(options.get("option")) && !"all".equals(options.get("option"))) {
				if("uncontacted".equals(options.get("option"))) {
					query.append(" AND contacted = FALSE");
				}
			}

			if(options.containsKey("search")) {
				String search = options.get("search");
				query.append(" AND (firstname LIKE '" + search + "%' OR lastname LIKE '" + search + "%' OR city LIKE '" + search  + "%')");
			}

			query.append(" ORDER BY " + (options.containsKey("orderby") ? options.get("orderby") : "date"));
			query.append((options.containsKey("orderby-direction") ? options.get("orderby-direction") : " desc"));

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				inspections.add(new Inspection(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7), results.getTimestamp(8), results.getString(9), results.getString(10), results.getInt(11), results.getInt(12), results.getBoolean(13), results.getBoolean(14), results.getString(15)));
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

		return inspections;
	}
}
