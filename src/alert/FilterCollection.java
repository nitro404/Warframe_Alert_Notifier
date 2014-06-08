package alert;

import java.io.*;
import java.util.*;
import exception.*;

public class FilterCollection {
	
	private boolean m_filterCredits;
	private int m_filterCreditAmount;
	private ComparisonOperatorType m_filterCreditsComparisonOperator;
	private boolean m_filterAllRewards;
	private boolean[] m_rewardCategoryFilters;
	private Vector<Reward> m_rewardFilters;
	
	final public static boolean DEFAULT_FILTER_CREDITS = false;
	final public static int DEFAULT_FILTER_CREDIT_AMOUNT = 0;
	final public static ComparisonOperatorType DEFAULT_COMPARISON_OPERATOR = ComparisonOperatorType.defaultOperator;
	final public static boolean DEFUALT_FILTER_ALL_REWARDS = false;
	final public static boolean DEFUALT_FILTER_CATEGORIES = false;
	
	public FilterCollection() {
		m_rewardCategoryFilters = new boolean[RewardCategory.values().length];
		m_rewardFilters = new Vector<Reward>();
		reset();
	}
	
	public boolean getFilterCredits() {
		return m_filterCredits;
	}
	
	public void setFilterCredits(boolean filterCredits) {
		m_filterCredits = filterCredits;
	}
	
	public int getFilterCreditAmount() {
		return m_filterCreditAmount;
	}
	
	public boolean setFilterCreditAmount(int creditAmount) {
		if(creditAmount < 0) { return false; }
		
		m_filterCreditAmount = creditAmount;
		
		return true;
	}
	
	public ComparisonOperatorType getFilterCreditsComparisonOperator() {
		return m_filterCreditsComparisonOperator;
	}
	
	public void setFilterCreditsComparisonOperator(ComparisonOperatorType comparisonOperator) {
		m_filterCreditsComparisonOperator = comparisonOperator;
	}
	
	public boolean areAllRewardsFiltered() {
		return m_filterAllRewards;
	}
	
	public void setAllRewardsFiltered(boolean value) {
		m_filterAllRewards = value;
	}
	
	public void toggleAllRewardsFiltered() {
		m_filterAllRewards = !m_filterAllRewards;
	}
	
	public boolean isRewardCategoryFiltered(RewardCategory rewardCategory) {
		return m_rewardCategoryFilters[rewardCategory.ordinal()];
	}
	
	public boolean isRewardCategoryFiltered(int index) {
		return index >= 0 && index < RewardCategory.values().length ? m_rewardCategoryFilters[index] : false;
	}
	
	public void setRewardCategoryFiltered(RewardCategory rewardCategory, boolean value) {
		m_rewardCategoryFilters[rewardCategory.ordinal()] = value;
	}
	
	public boolean setRewardCategoryFiltered(int index, boolean value) {
		if(index < 0 || index >= RewardCategory.values().length) {
			return false;
		}
		
		m_rewardCategoryFilters[index] = value;
		
		return true;
	}
	
	public void toggleRewardCategoryFiltered(RewardCategory rewardCategory) {
		m_rewardCategoryFilters[rewardCategory.ordinal()] = !m_rewardCategoryFilters[rewardCategory.ordinal()];
	}
	
	public boolean toggleRewardCategoryFiltered(int index) {
		if(index < 0 || index >= RewardCategory.values().length) {
			return false;
		}
		
		m_rewardCategoryFilters[index] = !m_rewardCategoryFilters[index];
		
		return true;
	}
	
	public int numberOfRewardFilters() {
		return m_rewardFilters.size();
	}
	
	public Reward getRewardFilter(int index) {
		if(index < 0 || index >= m_rewardFilters.size()) { return null; }
		return m_rewardFilters.elementAt(index);
	}
	
	public Reward getRewardFilter(String name) {
		if(name == null) { return null; }
		String temp = name.trim();
		if(temp.length() == 0) { return null; }
		
		for(int i=0;i<m_rewardFilters.size();i++) {
			if(m_rewardFilters.elementAt(i).getName().equalsIgnoreCase(name)) {
				return m_rewardFilters.elementAt(i);
			}
		}
		return null;
	}
	
	public boolean hasRewardFilter(Reward r) {
		if(r == null) { return false; }
		return m_rewardFilters.contains(r);
	}
	
	public boolean hasRewardFilter(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_rewardFilters.size();i++) {
			if(m_rewardFilters.elementAt(i).getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfRewardFilter(Reward r) {
		if(r == null) { return -1; }
		return m_rewardFilters.indexOf(r);
	}
	
	public int indexOfRewardFilter(String name) {
		if(name == null) { return -1; }
		String temp = name.trim();
		if(temp.length() == 0) { return -1; }
		
		for(int i=0;i<m_rewardFilters.size();i++) {
			if(m_rewardFilters.elementAt(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addRewardFilter(Reward r) {
		if(r == null || r.getType() == RewardType.None || hasRewardFilter(r)) { return false; }
		
		m_rewardFilters.add(r);
		
		return true;
	}
	
	public boolean removeRewardFilter(int index) {
		if(index < 0 || index >= m_rewardFilters.size()) { return false; }
		m_rewardFilters.remove(index);
		return true;
	}
	
	public boolean removeRewardFilter(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_rewardFilters.size();i++) {
			if(m_rewardFilters.elementAt(i).getName().equalsIgnoreCase(name)) {
				m_rewardFilters.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public boolean removeRewardFilter(Reward r) {
		if(r == null) { return false; }
		return m_rewardFilters.remove(r);
	}
	
	public void clearRewardFilters() {
		m_rewardFilters.clear();
	}
	
	public void reset() {
		m_filterCredits = DEFAULT_FILTER_CREDITS;
		m_filterCreditAmount = DEFAULT_FILTER_CREDIT_AMOUNT;
		m_filterCreditsComparisonOperator = DEFAULT_COMPARISON_OPERATOR;
		m_filterAllRewards = DEFUALT_FILTER_ALL_REWARDS;
		for(int i=0;i<RewardCategory.values().length;i++) {
			m_rewardCategoryFilters[i] = DEFUALT_FILTER_CATEGORIES;
		}
		m_rewardFilters.clear();
	}
	
	public boolean filterAlert(Alert a) {
		if(a == null) { return false; }
		
		if(a.getTimeLeftInMilliseconds() <= 0) {
			return false;
		}
		
		if(m_filterCredits) {
			if(m_filterCreditsComparisonOperator == ComparisonOperatorType.Equal) {
				if(a.getCredits() == m_filterCreditAmount) {
					return true;
				}
			}
			else if(m_filterCreditsComparisonOperator == ComparisonOperatorType.NotEqual) {
				if(a.getCredits() != m_filterCreditAmount) {
					return true;
				}
			}
			else if(m_filterCreditsComparisonOperator == ComparisonOperatorType.LessThan) {
				if(a.getCredits() < m_filterCreditAmount) {
					return true;
				}
			}
			else if(m_filterCreditsComparisonOperator == ComparisonOperatorType.LessThanOrEqualTo) {
				if(a.getCredits() <= m_filterCreditAmount) {
					return true;
				}
			}
			else if(m_filterCreditsComparisonOperator == ComparisonOperatorType.GreaterThan) {
				if(a.getCredits() > m_filterCreditAmount) {
					return true;
				}
			}
			else if(m_filterCreditsComparisonOperator == ComparisonOperatorType.GreaterThanOrEqualTo) {
				if(a.getCredits() >= m_filterCreditAmount) {
					return true;
				}
			}
		}
		
		Reward r = AlertNotifier.rewards.getReward(a.getRewardName());
		
		if(r != null) {
			if(m_filterAllRewards) {
				return true;
			}
			
			for(int i=0;i<RewardCategory.values().length;i++) {
				if(m_rewardCategoryFilters[i] && r.getCategory() == RewardCategory.values()[i]) {
					return true;
				}
			}
			
			if(m_rewardFilters.contains(r)) {
				return true;
			}
		}
		else {
			if(a.getRewardName() != null) {
				AlertNotifier.console.writeLine("Alert found with reward that was not specified in reward list: " + a.getRewardName() + " (" + a.getRewardType() + ")");
			}
			return false;
		}
		
		return false;
	}
	
	private String[] parseParts(String data) {
		if(data == null) { return null; }
		
		String[] parts = data.replaceAll("([\n\r]+|^[\t ]+|[\t ]+$)", "").split("[\t ]*[:=][\t ]*", 2);
		if(parts.length != 2) { return null; }
		
		return parts;
	}
	
	public boolean loadFrom(String fileName) {
		if(fileName == null) { return false; }
		
		BufferedReader in = null;
		String input = null;
		String data = null;
		FilterType filterType = FilterType.Credits;
		boolean filterCredits = FilterCollection.DEFAULT_FILTER_CREDITS;
		int creditAmount = DEFAULT_FILTER_CREDIT_AMOUNT;
		ComparisonOperatorType comparisonOperator = DEFAULT_COMPARISON_OPERATOR;
		boolean filterAllRewards = DEFUALT_FILTER_ALL_REWARDS;
		boolean[] rewardCategoryFilters = new boolean[RewardCategory.values().length];
		for(int i=0;i<RewardCategory.values().length;i++) {
			rewardCategoryFilters[i] = DEFUALT_FILTER_CATEGORIES;
		}
		Vector<Reward> rewardFilters = new Vector<Reward>();
		
		try {
			in = new BufferedReader(new FileReader(fileName));
			
			while((input = in.readLine()) != null) {
				data = input.trim();
				
				if(data.length() == 0) { continue; }
				
				if(data.matches("^\\[.+\\]$")) {
					int startIndex = data.indexOf('[');
					int endIndex = data.indexOf(']');
					if(startIndex >= endIndex) { return false; }
					
					String tempType = data.substring(startIndex + 1, endIndex).trim();
					if(tempType.length() == 0) {
						AlertNotifier.console.writeLine("Empty filter type header encountered while parsing filter list file: " + AlertNotifier.settings.filterListFileName);
						return false;
					}
					
					try {
						filterType = FilterType.parseFrom(tempType);
					}
					catch(InvalidFilterException e) {
						AlertNotifier.console.writeLine("Invalid filter type header: \"" + tempType + "\" encountered while parsing filter list file: " + AlertNotifier.settings.filterListFileName);
						return false;
					}
					
					continue;
				}
				
				if(filterType == FilterType.Credits) {
					String parts[] = parseParts(data);
					if(parts == null) { return false; }
					
					if(parts[0].equalsIgnoreCase("Filter Credits")) {
						if(parts[1].equalsIgnoreCase("true")) {
							filterCredits = true;
						}
						else if(parts[1].equalsIgnoreCase("false")) {
							filterCredits = false;
						}
						else {
							AlertNotifier.console.writeLine("Invalid value: \"" + parts[1] + "\" encountered when parsing \"Filter Credits\" in file: " + AlertNotifier.settings.filterListFileName + " (expected true or false)");
						}
					}
					else if(parts[0].equalsIgnoreCase("Credit Amount")) {
						try {
							creditAmount = Integer.parseInt(parts[1]);
						}
						catch(NumberFormatException e) {
							AlertNotifier.console.writeLine("Invalid number: \"" + parts[1] + "\" encountered when parsing \"Credit Amount\" in file: " + AlertNotifier.settings.filterListFileName + " (expected integer value)");
						}
						
						if(creditAmount < 0) {
							creditAmount = DEFAULT_FILTER_CREDIT_AMOUNT;
							
							AlertNotifier.console.writeLine("Encountered negative credit filter value when parsing \"Credit Amount\" in file: " + AlertNotifier.settings.filterListFileName + " (expected positive integer value)");
						}
					}
					else if(parts[0].equalsIgnoreCase("Comparison Operator")) {
						try {
							comparisonOperator = ComparisonOperatorType.parseFrom(parts[1]);
						}
						catch(InvalidComparisonOperatorException e) {
							AlertNotifier.console.writeLine("Invalid comparison operator: \"" + parts[1] + "\" encountered when parsing \"Comparison Operator\" in file: " + AlertNotifier.settings.filterListFileName + " (expected one of: == != < <= > >=)");
						}
					}
					else {
						AlertNotifier.console.writeLine("Invalid token: \"" + parts[0] + "\" encountered when parsing credit filters in file: " + AlertNotifier.settings.filterListFileName);
					}
				}
				else if(filterType == FilterType.Categories) {
					String parts[] = parseParts(data);
					if(parts == null) { return false; }
					
					if(parts[0].equalsIgnoreCase("Filter All Rewards")) {
						if(parts[1].equalsIgnoreCase("true")) {
							filterAllRewards = true;
						}
						else if(parts[1].equalsIgnoreCase("false")) {
							filterAllRewards = false;
						}
						else {
							AlertNotifier.console.writeLine("Invalid value: \"" + parts[1] + "\" encountered when parsing \"Filter All Rewards\" in file: " + AlertNotifier.settings.filterListFileName + " (expected true or false)");
						}
					}
					else {
						String[] categoryData = parts[0].split("[ ]+", 2);
						
						if(categoryData.length == 2 && categoryData[0].equalsIgnoreCase("Filter")) {
							RewardCategory rewardCategory = RewardCategory.parseFrom(categoryData[1]);
							
							if(parts[1].equalsIgnoreCase("true")) {
								rewardCategoryFilters[rewardCategory.ordinal()] = true;
							}
							else if(parts[1].equalsIgnoreCase("false")) {
								rewardCategoryFilters[rewardCategory.ordinal()] = false;
							}
							else {
								AlertNotifier.console.writeLine("Invalid boolean value: \"" + parts[1] + "\" encountered when parsing \"Filter " + RewardCategory.displayNames[rewardCategory.ordinal()] + "\" in file: " + AlertNotifier.settings.filterListFileName + " (expected true or false)");
							}
						}
						else {
							AlertNotifier.console.writeLine("Invalid category filter: \"" + parts[0] + "\" encountered when parsing category filters in file: " + AlertNotifier.settings.filterListFileName);
						}
					}
				}
				else if(filterType == FilterType.Rewards) {
					Reward r = AlertNotifier.rewards.getReward(data);
					if(r == null) {
						AlertNotifier.console.writeLine("Encountered non-existant reward filter: \"" + data + "\" (does not exist in the reward list) while parsing file: " + AlertNotifier.settings.filterListFileName);
						continue;
					}
					
					rewardFilters.add(r);
				}
			}
			
			m_filterCredits = filterCredits;
			m_filterCreditAmount = creditAmount;
			m_filterCreditsComparisonOperator = comparisonOperator;
			m_filterAllRewards = filterAllRewards;
			m_rewardCategoryFilters = rewardCategoryFilters;
			m_rewardFilters = rewardFilters;
			
			in.close();
		}
		catch(IOException e) {
			return false;
		}
		
		return true;
	}
	
	public boolean writeTo(String fileName) {
		if(fileName == null) { return false; }
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new FileWriter(fileName));
			
			writeTo(out);
			
			out.close();
		}
		catch(IOException e) {
			return false;
		}
		
		return true;
	}
	
	public boolean writeTo(PrintWriter out) {
		if(out == null) { return false; }
		
		out.println("[" + FilterType.Credits.name() + "]");
		out.println("Filter Credits: " + m_filterCredits);
		out.println("Credit Amount: " + m_filterCreditAmount);
		out.println("Comparison Operator: " + ComparisonOperatorType.toString(m_filterCreditsComparisonOperator));
		
		out.println();
		
		out.println("[" + FilterType.Categories.name() + "]");
		out.println("Filter All Rewards: " + m_filterAllRewards);
		for(int i=0;i<RewardCategory.values().length;i++) {
			out.println("Filter " + RewardCategory.displayNames[i] + ": " + m_rewardCategoryFilters[i]);
		}
		
		out.println();
		
		out.println("[" + FilterType.Rewards.name() + "]");
		for(int i=0;i<m_rewardFilters.size();i++) {
			out.println(m_rewardFilters.elementAt(i).getName());
		}
		
		return true;
	}
	
}
