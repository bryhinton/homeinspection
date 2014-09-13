package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 2/18/13
 * Time: 10:28 AM
 */
public class QuoteItems {
	public static List<QuoteItem> getAllForInspection(int inspectionID)
	{
		List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT qi.* FROM quoteitem qi, inspectionarealineitem iali, inspectionarea ia WHERE qi.inspectionarealineitem = iali.id AND iali.inspectionarea = ia.id AND ia.inspection = " + inspectionID + " ORDER BY ia.name, qi.inspectionarealineitem");
			results = stmt.executeQuery();

			while (results.next())
			{
				quoteItems.add(new QuoteItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getBoolean(4), results.getBoolean(5)));
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

		return quoteItems;
	}

	public static List<QuoteItem> getAllForInspectionAreaLineItem(int id)
	{
		List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM quoteitem WHERE quoteitem.inspectionarealineitem = " + id);
			results = stmt.executeQuery();

			while (results.next())
			{
				quoteItems.add(new QuoteItem(results.getInt(1), results.getInt(2), results.getInt(3), results.getBoolean(4), results.getBoolean(5)));
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

		return quoteItems;
	}
}
