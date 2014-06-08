package alert;

public enum RewardType {
	Blueprint,
	Item,
	Aura,
	Mod,
	Resource,
	Event,
	None;
	
	final public static String[] displayNames = {
		"Blueprint",
		"Item",
		"Aura",
		"Mod",
		"Resource",
		"Event",
		"None"
	};
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(RewardType rewardtype) {
		return displayNames[rewardtype.ordinal()];
	}
	
	public static RewardType parseFrom(String data) {
		if(data == null) { return None; }
		String temp = data.trim();
		if(temp.length() == 0) { return None; }
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Item;
	}
}
