package alert;

public enum RewardCategory {
	ItemBlueprint,
//	Item,
	WeaponBlueprint,
	HelmetBlueprint,
	VaubanBlueprint,
	Aura,
	NightmareMod,
	Resource,
	ClanTech,
	Event,
	Custom;
	
	final public static String[] displayNames = {
		"Item Blueprint",
//		"Item",
		"Weapon Blueprint",
		"Helmet Blueprint",
		"Vauban Blueprint",
		"Aura",
		"Nightmare Mod",
		"Resource",
		"Clan Tech",
		"Event",
		"Custom"
	};
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(RewardCategory rewardCategory) {
		return displayNames[rewardCategory.ordinal()];
	}
	
	public static RewardCategory parseFrom(String data) {
		if(data == null) { return Custom; }
		String temp = data.trim();
		if(temp.length() == 0) { return Custom; }
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Custom;
	}
}
