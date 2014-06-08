package alert;

import exception.*;

public enum FilterType {
	Credits,
	Rewards;
	
	public static FilterType parseFrom(String data) throws InvalidFilterException {
		if(data == null) { throw new InvalidFilterException("filter type cannot be null"); }
		String temp = data.trim();
		if(temp.length() == 0) { throw new InvalidFilterException("filter type cannot be empty"); }
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name())) {
				return values()[i];
			}
		}
		
		throw new InvalidFilterException("filter type does not exist: " + temp);
	}
}
