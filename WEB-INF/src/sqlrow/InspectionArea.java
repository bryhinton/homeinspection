package sqlrow;

import java.util.List;

public class InspectionArea extends SQLRow
{
	protected int inspection = 0;
	protected int areaID = 0;
	protected String name = "";

	protected List<InspectionAreaLineItem> inspectionAreaLineItems = null;
	protected List<CustomLineItem> customLineItems = null;
	protected Area area = null;

	public InspectionArea()
	{
		init();
	}

	public InspectionArea(int inID, int inInspection, int inAreaID, String inName)
	{
		setID(inID);
		setInspection(inInspection);
		setAreaID(inAreaID);
		setName(inName);

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
		return "inspectionarea";
	}

	@Override
	public String getXMLName()
	{
		return "inspectionarea";
	}

	@Override
	public void init()
	{
		try
		{
			updateList.put("Inspection", this.getClass().getMethod("getInspection", new Class[]
			{}));
			updateList.put("Area", this.getClass().getMethod("getAreaID", new Class[]
			{}));
			updateList.put("Name", this.getClass().getMethod("getName", new Class[]
			{}));

			loadList.put("Inspection", this.getClass().getMethod("setInspection", new Class[]
			{ Integer.TYPE }));
			loadList.put("Area", this.getClass().getMethod("setAreaID", new Class[]
			{ Integer.TYPE }));
			loadList.put("Name", this.getClass().getMethod("setName", new Class[]
			{ String.class }));

			nestedObjects.put("inspectionAreaLineItems", this.getClass().getMethod("getInspectionAreaLineItems", new Class[]{}));
			nestedObjects.put("customLineItems", this.getClass().getMethod("getCustomLineItems", new Class[]{}));
			nestedObjects.put("area", this.getClass().getMethod("getArea", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage().toString());
		}
	}

	public int getInspection()
	{
		return inspection;
	}

	public void setInspection(int inspection)
	{
		this.inspection = inspection;
	}

	public int getAreaID()
	{
		return areaID;
	}

	public void setAreaID(int areaID)
	{
		this.areaID = areaID;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void loadNestedObjects() {
		if(inspectionAreaLineItems == null) {
			inspectionAreaLineItems = InspectionAreaLineItems.getAllForArea(getID());
		}

		if(customLineItems == null) {
			customLineItems = CustomLineItems.getAllForArea(getID());
		}

		if(area == null) {
			area = Areas.getArea(getAreaID());
		}
	}

	public List<InspectionAreaLineItem> getInspectionAreaLineItems() {
		loadNestedObjects();
		return inspectionAreaLineItems;
	}

	public List<CustomLineItem> getCustomLineItems() {
		loadNestedObjects();
		return customLineItems;
	}

	public Area getArea() {
		loadNestedObjects();
		return area;
	}
}
