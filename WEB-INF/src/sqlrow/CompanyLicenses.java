package sqlrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/13/12
 * Time: 7:54 PM
 */
public class CompanyLicenses {

	public static CompanyLicense getByKey(int companyID, String key)
	{
		CompanyLicense companyLicense = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM companylicense WHERE company = '" + companyID + "' AND passkey = '" + key + "'");
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				companyLicense = new CompanyLicense(results.getInt(1), results.getInt(2), results.getString(3), results.getInt(4));
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

		return companyLicense;
	}

	public static CompanyLicense getByID(int id)
	{
		CompanyLicense companyLicense = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM companylicense WHERE id = '" + id + "'");
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				companyLicense = new CompanyLicense(results.getInt(1), results.getInt(2), results.getString(3), results.getInt(4));
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

		return companyLicense;
	}

	public static CompanyLicense getNewLicenseForCompany(int companyID) {
		CompanyLicense companyLicense = null;

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM companylicense WHERE company = " + companyID + " AND passkey = '' LIMIT 1");
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				companyLicense = new CompanyLicense(results.getInt(1), results.getInt(2), results.getString(3), results.getInt(4));
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

		return companyLicense;
	}

	public static List<CompanyLicense> getAllForCompany(int companyID) {
		List<CompanyLicense> companyLicenses = new ArrayList<CompanyLicense>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM companylicense WHERE company = " + companyID);
			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while (results.next())
			{
				companyLicenses.add(new CompanyLicense(results.getInt(1), results.getInt(2), results.getString(3), results.getInt(4)));
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

		return companyLicenses;
	}
}
