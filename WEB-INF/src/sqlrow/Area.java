package sqlrow;

import java.util.List;

public class Area extends SQLRow
{
	protected String name = "";
	protected int companyID;
	protected boolean isActive;
	protected List<LineItem> sections = null;

	public Area()
	{
		init();
	}

	public Area(int inID, String inName, int inCompanyID, boolean inActive)
	{
		setID(inID);
		setName(inName);
		setCompanyID(inCompanyID);
		setIsActive(inActive);

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
		return "area";
	}

	@Override
	public String getXMLName()
	{
		return "area";
	}

	@Override
	public void init()
	{
		try
		{
			updateList.put("name", this.getClass().getMethod("getName", new Class[]
			{}));
			updateList.put("company", this.getClass().getMethod("getCompanyID", new Class[]
			{}));
			updateList.put("isActive", this.getClass().getMethod("isActive", new Class[]
			{}));


			loadList.put("name", this.getClass().getMethod("setName", new Class[]
			{ String.class }));
			loadList.put("company", this.getClass().getMethod("setCompanyID", new Class[]
			{ Integer.TYPE }));
			loadList.put("isActive", this.getClass().getMethod("setIsActive", new Class[]
			{ Boolean.TYPE }));

			nestedObjects.put("sections", this.getClass().getMethod("getSections", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	}

	public void loadSections() {
		if(sections == null) {
			sections = LineItems.getTopLevelLineItems(getID(), false);
		}
	}

	public List<LineItem> getSections() {
		loadSections();
		return sections;
	}
}
