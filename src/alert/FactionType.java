package alert;

import exception.*;

public enum FactionType {
	Corpus,
	Grineer,
	Infested,
	Unknown;
	
	public static FactionType parseFrom(String data) throws InvalidFactionException {
		if(data == null) { throw new InvalidFactionException("null faction"); }
		String temp = data.trim();
		
		if(temp.equalsIgnoreCase("FC_CORPUS")) {
			return Corpus;
		}
		else if(temp.equalsIgnoreCase("FC_GRINEER")) {
			return Grineer;
		}
		else if(temp.equalsIgnoreCase("FC_INFESTATION")) {
			return Infested;
		}
		
		throw new InvalidFactionException("invalid faction: " + temp);
	}
}
