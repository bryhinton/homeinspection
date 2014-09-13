package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 2/7/13
 * Time: 7:42 PM
 */
public class QuoteItem  extends SQLRow {
	protected int inspectionAreaLineItemID;
	protected int customLineItemID;
	protected int taskID;
	protected boolean addOn;
	protected boolean active;

	protected Task task = null;

	public QuoteItem() {
		init();
	}

	public QuoteItem(int inInspectionAreaLineItemID, int inTaskID) {
		setInspectionAreaLineItemID(inInspectionAreaLineItemID);
		setTaskID(inTaskID);

		init();
	}

	public QuoteItem(int inID, int inInspectionAreaLineItemID, int inTaskID, boolean inAddOn, boolean inActive) {
		setID(inID);
		setInspectionAreaLineItemID(inInspectionAreaLineItemID);
		setTaskID(inTaskID);
		setAddOn(inAddOn);
		setActive(inActive);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("InspectionAreaLineItem", this.getClass().getMethod("getInspectionAreaLineItemID", new Class[]
					{}));
			updateList.put("Task", this.getClass().getMethod("getTaskID", new Class[]
					{}));
			updateList.put("AddOn", this.getClass().getMethod("isAddOn", new Class[]
					{}));
			updateList.put("Active", this.getClass().getMethod("isActive", new Class[]
					{}));

			loadList.put("InspectionAreaLineItem", this.getClass().getMethod("setInspectionAreaLineItemID", new Class[]
					{ Integer.TYPE }));
			loadList.put("Task", this.getClass().getMethod("setTaskID", new Class[]
					{ Integer.TYPE }));
			loadList.put("AddOn", this.getClass().getMethod("setAddOn", new Class[]
					{ Boolean.TYPE }));
			loadList.put("Active", this.getClass().getMethod("setActive", new Class[]
					{ Boolean.TYPE }));

			nestedObjects.put("task", this.getClass().getMethod("getTask", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getTable() {
		return "quoteitem";
	}

	public int getInspectionAreaLineItemID() {
		return inspectionAreaLineItemID;
	}

	public void setInspectionAreaLineItemID(int inspectionAreaLineItemID) {
		this.inspectionAreaLineItemID = inspectionAreaLineItemID;
	}

	public int getCustomLineItemID() {
		return customLineItemID;
	}

	public void setCustomLineItemID(int customLineItemID) {
		this.customLineItemID = customLineItemID;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public boolean isAddOn() {
		return addOn;
	}

	public void setAddOn(boolean addOn) {
		this.addOn = addOn;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void loadNestedObjects() {
		if(task == null) {
			task = Tasks.getByID(getTaskID());
		}
	}

	public Task getTask() {
		loadNestedObjects();
		return task;
	}
}
