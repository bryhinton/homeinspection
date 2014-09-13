package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 6/27/13
 * Time: 8:15 PM
 */
public class Fields {
	public static List<Field> getAllForCompany(int companyID)
	{
		List<Field> fields = new ArrayList<Field>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM fields WHERE company = " + companyID + " ORDER BY displayorder");
			results = stmt.executeQuery();

			while (results.next())
			{
				fields.add(new Field(results.getInt(1), results.getString(2), results.getString(3), results.getInt(4), results.getInt(5), results.getBoolean(6), results.getBoolean(7), results.getBoolean(8)));
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

		return fields;
	}

	public static Field getField(int ID) {
		Field field = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM fields WHERE id = " + ID);
			results = stmt.executeQuery();

			while (results.next())
			{
				field = new Field(results.getInt(1), results.getString(2), results.getString(3), results.getInt(4), results.getInt(5), results.getBoolean(6), results.getBoolean(7), results.getBoolean(8));
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

		return field;
	}
}
