package sqlrow;

public class InspectionAreaLineItem extends SQLRow
{
	protected int inspectionArea = 0;
	protected int lineItemID = 0;
	protected String result = "";
	protected String comment = "";

	protected LineItem lineItem = null;
	protected String path = null;

	public InspectionAreaLineItem()
	{
		init();
	}

	public InspectionAreaLineItem(int inID, int inInspectionArea, int inLineItemID, String inResult, String inComment)
	{
		setID(inID);
		setInspectionArea(inInspectionArea);
		setLineItemID(inLineItemID);
		setResult(inResult);
		setComment(inComment);

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
		return "inspectionarealineitem";
	}

	@Override
	public String getXMLName()
	{
		return "inspectionarealineitem";
	}

	@Override
	public void init()
	{
		try
		{
			updateList.put("InspectionArea", this.getClass().getMethod("getInspectionArea", new Class[]
			{}));
			updateList.put("LineItem", this.getClass().getMethod("getLineItemID", new Class[]
			{}));
			updateList.put("Result", this.getClass().getMethod("getResult", new Class[]
			{}));
			updateList.put("Comment", this.getClass().getMethod("getComment", new Class[]
			{}));

			loadList.put("InspectionArea", this.getClass().getMethod("setInspectionArea", new Class[]
			{ Integer.TYPE }));
			loadList.put("LineItem", this.getClass().getMethod("setLineItemID", new Class[]
			{ Integer.TYPE }));
			loadList.put("Result", this.getClass().getMethod("setResult", new Class[]
			{ String.class }));
			loadList.put("Comment", this.getClass().getMethod("setComment", new Class[]
			{ String.class }));

			nestedObjects.put("lineItem", this.getClass().getMethod("getLineItem", new Class[]{}));
			nestedObjects.put("path", this.getClass().getMethod("getPath", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public int getInspectionArea()
	{
		return inspectionArea;
	}

	public void setInspectionArea(int inspectionArea)
	{
		this.inspectionArea = inspectionArea;
	}

	public int getLineItemID()
	{
		return lineItemID;
	}

	public void setLineItemID(int lineItemID)
	{
		this.lineItemID = lineItemID;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public LineItem getLineItem() {
		if(lineItem == null) {
			lineItem = LineItems.getLineItem(getLineItemID());
		}

		return lineItem;
	}

	public String getPath() {
		if(path == null) {
			Area area = Areas.getAreaFromInspectionArea(getInspectionArea());
			LineItem parent = LineItems.getLineItem(getLineItem().getParent());

			path = area.getName() + " > " + parent.getName() + " > ";
		}

		return path;
	}
}
