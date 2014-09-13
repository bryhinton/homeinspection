package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 2/7/13
 * Time: 8:31 PM
 */
public class Categories {

	public static void deleteAll(int companyID)
	{
		Connection db = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("DELETE FROM category WHERE company = " + companyID);
			stmt.executeUpdate();
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
	}

	public static List<Category> getAllForCompany(int companyID) {
		List<Category> categories = new ArrayList<Category>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM category WHERE parent = 0 AND company = " + companyID);

			query.append(" ORDER BY name");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				categories.add(new Category(results.getInt(1), results.getString(2), results.getInt(3), results.getInt(4)));
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

		return categories;
	}

	public static List<Category> getSubCategories(int categoryID) {
		List<Category> categories = new ArrayList<Category>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM category WHERE parent = " + categoryID);

			query.append(" ORDER BY name");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				categories.add(new Category(results.getInt(1), results.getString(2), results.getInt(3), results.getInt(4)));
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

		return categories;
	}
}
