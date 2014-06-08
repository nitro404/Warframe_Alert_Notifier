package alert;

import java.text.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import exception.*;

public class AlertMonitor {
	
	private Vector<Alert> m_alerts;
	private Vector<Alert> m_filteredAlerts;
	private Vector<AlertListener> m_alertListeners;
	private boolean m_running;
	private Thread m_alertUpdateThread;
	
	private static String TITLE = "title";
	private static String LINK = "link";
	private static String DESCRIPTION = "description";
	private static String ITEM = "item";
	private static String FACTION = "faction";
	private static String PUBDATE = "pubDate";
	private static String EXPIRY = "expiry";
	
	public AlertMonitor() {
		m_alerts = new Vector<Alert>();
		m_filteredAlerts = new Vector<Alert>();
		m_alertListeners = new Vector<AlertListener>();
		
		m_running = false;
		
		m_alertUpdateThread = null;
	}
	
	public boolean start() {
		if(m_running) { return true; }
		
		// start the alert update thread
		m_alertUpdateThread = new Thread(new Runnable() {
			public void run() {
				m_running = true;
				
				while(m_running) {
					updateAlerts();
					
					try { Thread.sleep(AlertNotifier.settings.alertUpdateFrequency); }
					catch(InterruptedException e) { }
				}
			}
		});
		m_alertUpdateThread.start();
		
		m_running = true;
		
		return true;
	}
	
	public boolean stop() {
		if(!m_running) { return true; }
		
		m_running = false;
		
		try { m_alertUpdateThread.interrupt(); }
		catch(Exception e) { }
		
		m_alertUpdateThread = null;
		
		return true;
	}
	
	public void forceUpdate() {
		if(m_running) {
			m_alertUpdateThread.interrupt();
		}
		else {
			updateAlerts();
		}
	}
	
	public int numberOfAlerts() {
		return m_alerts.size();
	}
	
	public Alert getAlert(int index) {
		if(index < 0 || index >= m_alerts.size()) { return null; }
		return m_alerts.elementAt(index);
	}
	
	public boolean hasAlert(Alert a) {
		if(a == null) { return false; }
		return m_alerts.contains(a);
	}
	
	public int indexOfAlert(Alert a) {
		if(a == null) { return -1; }
		return m_alerts.indexOf(a);
	}
	
	public boolean addAlert(Alert a) {
		if(hasAlert(a)) { return false; }
		
		m_alerts.add(a);
		
		handleNewAlert(a);
		
		return true;
	}
	
	public boolean removeAlert(int index) {
		if(index < 0 || index >= m_alerts.size()) { return false; }
		m_alerts.remove(index);
		return true;
	}
	
	public boolean removeAlert(Alert a) {
		if(a == null) { return false; }
		return m_alerts.remove(a);
	}
	
	public void clearAlerts() {
		m_alerts.clear();
	}
	
	public void clearExpiredAlerts() {
		for(int i=0;i<m_alerts.size();i++) {
			if(m_alerts.elementAt(i).getTimeLeftInMilliseconds() <= 0L) {
				m_alerts.remove(i);
				
				i--;
			}
		}
	}
	
	public int numberOfFilteredAlerts() {
		return m_filteredAlerts.size();
	}
	
	public Alert getFilteredAlert(int index) {
		if(index < 0 || index >= m_filteredAlerts.size()) { return null; }
		return m_filteredAlerts.elementAt(index);
	}
	
	public boolean hasFilteredAlert(Alert a) {
		if(a == null) { return false; }
		return m_filteredAlerts.contains(a);
	}
	
	public int indexOfFilteredAlert(Alert a) {
		if(a == null) { return -1; }
		return m_filteredAlerts.indexOf(a);
	}
	
	public boolean addFilteredAlert(Alert a) {
		if(hasFilteredAlert(a)) { return false; }
		
		m_filteredAlerts.add(a);
		
		return true;
	}
	
	public boolean removeFilteredAlert(int index) {
		if(index < 0 || index >= m_filteredAlerts.size()) { return false; }
		m_filteredAlerts.remove(index);
		return true;
	}
	
	public boolean removeFilteredAlert(Alert a) {
		if(a == null) { return false; }
		return m_filteredAlerts.remove(a);
	}
	
	public void clearFilteredAlerts() {
		m_filteredAlerts.clear();
	}
	
	public void clearExpiredFilteredAlerts() {
		for(int i=0;i<m_filteredAlerts.size();i++) {
			if(m_filteredAlerts.elementAt(i).getTimeLeftInMilliseconds() <= 0L) {
				m_filteredAlerts.remove(i);
				
				i--;
			}
		}
	}
	
	public int numberOfAlertListeners() {
		return m_alertListeners.size();
	}
	
	public AlertListener getAlertListener(int index) {
		if(index < 0 || index >= m_alertListeners.size()) { return null; }
		return m_alertListeners.elementAt(index);
	}
	
	public boolean hasAlertListener(AlertListener a) {
		return m_alertListeners.contains(a);
	}
	
	public int indexOfAlertListener(AlertListener a) {
		return m_alertListeners.indexOf(a);
	}
	
	public boolean addAlertListener(AlertListener a) {
		if(a == null || m_alertListeners.contains(a)) { return false; }
		
		m_alertListeners.add(a);
		
		return true;
	}
	
	public boolean removeAlertListener(int index) {
		if(index < 0 || index >= m_alertListeners.size()) { return false; }
		m_alertListeners.remove(index);
		return true;
	}
	
	public boolean removeAlertListener(AlertListener a) {
		if(a == null) { return false; }
		return m_alertListeners.remove(a);
	}
	
	public void clearAlertListeners() {
		m_alertListeners.clear();
	}
	
	public void handleNewAlert(Alert a) {
		if(a == null) { return; }
		
		for(int i=0;i<m_alertListeners.size();i++) {
			m_alertListeners.elementAt(i).notifyNewAlert(a);
		}
		
		if(AlertNotifier.filters.filterAlert(a)) {
			addFilteredAlert(a);
			
			for(int i=0;i<m_alertListeners.size();i++) {
				m_alertListeners.elementAt(i).notifyFilteredAlert(a);
			}
		}
	}
	
	private void updateAlerts() {
		try {
			update(AlertNotifier.settings.alertURL);
		}
		catch(NumberFormatException e) {
			AlertNotifier.console.writeLine("Encountered an improperly formatted number while updating alerts: " + e.getMessage());
		}
		catch(MalformedURLException e) {
			AlertNotifier.console.writeLine("Alert URL is invalid or malformed, please check that it is correct or reset your settings:" + e.getMessage());
		}
		catch(IllegalArgumentException e) {
			AlertNotifier.console.writeLine("Illegal argument encountered while updating alerts: " + e.getMessage());
		}
		catch(ParseException e) {
			AlertNotifier.console.writeLine("Failed to parse time stamp while updating alerts: " + e.getMessage());
		}
		catch(IOException e) {
			AlertNotifier.console.writeLine("Read exception thrown while attempting to read alert data: " + e.getMessage());
		}
		catch(XMLStreamException e) {
			AlertNotifier.console.writeLine("XML stream exception thrown while attempting to read alert stream: " + e.getMessage());
		}
		catch(MissingDataException e) {
			AlertNotifier.console.writeLine("Missing data encountered while updating alerts: " + e.getMessage());
		}
		catch(InvalidRewardFormatException e) {
			AlertNotifier.console.writeLine("Invalid reward in alert: " + e.getMessage());
		}
		catch(Exception e) {
			AlertNotifier.console.writeLine("Unknown exception thrown while updating alerts: " + e.getMessage());
		}
	}
	
	public boolean update(String link) throws NumberFormatException, MalformedURLException, IllegalArgumentException, ParseException, IOException, XMLStreamException, MissingDataException, InvalidRewardFormatException {
		if(link == null) { throw new IllegalArgumentException("url cannot be null"); }
		
		URL url = new URL(link);
		
		InputStream in = url.openStream();
		
		boolean isHeader = true;
		String title = null;
		String description = null;
		String faction = null;
		String pubTime = null;
		String expTime = null;
		XMLEvent event = null;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

		while(eventReader.hasNext()) {
			event = eventReader.nextEvent();
			
			if(event.isStartElement()) {
				String localPart = event.asStartElement().getName().getLocalPart();
				
				if(localPart.equalsIgnoreCase(ITEM)) {
					isHeader = false;
				}
				
				if(!isHeader) {
					if(localPart.equalsIgnoreCase(TITLE)) {
						title = getCharacterData(eventReader);
					}
					else if(localPart.equalsIgnoreCase(DESCRIPTION)) {
						description = getCharacterData(eventReader);
					}
					else if(localPart.equalsIgnoreCase(FACTION)) {
						faction = getCharacterData(eventReader);
					}
					else if(localPart.equalsIgnoreCase(LINK)) {
						continue;
					}
					else if(localPart.equalsIgnoreCase(PUBDATE)) {
						pubTime = getCharacterData(eventReader);
					}
					else if(localPart.equalsIgnoreCase(EXPIRY)) {
						expTime = getCharacterData(eventReader);
					}
				}
			}
			else if(event.isEndElement()) {
				if(isHeader) { continue; }
				
				String localPart = event.asEndElement().getName().getLocalPart();
				
				if(localPart.equalsIgnoreCase(ITEM)) {
					Alert newAlert;
					try { newAlert = new Alert(title, description, faction, pubTime, expTime); }
					catch(IllegalArgumentException e) { throw new MissingDataException("alert is missing information"); }
					
					addAlert(newAlert);
					
					title = null;
					description = null;
					faction = null;
					pubTime = null;
					expTime = null;
					
					event = eventReader.nextEvent();
				}
			}
		}
		
		try { in.close(); }
		catch(IOException e) { }
		
		return true;
	}
	
	private static String getCharacterData(XMLEventReader eventReader) throws XMLStreamException {
		if(eventReader == null) { return null; }
		
		XMLEvent event = null;
		
		event = eventReader.nextEvent();
		
		if(!event.isCharacters()) { return ""; }
		
		return event.asCharacters().getData();
	}
	
}
