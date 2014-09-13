package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 6/26/13
 * Time: 8:23 PM
 */
public class Field extends SQLRow {
	protected String name;
	protected String type;
	protected int companyID;
	protected int displayOrder;
	protected boolean required;
	protected boolean enabled;
	protected boolean sum;

	public Field() {
		init();
	}

	public Field(int inID, String inName, String inType, int inCompanyID, int inDisplayOrder, boolean inRequired, boolean inEnabled, boolean inSum){
		setID(inID);
		setName(inName);
		setType(inType);
		setCompanyID(inCompanyID);
		setDisplayOrder(inDisplayOrder);
		setRequired(inRequired);
		setEnabled(inEnabled);
		setSum(inSum);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("Name", this.getClass().getMethod("getName", new Class[]
					{}));
			updateList.put("Type", this.getClass().getMethod("getType", new Class[]
					{}));
			updateList.put("Company", this.getClass().getMethod("getCompanyID", new Class[]
					{}));
			updateList.put("DisplayOrder", this.getClass().getMethod("getDisplayOrder", new Class[]
					{}));
			updateList.put("Required", this.getClass().getMethod("isRequired", new Class[]
					{}));
			updateList.put("Enabled", this.getClass().getMethod("isEnabled", new Class[]
					{}));
			updateList.put("Sum", this.getClass().getMethod("shouldSum", new Class[]
					{}));

			loadList.put("Name", this.getClass().getMethod("setName", new Class[]
					{ String.class }));
			loadList.put("Type", this.getClass().getMethod("setType", new Class[]
					{ String.class }));
			loadList.put("Company", this.getClass().getMethod("setCompanyID", new Class[]
					{ Integer.TYPE }));
			loadList.put("DisplayOrder", this.getClass().getMethod("setDisplayOrder", new Class[]
					{ Integer.TYPE }));
			loadList.put("Required", this.getClass().getMethod("setRequired", new Class[]
					{ Boolean.TYPE }));
			loadList.put("Enabled", this.getClass().getMethod("setEnabled", new Class[]
					{ Boolean.TYPE }));
			loadList.put("Sum", this.getClass().getMethod("setSum", new Class[]
					{ Boolean.TYPE }));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getTable() {
		return "fields";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean shouldSum() {
		return sum;
	}

	public void setSum(boolean sum) {
		this.sum = sum;
	}
}
