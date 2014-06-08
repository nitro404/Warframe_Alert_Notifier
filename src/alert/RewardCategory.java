package alert;

public enum RewardCategory {
	ItemBlueprint,
	WeaponBlueprint,
	HelmetSeries1Blueprint,
	HelmetSeries2Blueprint,
	VaubanBlueprint,
	Aura,
	NightmareMod,
	Resource,
	Event,
	Custom;
	
	final public static String[] displayNames = {
		"Item Blueprint",
		"Weapon Blueprint",
		"Helmet Series 1 Blueprint",
		"Helmet Series 2 Blueprint",
		"Vauban Blueprint",
		"Aura",
		"Nightmare Mod",
		"Resource",
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
