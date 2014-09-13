package sqlrow;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 6/27/13
 * Time: 8:49 PM
 */
public enum FieldType {
	DATE("Date", "date"),
	NUMBER("Number", "number"),
	TEXT("Text", "text"),
	TIMECOUNT("Time Count", "timecount");

	String label;
	String value;

	FieldType(String inLabel, String inValue) {
		label = inLabel;
		value = inValue;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}
}
