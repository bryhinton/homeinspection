package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 2/16/13
 * Time: 2:16 PM
 */
public class Tasks {
	public static Task getByNumber(String taskNumber, int companyID)
	{
		Task task = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT task.* FROM task, category WHERE number = '" + taskNumber + "' AND task.category = category.id AND category.company = " + companyID);
			results = stmt.executeQuery();

			while (results.next())
			{
				task = new Task(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getInt(5), results.getDouble(6), results.getDouble(7), results.getDouble(8), results.getDouble(9), results.getDouble(10), results.getInt(11));
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

		return task;
	}

	public static Task getByID(int id)
	{
		Task task = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM task WHERE id = '" + id + "'");
			results = stmt.executeQuery();

			while (results.next())
			{
				task = new Task(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getInt(5), results.getDouble(6), results.getDouble(7), results.getDouble(8), results.getDouble(9), results.getDouble(10), results.getInt(11));
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

		return task;
	}

	public static List<Task> getAllForInspection(int inspectionID)
	{
		List<Task> tasks = new ArrayList<Task>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT task.* FROM task, quoteitem, inspectionarealineitem, inspectionarea " +
					"WHERE task.id = quoteitem.task AND quoteitem.inspectionarealineitem = inspectionarealineitem.id AND inspectionarealineitem.inspectionarea = inspectionarea.id AND inspectionarea.inspection = " + inspectionID);
			results = stmt.executeQuery();

			while (results.next())
			{
				tasks.add(new Task(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getInt(5), results.getDouble(6), results.getDouble(7), results.getDouble(8), results.getDouble(9), results.getDouble(10), results.getInt(11)));
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

		return tasks;
	}

	public static List<Task> getAllForCategory(int categoryID)
	{
		List<Task> tasks = new ArrayList<Task>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM task WHERE category = " + categoryID);
			results = stmt.executeQuery();

			while (results.next())
			{
				tasks.add(new Task(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getInt(5), results.getDouble(6), results.getDouble(7), results.getDouble(8), results.getDouble(9), results.getDouble(10), results.getInt(11)));
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

		return tasks;
	}
}
