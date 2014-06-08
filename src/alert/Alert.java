package alert;

import java.text.*;
import java.io.*;
import java.util.*;
import exception.*;

public class Alert {

	private String m_title;
	private String m_location;
	private String m_planet;
	private String m_missionType;
	private int m_credits;
	private String m_rewardName;
	private RewardType m_rewardType;
	private String m_description;
	private FactionType m_factionType;
	private Calendar m_publishTime;
	private Calendar m_expiryTime;

	public static DateFormat ALERT_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	public static DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");

	public Alert(String title, String description, String factionType, String pubTime, String expTime) throws IllegalArgumentException, NumberFormatException, ParseException, InvalidRewardFormatException {
		if(title == null) { throw new IllegalArgumentException("title cannot be null"); }
		if(description == null) { throw new IllegalArgumentException("description cannot be null"); }
		if(factionType == null) { throw new IllegalArgumentException("faction type cannot be null"); }
		if(pubTime == null) { throw new IllegalArgumentException("publish time cannot be null"); }
		if(expTime == null) { throw new IllegalArgumentException("expiry time cannot be null"); }

		m_title = title.trim();
		m_description = description.trim();
		try {
			m_factionType = FactionType.parseFrom(factionType);
		}
		catch(InvalidFactionException e) {
			m_factionType = FactionType.Unknown;
		}
		m_publishTime = Calendar.getInstance();
		m_expiryTime = Calendar.getInstance();
		m_publishTime.setTime(ALERT_DATE_FORMAT.parse(pubTime));
		m_expiryTime.setTime(ALERT_DATE_FORMAT.parse(expTime));

		String[] data = title.split(":", 2);
		if(data.length != 2) {
			throw new InvalidRewardFormatException("expected title data to have 2 parts, found " + data.length + " parts: " + data);
		}

		String[] planetData = data[0].trim().split("[()]");
		if(planetData.length != 2) {
			throw new InvalidRewardFormatException("expected planet data to have 2 parts, found " + planetData.length + " parts: " + data[0]);
		}
		m_location = planetData[0].trim();
		m_planet = planetData[1].trim();

		String[] missionData = data[1].split(" - ", 4);
		if(missionData.length < 3 || missionData.length > 4) {
			throw new InvalidRewardFormatException("expected mission data to have 3 or 4 parts, found " + missionData.length + " parts: " + data[1]);
		}
		m_missionType = missionData[0].trim();
		m_credits = Integer.parseInt(missionData[2].trim().replaceAll("[^0-9]", ""));
		
		m_rewardName = null;
		m_rewardType = RewardType.None;
		if(missionData.length > 3) {
			String[] rewardData = missionData[3].trim().split("[()]");
			if(rewardData.length == 1) {
				m_rewardName = rewardData[0].trim();
				m_rewardType = RewardType.Item;
			}
			else if(rewardData.length == 2) {
				m_rewardName = rewardData[0].trim();
				m_rewardType = RewardType.parseFrom(rewardData[1]);
			}
			else {
				throw new InvalidRewardFormatException("invalid reward format, expected \"Name\" or \"Name (Type)\": " + missionData[3].trim());
			}
		}
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

	public String getMissionType() {
		return m_missionType;
	}
	
	public int getCredits() {
		return m_credits;
	}

	public String getRewardName() {
		return m_rewardName;
	}
	
	public RewardType getRewardType() {
		return m_rewardType;
	}

	public String description() {
		return m_description;
	}

	public FactionType getFactionType() {
		return m_factionType;
	}

	public String getFactionName() {
		return m_factionType.toString();
	}

	public Calendar getPublishTime() {
		return m_publishTime;
	}

	public Calendar getExpiryTime() {
		return m_expiryTime;
	}

	public long getTotalTimeInMilliseconds() {
		return m_expiryTime.getTimeInMillis() - m_publishTime.getTimeInMillis();
	}

	public long getTimeLeftInMilliseconds() {
		return Calendar.getInstance().compareTo(m_expiryTime) >= 0 ? 0L : m_expiryTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
	}
	
	public long getTotalTimeHours() {
		return (getTotalTimeInMilliseconds() / 3600000) % 24;
	}

	public long getTotalTimeMinutes() {
		return (getTotalTimeInMilliseconds() / 60000) % 60;
	}

	public long getTotalTimeSeconds() {
		return (getTotalTimeInMilliseconds() / 1000) % 60;
	}

	public long getTimeLeftHours() {
		return (getTimeLeftInMilliseconds() / 3600000) % 24;
	}

	public long getTimeLeftMinutes() {
		return (getTimeLeftInMilliseconds() / 60000) % 60;
	}

	public long getTimeLeftSeconds() {
		return (getTimeLeftInMilliseconds() / 1000) % 60;
	}
	
	public String getTotalTimeString() {
		long hours = getTotalTimeHours();
		long minutes = getTotalTimeMinutes();
		long seconds = getTotalTimeSeconds();
		
		return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}
	
	public String getTimeLeftString() {
		long hours = getTimeLeftHours();
		long minutes = getTimeLeftMinutes();
		long seconds = getTimeLeftSeconds();
		
		return (hours == 0 ? "" : (hours < 10 ? "0" : "") + hours + ":") + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}
	
	public boolean writeTo(PrintWriter out) {
		if(out == null) { return false; }
		
		out.println(LOG_DATE_FORMAT.format(m_publishTime.getTime()) + ": " + m_location + " (" + m_planet + "): " + m_missionType + " - " + (getTotalTimeInMilliseconds() / 60000) + "m - " + m_credits + "cr" + (m_rewardName == null ? "" : " - " + m_rewardName + " (" + m_rewardType.toString() + ")"));
		
		return true;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Alert)) { return false; }
		Alert a = (Alert) o;
		if (m_title != null && a.m_title != null) {
			return m_publishTime.equals(a.m_publishTime) && m_title.equalsIgnoreCase(a.m_title);
		}
		return false;
	}

	public String toString() {
		return m_title;
	}

}
