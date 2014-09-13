package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 11/24/12
 * Time: 9:32 AM
 */
public class Technicians {
	public static Technician login(String username, String password)
	{
		Technician technician = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM technician WHERE username = '" + username + "' AND password = '" + password + "' AND isActive = TRUE");
			results = stmt.executeQuery();

			while (results.next())
			{
				technician = new Technician(results.getInt(1), results.getString(2), results.getString(3), results.getInt(4), results.getString(5), results.getString(6), results.getBoolean(7), results.getBoolean(8), results.getBoolean(9));
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

		return technician;
	}

	public static Technician getTechnician(int id)
	{
		Technician technician = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM technician WHERE id = '" + id + "'");
			results = stmt.executeQuery();

			while (results.next())
			{
				technician = new Technician(results.getInt(1), results.getString(2), results.getString(3), results.getInt(4), results.getString(5), results.getString(6), results.getBoolean(7), results.getBoolean(8), results.getBoolean(9));
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

		return technician;
	}

	public static List<Technician> getAllForCompany(int companyID)
	{
		List<Technician> technicians = new ArrayList<Technician>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM technician WHERE company = '" + companyID + "' ORDER BY lastname");
			results = stmt.executeQuery();

			while (results.next())
			{
				technicians.add(new Technician(results.getInt(1), results.getString(2), results.getString(3), results.getInt(4), results.getString(5), results.getString(6), results.getBoolean(7), results.getBoolean(8), results.getBoolean(9)));
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

		return technicians;
	}
}
