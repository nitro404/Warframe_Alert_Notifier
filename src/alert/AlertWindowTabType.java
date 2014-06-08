package alert;

import exception.*;

public enum AlertWindowTabType {
	ActiveAlerts,
	FilteredAlerts,
	Console;
	
	public static AlertWindowTabType parseFrom(String data) throws InvalidAlertWindowTabException {
		if(data == null) { throw new InvalidAlertWindowTabException("null alert window tab type"); }
		String temp = data.trim();
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name())) {
				return values()[i];
			}
		}
		
		throw new InvalidAlertWindowTabException("invalid alert window tab type: " + temp);
	}
}
