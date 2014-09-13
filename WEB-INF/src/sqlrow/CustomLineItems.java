package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/11/12
 * Time: 8:10 PM
 */
public class CustomLineItems {

	public static List<CustomLineItem> getAllForTopLevelLineItem(int inspectionAreaID, int topLevelID)
	{
		List<CustomLineItem> customLineItems = new ArrayList<CustomLineItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM customlineitem ");
			query.append("WHERE customlineitem.inspectionarea = " + inspectionAreaID + " AND customlineitem.parent = " + topLevelID);
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				customLineItems.add(new CustomLineItem(results.getInt(1), results.getString(2), results.getInt(3), results.getInt(4), results.getString(5), results.getString(6)));
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

		return customLineItems;
	}
}
