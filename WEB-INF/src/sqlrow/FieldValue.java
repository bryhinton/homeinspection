package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 6/26/13
 * Time: 8:32 PM
 */
public class FieldValue extends SQLRow {
	protected String type;

	protected int fieldID;
	protected Object value;

	public FieldValue() {
		init();
	}

	public FieldValue(int inID, int inFieldID, String inType, Object inValue, String inInfo) {
		setID(inID);
		setFieldID(inFieldID);
		setType(inType);
		setValue(inValue);
		resetInfo(inInfo);

		init();
	}

	@Override
	public void init() {
		try
		{
			updateList.put("Field", this.getClass().getMethod("getFieldID", new Class[]
					{}));
			updateList.put("Value", this.getClass().getMethod("getValue", new Class[]
					{}));

			loadList.put("Field", this.getClass().getMethod("setFieldID", new Class[]
					{ Integer.TYPE }));
			loadList.put("Value", this.getClass().getMethod("setValue", new Class[]
					{ Object.class }));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getTable() {
		if("DATE".equals(type)) {
			return "datevalues";
		}
		else if("STRING".equals(type)) {
			return "stringvalues";
		}
		else {
			return "numbervalues";
		}
	}

	@Override
	public boolean hasInfo() {
		return true;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getFieldID() {
		return fieldID;
	}

	public void setFieldID(int fieldID) {
		this.fieldID = fieldID;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
