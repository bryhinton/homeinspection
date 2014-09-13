package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 2/7/13
 * Time: 7:50 PM
 */
public class Task extends SQLRow {
	protected String name = "";
	protected String number = "";
	protected String description = "";
	protected int time = 0;
	protected double parts = 0.0;
	protected double standard = 0.0;
	protected double standardAddOn = 0.0;
	protected double member = 0.0;
	protected double memberAddOn = 0.0;
	protected int categoryID = 0;

	public Task() {
		init();
	}

	public Task(String inName, String inNumber, String inDescription, int inTime, double inParts, double inStandard, double inStandardAddOn, double inMember, double inMemberAddOn, int inCategoryID) {
		setName(inName);
		setNumber(inNumber);
		setDescription(inDescription);
		setCategoryID(inCategoryID);

		init();
	}

	public Task(int inID, String inName, String inNumber, String inDescription, int inTime, double inParts, double inStandard, double inStandardAddOn, double inMember, double inMemberAddOn, int inCategoryID) {
		setID(inID);
		setName(inName);
		setNumber(inNumber);
		setDescription(inDescription);
		setTime(inTime);
		setParts(inParts);
		setStandard(inStandard);
		setStandardAddOn(inStandardAddOn);
		setMember(inMember);
		setMemberAddOn(inMemberAddOn);
		setCategoryID(inCategoryID);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("name", this.getClass().getMethod("getName", new Class[]
					{}));
			updateList.put("number", this.getClass().getMethod("getNumber", new Class[]
					{}));
			updateList.put("description", this.getClass().getMethod("getDescription", new Class[]
					{}));
			updateList.put("time", this.getClass().getMethod("getTime", new Class[]
					{}));
			updateList.put("parts", this.getClass().getMethod("getParts", new Class[]
					{}));
			updateList.put("standard", this.getClass().getMethod("getStandard", new Class[]
					{}));
			updateList.put("standardAddOn", this.getClass().getMethod("getStandardAddOn", new Class[]
					{}));
			updateList.put("member", this.getClass().getMethod("getMember", new Class[]
					{}));
			updateList.put("memberAddOn", this.getClass().getMethod("getMemberAddOn", new Class[]
					{}));
			updateList.put("category", this.getClass().getMethod("getCategoryID", new Class[]
					{}));

			loadList.put("name", this.getClass().getMethod("setName", new Class[]
					{ String.class }));
			loadList.put("number", this.getClass().getMethod("setNumber", new Class[]
					{ String.class }));
			loadList.put("description", this.getClass().getMethod("setDescription", new Class[]
					{ String.class }));
			loadList.put("time", this.getClass().getMethod("setTime", new Class[]
					{ Integer.TYPE }));
			loadList.put("parts", this.getClass().getMethod("setParts", new Class[]
					{ Double.TYPE }));
			loadList.put("standard", this.getClass().getMethod("setStandard", new Class[]
					{ Double.TYPE }));
			loadList.put("standardAddOn", this.getClass().getMethod("setStandardAddOn", new Class[]
					{ Double.TYPE }));
			loadList.put("member", this.getClass().getMethod("setMember", new Class[]
					{ Double.TYPE }));
			loadList.put("memberAddOn", this.getClass().getMethod("setMemberAddOn", new Class[]
					{ Double.TYPE }));
			loadList.put("category", this.getClass().getMethod("setCategoryID", new Class[]
					{ Integer.TYPE }));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getTable() {
		return "task";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getParts() {
		return parts;
	}

	public void setParts(double parts) {
		this.parts = parts;
	}

	public double getStandard() {
		return standard;
	}

	public void setStandard(double standard) {
		this.standard = standard;
	}

	public double getStandardAddOn() {
		return standardAddOn;
	}

	public void setStandardAddOn(double standardAddOn) {
		this.standardAddOn = standardAddOn;
	}

	public double getMember() {
		return member;
	}

	public void setMember(double member) {
		this.member = member;
	}

	public double getMemberAddOn() {
		return memberAddOn;
	}

	public void setMemberAddOn(double memberAddOn) {
		this.memberAddOn = memberAddOn;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
}
