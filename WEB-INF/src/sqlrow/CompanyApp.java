package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 6/26/13
 * Time: 8:32 PM
 */
public class CompanyApp extends SQLRow {
	protected int companyID;
	protected String app;

	public CompanyApp() {
		init();
	}

	public CompanyApp(int inID, int inCompanyID, String inApp) {
		setID(inID);
		setCompanyID(inCompanyID);
		setApp(inApp);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("company", this.getClass().getMethod("getCompanyID", new Class[]
					{}));
			updateList.put("app", this.getClass().getMethod("getApp", new Class[]
					{}));

			loadList.put("company", this.getClass().getMethod("setCompanyID", new Class[]
					{ Integer.TYPE }));
			loadList.put("app", this.getClass().getMethod("setApp", new Class[]
					{ String.class }));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getTable() {
		return "companyapp";
	}

	@Override
	public boolean hasInfo() {
		return false;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
}
