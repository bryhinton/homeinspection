package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/7/12
 * Time: 8:37 PM
 */
public class Companies {

	public static Company getCompany(int ID)
	{
		Company company = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM company WHERE id = " + ID);
			results = stmt.executeQuery();

			while (results.next())
			{
				company = new Company(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getTimestamp(7), results.getBoolean(8), results.getBoolean(9));
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

		return company;
	}

	public static List<Company> getAll()
	{
		List<Company> companies = new ArrayList<Company>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM company ORDER BY isactive DESC, name");
			results = stmt.executeQuery();

			while (results.next())
			{
				companies.add(new Company(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getTimestamp(7), results.getBoolean(8), results.getBoolean(9)));
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

		return companies;
	}

	public static Map<Integer, String> getDefaultCompanies() {
		Map<Integer, String> defaults = new HashMap<Integer, String>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM defaultcompany ORDER BY name");
			results = stmt.executeQuery();

			while (results.next())
			{
				defaults.put(results.getInt(1), results.getString(2));
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

		return defaults;
	}
}
