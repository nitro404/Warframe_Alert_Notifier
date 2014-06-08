package alert;

import java.io.*;
import java.text.*;
import java.util.*;
import exception.*;

public class Outbreak {
	
	private String m_guid;
	private String m_title;
	private String m_location;
	private String m_planet;
	private int m_credits;
	private int m_rewardAmount;
	private String m_rewardName;
	private RewardType m_rewardType;
	private Calendar m_publishTime;
	private boolean m_expired;

	public static DateFormat ALERT_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	public static DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
	
	public Outbreak(String guid, String title, String pubTime) throws IllegalArgumentException, NumberFormatException, ParseException, InvalidRewardFormatException {
		if(guid == null) { throw new IllegalArgumentException("outbreak guid cannot be null"); }
		if(title == null) { throw new IllegalArgumentException("outbreak title cannot be null"); }
		if(pubTime == null) { throw new IllegalArgumentException("outbreak publish time cannot be null"); }

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
			throw new InvalidRewardFormatException("expected outbreak mission data to have 2 parts, found " + missionData.length + " parts: " + msg);
		}
		
		m_rewardName = null;
		m_rewardType = RewardType.None;
		m_rewardAmount = 0;
		m_credits = 0;
		
		String temp = missionData[0].trim();
		if(temp.matches("[0-9]+[Cc][Rr]")) {
			m_credits = Integer.parseInt(temp.replaceAll("[^0-9]", ""));
		}
		else if(temp.matches("[0-9]+[Kk]")) {
			m_credits = Integer.parseInt(temp.replaceAll("[^0-9]", "")) * 1000;
		}
		else {
			String[] rewardData = temp.split("[()]");
			if(rewardData.length == 1) {
				if(temp.matches("[0-9]+x .+")) {
					String[] tempData = temp.split("[x]", 2);
					if(tempData.length != 2) {
						String msg = "";
						for(int i=0;i<tempData.length;i++) {
							msg += tempData[i] + (i < tempData.length - 1 ? ", " : "");
						}
						throw new InvalidRewardFormatException("expected outbreak reward data to have 2 parts, found " + tempData.length + " parts: " + msg);
					}
					m_rewardAmount = Integer.parseInt(tempData[0].replaceAll("[^0-9]", ""));
					m_rewardName = tempData[1].trim();
					m_rewardType = RewardType.Item;
					
				}
				else {
					boolean foundRewardType = false;
					String[] tempData = temp.split("[ ]");
					String tempRewardType = tempData[tempData.length - 1];
					for(int i=0;i<RewardType.values().length;i++) {
						if(i == RewardType.Item.ordinal() || i == RewardType.None.ordinal()) {
							continue;
						}
						
						if(tempRewardType.equalsIgnoreCase(RewardType.displayNames[i])) {
							foundRewardType = true;
							
							String tempRewardName = "";
							for(int j=0;j<tempData.length - 1;j++) {
								tempRewardName += tempData[j];
								if(j<tempData.length - 2) {
									tempRewardName += " ";
								}
							}
							
							m_rewardAmount = 1;
							m_rewardName = tempRewardName;
							m_rewardType = RewardType.values()[i];
						}
					}
					
					if(!foundRewardType) {
						m_rewardAmount = 1;
						m_rewardName = rewardData[0].trim();
						m_rewardType = RewardType.Item;
					}
				}
			}
			else if(rewardData.length == 2) {
				m_rewardAmount = 1;
				m_rewardName = rewardData[0].trim();
				m_rewardType = RewardType.parseFrom(rewardData[1]);
			}
			else {
				throw new InvalidRewardFormatException("invalid outbreak reward format, expected \"#x Name\" or \"Name (Type)\": " + missionData[0].trim());
			}
		}
		
		if(m_rewardName.matches(".*[Vv][Ss]\\..*")) {
			m_rewardName = "Unknown";
		}
		
		String[] planetData = missionData[1].trim().split("[()]");
		if(planetData.length != 2) {
			String msg = "";
			for(int i=0;i<planetData.length;i++) {
				msg += planetData[i] + (i < planetData.length - 1 ? ", " : "");
			}
			throw new InvalidRewardFormatException("expected outbreak planet data to have 2 parts, found " + planetData.length + " parts: " + msg);
		}
		if(planetData[0].trim().matches("^.+ [Ss][Pp][Aa][Ww][Nn].*$")) {
			m_location = planetData[0].trim().replaceFirst(".+ [Ss][Pp][Aa][Ww][Nn]", "").trim();
		}
		else {
			m_location = planetData[0].trim();
		}
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
	
	public int getCredits() {
		return m_credits;
	}
	
	public boolean hasReward() {
		return m_rewardName != null;
	}

	public String getRewardName() {
		return m_rewardName;
	}
	
	public RewardType getRewardType() {
		return m_rewardType;
	}
	
	public int getRewardAmount() {
		return m_rewardAmount;
	}
	
	public String getRewardInformation() {
		if(!hasReward()) { return ""; }
		
		return (m_rewardAmount > 1 ? m_rewardAmount + "x " : "") +  m_rewardName + (m_rewardType == RewardType.None || m_rewardType == RewardType.Item ? "" : " (" + m_rewardType.getDisplayName() + ")"); 
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
		
		out.println(LOG_DATE_FORMAT.format(m_publishTime.getTime()) + ": " + toString());
		
		return true;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Outbreak)) { return false; }
		Outbreak b = (Outbreak) o;
		return m_guid.equals(b.m_guid);
	}

	public String toString() {
		return m_location + " (" + m_planet + "): " + (m_credits > 0 ? m_credits + "cr" : "") + (hasReward() ? " - " + getRewardInformation() : "");
	}

}
