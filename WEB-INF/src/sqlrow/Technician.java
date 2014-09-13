package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan Hinton
 * Date: 11/13/12
 * Time: 7:00 PM
 */
public class Technician extends SQLRow {

	protected String firstName;
	protected String lastName;
	protected int companyID;
	protected String username;
	protected String password;
	protected boolean isAdmin;
	protected boolean isSystemAdmin;
	protected boolean isActive;

	protected Company company = null;

	public Technician() {
		init();
	}

	public Technician(int inID, String inFirstName, String inLastName, int inCompanyID, String inUsername, String inPassword, boolean inAdmin, boolean inSystemAdmin, boolean inActive) {
		setID(inID);
		setFirstName(inFirstName);
		setLastName(inLastName);
		setCompanyID(inCompanyID);
		setUsername(inUsername);
		setPassword(inPassword);
		setIsAdmin(inAdmin);
		setIsSystemAdmin(inSystemAdmin);
		setIsActive(inActive);

		init();
	}

	public Technician(String inFirstName, String inLastName, int inCompanyID, String inUsername, String inPassword, boolean inAdmin, boolean inSystemAdmin, boolean inActive) {
		setFirstName(inFirstName);
		setLastName(inLastName);
		setCompanyID(inCompanyID);
		setUsername(inUsername);
		setPassword(inPassword);
		setIsAdmin(inAdmin);
		setIsSystemAdmin(inSystemAdmin);
		setIsActive(inActive);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("FirstName", this.getClass().getMethod("getFirstName", new Class[]
					{}));
			updateList.put("LastName", this.getClass().getMethod("getLastName", new Class[]
					{}));
			updateList.put("Company", this.getClass().getMethod("getCompanyID", new Class[]
					{}));
			updateList.put("Username", this.getClass().getMethod("getUsername", new Class[]
					{}));
			updateList.put("Password", this.getClass().getMethod("getPassword", new Class[]
					{}));
			updateList.put("IsAdmin", this.getClass().getMethod("isAdmin", new Class[]
					{}));
			updateList.put("IsSystemAdmin", this.getClass().getMethod("isSystemAdmin", new Class[]
					{}));
			updateList.put("IsActive", this.getClass().getMethod("isActive", new Class[]
					{}));

			loadList.put("FirstName", this.getClass().getMethod("setFirstName", new Class[]
					{ String.class }));
			loadList.put("LastName", this.getClass().getMethod("setLastName", new Class[]
					{ String.class }));
			loadList.put("Company", this.getClass().getMethod("setCompanyID", new Class[]
					{ Integer.TYPE }));
			loadList.put("Username", this.getClass().getMethod("setUsername", new Class[]
					{ String.class }));
			loadList.put("Password", this.getClass().getMethod("setPassword", new Class[]
					{ String.class }));
			loadList.put("IsAdmin", this.getClass().getMethod("setIsAdmin", new Class[]
					{ Boolean.TYPE }));
			loadList.put("IsSystemAdmin", this.getClass().getMethod("setIsSystemAdmin", new Class[]
					{ Boolean.TYPE }));
			loadList.put("IsActive", this.getClass().getMethod("setIsActive", new Class[]
					{ Boolean.TYPE }));

			nestedObjects.put("company", this.getClass().getMethod("getCompany", new Class[]{}));
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
		return "technician";
	}

	@Override
	public String getXMLName() {
		return "technician";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isActive && (isAdmin || isSystemAdmin);
	}

	public void setIsAdmin(boolean admin) {
		isAdmin = admin;
	}

	public boolean isSystemAdmin() {
		return isSystemAdmin;
	}

	public void setIsSystemAdmin(boolean systemAdmin) {
		isSystemAdmin = systemAdmin;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	}

	public Company getCompany() {
		if(company == null) {
			company = Companies.getCompany(companyID);
		}

		return company;
	}
}
