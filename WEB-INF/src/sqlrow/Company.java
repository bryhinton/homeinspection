package sqlrow;

import sun.util.resources.CalendarData;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan Hinton
 * Date: 11/13/12
 * Time: 7:08 PM
 */

public class Company extends SQLRow
{
	protected String name = "";
	protected String phone = "";
	protected String email = "";
	protected String state = "";
	protected String logo = "";
	protected Timestamp trialExpiration;
	protected boolean isActive;
	protected boolean sendQuote;

	protected List<CompanyApp> companyApps = null;
	protected List<CompanyLicense> companyLicenses = null;
	protected List<Area> companyAreas = null;
	protected List<Category> companyCategories = null;

	public Company()
	{
		init();
	}

	public Company(int inID, String inName, String inPhone, String inEmail, String inState, String inLogo, Timestamp inTrialExpiration, boolean inActive, boolean inSendQuote)
	{
		setID(inID);
		setName(inName);
		setPhone(inPhone);
		setEmail(inEmail);
		setState(inState);
		setLogo(inLogo);
		setTrialExpiration(inTrialExpiration);
		setIsActive(inActive);
		setSendQuote(inSendQuote);

		init();
	}

	@Override
	public String getDatabase()
	{
		return "homeinspection";
	}

	@Override
	public String getTable()
	{
		return "company";
	}

	@Override
	public String getXMLName()
	{
		return "company";
	}

	@Override
	public void init()
	{
		try
		{
			updateList.put("name", this.getClass().getMethod("getName", new Class[]
					{}));
			updateList.put("phone", this.getClass().getMethod("getPhone", new Class[]
					{}));
			updateList.put("email", this.getClass().getMethod("getEmail", new Class[]
					{}));
			updateList.put("state", this.getClass().getMethod("getState", new Class[]
					{}));
			updateList.put("logo", this.getClass().getMethod("getLogo", new Class[]
					{}));
			updateList.put("trialExpiration", this.getClass().getMethod("getTrialExpiration", new Class[]
					{}));
			updateList.put("isActive", this.getClass().getMethod("isActive", new Class[]
					{}));
			updateList.put("sendQuote", this.getClass().getMethod("shouldSendQuote", new Class[]
					{}));

			loadList.put("name", this.getClass().getMethod("setName", new Class[]
					{ String.class }));
			loadList.put("phone", this.getClass().getMethod("setPhone", new Class[]
					{ String.class }));
			loadList.put("email", this.getClass().getMethod("setEmail", new Class[]
					{ String.class }));
			loadList.put("state", this.getClass().getMethod("setState", new Class[]
					{ String.class }));
			loadList.put("logo", this.getClass().getMethod("setLogo", new Class[]
					{ String.class }));
			loadList.put("trialExpiration", this.getClass().getMethod("setTrialExpiration", new Class[]
					{ Timestamp.class }));
			loadList.put("isActive", this.getClass().getMethod("setIsActive", new Class[]
					{ Boolean.TYPE }));
			loadList.put("sendQuote", this.getClass().getMethod("setSendQuote", new Class[]
					{ Boolean.TYPE }));

			nestedObjects.put("companyApps", this.getClass().getMethod("getCompanyApps", new Class[]{}));
			nestedObjects.put("companyLicenses", this.getClass().getMethod("getCompanyLicenses", new Class[]{}));
			nestedObjects.put("areas", this.getClass().getMethod("getCompanyAreas", new Class[]{}));
			nestedObjects.put("categories", this.getClass().getMethod("getCompanyCategories", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Timestamp getTrialExpiration() {
		return trialExpiration;
	}

	public void setTrialExpiration(Timestamp trialExpiration) {
		this.trialExpiration = trialExpiration;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	}

	public boolean hasAccess() {
		boolean onTrial = false;

		if(trialExpiration != null) {
			Calendar yesterday = Calendar.getInstance();
			yesterday.setTime(new Date());
			yesterday.add(Calendar.DAY_OF_MONTH, -1);

			if(trialExpiration.after(yesterday.getTime())) {
				onTrial = true;
			}
		}

		return isActive || onTrial;
	}

	public boolean shouldSendQuote() {
		return sendQuote;
	}

	public void setSendQuote(boolean sendQuote) {
		this.sendQuote = sendQuote;
	}

	public List<CompanyApp> getCompanyApps() {
		if(companyApps == null) {
			companyApps = CompanyApps.getAllForCompany(getID());
		}

		return companyApps;
	}

	public List<CompanyLicense> getCompanyLicenses() {
		if(companyLicenses == null) {
			companyLicenses = CompanyLicenses.getAllForCompany(getID());
		}

		return companyLicenses;
	}

	public List<Area> getCompanyAreas() {
		if(companyAreas == null) {
			companyAreas = Areas.getAllForCompany(getID(), false);
		}

		return companyAreas;
	}

	public List<Category> getCompanyCategories() {
		if(companyCategories == null) {
			companyCategories = Categories.getAllForCompany(getID());
		}

		return companyCategories;
	}
}
