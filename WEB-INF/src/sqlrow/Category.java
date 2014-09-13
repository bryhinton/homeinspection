package sqlrow;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 2/7/13
 * Time: 7:47 PM
 */
public class Category extends SQLRow {
	protected String name = "";
	protected int parentID = 0;
	protected int companyID = 0;

	protected List<Category> subCategories = null;
	protected List<Task> tasks = null;

	public Category() {
		init();
	}

	public Category(String inName, int inParentID) {
		setName(inName);
		setParentID(inParentID);

		init();
	}

	public Category(int inID, String inName, int inParentID, int inCompanyID) {
		setID(inID);
		setName(inName);
		setParentID(inParentID);
		setCompanyID(inCompanyID);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("name", this.getClass().getMethod("getName", new Class[]
					{}));
			updateList.put("parent", this.getClass().getMethod("getParentID", new Class[]
					{}));
			updateList.put("company", this.getClass().getMethod("getCompanyID", new Class[]
					{}));

			loadList.put("name", this.getClass().getMethod("setName", new Class[]
					{ String.class }));
			loadList.put("parent", this.getClass().getMethod("setParentID", new Class[]
					{ Integer.TYPE }));
			loadList.put("company", this.getClass().getMethod("setCompanyID", new Class[]
					{ Integer.TYPE }));

			nestedObjects.put("tasks", this.getClass().getMethod("getTasks", new Class[]{}));
			nestedObjects.put("subCategories", this.getClass().getMethod("getSubCategories", new Class[]{}));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getTable() {
		return "category";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public List<Task> getTasks() {
		if(tasks == null) {
			tasks = Tasks.getAllForCategory(getID());
		}

		return tasks;
	}

	public List<Category> getSubCategories() {
		if(subCategories == null) {
			subCategories = Categories.getSubCategories(getID());
		}

		return subCategories;
	}
}
