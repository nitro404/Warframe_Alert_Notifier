package alert;

import java.text.*;
import java.io.*;
import java.util.*;
import exception.*;

public class Invasion {
	
	private String m_guid;
	private String m_title;
	private String m_location;
	private String m_planet;
	private FactionType[] m_factionType;
	private int[] m_credits;
	private int[] m_rewardAmount;
	private String[] m_rewardName;
	private RewardType[] m_rewardType;
	private Calendar m_publishTime;
	private boolean m_expired;

	public static DateFormat ALERT_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	public static DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");

	public Invasion(String guid, String title, String pubTime) throws IllegalArgumentException, NumberFormatException, ParseException, InvalidRewardFormatException, InvalidFactionException {
		if(guid == null) { throw new IllegalArgumentException("invasion guid cannot be null"); }
		if(title == null) { throw new IllegalArgumentException("invasion title cannot be null"); }
		if(pubTime == null) { throw new IllegalArgumentException("invasion publish time cannot be null"); }
		
		m_guid = guid.trim();
		m_title = title.trim();
		m_publishTime = Calendar.getInstance();
		m_publishTime.setTime(ALERT_DATE_FORMAT.parse(pubTime));
		m_expired = false;
		
		String[] missionData = title.split(" - ", 2);
		if(missionData.length != 2) {
			String msg = "";
			for(int i=0;i<missionData.length;i++) {
				msg += missionData[i] + (i < missionData.length - 1 ? ", " : "");
			}
			throw new InvalidRewardFormatException("expected invasion mission data to 2 parts, found " + missionData.length + " parts: " + msg);
		}
		
		String[] versusData = missionData[0].split("[Vv][Ss][.]", 2);
		if(versusData.length != 2) {
			String msg = "";
			for(int i=0;i<versusData.length;i++) {
				msg += versusData[i] + (i < versusData.length - 1 ? ", " : "");
			}
			throw new InvalidRewardFormatException("expected invasion versus data to have 2 parts, found " + versusData.length + " parts: " + msg);
		}
		
		m_factionType = new FactionType[2];
		m_credits = new int[2];
		m_rewardAmount = new int[2];
		m_rewardName = new String[2];
		m_rewardType = new RewardType[2];
		for(int i=0;i<2;i++) {
			String[] rewardData = versusData[i].trim().split("[()]");
			if(rewardData.length != 2) {
				String msg = "";
				for(int j=0;j<rewardData.length;i++) {
					msg += rewardData[j] + (j < rewardData.length - 1 ? ", " : "");
				}
				throw new InvalidRewardFormatException("expected invasion reward data to have 2 parts, found " + rewardData.length + " parts: " + msg);
			}
			
			m_factionType[i] = FactionType.parseFrom(rewardData[0]);
			m_credits[i] = 0;
			m_rewardAmount[i] = 0;
			m_rewardName[i] = null;
			m_rewardType[i] = RewardType.None;
			
			String temp = rewardData[1].trim();
			if(temp.matches("[0-9]+[Cc][Rr]")) {
				m_credits[i] = Integer.parseInt(temp.replaceAll("[^0-9]", ""));
			}
			else if(temp.matches("[0-9]+[Kk]")) {
				m_credits[i] = Integer.parseInt(temp.replaceAll("[^0-9]", "")) * 1000;
			}
			else {
				String[] itemData = temp.split("[()]");
				if(itemData.length == 1) {
					if(temp.matches("[0-9]+x .+")) {
						String[] tempData = temp.split("[x]", 2);
						if(tempData.length != 2) {
							String msg = "";
							for(int j=0;j<tempData.length;j++) {
								msg += tempData[j] + (j < tempData.length - 1 ? ", " : "");
							}
							throw new InvalidRewardFormatException("expected invasion item data to have 2 parts, found " + tempData.length + " parts: " + msg);
						}
						m_rewardName[i] = tempData[1].trim();
						m_rewardType[i] = RewardType.Item;
						m_rewardAmount[i] = Integer.parseInt(tempData[0].replaceAll("[^0-9]", ""));
					}
					else {
						boolean foundRewardType = false;
						String[] tempData = temp.split("[ ]");
						String tempRewardType = tempData[tempData.length - 1];
						for(int j=0;j<RewardType.values().length;j++) {
							if(j == RewardType.Item.ordinal() || j == RewardType.None.ordinal()) {
								continue;
							}
							
							if(tempRewardType.equalsIgnoreCase(RewardType.displayNames[j])) {
								foundRewardType = true;
								
								String tempRewardName = "";
								for(int k=0;k<tempData.length - 1;k++) {
									tempRewardName += tempData[k];
									if(k<tempData.length - 2) {
										tempRewardName += " ";
									}
								}
								
								m_rewardAmount[i] = 1;
								m_rewardName[i] = tempRewardName;
								m_rewardType[i] = RewardType.values()[j];
							}
						}
						
						if(!foundRewardType) {
							m_rewardAmount[i] = 1;
							m_rewardName[i] = rewardData[0].trim();
							m_rewardType[i] = RewardType.Item;
						}
					}
				}
				else if(itemData.length == 2) {
					m_rewardAmount[i] = 1;
					m_rewardName[i] = itemData[0].trim();
					m_rewardType[i] = RewardType.parseFrom(itemData[1]);
				}
				else {
					throw new InvalidRewardFormatException("invalid invasion reward format, expected \"#cr\" or \"#K\" or \"#x Name\" or \"Name Type\" or \"Name (Type)\": " + missionData[0].trim());
				}
			}
		}

		String[] planetData = missionData[1].trim().split("[()]");
		if(planetData.length != 2) {
			String msg = "";
			for(int i=0;i<planetData.length;i++) {
				msg += planetData[i] + (i < planetData.length - 1 ? ", " : "");
			}
			throw new InvalidRewardFormatException("expected invasion planet data to have 2 parts, found " + planetData.length + " parts: " + msg);
		}
		m_location = planetData[0].trim();
		m_planet = planetData[1].trim();
	}
	
	public String getGUID() {
		return m_guid;
	}

	public String getTitle() {
		return m_title;
	}

	public String getLocation() {
		return m_location;
	}

	public String getPlanet() {
		return m_planet;
	}
	
	public int getCredits(int index) {
		if(index < 0 || index > 1) { return -1; }
		return m_credits[index];
	}
	
	public int getCredits(FactionType factionType) {
		int indexOfFaction = indexOfFaction(factionType);
		if(indexOfFaction < 0) { return -1; }
		
		return m_credits[indexOfFaction];
	}
	
	public boolean hasReward(int index) {
		if(index < 0 || index > 1) { return false; }
		return m_rewardName[index] != null;
	}
	
	public boolean hasReward(FactionType factionType) {
		int indexOfFaction = indexOfFaction(factionType);
		if(indexOfFaction < 0) { return false; }
		
		return m_rewardName[indexOfFaction] != null;
	}
	
	public String getRewardName(int index) {
		if(index < 0 || index > 1) { return null; }
		return m_rewardName[index];
	}
	
	public String getRewardName(FactionType factionType) {
		int indexOfFaction = indexOfFaction(factionType);
		if(indexOfFaction < 0) { return null; }
		
		return m_rewardName[indexOfFaction];
	}
	
	public RewardType getRewardType(int index) {
		if(index < 0 || index > 1) { return RewardType.None; }
		return m_rewardType[index];
	}
	
	public RewardType getRewardType(FactionType factionType) {
		int indexOfFaction = indexOfFaction(factionType);
		if(indexOfFaction < 0) { return RewardType.None; }
		
		return m_rewardType[indexOfFaction];
	}
	
	public int getRewardAmount(int index) {
		if(index < 0 || index > 1) { return -1; }
		return m_rewardAmount[index];
	}
	
	public int getRewardAmount(FactionType factionType) {
		int indexOfFaction = indexOfFaction(factionType);
		if(indexOfFaction < 0) { return -1; }
		
		return m_rewardAmount[indexOfFaction];
	}
	
	public String getRewardInformation(int index) {
		if(index < 0 || index > 1 || !hasReward(index)) { return ""; }
		
		return (m_rewardAmount[index] > 1 ? m_rewardAmount[index] + "x " : "") +  m_rewardName[index] + (m_rewardType[index] == RewardType.None || m_rewardType[index] == RewardType.Item ? "" : " (" + m_rewardType[index].getDisplayName() + ")");
	}
	
	public String getRewardInformation(FactionType factionType) {
		int indexOfFaction = indexOfFaction(factionType);
		if(indexOfFaction < 0) { return ""; }
		
		return getRewardInformation(indexOfFaction);
	}
	
	public int indexOfFaction(FactionType factionType) {
		if(factionType == FactionType.Unknown) { return -1; }
		
		for(int i=0;i<2;i++) {
			if(m_factionType[i] == factionType) {
				return i;
			}
		}
			
		return -1;
	}

	public FactionType getFactionType(int index) {
		if(index < 0 || index > 1) { return null; }
		return m_factionType[index];
	}

	public String getFactionName(int index) {
		if(index < 0 || index > 1) { return null; }
		return m_factionType[index].toString();
	}

	public Calendar getPublishTime() {
		return m_publishTime;
	}
	
	public boolean isExpired() {
		return m_expired;
	}
	
	public void setExpired(boolean expired) {
		m_expired = expired;
	}
	
	public boolean writeTo(PrintWriter out) {
		if(out == null) { return false; }
		
		out.print(LOG_DATE_FORMAT.format(m_publishTime.getTime()) + ": " + toString());
		
		return true;
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Invasion)) { return false; }
		Invasion i = (Invasion) o;
		return m_guid.equals(i.m_guid);
	}
	
	public String toString() {
		String invasion =  m_location + " (" + m_planet + "): ";
		
		for(int i=0;i<2;i++) {
			invasion += m_factionType[i].getDisplayName() + " (" + (m_credits[i] > 0 ? m_credits[i] + "cr" : getRewardInformation(i)) + ")" + (i < 1 ? " VS. " : "");
		}
		
		return invasion;
	}
	
}
