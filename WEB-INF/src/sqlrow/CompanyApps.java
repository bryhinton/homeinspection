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
 * Time: 8:32 PM
 */
public class CompanyApps {

	public static final String INSPECTION = "INSPECTION";
	public static final String TIMECARD = "TIMECARD";

	public static List<CompanyApp> getAllForCompany(int companyID)
	{
		List<CompanyApp> companyApps = new ArrayList<CompanyApp>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM companyapp WHERE company = " + companyID);
			results = stmt.executeQuery();

			while (results.next())
			{
				companyApps.add(new CompanyApp(results.getInt(1), results.getInt(2), results.getString(3)));
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

		return companyApps;
	}

	public static boolean contains(List<CompanyApp> apps, String appName) {
		for(CompanyApp app : apps) {
			if(app.getApp().equals(appName)) {
				return true;
			}
		}

		return false;
	}
}
