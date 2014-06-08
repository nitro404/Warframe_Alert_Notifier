package settings;

import java.awt.*;
import exception.*;
import alert.*;
import variable.*;
import utilities.*;

public class SettingsManager {
	
	private VariableSystem m_settings;
	
	public String settingsFileName = defaultSettingsFileName;
	public boolean autoSaveSettings;
	public String rewardListFileName;
	public String filterListFileName;
	public String soundListFileName;
	public String soundDirectoryName;
	public String alertsLogFileName;
	public String filteredAlertsLogFileName;
	public String consoleLogFileName;
	public String logDirectoryName;
	public boolean autoClearExpiredActiveAlerts;
	public boolean autoClearExpiredFilteredAlerts;
	public boolean autoSaveRewardList;
	public boolean autoSaveFilterList;
	public boolean autoSaveSoundList;
	public boolean logAlerts;
	public boolean logFilteredAlerts;
	public boolean logConsole;
	public String alertURL;
	public int windowPositionX;
	public int windowPositionY;
	public int windowWidth;
	public int windowHeight;
	public AlertWindowTabType currentAlertWindowTab;
	public boolean autoScrollConsole;
	public int maxConsoleHistory;
	public long alertUpdateFrequency;
	public boolean soundsEnabled;
	public float volume;
	
	final public static String defaultSettingsFileName = "Settings.cfg";
	final public static boolean defaultAutoSaveSettings = true;
	final public static String defaultRewardListFileName = "Rewards.cfg";
	final public static String defaultFilterListFileName = "Filters.cfg";
	final public static String defaultSoundListFileName = "Sounds.cfg";
	final public static String defaultSoundDirectoryName = "Sounds";
	final public static String defaultAlertsLogFileName = "Alerts.log";
	final public static String defaultFilteredAlertsLogFileName = "Filtered Alerts.log";
	final public static String defaultConsoleLogFileName = "Console.log";
	final public static String defaultLogDirectoryName = "Logs";
	final public static boolean defaultAutoClearExpiredActiveAlerts = false;
	final public static boolean defaultAutoClearExpiredFilteredAlerts = false;
	final public static boolean defaultAutoSaveRewardList = true;
	final public static boolean defaultAutoSaveFilterList = true;
	final public static boolean defaultAutoSaveSoundList = true;
	final public static boolean defaultLogAlerts = false;
	final public static boolean defaultLogFilteredAlerts = false;
	final public static boolean defaultLogConsole = false;
	final public static String defaultAlertURL = "http://content.warframe.com/dynamic/rss.php";
	final public static int defaultWindowPositionX = 0;
	final public static int defaultWindowPositionY = 0;
	final public static int defaultWindowWidth = 800;
	final public static int defaultWindowHeight = 600;
	final public static AlertWindowTabType defaultAlertWindowTab = AlertWindowTabType.ActiveAlerts;
	final public static boolean defaultAutoScrollConsole = true;
	final public static int defaultMaxConsoleHistory = 512;
	final public static long minimumAlertUpdateFrequency = 30000L;
	final public static long defaultAlertUpdateFrequency = 120000L;
	final public static boolean defaultSoundsEnabled = true;
	final public static float defaultVolume = -10.0f;
	final public static float minimumVolume = -20.0f;
	final public static float maximumVolume = 5.0f;
	
	public SettingsManager() {
		m_settings = new VariableSystem();
		reset();
	}
	
	public void reset() {
		autoSaveSettings = defaultAutoSaveSettings;
		rewardListFileName = defaultRewardListFileName;
		filterListFileName = defaultFilterListFileName;
		soundListFileName = defaultSoundListFileName;
		soundDirectoryName = defaultSoundDirectoryName;
		alertsLogFileName = defaultAlertsLogFileName;
		filteredAlertsLogFileName = defaultFilteredAlertsLogFileName;
		consoleLogFileName = defaultConsoleLogFileName;
		logDirectoryName = defaultLogDirectoryName;
		autoClearExpiredActiveAlerts = defaultAutoClearExpiredActiveAlerts;
		autoClearExpiredFilteredAlerts = defaultAutoClearExpiredFilteredAlerts;
		autoSaveRewardList = defaultAutoSaveRewardList;
		autoSaveFilterList = defaultAutoSaveFilterList;
		autoSaveSoundList = defaultAutoSaveSoundList;
		logAlerts = defaultLogAlerts;
		logFilteredAlerts = defaultLogFilteredAlerts;
		logConsole = defaultLogConsole;
		alertURL = defaultAlertURL;
		windowPositionX = defaultWindowPositionX;
		windowPositionY = defaultWindowPositionY;
		windowWidth = defaultWindowWidth;
		windowHeight = defaultWindowHeight;
		currentAlertWindowTab = defaultAlertWindowTab;
		autoScrollConsole = defaultAutoScrollConsole;
		maxConsoleHistory = defaultMaxConsoleHistory;
		alertUpdateFrequency = defaultAlertUpdateFrequency;
		soundsEnabled = defaultSoundsEnabled;
		volume = defaultVolume;
	}
	
	public boolean load() { return loadFrom(settingsFileName); }
	
	public boolean save() { return saveTo(settingsFileName); }
	
	public boolean loadFrom(String fileName) {
		VariableSystem variables = VariableSystem.readFrom(fileName);
		if(variables == null) { return false; }
		
		m_settings = variables;
		
		int tempInt = -1;
		long tempLong = -1L;
		float tempFloat = -1.0f;
		String tempString = null;
		Point tempPoint = null;
		Dimension tempDimension = null;
		AlertWindowTabType tempAlertWindowTab;
		
		// parse auto-save settings value
		tempString = m_settings.getValue("Auto-Save Settings", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoSaveSettings = true;
			}
			else if(tempString.equals("false")) {
				autoSaveSettings = false;
			}
		}
		
		// parse reward list file name
		tempString = m_settings.getValue("Reward List File Name", "Paths");
		if(tempString != null) {
			rewardListFileName = tempString;
		}
		
		// parse filter list file name
		tempString = m_settings.getValue("Filter List File Name", "Paths");
		if(tempString != null) {
			filterListFileName = tempString;
		}
		
		// parse sound list file name
		tempString = m_settings.getValue("Sound List File Name", "Paths");
		if(tempString != null) {
			soundListFileName = tempString;
		}
		
		// parse sound directory name
		tempString = m_settings.getValue("Sound Directory Name", "Paths");
		if(tempString != null) {
			soundDirectoryName = tempString;
		}
		
		// parse alerts log file name
		tempString = m_settings.getValue("Alerts Log File Name", "Paths");
		if(tempString != null) {
			alertsLogFileName = tempString;
		}
		
		// parse filtered alerts log file name
		tempString = m_settings.getValue("Filtered Alerts Log File Name", "Paths");
		if(tempString != null) {
			filteredAlertsLogFileName = tempString;
		}
		
		// parse console log file name
		tempString = m_settings.getValue("Console Log File Name", "Paths");
		if(tempString != null) {
			consoleLogFileName = tempString;
		}
		
		// parse log directory name
		tempString = m_settings.getValue("Log Directory Name", "Paths");
		if(tempString != null) {
			logDirectoryName = tempString;
		}
		
		// parse auto-clear expired active alerts value
		tempString = m_settings.getValue("Auto-Clear Expired Active Alerts", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoClearExpiredActiveAlerts = true;
			}
			else if(tempString.equals("false")) {
				autoClearExpiredActiveAlerts = false;
			}
		}
		
		// parse auto-clear expired filtered alerts value
		tempString = m_settings.getValue("Auto-Clear Expired Filtered Alerts", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoClearExpiredFilteredAlerts = true;
			}
			else if(tempString.equals("false")) {
				autoClearExpiredFilteredAlerts = false;
			}
		}
		
		// parse auto-save reward list value
		tempString = m_settings.getValue("Auto-Save Reward List", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoSaveRewardList = true;
			}
			else if(tempString.equals("false")) {
				autoSaveRewardList = false;
			}
		}

		// parse auto-save filter list value
		tempString = m_settings.getValue("Auto-Save Filter List", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoSaveFilterList = true;
			}
			else if(tempString.equals("false")) {
				autoSaveFilterList = false;
			}
		}
		
		// parse auto-save sound list value
		tempString = m_settings.getValue("Auto-Save Sound List", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoSaveSoundList = true;
			}
			else if(tempString.equals("false")) {
				autoSaveSoundList = false;
			}
		}
		
		// parse log alerts value
		tempString = m_settings.getValue("Log Alerts", "Log");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				logAlerts = true;
			}
			else if(tempString.equals("false")) {
				logAlerts = false;
			}
		}
		
		// parse log filtered alerts value
		tempString = m_settings.getValue("Log Filtered Alerts", "Log");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				logFilteredAlerts = true;
			}
			else if(tempString.equals("false")) {
				logFilteredAlerts = false;
			}
		}
		
		// parse log console value
		tempString = m_settings.getValue("Log Console", "Log");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				logConsole = true;
			}
			else if(tempString.equals("false")) {
				logConsole = false;
			}
		}
		
		// parse alert url string
		tempString = m_settings.getValue("Alert URL", "Paths");
		if(tempString != null) {
			alertURL = tempString;
		}
		
		// parse server window position
		tempPoint = Utilities.parsePoint(m_settings.getValue("Window Position", "Interface"));
		if(tempPoint != null && tempPoint.x > 0 && tempPoint.y > 0) {
			windowPositionX = tempPoint.x;
			windowPositionY = tempPoint.y;
		}
		
		// parse server window size
		tempDimension = Utilities.parseDimension(m_settings.getValue("Window Size", "Interface"));
		if(tempDimension != null && tempDimension.width > 0 && tempDimension.height > 0) {
			windowWidth = tempDimension.width;
			windowHeight = tempDimension.height;
		}
		
		// parse selected alert window tab
		try {
			tempAlertWindowTab = AlertWindowTabType.parseFrom(m_settings.getValue("Selected Alert Window Tab", "Interface"));
			currentAlertWindowTab = tempAlertWindowTab;
		}
		catch(InvalidAlertWindowTabException e) { }
		
		// parse console auto-scrolling
		tempString = m_settings.getValue("Auto-Scroll Console", "Console");
		if(tempString != null) {
			tempString = tempString.trim().toLowerCase();
			if(tempString.equals("true")) {
				autoScrollConsole = true;
			}
			else if(tempString.equals("false")) {
				autoScrollConsole = false;
			}
		}

		// parse max console history
		tempInt = -1;
		try { tempInt = Integer.parseInt(m_settings.getValue("Max Console History", "Console")); } catch(NumberFormatException e) { } 
		if(tempInt >= 1) { maxConsoleHistory = tempInt; }
		
		// parse max console history
		tempLong = -1L;
		try { tempLong = Long.parseLong(m_settings.getValue("Alert Update Frequency", "Alerts")); } catch(NumberFormatException e) { } 
		if(tempLong >= minimumAlertUpdateFrequency) { alertUpdateFrequency = tempLong; }
		
		// parse enable sounds value
		tempString = m_settings.getValue("Sounds Enabled", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				soundsEnabled = true;
			}
			else if(tempString.equals("false")) {
				soundsEnabled = false;
			}
		}
		
		// parse volume
		tempFloat = 0.0f;
		try {
			tempString = m_settings.getValue("Volume", "Interface");
			
			if(tempString != null) {
				tempFloat = Float.parseFloat(tempString);
				
				if(tempFloat >= minimumVolume && tempFloat <= maximumVolume) {
					volume = tempFloat;
				}
			}
		}
		catch(NumberFormatException e) { }
		
		return true;
	}
	
	public boolean saveTo(String fileName) {
		// update variables collection
		m_settings.setValue("Auto-Save Settings", autoSaveSettings, "Interface");
		m_settings.setValue("Reward List File Name", rewardListFileName, "Paths");
		m_settings.setValue("Filter List File Name", filterListFileName, "Paths");
		m_settings.setValue("Sound List File Name", soundListFileName, "Paths");
		m_settings.setValue("Sound Directory Name", soundDirectoryName, "Paths");
		m_settings.setValue("Alerts Log File Name", alertsLogFileName, "Paths");
		m_settings.setValue("Filtered Alerts Log File Name", filteredAlertsLogFileName, "Paths");
		m_settings.setValue("Console Log File Name", consoleLogFileName, "Paths");
		m_settings.setValue("Log Directory Name", logDirectoryName, "Paths");
		m_settings.setValue("Auto-Clear Expired Active Alerts", autoClearExpiredActiveAlerts, "Interface");
		m_settings.setValue("Auto-Clear Expired Filtered Alerts", autoClearExpiredFilteredAlerts, "Interface");
		m_settings.setValue("Auto-Save Reward List", autoSaveRewardList, "Interface");
		m_settings.setValue("Auto-Save Filter List", autoSaveFilterList, "Interface");
		m_settings.setValue("Auto-Save Sound List", autoSaveSoundList, "Interface");
		m_settings.setValue("Log Alerts", logAlerts, "Log");
		m_settings.setValue("Log Filtered Alerts", logFilteredAlerts, "Log");
		m_settings.setValue("Log Console", logConsole, "Log");
		m_settings.setValue("Alert URL", alertURL, "Paths");
		m_settings.setValue("Window Position", windowPositionX + ", " + windowPositionY, "Interface");
		m_settings.setValue("Window Size", windowWidth + ", " + windowHeight, "Interface");
		m_settings.setValue("Selected Alert Window Tab", currentAlertWindowTab.name(), "Interface");
		m_settings.setValue("Auto-Scroll Console", autoScrollConsole, "Console");
		m_settings.setValue("Max Console History", maxConsoleHistory, "Console");
		m_settings.setValue("Alert Update Frequency", alertUpdateFrequency, "Alerts");
		m_settings.setValue("Sounds Enabled", soundsEnabled, "Interface");
		m_settings.setValue("Volume", volume, "Interface");
		
		m_settings.sort();
		
		return m_settings.writeTo(fileName);
	}
	
}
