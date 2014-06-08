package alert;

public enum RewardType {
	Blueprint,
	Aura,
	Mod,
	Resource,
	Item,
	Event,
	None;
	
	public static RewardType parseFrom(String data) {
		if(data == null) { return None; }
		String temp = data.trim();
		if(temp.length() == 0) { return None; }
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name())) {
				return values()[i];
			}
		}
		return Item;
	}
}
