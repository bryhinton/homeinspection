package sqlrow;

import java.util.List;

public class LineItem extends SQLRow
{
	protected String name = "";
	protected int parent = 0;
	protected int area = 0;
	protected boolean isActive;
	protected List<LineItem> lineItems;

	public LineItem()
	{
		init();
	}

	public LineItem(int inID, String inName, int inParent, int inArea, boolean inActive)
	{
		setID(inID);
		setName(inName);
		setParent(inParent);
		setArea(inArea);
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
		return "lineitem";
	}

	@Override
	public String getXMLName()
	{
		return "lineitem";
	}

	@Override
	public void init()
	{
		try
		{
			updateList.put("name", this.getClass().getMethod("getName", new Class[]
			{}));
			updateList.put("parent", this.getClass().getMethod("getParent", new Class[]
			{}));
			updateList.put("area", this.getClass().getMethod("getArea", new Class[]
			{}));
			updateList.put("isactive", this.getClass().getMethod("isActive", new Class[]
			{}));

			loadList.put("name", this.getClass().getMethod("setName", new Class[]
			{ String.class }));
			loadList.put("parent", this.getClass().getMethod("setParent", new Class[]
			{ Integer.TYPE }));
			loadList.put("area", this.getClass().getMethod("setArea", new Class[]
			{ Integer.TYPE }));
			loadList.put("isactive", this.getClass().getMethod("setIsActive", new Class[]
			{ Boolean.TYPE }));

			nestedObjects.put("lineItems", this.getClass().getMethod("getLineItems", new Class[]{}));
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

	public int getParent()
	{
		return parent;
	}

	public void setParent(int parent)
	{
		this.parent = parent;
	}

	public int getArea()
	{
		return area;
	}

	public void setArea(int area)
	{
		this.area = area;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	}

	public void loadLineItems() {
		if(lineItems == null) {
			lineItems = LineItems.getChildren(getID(), false);
		}
	}

	public List<LineItem> getLineItems() {
		loadLineItems();
		return lineItems;
	}
}
