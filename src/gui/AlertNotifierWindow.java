package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import settings.*;
import alert.*;
import utilities.*;

public class AlertNotifierWindow implements WindowListener, ChangeListener, ActionListener, Updatable {
	
	private JFrame m_frame;
	private JTabbedPane m_mainTabbedPane;
    private JTable m_activeAlertsTable;
    private JScrollPane m_activeAlertsScrollPane;
    private JTable m_filteredAlertsTable;
    private JScrollPane m_filteredAlertsScrollPane;
    private JTextArea m_consoleText;
	private Font m_consoleFont;
	private JScrollPane m_consoleScrollPane;
	private FilterEditorDialog m_filterEditorDialog;
	
	private JMenuBar m_menuBar;
	private JMenu m_fileMenu;
	private JMenuItem m_fileExitMenuItem;
	private JMenu m_alertsMenu;
	private JMenuItem m_alertsManualUpdateMenuItem;
	private JMenuItem m_alertsClearExpiredActiveMenuItem;
	private JMenuItem m_alertsClearExpiredFilteredMenuItem;
	private JMenuItem m_alertsClearExpiredAllMenuItem;
	private JCheckBoxMenuItem m_alertsAutoClearExpiredActiveMenuItem;
	private JCheckBoxMenuItem m_alertsAutoClearExpiredFilteredMenuItem;
	private JMenuItem m_alertsClearActiveMenuItem;
	private JMenuItem m_alertsClearFilteredMenuItem;
	private JMenuItem m_alertsClearAllMenuItem;
	private JMenuItem m_alertsAlertURLMenuItem;
	private JMenuItem m_alertsAlertUpdateFrequencyMenuItem;
	private JCheckBoxMenuItem m_alertsLogAllMenuItem;
	private JCheckBoxMenuItem m_alertsLogFilteredMenuItem;
	private JCheckBoxMenuItem m_alertsAutoSaveRewardListMenuItem;
	private JMenuItem m_alertsSaveRewardListMenuItem;
	private JMenuItem m_alertsReloadRewardListMenuItem;
	private JMenuItem m_alertsResetRewardListMenuItem;
	private JMenu m_filtersMenu;
	private JMenuItem m_filtersEditFiltersMenuItem;
	private JCheckBoxMenuItem m_filtersAutoSaveFilterListMenuItem;
	private JMenuItem m_filtersSaveFilterListMenuItem;
	private JMenuItem m_filtersReloadFilterListMenuItem;
	private JMenuItem m_filtersResetFilterListMenuItem;
	private JMenu m_soundsMenu;
	private JCheckBoxMenuItem m_soundsSoundsEnabledMenuItem;
	private JMenuItem m_soundsVolumeMenuItem;
	private JCheckBoxMenuItem m_soundsAutoSaveSoundListMenuItem;
	private JMenuItem m_soundsSaveSoundListMenuItem;
	private JMenuItem m_soundsReloadSoundListMenuItem;
	private JMenuItem m_soundsResetSoundListMenuItem;
	private JMenu m_settingsMenu;
	private JMenuItem m_settingsRewardListFileNameMenuItem;
	private JMenuItem m_settingsFilterListFileNameMenuItem;
	private JMenuItem m_settingsSoundListFileNameMenuItem;
	private JMenuItem m_settingsSoundDirectoryNameMenuItem;
	private JMenuItem m_settingsAlertsLogFileNameMenuItem;
	private JMenuItem m_settingsFilteredAlertsLogFileNameMenuItem;
	private JMenuItem m_settingsConsoleLogFileNameMenuItem;
	private JMenuItem m_settingsLogDirectoryNameMenuItem;
	private JCheckBoxMenuItem m_settingsAutoScrollConsoleMenuItem;
	private JMenuItem m_settingsMaxConsoleHistoryMenuItem;
	private JCheckBoxMenuItem m_settingsLogConsoleMenuItem;
	private JCheckBoxMenuItem m_settingsAutoSaveSettingsMenuItem;
	private JMenuItem m_settingsSaveSettingsMenuItem;
	private JMenuItem m_settingsReloadSettingsMenuItem;
	private JMenuItem m_settingsResetSettingsMenuItem;
	private JMenu m_windowMenu;
	private JMenuItem m_windowResetPositionMenuItem;
	private JMenu m_helpMenu;
	private JMenuItem m_helpAboutMenuItem;
	
	private boolean m_initialized;
	private boolean m_running;
	private boolean m_updating;
	private boolean m_autoSizeColumns;
	private Thread m_updateThread;
	
	public AlertNotifierWindow() {
		m_frame = new JFrame("Warframe Alert Notifier");
		m_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		m_frame.setMinimumSize(new Dimension(320, 240));
		m_frame.setLocation(SettingsManager.defaultWindowPositionX, SettingsManager.defaultWindowPositionY);
		m_frame.setSize(SettingsManager.defaultWindowWidth, SettingsManager.defaultWindowHeight);
		m_frame.addWindowListener(this);
		
		m_initialized = false;
		m_running = false;
		m_updating = false;
		m_autoSizeColumns = true;
		
		initMenu();
 		initComponents();
 		
		m_filterEditorDialog = new FilterEditorDialog(m_frame);
	}
	
	public boolean initialize() {
		if(m_initialized) { return false; }
		
		updateWindow();
		
		m_frame.setLocation(AlertNotifier.settings.windowPositionX, AlertNotifier.settings.windowPositionY);
		m_frame.setSize(AlertNotifier.settings.windowWidth, AlertNotifier.settings.windowHeight);
		
		// update and show the gui window
		update();
		m_frame.setVisible(true);
		
		m_initialized = true;
		
		// start the gui update thread
		m_updateThread = new Thread(new Runnable() {
			public void run() {
				if(!m_initialized) { return; }
				
				m_running = true;
				
				while(m_running) {
					// auto-clear expired active alerts
					if(AlertNotifier.settings.autoClearExpiredActiveAlerts) {
						AlertNotifier.alertMonitor.clearExpiredAlerts();
					}
					
					// auto-clear expired filtered alerts
					if(AlertNotifier.settings.autoClearExpiredFilteredAlerts) {
						AlertNotifier.alertMonitor.clearExpiredFilteredAlerts();
					}
					
					// update windows
					updateActiveAlertsTable(m_autoSizeColumns);
					updateFilteredAlertsTable(m_autoSizeColumns);
					
					// only autosize columns once
					m_autoSizeColumns = false;
					
					try { Thread.sleep(1000L); }
					catch(InterruptedException e) { }
				}
			}
		});
		m_updateThread.start();
		
		return true;
	}
	
	// initialize the menu
	private void initMenu() {
		m_menuBar = new JMenuBar();
		
		m_fileMenu = new JMenu("File");
		m_fileExitMenuItem = new JMenuItem("Exit");
		
		m_alertsMenu = new JMenu("Alerts");
		m_alertsManualUpdateMenuItem = new JMenuItem("Manual Update");
		m_alertsClearExpiredActiveMenuItem = new JMenuItem("Clear Expired Active Alerts");
		m_alertsClearExpiredFilteredMenuItem = new JMenuItem("Clear Expired Filtered Alerts");
		m_alertsClearExpiredAllMenuItem = new JMenuItem("Clear All Expired Alerts");
		m_alertsAutoClearExpiredActiveMenuItem = new JCheckBoxMenuItem("Auto-Clear Expired Active Alerts");
		m_alertsAutoClearExpiredFilteredMenuItem = new JCheckBoxMenuItem("Auto-Clear Expired Filtered Alerts");
		m_alertsClearActiveMenuItem = new JMenuItem("Clear Active Alerts");
		m_alertsClearFilteredMenuItem = new JMenuItem("Clear Filtered Alerts");
		m_alertsClearAllMenuItem = new JMenuItem("Clear All Alerts");
		m_alertsAlertURLMenuItem = new JMenuItem("Alert URL");
		m_alertsAlertUpdateFrequencyMenuItem = new JMenuItem("Alert Update Frequency");
		m_alertsLogAllMenuItem = new JCheckBoxMenuItem("Log All Alerts");
		m_alertsLogFilteredMenuItem = new JCheckBoxMenuItem("Log Filtered Alerts");
		m_alertsAutoSaveRewardListMenuItem = new JCheckBoxMenuItem("Auto-Save Reward List");
		m_alertsSaveRewardListMenuItem = new JMenuItem("Save Reward List");
		m_alertsReloadRewardListMenuItem = new JMenuItem("Reload Reward List");
		m_alertsResetRewardListMenuItem = new JMenuItem("Reset Reward List");
		m_alertsAutoClearExpiredActiveMenuItem.setSelected(SettingsManager.defaultAutoClearExpiredActiveAlerts);
		m_alertsAutoClearExpiredFilteredMenuItem.setSelected(SettingsManager.defaultAutoClearExpiredFilteredAlerts);
		m_alertsLogAllMenuItem.setSelected(SettingsManager.defaultLogAlerts);
		m_alertsLogFilteredMenuItem.setSelected(SettingsManager.defaultLogFilteredAlerts);
		m_alertsAutoSaveRewardListMenuItem.setSelected(SettingsManager.defaultAutoSaveRewardList);
		
		m_filtersMenu = new JMenu("Filters");
		m_filtersEditFiltersMenuItem = new JMenuItem("Edit Filters");
		m_filtersAutoSaveFilterListMenuItem = new JCheckBoxMenuItem("Auto-Save Filter List");
		m_filtersSaveFilterListMenuItem = new JMenuItem("Save Filter List");
		m_filtersReloadFilterListMenuItem = new JMenuItem("Reload Filter List");
		m_filtersResetFilterListMenuItem = new JMenuItem("Reset Filter List");
		m_filtersAutoSaveFilterListMenuItem.setSelected(SettingsManager.defaultAutoSaveFilterList);
		
		m_soundsMenu = new JMenu("Sounds");
		m_soundsSoundsEnabledMenuItem = new JCheckBoxMenuItem("Sounds Enabled");
		m_soundsVolumeMenuItem = new JMenuItem("Volume");
		m_soundsAutoSaveSoundListMenuItem = new JCheckBoxMenuItem("Auto-Save Sound List");
		m_soundsSaveSoundListMenuItem = new JMenuItem("Save Sound List");
		m_soundsReloadSoundListMenuItem = new JMenuItem("Reload Sound List");
		m_soundsResetSoundListMenuItem = new JMenuItem("Reset Sound List");
		m_soundsSoundsEnabledMenuItem.setSelected(SettingsManager.defaultSoundsEnabled);
		m_soundsAutoSaveSoundListMenuItem.setSelected(SettingsManager.defaultAutoSaveSoundList);
		
		m_settingsMenu = new JMenu("Settings");
		m_settingsRewardListFileNameMenuItem = new JMenuItem("Reward List File Name");
		m_settingsFilterListFileNameMenuItem = new JMenuItem("Filter List File Name");
		m_settingsSoundListFileNameMenuItem = new JMenuItem("Sound List File Name");
		m_settingsSoundDirectoryNameMenuItem = new JMenuItem("Sound Directory Name");
		m_settingsAlertsLogFileNameMenuItem = new JMenuItem("Alerts Log File Name");
		m_settingsFilteredAlertsLogFileNameMenuItem = new JMenuItem("Filtered Alerts Log File Name");
		m_settingsConsoleLogFileNameMenuItem = new JMenuItem("Console Log File Name");
		m_settingsLogDirectoryNameMenuItem = new JMenuItem("Log Directory Name");
		m_settingsAutoScrollConsoleMenuItem = new JCheckBoxMenuItem("Auto-Scroll Console");
		m_settingsMaxConsoleHistoryMenuItem = new JMenuItem("Max Console History");
		m_settingsLogConsoleMenuItem = new JCheckBoxMenuItem("Log Console");
		m_settingsAutoSaveSettingsMenuItem = new JCheckBoxMenuItem("Auto-Save Settings");
		m_settingsSaveSettingsMenuItem = new JMenuItem("Save Settings");
		m_settingsReloadSettingsMenuItem = new JMenuItem("Reload Settings");
		m_settingsResetSettingsMenuItem = new JMenuItem("Reset Settings");
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.defaultAutoScrollConsole);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.defaultAutoSaveSettings);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.defaultLogConsole);

		m_windowMenu = new JMenu("Window");
		m_windowResetPositionMenuItem = new JMenuItem("Reset Window Position");
		
		m_helpMenu = new JMenu("Help");
		m_helpAboutMenuItem = new JMenuItem("About");
		
		m_fileExitMenuItem.addActionListener(this);
		m_alertsManualUpdateMenuItem.addActionListener(this);
		m_alertsClearExpiredActiveMenuItem.addActionListener(this);
		m_alertsClearExpiredFilteredMenuItem.addActionListener(this);
		m_alertsClearExpiredAllMenuItem.addActionListener(this);
		m_alertsAutoClearExpiredActiveMenuItem.addActionListener(this);
		m_alertsAutoClearExpiredFilteredMenuItem.addActionListener(this);
		m_alertsClearActiveMenuItem.addActionListener(this);
		m_alertsClearFilteredMenuItem.addActionListener(this);
		m_alertsClearAllMenuItem.addActionListener(this);
		m_alertsAlertURLMenuItem.addActionListener(this);
		m_alertsAlertUpdateFrequencyMenuItem.addActionListener(this);
		m_alertsLogAllMenuItem.addActionListener(this);
		m_alertsLogFilteredMenuItem.addActionListener(this);
		m_alertsAutoSaveRewardListMenuItem.addActionListener(this);
		m_alertsAutoSaveRewardListMenuItem.addActionListener(this);
		m_alertsSaveRewardListMenuItem.addActionListener(this);
		m_alertsReloadRewardListMenuItem.addActionListener(this);
		m_alertsResetRewardListMenuItem.addActionListener(this);
		m_filtersEditFiltersMenuItem.addActionListener(this);
		m_filtersAutoSaveFilterListMenuItem.addActionListener(this);
		m_filtersSaveFilterListMenuItem.addActionListener(this);
		m_filtersReloadFilterListMenuItem.addActionListener(this);
		m_filtersResetFilterListMenuItem.addActionListener(this);
		m_soundsSoundsEnabledMenuItem.addActionListener(this);
		m_soundsVolumeMenuItem.addActionListener(this);
		m_soundsAutoSaveSoundListMenuItem.addActionListener(this);
		m_soundsSaveSoundListMenuItem.addActionListener(this);
		m_soundsReloadSoundListMenuItem.addActionListener(this);
		m_soundsResetSoundListMenuItem.addActionListener(this);
		m_settingsRewardListFileNameMenuItem.addActionListener(this);
		m_settingsFilterListFileNameMenuItem.addActionListener(this);
		m_settingsSoundListFileNameMenuItem.addActionListener(this);
		m_settingsSoundDirectoryNameMenuItem.addActionListener(this);
		m_settingsAlertsLogFileNameMenuItem.addActionListener(this);
		m_settingsFilteredAlertsLogFileNameMenuItem.addActionListener(this);
		m_settingsConsoleLogFileNameMenuItem.addActionListener(this);
		m_settingsLogDirectoryNameMenuItem.addActionListener(this);
		m_settingsAutoScrollConsoleMenuItem.addActionListener(this);
		m_settingsMaxConsoleHistoryMenuItem.addActionListener(this);
		m_settingsLogConsoleMenuItem.addActionListener(this);
		m_settingsAutoSaveSettingsMenuItem.addActionListener(this);
		m_settingsSaveSettingsMenuItem.addActionListener(this);
		m_settingsReloadSettingsMenuItem.addActionListener(this);
		m_settingsResetSettingsMenuItem.addActionListener(this);
		m_windowResetPositionMenuItem.addActionListener(this);
		m_helpAboutMenuItem.addActionListener(this);
		
		m_fileMenu.add(m_fileExitMenuItem);
		
		m_alertsMenu.add(m_alertsManualUpdateMenuItem);
		m_alertsMenu.add(m_alertsClearExpiredActiveMenuItem);
		m_alertsMenu.add(m_alertsClearExpiredFilteredMenuItem);
		m_alertsMenu.add(m_alertsClearExpiredAllMenuItem);
		m_alertsMenu.add(m_alertsAutoClearExpiredActiveMenuItem);
		m_alertsMenu.add(m_alertsAutoClearExpiredFilteredMenuItem);
		m_alertsMenu.add(m_alertsClearActiveMenuItem);
		m_alertsMenu.add(m_alertsClearFilteredMenuItem);
		m_alertsMenu.add(m_alertsClearAllMenuItem);
		m_alertsMenu.add(m_alertsAlertURLMenuItem);
		m_alertsMenu.add(m_alertsAlertUpdateFrequencyMenuItem);
		m_alertsMenu.add(m_alertsLogAllMenuItem);
		m_alertsMenu.add(m_alertsLogFilteredMenuItem);
		m_alertsMenu.addSeparator();
		m_alertsMenu.add(m_alertsAutoSaveRewardListMenuItem);
		m_alertsMenu.add(m_alertsSaveRewardListMenuItem);
		m_alertsMenu.add(m_alertsReloadRewardListMenuItem);
		m_alertsMenu.add(m_alertsResetRewardListMenuItem);
		
		m_filtersMenu.add(m_filtersEditFiltersMenuItem);
		m_filtersMenu.addSeparator();
		m_filtersMenu.add(m_filtersAutoSaveFilterListMenuItem);
		m_filtersMenu.add(m_filtersSaveFilterListMenuItem);
		m_filtersMenu.add(m_filtersReloadFilterListMenuItem);
		m_filtersMenu.add(m_filtersResetFilterListMenuItem);
		
		m_soundsMenu.add(m_soundsSoundsEnabledMenuItem);
		m_soundsMenu.add(m_soundsVolumeMenuItem);
		m_soundsMenu.addSeparator();
		m_soundsMenu.add(m_soundsAutoSaveSoundListMenuItem);
		m_soundsMenu.add(m_soundsSaveSoundListMenuItem);
		m_soundsMenu.add(m_soundsReloadSoundListMenuItem);
		m_soundsMenu.add(m_soundsResetSoundListMenuItem);
		
		m_settingsMenu.add(m_settingsRewardListFileNameMenuItem);
		m_settingsMenu.add(m_settingsFilterListFileNameMenuItem);
		m_settingsMenu.add(m_settingsSoundListFileNameMenuItem);
		m_settingsMenu.add(m_settingsSoundDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsAlertsLogFileNameMenuItem);
		m_settingsMenu.add(m_settingsFilteredAlertsLogFileNameMenuItem);
		m_settingsMenu.add(m_settingsConsoleLogFileNameMenuItem);
		m_settingsMenu.add(m_settingsLogDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsAutoScrollConsoleMenuItem);
		m_settingsMenu.add(m_settingsMaxConsoleHistoryMenuItem);
		m_settingsMenu.add(m_settingsLogConsoleMenuItem);
		m_settingsMenu.addSeparator();
		m_settingsMenu.add(m_settingsAutoSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsReloadSettingsMenuItem);
		m_settingsMenu.add(m_settingsResetSettingsMenuItem);
		
		m_windowMenu.add(m_windowResetPositionMenuItem);
		
		m_helpMenu.add(m_helpAboutMenuItem);
		
		m_menuBar.add(m_fileMenu);
		m_menuBar.add(m_alertsMenu);
		m_menuBar.add(m_filtersMenu);
		m_menuBar.add(m_soundsMenu);
		m_menuBar.add(m_settingsMenu);
		m_menuBar.add(m_windowMenu);
		m_menuBar.add(m_helpMenu);
		
		m_frame.setJMenuBar(m_menuBar);
	}

	// initialize the gui components
	private void initComponents() {
		// initialize the main tabbed pane
		m_mainTabbedPane = new JTabbedPane();
		
		// initialize the active alerts tab
		m_activeAlertsScrollPane = new JScrollPane();
		m_activeAlertsTable = new JTable() {
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		
		DefaultTableModel activeAlertsTableModel = new DefaultTableModel(
			null,
			new String[] {
				"Time Left", "Location", "Planet", "Mission Description", "Faction", "Credits", "Reward", "Reward Type"
			}
		);
		
		m_activeAlertsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_activeAlertsTable.setModel(activeAlertsTableModel);
		m_activeAlertsScrollPane.setViewportView(m_activeAlertsTable);
		m_mainTabbedPane.addTab("Active Alerts", null, m_activeAlertsScrollPane, "Displays all currently active alerts");
		
		// initialize the filtered alerts tab
		m_filteredAlertsScrollPane = new JScrollPane();
		m_filteredAlertsTable = new JTable() {
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		
		DefaultTableModel filteredAlertsTableModel = new DefaultTableModel(
			null,
			new String[] {
				"Time Left", "Location", "Planet", "Mission Description", "Faction", "Credits", "Reward", "Reward Type"
			}
		);
		
		m_filteredAlertsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_filteredAlertsTable.setModel(filteredAlertsTableModel);
		m_filteredAlertsScrollPane.setViewportView(m_filteredAlertsTable);
		m_mainTabbedPane.addTab("Filtered Alerts", null, m_filteredAlertsScrollPane, "Displays all currently filtered alerts");
		
		// initialize the console tab
		m_consoleText = new JTextArea();
		m_consoleFont = new Font("Verdana", Font.PLAIN, 14);
		m_consoleText.setFont(m_consoleFont);
		m_consoleText.setEditable(false);
		m_consoleScrollPane = new JScrollPane(m_consoleText);
		m_mainTabbedPane.add(m_consoleScrollPane);
		
		m_mainTabbedPane.addTab("Console", null, m_consoleScrollPane, "Displays debugging information from the application.");
		
		m_mainTabbedPane.addChangeListener(this);
		
		m_frame.add(m_mainTabbedPane);
	}
	
	public JFrame getFrame() {
		return m_frame;
	}
	
	private void updateWindow() {
		if(m_mainTabbedPane.getSelectedIndex() != AlertNotifier.settings.currentAlertWindowTab.ordinal()) {
			m_mainTabbedPane.setSelectedIndex(AlertNotifier.settings.currentAlertWindowTab.ordinal());
		}
		
		m_alertsAutoClearExpiredActiveMenuItem.setSelected(AlertNotifier.settings.autoClearExpiredActiveAlerts);
		m_alertsAutoClearExpiredFilteredMenuItem.setSelected(AlertNotifier.settings.autoClearExpiredFilteredAlerts);
		m_alertsLogAllMenuItem.setSelected(AlertNotifier.settings.logAlerts);
		m_alertsLogFilteredMenuItem.setSelected(AlertNotifier.settings.logFilteredAlerts);
		m_alertsAutoSaveRewardListMenuItem.setSelected(AlertNotifier.settings.autoSaveRewardList);
		m_filtersAutoSaveFilterListMenuItem.setSelected(AlertNotifier.settings.autoSaveFilterList);
		m_soundsSoundsEnabledMenuItem.setSelected(AlertNotifier.settings.soundsEnabled);
		m_soundsAutoSaveSoundListMenuItem.setSelected(AlertNotifier.settings.autoSaveSoundList);
		m_settingsAutoScrollConsoleMenuItem.setSelected(AlertNotifier.settings.autoScrollConsole);
		m_settingsLogConsoleMenuItem.setSelected(AlertNotifier.settings.logConsole);
		m_settingsAutoSaveSettingsMenuItem.setSelected(AlertNotifier.settings.autoSaveSettings);
	}
	
	// update the server window
	public void update() {
		if(!m_initialized) { return; }
		
		// update and automatically scroll to the end of the text
		m_consoleText.setText(AlertNotifier.console.toString());
		
		if(AlertNotifier.settings.autoScrollConsole) {
			JScrollBar hScrollBar = m_consoleScrollPane.getHorizontalScrollBar();
			JScrollBar vScrollBar = m_consoleScrollPane.getVerticalScrollBar();
			
			if(!hScrollBar.getValueIsAdjusting() && !vScrollBar.getValueIsAdjusting()) {
				hScrollBar.setValue(hScrollBar.getMinimum());
				vScrollBar.setValue(vScrollBar.getMaximum());
			}
		}
		
		m_updating = true;
		
		updateWindow();
		
		m_updating = false;
	}
	
	public void resetWindowPosition() {
		AlertNotifier.settings.windowWidth = SettingsManager.defaultWindowWidth;
		AlertNotifier.settings.windowHeight = SettingsManager.defaultWindowHeight;
		AlertNotifier.settings.windowPositionX = SettingsManager.defaultWindowPositionX;
		AlertNotifier.settings.windowPositionY = SettingsManager.defaultWindowPositionY;
		
		m_frame.setLocation(AlertNotifier.settings.windowPositionX, AlertNotifier.settings.windowPositionY);
		m_frame.setSize(AlertNotifier.settings.windowWidth, AlertNotifier.settings.windowHeight);
	}
	
	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	public void windowClosing(WindowEvent e) {
		if(e.getSource() == m_frame) {
			close();
			m_frame.dispose();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(m_updating) { return; }
		
		// close the program
		if(e.getSource() == m_fileExitMenuItem) {
			close();
			System.exit(0);
		}
		// manually update alerts
		else if(e.getSource() == m_alertsManualUpdateMenuItem) {
			AlertNotifier.alertMonitor.forceUpdate();
		}
		// clear all expired active alerts
		else if(e.getSource() == m_alertsClearExpiredActiveMenuItem) {
			AlertNotifier.alertMonitor.clearExpiredAlerts();
		}
		// clear all expired filtered alerts
		else if(e.getSource() == m_alertsClearExpiredFilteredMenuItem) {
			AlertNotifier.alertMonitor.clearExpiredFilteredAlerts();
		}
		// clear all expired alerts
		else if(e.getSource() == m_alertsClearExpiredAllMenuItem) {
			AlertNotifier.alertMonitor.clearExpiredAlerts();
			AlertNotifier.alertMonitor.clearExpiredFilteredAlerts();
		}
		// toggle auto-clear expired active alerts
		else if(e.getSource() == m_alertsAutoClearExpiredActiveMenuItem) {
			AlertNotifier.settings.autoClearExpiredActiveAlerts = m_alertsAutoClearExpiredActiveMenuItem.isSelected();
		}
		// toggle auto-clear expired filtered alerts
		else if(e.getSource() == m_alertsAutoClearExpiredFilteredMenuItem) {
			AlertNotifier.settings.autoClearExpiredFilteredAlerts = m_alertsAutoClearExpiredFilteredMenuItem.isSelected();
		}
		// clear active alerts
		else if(e.getSource() == m_alertsClearActiveMenuItem) {
			AlertNotifier.alertMonitor.clearAlerts();
		}
		// clear filtered alerts
		else if(e.getSource() == m_alertsClearFilteredMenuItem) {
			AlertNotifier.alertMonitor.clearFilteredAlerts();
		}
		// clear all alerts
		else if(e.getSource() == m_alertsClearAllMenuItem) {
			AlertNotifier.alertMonitor.clearAlerts();
			AlertNotifier.alertMonitor.clearFilteredAlerts();
		}
		// change the alert url
		else if(e.getSource() == m_alertsAlertURLMenuItem) {
			// prompt for the alert url
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the Alert URL:", AlertNotifier.settings.alertURL);
			if(input == null) { return; }
			
			String newAlertURL = input.trim();
			if(newAlertURL.length() == 0) { return; }
			
			AlertNotifier.settings.alertURL = newAlertURL;
		}
		// change the alert update frequency
		else if(e.getSource() == m_alertsAlertUpdateFrequencyMenuItem) {
			// prompt for the alert update frequency
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the alert update frequency in milliseconds:", AlertNotifier.settings.alertUpdateFrequency);
			if(input == null) { return; }
			
			// set the new console history size
			long newAlertUpdateFrequency = -1L;
			try {
				newAlertUpdateFrequency = Long.parseLong(input);
			}
			catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(m_frame, "Invalid number entered for alert update frequency.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(newAlertUpdateFrequency >= SettingsManager.minimumAlertUpdateFrequency) {
				AlertNotifier.settings.alertUpdateFrequency = newAlertUpdateFrequency;
			}
			else {
				JOptionPane.showMessageDialog(m_frame, "Alert Update Frequency must be larger than " + SettingsManager.minimumAlertUpdateFrequency + ".", "Number Too Small", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		// change alert logging
		else if(e.getSource() == m_alertsLogAllMenuItem) {
			AlertNotifier.settings.logAlerts = m_alertsLogAllMenuItem.isSelected();
		}
		// change filtered alert logging
		else if(e.getSource() == m_alertsLogFilteredMenuItem) {
			AlertNotifier.settings.logFilteredAlerts = m_alertsLogFilteredMenuItem.isSelected();
		}
		// change reward list auto-saving
		else if(e.getSource() == m_alertsAutoSaveRewardListMenuItem) {
			AlertNotifier.settings.autoSaveRewardList = m_alertsAutoSaveRewardListMenuItem.isSelected();
		}
		// save reward list
		else if(e.getSource() == m_alertsSaveRewardListMenuItem) {
			if(AlertNotifier.rewards.writeTo(AlertNotifier.settings.rewardListFileName)) {
				AlertNotifier.console.writeLine("Successfully saved reward list to file: " + AlertNotifier.settings.rewardListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Successfully saved reward list to file: " + AlertNotifier.settings.rewardListFileName, "Reward List Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to save reward list to file: " + AlertNotifier.settings.rewardListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to save reward list to file: " + AlertNotifier.settings.rewardListFileName, "Reward List Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reload reward list
		else if(e.getSource() == m_alertsReloadRewardListMenuItem) {
			if(AlertNotifier.rewards.loadFrom(AlertNotifier.settings.rewardListFileName)) {
				update();
				
				AlertNotifier.console.writeLine("Loaded " + AlertNotifier.rewards.numberOfRewards() + " reward" + (AlertNotifier.rewards.numberOfRewards() != 1 ? "s" : "") + " from from file: " + AlertNotifier.settings.rewardListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Loaded " + AlertNotifier.rewards.numberOfRewards() + " reward" + (AlertNotifier.rewards.numberOfRewards() != 1 ? "s" : "") + " from from file: " + AlertNotifier.settings.rewardListFileName, "Reward List Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to load reward list from file: " + AlertNotifier.settings.rewardListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to load reward list from file: " + AlertNotifier.settings.rewardListFileName, "Reward List Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reset reward list
		else if(e.getSource() == m_alertsResetRewardListMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset the reward list?", "Reset Reward List", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				AlertNotifier.rewards.reset();
				
				update();
				
				AlertNotifier.console.writeLine("Reward list reset to default list");
			}
		}
		// edit alert filters
		else if(e.getSource() == m_filtersEditFiltersMenuItem) {
			m_filterEditorDialog.display();
		}
		// change filter list auto-saving
		else if(e.getSource() == m_filtersAutoSaveFilterListMenuItem) {
			AlertNotifier.settings.autoSaveFilterList = m_filtersAutoSaveFilterListMenuItem.isSelected();
		}
		// save filter list
		else if(e.getSource() == m_filtersSaveFilterListMenuItem) {
			if(AlertNotifier.filters.writeTo(AlertNotifier.settings.filterListFileName)) {
				AlertNotifier.console.writeLine("Successfully saved filter list to file: " + AlertNotifier.settings.filterListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Successfully saved filter list to file: " + AlertNotifier.settings.filterListFileName, "Filter List Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to save filter list to file: " + AlertNotifier.settings.filterListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to save filter list to file: " + AlertNotifier.settings.filterListFileName, "Filter List Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reload filter list
		else if(e.getSource() == m_filtersReloadFilterListMenuItem) {
			if(AlertNotifier.filters.loadFrom(AlertNotifier.settings.filterListFileName)) {
				update();
				
				AlertNotifier.console.writeLine("Loaded " + AlertNotifier.filters.numberOfRewardFilters() + " reward filter" + (AlertNotifier.filters.numberOfRewardFilters() != 1 ? "s" : "") + " from file: " + AlertNotifier.settings.filterListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Loaded " + AlertNotifier.filters.numberOfRewardFilters() + " reward filter" + (AlertNotifier.filters.numberOfRewardFilters() != 1 ? "s" : "") + " from file: " + AlertNotifier.settings.filterListFileName, "Filter List Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to load filter list from file: " + AlertNotifier.settings.filterListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to load filter list from file: " + AlertNotifier.settings.filterListFileName, "Filter List Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reset filter list
		else if(e.getSource() == m_filtersResetFilterListMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset the filter list?", "Reset Filter List", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				AlertNotifier.filters.reset();
				
				update();
				
				AlertNotifier.console.writeLine("Filter list reset to default");
			}
		}
		// enable or disable sounds
		else if(e.getSource() == m_soundsSoundsEnabledMenuItem) {
			AlertNotifier.settings.soundsEnabled = m_soundsSoundsEnabledMenuItem.isSelected();
		}
		// change sound volume
		else if(e.getSource() == m_soundsVolumeMenuItem) {
			SliderDialog sliderDialog = new SliderDialog(m_frame);
			sliderDialog.display("Sound Volume", "Set sound volume", (int) SettingsManager.minimumVolume, (int) SettingsManager.maximumVolume, (int) AlertNotifier.settings.volume, "dB", true);
			if(!sliderDialog.userSubmitted()) { return; }
			
			AlertNotifier.settings.volume = sliderDialog.getValue();
		}
		// change sound list auto-saving
		else if(e.getSource() == m_soundsAutoSaveSoundListMenuItem) {
			AlertNotifier.settings.autoSaveSoundList = m_soundsAutoSaveSoundListMenuItem.isSelected();
		}
		// save sound list
		else if(e.getSource() == m_soundsSaveSoundListMenuItem) {
			if(AlertNotifier.sounds.writeTo(AlertNotifier.settings.soundListFileName)) {
				AlertNotifier.console.writeLine("Successfully saved sound list to file: " + AlertNotifier.settings.soundListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Successfully saved sound list to file: " + AlertNotifier.settings.soundListFileName, "Sound List Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to save sound list to file: " + AlertNotifier.settings.soundListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to save sound list to file: " + AlertNotifier.settings.soundListFileName, "Sound List Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reload sound list
		else if(e.getSource() == m_soundsReloadSoundListMenuItem) {
			if(AlertNotifier.sounds.loadFrom(AlertNotifier.settings.soundListFileName)) {
				update();
				
				AlertNotifier.console.writeLine("Loaded " + AlertNotifier.sounds.numberOfSounds() + " sound" + (AlertNotifier.sounds.numberOfSounds() != 1 ? "s" : "") + " from file: " + AlertNotifier.settings.soundListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Loaded " + AlertNotifier.sounds.numberOfSounds() + " sound" + (AlertNotifier.sounds.numberOfSounds() != 1 ? "s" : "") + " from file: " + AlertNotifier.settings.soundListFileName, "Sound List Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to load sound list from file: " + AlertNotifier.settings.soundListFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to load sound list from file: " + AlertNotifier.settings.soundListFileName, "Sound List Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reset sound list
		else if(e.getSource() == m_soundsResetSoundListMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset the sound list?", "Reset Sound List", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				AlertNotifier.sounds.reset();
				
				update();
				
				AlertNotifier.console.writeLine("Sound list reset to default");
			}
		}
		// change the reward list file name
		else if(e.getSource() == m_settingsRewardListFileNameMenuItem) {
			// prompt for the reward list file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the reward list file name:", AlertNotifier.settings.rewardListFileName);
			if(input == null) { return; }
			
			String newRewardListFileName = input.trim();
			if(newRewardListFileName.length() == 0) { return; }
			
			if(!newRewardListFileName.equalsIgnoreCase(AlertNotifier.settings.rewardListFileName)) {
				AlertNotifier.settings.rewardListFileName = newRewardListFileName;
			}
		}
		// change the filter list file name
		else if(e.getSource() == m_settingsFilterListFileNameMenuItem) {
			// prompt for the filter list file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the filter list file name:", AlertNotifier.settings.filterListFileName);
			if(input == null) { return; }
			
			String newFilterListFileName = input.trim();
			if(newFilterListFileName.length() == 0) { return; }
			
			if(!newFilterListFileName.equalsIgnoreCase(AlertNotifier.settings.filterListFileName)) {
				AlertNotifier.settings.filterListFileName = newFilterListFileName;
			}
		}
		// change the sound list file name
		else if(e.getSource() == m_settingsSoundListFileNameMenuItem) {
			// prompt for the sound list file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the sound list file name:", AlertNotifier.settings.soundListFileName);
			if(input == null) { return; }
			
			String newSoundListFileName = input.trim();
			if(newSoundListFileName.length() == 0) { return; }
			
			if(!newSoundListFileName.equalsIgnoreCase(AlertNotifier.settings.soundListFileName)) {
				AlertNotifier.settings.soundListFileName = newSoundListFileName;
			}
		}
		// change the sound directory name
		else if(e.getSource() == m_settingsSoundDirectoryNameMenuItem) {
			// prompt for the sound directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the sound directory name:", AlertNotifier.settings.soundDirectoryName);
			if(input == null) { return; }
			
			String newSoundDirectoryName = input.trim();
			if(newSoundDirectoryName.length() == 0) { return; }
			
			if(!newSoundDirectoryName.equalsIgnoreCase(AlertNotifier.settings.soundDirectoryName)) {
				AlertNotifier.settings.soundDirectoryName = newSoundDirectoryName;
			}
		}
		// change the alerts log file name
		else if(e.getSource() == m_settingsAlertsLogFileNameMenuItem) {
			// prompt for the alerts log file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the alerts log file name:", AlertNotifier.settings.alertsLogFileName);
			if(input == null) { return; }
			
			String newAlertsLogFileName = input.trim();
			if(newAlertsLogFileName.length() == 0) { return; }
			
			if(!newAlertsLogFileName.equalsIgnoreCase(AlertNotifier.settings.alertsLogFileName)) {
				AlertNotifier.instance.resetAlertsLogFileHeader();
				
				AlertNotifier.settings.alertsLogFileName = newAlertsLogFileName;
			}
		}
		// change the filtered alerts log file name
		else if(e.getSource() == m_settingsFilteredAlertsLogFileNameMenuItem) {
			// prompt for the filtered alerts log file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the filtered alerts log file name:", AlertNotifier.settings.filteredAlertsLogFileName);
			if(input == null) { return; }
			
			String newFilteredAlertsLogFileName = input.trim();
			if(newFilteredAlertsLogFileName.length() == 0) { return; }
			
			if(!newFilteredAlertsLogFileName.equalsIgnoreCase(AlertNotifier.settings.filteredAlertsLogFileName)) {
				AlertNotifier.instance.resetFilteredAlertsLogFileHeader();
				
				AlertNotifier.settings.filteredAlertsLogFileName = newFilteredAlertsLogFileName;
			}
		}
		// change the console log file name
		else if(e.getSource() == m_settingsConsoleLogFileNameMenuItem) {
			// prompt for the console log file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the console log file name:", AlertNotifier.settings.consoleLogFileName);
			if(input == null) { return; }
			
			String newConsoleLogFileName = input.trim();
			if(newConsoleLogFileName.length() == 0) { return; }
			
			if(!newConsoleLogFileName.equalsIgnoreCase(AlertNotifier.settings.consoleLogFileName)) {
				AlertNotifier.console.resetConsoleLogFileHeader();
				
				AlertNotifier.settings.consoleLogFileName = newConsoleLogFileName;
			}
		}
		// change the log directory name
		else if(e.getSource() == m_settingsLogDirectoryNameMenuItem) {
			// prompt for the log directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the log directory name:", AlertNotifier.settings.logDirectoryName);
			if(input == null) { return; }
			
			String newLogDirectoryName = input.trim();
			if(newLogDirectoryName.length() == 0) { return; }
			
			if(!newLogDirectoryName.equalsIgnoreCase(AlertNotifier.settings.logDirectoryName)) {
				AlertNotifier.settings.logDirectoryName = newLogDirectoryName;
			}
		}
		// change the console auto scrolling
		else if(e.getSource() == m_settingsAutoScrollConsoleMenuItem) {
			AlertNotifier.settings.autoScrollConsole = m_settingsAutoScrollConsoleMenuItem.isSelected();
		}
		// change the maximum number of elements the console can hold
		else if(e.getSource() == m_settingsMaxConsoleHistoryMenuItem) {
			// prompt for the maximum console history size
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the maximum console history size:", AlertNotifier.settings.maxConsoleHistory);
			if(input == null) { return; }
			
			// set the new console history size
			int maxConsoleHistory = -1;
			try {
				maxConsoleHistory = Integer.parseInt(input);
			}
			catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(m_frame, "Invalid number entered for maximum console history.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(maxConsoleHistory > 1) {
				AlertNotifier.settings.maxConsoleHistory = maxConsoleHistory;
			}
		}
		// change console logging
		else if(e.getSource() == m_settingsLogConsoleMenuItem) {
			AlertNotifier.settings.logConsole = m_settingsLogConsoleMenuItem.isSelected();
		}
		else if(e.getSource() == m_settingsAutoSaveSettingsMenuItem) {
			AlertNotifier.settings.autoSaveSettings = m_settingsAutoSaveSettingsMenuItem.isSelected();
		}
		else if(e.getSource() == m_settingsSaveSettingsMenuItem) {
			if(AlertNotifier.settings.save()) {
				AlertNotifier.console.writeLine("Successfully saved settings to file: " + AlertNotifier.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Successfully saved settings to file: " + AlertNotifier.settings.settingsFileName, "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to save settings to file: " + AlertNotifier.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to save settings to file: " + AlertNotifier.settings.settingsFileName, "Settings Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource() == m_settingsReloadSettingsMenuItem) {
			if(AlertNotifier.settings.load()) {
				update();
				
				AlertNotifier.console.writeLine("Settings successfully loaded from file: " + AlertNotifier.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Settings successfully loaded from file: " + AlertNotifier.settings.settingsFileName, "Settings Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				AlertNotifier.console.writeLine("Failed to load settings from file: " + AlertNotifier.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to load settings from file: " + AlertNotifier.settings.settingsFileName, "Settings Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource() == m_settingsResetSettingsMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset all settings?", "Reset All Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				AlertNotifier.settings.reset();
				
				update();
				
				AlertNotifier.console.writeLine("All settings reset to default values");
			}
		}
		// reset the window position
		else if(e.getSource() == m_windowResetPositionMenuItem) {
			resetWindowPosition();
		}
		// display help message
		else if(e.getSource() == m_helpAboutMenuItem) {
			JOptionPane.showMessageDialog(m_frame, "Warframe Alert Notifier\nCreated by Kevin Scroggins (a.k.a. nitro_glycerine)\nE-Mail: nitro404@gmail.com\nWebsite: http://www.nitro404.com/", "About Warframe Alert Notifier", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		if(m_updating) { return; }
		
		if(e.getSource() == m_mainTabbedPane) {
			if(m_mainTabbedPane.getSelectedIndex() >= 0 && m_mainTabbedPane.getSelectedIndex() < AlertWindowTabType.values().length) {
				AlertNotifier.settings.currentAlertWindowTab = AlertWindowTabType.values()[m_mainTabbedPane.getSelectedIndex()];
			}
		}
	}
	
	private void updateActiveAlertsTable(boolean autoSizeColumns) {
		try {
			// get the selected table cell
			int x = m_activeAlertsTable.getSelectedColumn();
			int y = m_activeAlertsTable.getSelectedRow();
			
			// get the table's model and clear it safely
			DefaultTableModel tableModel = (DefaultTableModel) m_activeAlertsTable.getModel();
			tableModel.getDataVector().removeAllElements();
			tableModel.fireTableDataChanged();
			
			// get all of the active alerts and add them to the table model
			Alert a = null;
			for(int i=AlertNotifier.alertMonitor.numberOfAlerts()-1;i>=0;i--) {
				a = AlertNotifier.alertMonitor.getAlert(i);
				tableModel.addRow(new String[] {
					a.getTimeLeftString(),
					a.getLocation(),
					a.getPlanet(),
					a.getMissionType(),
					a.getFactionName(),
					Integer.toString(a.getCredits()),
					a.getRewardName() == null ? "" : a.getRewardName(),
					a.getRewardType() == RewardType.None ? "" : a.getRewardType().toString()
				});
			}
			
			// if the previous selection is still valid, re-select the previously selected cell
			if(x >= 0 && y >= 0 && x < m_activeAlertsTable.getColumnCount() && y < m_activeAlertsTable.getRowCount()) { 
				m_activeAlertsTable.getSelectionModel().setSelectionInterval(y, y);
				m_activeAlertsTable.getColumnModel().getSelectionModel().setSelectionInterval(x, x);
			}
			
			// if the columns should be autosized, then autosize them using the table renderer
			if(autoSizeColumns) {
				final TableCellRenderer renderer = m_activeAlertsTable.getTableHeader().getDefaultRenderer();
				
				for(int i=0;i<m_activeAlertsTable.getColumnCount();i++) {
					m_activeAlertsTable.getColumnModel().getColumn(i).setPreferredWidth(renderer.getTableCellRendererComponent(m_activeAlertsTable, m_activeAlertsTable.getModel().getColumnName(i), false, false, 0, i).getPreferredSize().width);
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	private void updateFilteredAlertsTable(boolean autoSizeColumns) {
		try {
			// get the selected table cell
			int x = m_filteredAlertsTable.getSelectedColumn();
			int y = m_filteredAlertsTable.getSelectedRow();
			
			// get the table's model and clear it safely
			DefaultTableModel tableModel = (DefaultTableModel) m_filteredAlertsTable.getModel();
			tableModel.getDataVector().removeAllElements();
			tableModel.fireTableDataChanged();
			
			// get all of the filtered alerts and add them to the table model
			Alert a = null;
			for(int i=AlertNotifier.alertMonitor.numberOfFilteredAlerts()-1;i>=0;i--) {
				a = AlertNotifier.alertMonitor.getFilteredAlert(i);
				tableModel.addRow(new String[] {
					a.getTimeLeftString(),
					a.getLocation(),
					a.getPlanet(),
					a.getMissionType(),
					a.getFactionName(),
					Integer.toString(a.getCredits()),
					a.getRewardName() == null ? "" : a.getRewardName(),
					a.getRewardType() == RewardType.None ? "" : a.getRewardType().toString()	
				});
			}
			
			// if the previous selection is still valid, re-select the previously selected cell
			if(x >= 0 && y >= 0 && x < m_filteredAlertsTable.getColumnCount() && y < m_filteredAlertsTable.getRowCount()) { 
				m_filteredAlertsTable.getSelectionModel().setSelectionInterval(y, y);
				m_filteredAlertsTable.getColumnModel().getSelectionModel().setSelectionInterval(x, x);
			}
			
			// if the columns should be autosized, then autosize them using the table renderer
			if(autoSizeColumns) {
				final TableCellRenderer renderer = m_filteredAlertsTable.getTableHeader().getDefaultRenderer();
				
				for(int i=0;i<m_filteredAlertsTable.getColumnCount();i++) {
					m_filteredAlertsTable.getColumnModel().getColumn(i).setPreferredWidth(renderer.getTableCellRendererComponent(m_filteredAlertsTable, m_filteredAlertsTable.getModel().getColumnName(i), false, false, 0, i).getPreferredSize().width);
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public void close() {
		// reset initialization variables
		m_initialized = false;
		m_running = false;
		
		// stop all threads
		try { m_updateThread.interrupt(); } catch(Exception e) { }
		
		AlertNotifier.settings.windowPositionX = m_frame.getX();
		AlertNotifier.settings.windowPositionY = m_frame.getY();
		AlertNotifier.settings.windowWidth = m_frame.getWidth();
		AlertNotifier.settings.windowHeight = m_frame.getHeight();
		
		// close the server
		AlertNotifier.instance.close();
	}
	
}
