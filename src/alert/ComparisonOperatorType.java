package alert;

import exception.*;

public enum ComparisonOperatorType {
	Equal,
	NotEqual,
	LessThan,
	LessThanOrEqualTo,
	GreaterThan,
	GreaterThanOrEqualTo;
	
	final public static String[] operatorValues = { "==", "!=", "<", "<=", ">", ">=" };
	
	final public static ComparisonOperatorType defaultOperator = GreaterThanOrEqualTo;
	
	public static ComparisonOperatorType parseFrom(String data) throws InvalidComparisonOperatorException {
		if(data == null) { throw new InvalidComparisonOperatorException("null comparison operator type"); }
		String temp = data.trim();
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(operatorValues[i])) {
				return values()[i];
			}
		}
		
		if(temp.equalsIgnoreCase("Not Equal") || temp.equalsIgnoreCase("<>")) {
			return NotEqual;
		}
		else if(temp.equalsIgnoreCase("Less Than")) {
			return LessThan;
		}
		else if(temp.equalsIgnoreCase("Less Than Or Equal To")) {
			return LessThanOrEqualTo;
		}
		else if(temp.equalsIgnoreCase("Greater Than")) {
			return GreaterThan;
		}
		else if(temp.equalsIgnoreCase("Greather Than Or Equal To")) {
			return GreaterThanOrEqualTo;
		}
		
		throw new InvalidComparisonOperatorException("invalid comparison operator type: " + temp);
	}
	
	public static String toString(ComparisonOperatorType comparisonOperator) {
		return operatorValues[comparisonOperator.ordinal()];
	}
	
}
