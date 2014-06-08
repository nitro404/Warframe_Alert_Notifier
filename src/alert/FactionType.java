package alert;

import exception.*;

public enum FactionType {
	Corpus,
	Grineer,
	Infested,
	Unknown;
	
	final public static String[] displayNames = {
		"Corpus",
		"Grineer",
		"Infested",
		"Unknown"
	};
	
	final public static String[] xmlNames = {
		"FC_CORPUS",
		"FC_GRINEER",
		"FC_INFESTATION"
	};
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(FactionType factiontype) {
		return displayNames[factiontype.ordinal()];
	}
	
	public String getXMLName() {
		return xmlNames[ordinal()];
	}
	
	public static String getXMLName(FactionType factiontype) {
		return xmlNames[factiontype.ordinal()];
	}
	
	public static FactionType parseFrom(String data) throws InvalidFactionException {
		if(data == null) { throw new InvalidFactionException("null faction"); }
		String temp = data.trim();
		
		for(int i=0;i<xmlNames.length;i++) {
			if(temp.equalsIgnoreCase(xmlNames[i]) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		
		throw new InvalidFactionException("invalid faction: " + temp);
	}
}
