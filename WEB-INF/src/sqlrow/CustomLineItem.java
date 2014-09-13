package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 11/25/12
 * Time: 7:35 AM
 */
public class CustomLineItem extends SQLRow {
	protected String name;
	protected int parent;
	protected int inspectionArea = 0;
	protected String result;
	protected String comment;

	public CustomLineItem()
	{
		init();
	}

	public CustomLineItem(int inID, String inName, int inParent, int inInspectionArea, String inResult, String inComment)
	{
		setID(inID);
		setName(inName);
		setParent(inParent);
		setInspectionArea(inInspectionArea);
		setResult(inResult);
		setComment(inComment);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("Name", this.getClass().getMethod("getName", new Class[]
					{}));
			updateList.put("Parent", this.getClass().getMethod("getParent", new Class[]
					{}));
			updateList.put("InspectionArea", this.getClass().getMethod("getInspectionArea", new Class[]
					{}));
			updateList.put("Result", this.getClass().getMethod("getResult", new Class[]
					{}));
			updateList.put("Comment", this.getClass().getMethod("getComment", new Class[]
					{}));

			loadList.put("Name", this.getClass().getMethod("setName", new Class[]
					{ String.class }));
			loadList.put("Parent", this.getClass().getMethod("setParent", new Class[]
					{ Integer.TYPE }));
			loadList.put("InspectionArea", this.getClass().getMethod("setInspectionArea", new Class[]
					{ Integer.TYPE }));
			loadList.put("Result", this.getClass().getMethod("setResult", new Class[]
					{ String.class }));
			loadList.put("Comment", this.getClass().getMethod("setComment", new Class[]
					{ String.class }));
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
		return "customlineitem";
	}

	@Override
	public String getXMLName() {
		return "customlineitem";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public int getInspectionArea() {
		return inspectionArea;
	}

	public void setInspectionArea(int inspectionArea) {
		this.inspectionArea = inspectionArea;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
