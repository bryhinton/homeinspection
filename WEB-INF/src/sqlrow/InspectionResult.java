package sqlrow;

public enum InspectionResult
{
	PASS("Pass", "pass", "189413"),
	FAIL("Fail", "fail", "9C2819"),
	NA("N/A", "na", "BDB21C");

	protected String label;
	protected String value;
	protected String color;

	InspectionResult(String inLabel, String inValue, String inColor)
	{
		label = inLabel;
		value = inValue;
		color = inColor;
	}

	public static InspectionResult getByValue(String inValue) {
		for(InspectionResult result : values()) {
			if(result.value.equals(inValue)) {
				return result;
			}
		}

		return null;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public String getColor() {
		return color;
	}
}
