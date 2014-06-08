package alert;

import java.util.*;
import java.io.*;
import javax.swing.*;
import settings.*;
import console.*;
import audio.*;
import gui.*;

public class AlertNotifier implements AlertListener, OutbreakListener, InvasionListener {

	public static AlertNotifier instance;
	public static AlertNotifierWindow alertNotifierWindow;
	public static SettingsManager settings;
	public static SystemConsole console;
	public static SoundManager sounds;
	public static RewardCollection rewards;
	public static FilterCollection filters;
	public static AlertMonitor alertMonitor;
	private Sound m_alertSound;
	private Sound m_outbreakSound;
	private Sound m_invasionSound;
	private boolean m_initialized;
	private boolean m_headerAddedToAlertsLogFile;
	private boolean m_headerAddedToFilteredAlertsLogFile;
	final public static String VERSION = "1.1.2";
	
	public AlertNotifier() {
		alertNotifierWindow = new AlertNotifierWindow();
		
		m_initialized = false;
		
		m_headerAddedToAlertsLogFile = false;
		m_headerAddedToFilteredAlertsLogFile = false;
		
		instance = this;
		settings = new SettingsManager();
		console = new SystemConsole();
		sounds = new SoundManager();
		rewards = new RewardCollection();
		filters = new FilterCollection();
		alertMonitor = new AlertMonitor();
		
		m_alertSound = null;
		m_outbreakSound = null;
		m_invasionSound = null;
	}

	public boolean initialize(String[] args) {
		if(m_initialized) { return false; }
		
		if(args != null && args.length > 0 && args[0] != null) {
			String temp = args[0].trim();
			if(temp.length() > 0) {
				settings.settingsFileName = temp;
			}
		}
		
		if(settings.load()) {
			console.writeLine("Settings successfully loaded from file: " + settings.settingsFileName);
		}
		else {
			console.writeLine("Failed to load settings from file: " + settings.settingsFileName);
			
			if(settings.settingsFileName != null && !SettingsManager.defaultSettingsFileName.equalsIgnoreCase(settings.settingsFileName)) {
				boolean loaded = false;
				
				while(!loaded) {
					int choice = JOptionPane.showConfirmDialog(null, "Unable to load settings from custom settings file. Use alternate settings file?\nNote that when the program is closed, this settings file will be generated if it does not exist.", "Settings Loading Failed", JOptionPane.YES_NO_CANCEL_OPTION);
					if(choice == JOptionPane.YES_OPTION) {
						String newSettingsFileName = JOptionPane.showInputDialog(null, "Enter a settings file name:", SettingsManager.defaultSettingsFileName);
						if(newSettingsFileName != null) {
							settings.settingsFileName = newSettingsFileName;
							loaded = settings.load();
							
							if(loaded) {
								console.writeLine("Settings successfully loaded from file: " + settings.settingsFileName);
							}
						}
						else {
							break;
						}
					}
					else {
						break;
					}
				}
			}
		}
		
		if(!rewards.loadFrom(settings.rewardListFileName)) {
			console.writeLine("Failed to load reward list from file: " + settings.rewardListFileName);
		}
		else {
			console.writeLine("Loaded " + rewards.numberOfRewards() + " reward" + (rewards.numberOfRewards() != 1 ? "s" : "") + " from from file: " + settings.rewardListFileName);
		}
		
		if(!filters.loadFrom(settings.filterListFileName)) {
			console.writeLine("Failed to load filter list from file: " + settings.filterListFileName);
		}
		else {
			console.writeLine("Loaded " + filters.numberOfRewardFilters() + " reward filter" + (filters.numberOfRewardFilters() != 1 ? "s" : "") + " from file: " + settings.filterListFileName);
		}
		
		if(!sounds.loadFrom(settings.soundListFileName)) {
			console.writeLine("Failed to load sound list from file: " + settings.soundListFileName);
		}
		else {
			console.writeLine("Loaded " + sounds.numberOfSounds() + " sound" + (sounds.numberOfSounds() != 1 ? "s" : "") + " from file: " + settings.soundListFileName);
		}
		
		m_alertSound = sounds.getSound("Alert");
// TODO: add custom sounds for outbreaks and invasions
		m_outbreakSound = sounds.getSound("Alert");
		m_invasionSound = sounds.getSound("Alert");
		if(m_alertSound == null) {
			console.writeLine("Default alert sound missing!");
		}
		
		m_initialized = true;
		
		boolean error = false;
		
		console.addTarget(alertNotifierWindow);
		alertMonitor.addAlertListener(this);
		alertMonitor.addOutbreakListener(this);
		alertMonitor.addInvasionListener(this);
		
		if(!alertNotifierWindow.initialize()) {
			JOptionPane.showMessageDialog(null, "Failed to initialize alert window!", "Alert Window Init Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		alertMonitor.start();
		
		if(!error) {
			console.writeLine("Warframe Alert Notifier initialized successfully!");
		}
		
		return true;
	}
	
	public boolean createLogDirectory() {
		if(settings.logDirectoryName.length() == 0) { return true; }
		
		File logDirectory = new File(settings.logDirectoryName);
		
		if(!logDirectory.exists()) {
			try {
				return logDirectory.mkdirs();
			}
			catch(SecurityException e) {
				console.writeLine("Failed to create log directory, check read / write permissions.");
				return false;
			}
		}
		
		return true;
	}
	
	public void resetAlertsLogFileHeader() {
		m_headerAddedToAlertsLogFile = false;
	}
	
	public void resetFilteredAlertsLogFileHeader() {
		m_headerAddedToFilteredAlertsLogFile = false;
	}
	
	public void playAlertSound() {
		if(m_alertSound == null || !settings.soundsEnabled) { return; }
		
		m_alertSound.play(settings.volume);
	}
	
	public void playOutbreakSound() {
		if(m_outbreakSound == null || !settings.soundsEnabled) { return; }
		
		m_outbreakSound.play(settings.volume);
	}
	
	public void playInvasionSound() {
		if(m_invasionSound == null || !settings.soundsEnabled) { return; }
		
		m_invasionSound.play(settings.volume);
	}
	
	public void notifyNewAlert(Alert a) {
		if(settings.logAlerts) {
			addHeaderToAlertsLogFile();
			if(!appendAlertToFile(a, settings.alertsLogFileName)) {
				console.writeLine("Failed to log alert to file: " + settings.alertsLogFileName);
			}
		}
	}
	
	public void notifyNewOutbreak(Outbreak b) {
// TODO: add logging for new outbreaks
/*
		if(settings.logOutbreaks) {
			addHeaderToOutbreaksLogFile();
			if(!appendOutbreakToFile(b, settings.outbreaksLogFileName)) {
				console.writeLine("Failed to log outbreak to file: " + settings.outbreaksLogFileName);
			}
		}
*/
	}
	
	public void notifyNewInvasion(Invasion v) {
// TODO: add logging for new invasions
/*
		if(settings.logInvasions) {
			addHeaderToInvasionsLogFile();
			if(!appendInvasionToFile(v, settings.invasionsLogFileName)) {
				console.writeLine("Failed to log invasion to file: " + settings.invasionsLogFileName);
			}
		}
*/
	}
	
	public void notifyFilteredAlert(Alert a) {
		if(settings.logFilteredAlerts) {
			addHeaderToFilteredAlertsLogFile();
			if(!appendAlertToFile(a, settings.filteredAlertsLogFileName)) {
				console.writeLine("Failed to log filtered alert to file: " + settings.filteredAlertsLogFileName);
			}
		}
		
		playAlertSound();
	}
	
	public void notifyFilteredOutbreak(Outbreak b) {
// TODO: add logging for filtered outbreaks
/*
		if(settings.logFilteredOutbreaks) {
			addHeaderToFilteredOutbreaksLogFile();
			if(!appendOutbreakToFile(b, settings.filteredOutbreaksLogFileName)) {
				console.writeLine("Failed to log filtered outbreak to file: " + settings.filteredOutbreaksLogFileName);
			}
		}
*/
		playOutbreakSound();
	}
	
	public void notifyFilteredInvasion(Invasion v) {
// TODO: add logging for filtered invasions
/*
		if(settings.logFilteredInvasions) {
			addHeaderToFilteredInvasionsLogFile();
			if(!appendInvasionToFile(v, settings.filteredInvasionsLogFileName)) {
				console.writeLine("Failed to log filtered invasion to file: " + settings.filteredInvasionsLogFileName);
			}
		}
*/
		playInvasionSound();
	}
	
	private boolean addHeaderToAlertsLogFile() {
		if(m_headerAddedToAlertsLogFile) { return false; }
		
		createLogDirectory();
		
		File alertsLogFile = new File((AlertNotifier.settings.logDirectoryName.length() == 0 ? "" : AlertNotifier.settings.logDirectoryName + (AlertNotifier.settings.logDirectoryName.charAt(AlertNotifier.settings.logDirectoryName.length() - 1) == '/' || AlertNotifier.settings.logDirectoryName.charAt(AlertNotifier.settings.logDirectoryName.length() - 1) == '\\' ? "" : "/")) + AlertNotifier.settings.alertsLogFileName);
		
		PrintWriter out = null;
		
		boolean fileExists = alertsLogFile.exists();
		
		try {
			out = new PrintWriter(new FileWriter(alertsLogFile, true));
			
			if(fileExists) { out.println(); }
			
			out.println("[Alerts from " + Alert.LOG_DATE_FORMAT.format(Calendar.getInstance().getTime()) + "]");
		}
		catch(IOException e) { return false; }
		
		if(out != null) { out.close(); }
		
		m_headerAddedToAlertsLogFile = true;
		
		return true;
	}
	
	private boolean addHeaderToFilteredAlertsLogFile() {
		if(m_headerAddedToFilteredAlertsLogFile) { return false; }
		
		createLogDirectory();
		
		File filteredAlertsLogFile = new File((AlertNotifier.settings.logDirectoryName.length() == 0 ? "" : AlertNotifier.settings.logDirectoryName + (AlertNotifier.settings.logDirectoryName.charAt(AlertNotifier.settings.logDirectoryName.length() - 1) == '/' || AlertNotifier.settings.logDirectoryName.charAt(AlertNotifier.settings.logDirectoryName.length() - 1) == '\\' ? "" : "/")) + AlertNotifier.settings.filteredAlertsLogFileName);
		
		PrintWriter out = null;
		
		boolean fileExists = filteredAlertsLogFile.exists();
		
		try {
			out = new PrintWriter(new FileWriter(filteredAlertsLogFile, true));
			
			if(fileExists) { out.println(); }
			
			out.println("[Filtered Alerts from " + Alert.LOG_DATE_FORMAT.format(Calendar.getInstance().getTime()) + "]");
			
			out.close();
		}
		catch(IOException e) { return false; }
		
		m_headerAddedToFilteredAlertsLogFile = true;
		
		return true;
	}
	
	private boolean appendAlertToFile(Alert a, String fileName) {
		if(a == null || fileName == null) { return false; }
		
		createLogDirectory();
		
		File alertsLogFile = new File((AlertNotifier.settings.logDirectoryName.length() == 0 ? "" : AlertNotifier.settings.logDirectoryName + (AlertNotifier.settings.logDirectoryName.charAt(AlertNotifier.settings.logDirectoryName.length() - 1) == '/' || AlertNotifier.settings.logDirectoryName.charAt(AlertNotifier.settings.logDirectoryName.length() - 1) == '\\' ? "" : "/")) + fileName);
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new FileWriter(alertsLogFile, true));
			
			a.writeTo(out);
			
			out.close();
		}
		catch(IOException e) { return false; }
		
		return true;
	}
	
	public void close() {
		m_initialized = false;
		
		alertMonitor.stop();
		
		if(settings.autoSaveRewardList) {
			rewards.writeTo(settings.rewardListFileName);
		}
		
		if(settings.autoSaveFilterList) {
			filters.writeTo(settings.filterListFileName);
		}
		
		if(settings.autoSaveSoundList) {
			sounds.writeTo(settings.soundListFileName);
		}
		
		if(settings.autoSaveSettings) {
			settings.save();
		}
	}
	
}
