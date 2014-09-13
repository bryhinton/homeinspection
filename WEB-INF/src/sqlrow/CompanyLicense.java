package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/13/12
 * Time: 7:50 PM
 */
public class CompanyLicense extends SQLRow {

	protected int companyID;
	protected String key = "";
	protected int lastTech;

	public CompanyLicense() {
		init();
	}

	public CompanyLicense(int inCompanyID) {
		setCompanyID(inCompanyID);

		init();
	}

	public CompanyLicense(int inCompanyID, String inKey, int inLastTech) {
		setCompanyID(inCompanyID);
		setKey(inKey);
		setLastTech(inLastTech);

		init();
	}

	public CompanyLicense(int inID, int inCompanyID, String inKey, int inLastTech) {
		setID(inID);
		setCompanyID(inCompanyID);
		setKey(inKey);
		setLastTech(inLastTech);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("Company", this.getClass().getMethod("getCompanyID", new Class[]
					{}));
			updateList.put("PassKey", this.getClass().getMethod("getKey", new Class[]
					{}));
			updateList.put("LastTech", this.getClass().getMethod("getLastTech", new Class[]
					{}));

			loadList.put("Company", this.getClass().getMethod("setCompanyID", new Class[]
					{ Integer.TYPE }));
			loadList.put("PassKey", this.getClass().getMethod("setKey", new Class[]
					{ String.class }));
			loadList.put("LastTech", this.getClass().getMethod("setLastTech", new Class[]
					{ Integer.TYPE }));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getDatabase() {
		return "homeinspection";
	}

	@Override
	public String getTable() {
		return "companylicense";
	}

	@Override
	public String getXMLName() {
		return "companylicense";
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLastTech() {
		return lastTech;
	}

	public void setLastTech(int lastTech) {
		this.lastTech = lastTech;
	}
}
