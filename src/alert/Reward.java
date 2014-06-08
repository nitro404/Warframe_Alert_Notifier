package alert;

public class Reward {
	
	private String m_name;
	private RewardType m_type;
	private RewardCategory m_category;
	
	public static RewardCategory DEFAULT_CATEGORY = RewardCategory.Custom;
	
	public Reward(String name, RewardType type) {
		this(name, type, DEFAULT_CATEGORY);
	}
	
	public Reward(String name, RewardType type, RewardCategory category) {
		if(name == null) { throw new IllegalArgumentException("name cannot be null"); }
		String newName = name.trim();
		if(newName.length() == 0) { throw new IllegalArgumentException("name cannot be empty"); }
		
		m_name = newName;
		m_type = type;
		m_category = category;
	}
	
	public String getName() {
		return m_name;
	}
	
	public RewardType getType() {
		return m_type;
	}
	
	public RewardCategory getCategory() {
		return m_category;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Reward)) { return false; }
		Reward r = (Reward) o;
		return  m_name.equalsIgnoreCase(r.m_name); 
	}
	
	public String toString() {
		return m_name + " (" + m_type.toString() + ")";
	}
	
}
