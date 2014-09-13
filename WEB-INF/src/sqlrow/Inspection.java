package sqlrow;

import screens.InspectionServlet;

import java.sql.Timestamp;
import java.util.List;

public class Inspection extends SQLRow
{
	protected String firstName = "";
	protected String lastName = "";
	protected String address = "";
	protected String city = "";
	protected String state = "";
	protected String ZIP = "";
	protected Timestamp date;
	protected String phone = "";
	protected String email = "";
	protected int techID = 0;
	protected int company = 0;
	protected boolean contacted = false;
	protected boolean sendQuote = false;
	protected String signature = "";

	protected List<InspectionArea> inspectionAreas = null;
	protected List<QuoteItem> quoteItems = null;
	protected List<InspectionAreaLineItem> failedLineItems = null;
	protected Technician technician = null;
	protected double standardTotal = 0.0;
	protected double memberTotal = 0.0;

	protected boolean totalsLoaded = false;

	public Inspection()
	{
		init();
	}

	public Inspection(int inID, String inFirstName, String inLastName, String inAddress, String inCity, String inState, String inZIP, Timestamp inDate, String inPhone, String inEmail, int inTechID, int inCompany, boolean inContacted, boolean inSendQuote, String inSignature)
	{
		setID(inID);
		setFirstName(inFirstName);
		setLastName(inLastName);
		setAddress(inAddress);
		setCity(inCity);
		setState(inState);
		setZIP(inZIP);
		setDate(inDate);
		setPhone(inPhone);
		setEmail(inEmail);
		setTechID(inTechID);
		setCompany(inCompany);
		setContacted(inContacted);
		setSendQuote(inSendQuote);
		setSignature(inSignature);

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
		return "inspection";
	}

	@Override
	public String getXMLName()
	{
		return "inspection";
	}

	@Override
	public String getDefaultSort() {
		return "DATE DESC";
	}

	@Override
	public void init()
	{
		try
		{
			updateList.put("FirstName", this.getClass().getMethod("getFirstName", new Class[]
			{}));
			updateList.put("LastName", this.getClass().getMethod("getLastName", new Class[]
			{}));
			updateList.put("Address", this.getClass().getMethod("getAddress", new Class[]
			{}));
			updateList.put("City", this.getClass().getMethod("getCity", new Class[]
			{}));
			updateList.put("State", this.getClass().getMethod("getState", new Class[]
			{}));
			updateList.put("ZIP", this.getClass().getMethod("getZIP", new Class[]
			{}));
			updateList.put("Date", this.getClass().getMethod("getDate", new Class[]
			{}));
			updateList.put("Phone", this.getClass().getMethod("getPhone", new Class[]
			{}));
			updateList.put("Email", this.getClass().getMethod("getEmail", new Class[]
			{}));
			updateList.put("Tech", this.getClass().getMethod("getTechID", new Class[]
			{}));
			updateList.put("Company", this.getClass().getMethod("getCompany", new Class[]
			{}));
			updateList.put("Contacted", this.getClass().getMethod("isContacted", new Class[]
			{}));
			updateList.put("SendQuote", this.getClass().getMethod("shouldSendQuote", new Class[]
			{}));
			updateList.put("Signature", this.getClass().getMethod("getSignature", new Class[]
			{}));

			loadList.put("FirstName", this.getClass().getMethod("setFirstName", new Class[]
			{ String.class }));
			loadList.put("LastName", this.getClass().getMethod("setLastName", new Class[]
			{ String.class }));
			loadList.put("Address", this.getClass().getMethod("setAddress", new Class[]
			{ String.class }));
			loadList.put("City", this.getClass().getMethod("setCity", new Class[]
			{ String.class }));
			loadList.put("State", this.getClass().getMethod("setState", new Class[]
			{ String.class }));
			loadList.put("ZIP", this.getClass().getMethod("setZIP", new Class[]
			{ String.class }));
			loadList.put("Date", this.getClass().getMethod("setDate", new Class[]
			{ Timestamp.class }));
			loadList.put("Phone", this.getClass().getMethod("setPhone", new Class[]
			{ String.class }));
			loadList.put("Email", this.getClass().getMethod("setEmail", new Class[]
			{ String.class }));
			loadList.put("Tech", this.getClass().getMethod("setTechID", new Class[]
			{ Integer.TYPE }));
			loadList.put("Company", this.getClass().getMethod("setCompany", new Class[]
			{ Integer.TYPE }));
			loadList.put("Contacted", this.getClass().getMethod("setContacted", new Class[]
			{ Boolean.TYPE }));
			loadList.put("SendQuote", this.getClass().getMethod("setSendQuote", new Class[]
			{ Boolean.TYPE }));
			loadList.put("Signature", this.getClass().getMethod("setSignature", new Class[]
			{ String.class }));

			nestedObjects.put("inspectionAreas", this.getClass().getMethod("getInspectionAreas", new Class[]{}));
			nestedObjects.put("quoteItems", this.getClass().getMethod("getQuoteItems", new Class[]{}));
			nestedObjects.put("failedLineItems", this.getClass().getMethod("getFailedLineItems", new Class[]{}));
			nestedObjects.put("technician", this.getClass().getMethod("getTechnician", new Class[]{}));
			nestedObjects.put("standardTotal", this.getClass().getMethod("getStandardTotal", new Class[]{}));
			nestedObjects.put("memberTotal", this.getClass().getMethod("getMemberTotal", new Class[]{}));

			actions.put("sendInspectionEmail", this.getClass().getMethod("sendInspectionEmail", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getZIP()
	{
		return ZIP;
	}

	public void setZIP(String zIP)
	{
		ZIP = zIP;
	}

	public Timestamp getDate()
	{
		return date;
	}

	public void setDate(Timestamp date)
	{
		this.date = date;
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

	public int getTechID() {
		return techID;
	}

	public void setTechID(int techID) {
		this.techID = techID;
	}

	public int getCompany() {
		return company;
	}

	public void setCompany(int company) {
		this.company = company;
	}

	public boolean isContacted() {
		return contacted;
	}

	public void setContacted(boolean contacted) {
		this.contacted = contacted;
	}

	public boolean shouldSendQuote() {
		return sendQuote;
	}

	public void setSendQuote(boolean sendQuote) {
		this.sendQuote = sendQuote;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public List<InspectionArea> getInspectionAreas() {
		if(inspectionAreas == null) {
			inspectionAreas = InspectionAreas.getAllForInspection(getID());
		}

		if(includedNested.contains("*")) {
			for(SQLRow sqlRow : inspectionAreas) {
				sqlRow.includedNested.add("*");
			}
		}

		return inspectionAreas;
	}

	public List<QuoteItem> getQuoteItems() {
		if(quoteItems == null) {
			quoteItems = QuoteItems.getAllForInspection(getID());
		}

		if(includedNested.contains("*")) {
			for(SQLRow sqlRow : quoteItems) {
				sqlRow.includedNested.add("*");
			}
		}

		return quoteItems;
	}

	public List<InspectionAreaLineItem> getFailedLineItems() {
		if(failedLineItems == null) {
			failedLineItems = InspectionAreaLineItems.getFailingLineItemsForInspection(getID());
		}

		if(includedNested.contains("*")) {
			for(SQLRow sqlRow : failedLineItems) {
				sqlRow.includedNested.add("*");
			}
		}

		return failedLineItems;
	}

	public Technician getTechnician() {
		if(technician == null) {
			technician = Technicians.getTechnician(getTechID());
		}

		if(includedNested.contains("*")) {
			technician.includedNested.add("*");
		}

		return technician;
	}

	public void loadTotals() {
		if(!totalsLoaded) {
			List<QuoteItem> allQuoteItems = getQuoteItems();

			for(QuoteItem quoteItem : allQuoteItems) {
				Task task = quoteItem.getTask();
				standardTotal += task.getStandard();
				memberTotal += task.getMember();
			}

			totalsLoaded = true;
		}
	}

	public double getStandardTotal() {
		loadTotals();
		return standardTotal;
	}

	public double getMemberTotal() {
		loadTotals();
		return memberTotal;
	}

	public String sendInspectionEmail() {
		InspectionServlet.sendEmail("http://inspection.servicetechapps.com/", this, false);

		return "Email sent successfully.";
	}
}
