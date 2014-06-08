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
	private Vector<Outbreak> m_outbreaks;
	private Vector<Invasion> m_invasions;
	private Vector<Alert> m_filteredAlerts;
	private Vector<Outbreak> m_filteredOutbreaks;
	private Vector<Invasion> m_filteredInvasions;
	private Vector<AlertListener> m_alertListeners;
	private Vector<OutbreakListener> m_outbreakListeners;
	private Vector<InvasionListener> m_invasionListeners;
	private boolean m_running;
	private Thread m_alertUpdateThread;
	
	private static String ALERT = "Alert";
	private static String INVASION = "Invasion";
	private static String OUTBREAK = "Outbreak";
	private static String GUID = "guid";
	private static String TITLE = "title";
	private static String LINK = "link";
	private static String AUTHOR = "author";
	private static String DESCRIPTION = "description";
	private static String ITEM = "item";
	private static String FACTION = "faction";
	private static String PUBDATE = "pubDate";
	private static String EXPIRY = "expiry";
	
	public AlertMonitor() {
		m_alerts = new Vector<Alert>();
		m_outbreaks = new Vector<Outbreak>();
		m_invasions = new Vector<Invasion>();
		m_filteredAlerts = new Vector<Alert>();
		m_filteredOutbreaks = new Vector<Outbreak>();
		m_filteredInvasions = new Vector<Invasion>();
		
		m_alertListeners = new Vector<AlertListener>();
		m_outbreakListeners = new Vector<OutbreakListener>();
		m_invasionListeners = new Vector<InvasionListener>();
		
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
			if(m_alerts.elementAt(i).isExpired()) {
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
			if(m_filteredAlerts.elementAt(i).isExpired()) {
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
	
	public int numberOfOutbreaks() {
		return m_outbreaks.size();
	}
	
	public Outbreak getOutbreak(int index) {
		if(index < 0 || index >= m_outbreaks.size()) { return null; }
		return m_outbreaks.elementAt(index);
	}
	
	public boolean hasOutbreak(Outbreak b) {
		if(b == null) { return false; }
		return m_outbreaks.contains(b);
	}
	
	public int indexOfOutbreak(Outbreak b) {
		if(b == null) { return -1; }
		return m_outbreaks.indexOf(b);
	}
	
	public boolean addOutbreak(Outbreak b) {
		if(hasOutbreak(b)) { return false; }
		
		m_outbreaks.add(b);
		
		handleNewOutbreak(b);
		
		return true;
	}
	
	public boolean removeOutbreak(int index) {
		if(index < 0 || index >= m_outbreaks.size()) { return false; }
		m_outbreaks.remove(index);
		return true;
	}
	
	public boolean removeOutbreak(Outbreak b) {
		if(b == null) { return false; }
		return m_outbreaks.remove(b);
	}
	
	public void clearOutbreaks() {
		m_outbreaks.clear();
	}
	
	public void clearExpiredOutbreaks() {
		for(int i=0;i<m_outbreaks.size();i++) {
			if(m_outbreaks.elementAt(i).isExpired()) {
				m_outbreaks.remove(i);
				
				i--;
			}
		}
	}
	
	public int numberOfFilteredOutbreaks() {
		return m_filteredOutbreaks.size();
	}
	
	public Outbreak getFilteredOutbreak(int index) {
		if(index < 0 || index >= m_filteredOutbreaks.size()) { return null; }
		return m_filteredOutbreaks.elementAt(index);
	}
	
	public boolean hasFilteredOutbreak(Outbreak b) {
		if(b == null) { return false; }
		return m_filteredOutbreaks.contains(b);
	}
	
	public int indexOfFilteredOutbreak(Outbreak b) {
		if(b == null) { return -1; }
		return m_filteredOutbreaks.indexOf(b);
	}
	
	public boolean addFilteredOutbreak(Outbreak b) {
		if(hasFilteredOutbreak(b)) { return false; }
		
		m_filteredOutbreaks.add(b);
		
		return true;
	}
	
	public boolean removeFilteredOutbreak(int index) {
		if(index < 0 || index >= m_filteredOutbreaks.size()) { return false; }
		m_filteredOutbreaks.remove(index);
		return true;
	}
	
	public boolean removeFilteredOutbreak(Outbreak b) {
		if(b == null) { return false; }
		return m_filteredOutbreaks.remove(b);
	}
	
	public void clearFilteredOutbreaks() {
		m_filteredOutbreaks.clear();
	}
	
	public void clearExpiredFilteredOutbreaks() {
		for(int i=0;i<m_filteredOutbreaks.size();i++) {
			if(m_filteredOutbreaks.elementAt(i).isExpired()) {
				m_filteredOutbreaks.remove(i);
				
				i--;
			}
		}
	}
	
	public int numberOfOutbreakListeners() {
		return m_outbreakListeners.size();
	}
	
	public OutbreakListener getOutbreakListener(int index) {
		if(index < 0 || index >= m_outbreakListeners.size()) { return null; }
		return m_outbreakListeners.elementAt(index);
	}
	
	public boolean hasOutbreakListener(OutbreakListener b) {
		return m_outbreakListeners.contains(b);
	}
	
	public int indexOfOutbreakListener(OutbreakListener b) {
		return m_outbreakListeners.indexOf(b);
	}
	
	public boolean addOutbreakListener(OutbreakListener b) {
		if(b == null || m_outbreakListeners.contains(b)) { return false; }
		
		m_outbreakListeners.add(b);
		
		return true;
	}
	
	public boolean removeOutbreakListener(int index) {
		if(index < 0 || index >= m_outbreakListeners.size()) { return false; }
		m_outbreakListeners.remove(index);
		return true;
	}
	
	public boolean removeOutbreakListener(OutbreakListener b) {
		if(b == null) { return false; }
		return m_outbreakListeners.remove(b);
	}
	
	public void clearOutbreakListeners() {
		m_outbreakListeners.clear();
	}
	
	public void handleNewOutbreak(Outbreak b) {
		if(b == null) { return; }
		
		for(int i=0;i<m_outbreakListeners.size();i++) {
			m_outbreakListeners.elementAt(i).notifyNewOutbreak(b);
		}
		
		if(AlertNotifier.filters.filterOutbreak(b)) {
			addFilteredOutbreak(b);
			
			for(int i=0;i<m_outbreakListeners.size();i++) {
				m_outbreakListeners.elementAt(i).notifyFilteredOutbreak(b);
			}
		}
	}
	
	public int numberOfInvasions() {
		return m_invasions.size();
	}
	
	public Invasion getInvasion(int index) {
		if(index < 0 || index >= m_invasions.size()) { return null; }
		return m_invasions.elementAt(index);
	}
	
	public boolean hasInvasion(Invasion i) {
		if(i == null) { return false; }

		return m_invasions.contains(i);
	}
	
	public int indexOfInvasion(Invasion i) {
		if(i == null) { return -1; }
		return m_invasions.indexOf(i);
	}
	
	public boolean addInvasion(Invasion i) {
		if(hasInvasion(i)) { return false; }
		
		m_invasions.add(i);
		
		handleNewInvasion(i);
		
		return true;
	}
	
	public boolean removeInvasion(int index) {
		if(index < 0 || index >= m_invasions.size()) { return false; }
		m_invasions.remove(index);
		return true;
	}
	
	public boolean removeInvasion(Invasion i) {
		if(i == null) { return false; }
		return m_invasions.remove(i);
	}
	
	public void clearInvasions() {
		m_invasions.clear();
	}
	
	public void clearExpiredInvasions() {
		for(int i=0;i<m_invasions.size();i++) {
			if(m_invasions.elementAt(i).isExpired()) {
				m_invasions.remove(i);
				
				i--;
			}
		}
	}
	
	public int numberOfFilteredInvasions() {
		return m_filteredInvasions.size();
	}
	
	public Invasion getFilteredInvasion(int index) {
		if(index < 0 || index >= m_filteredInvasions.size()) { return null; }
		return m_filteredInvasions.elementAt(index);
	}
	
	public boolean hasFilteredInvasion(Invasion i) {
		if(i == null) { return false; }
		return m_filteredInvasions.contains(i);
	}
	
	public int indexOfFilteredInvasion(Invasion i) {
		if(i == null) { return -1; }
		return m_filteredInvasions.indexOf(i);
	}
	
	public boolean addFilteredInvasion(Invasion i) {
		if(hasFilteredInvasion(i)) { return false; }
		
		m_filteredInvasions.add(i);
		
		return true;
	}
	
	public boolean removeFilteredInvasions(int index) {
		if(index < 0 || index >= m_filteredInvasions.size()) { return false; }
		m_filteredInvasions.remove(index);
		return true;
	}
	
	public boolean removeFilteredInvasion(Invasion i) {
		if(i == null) { return false; }
		return m_filteredInvasions.remove(i);
	}
	
	public void clearFilteredInvasions() {
		m_filteredInvasions.clear();
	}
	
	public void clearExpiredFilteredInvasions() {
		for(int i=0;i<m_filteredInvasions.size();i++) {
			if(m_filteredInvasions.elementAt(i).isExpired()) {
				m_filteredInvasions.remove(i);
				
				i--;
			}
		}
	}
	
	public int numberOfInvasionListeners() {
		return m_invasionListeners.size();
	}
	
	public InvasionListener getInvasionListener(int index) {
		if(index < 0 || index >= m_invasionListeners.size()) { return null; }
		return m_invasionListeners.elementAt(index);
	}
	
	public boolean hasInvasionListener(InvasionListener i) {
		return m_invasionListeners.contains(i);
	}
	
	public int indexOfInvasionListener(InvasionListener i) {
		return m_invasionListeners.indexOf(i);
	}
	
	public boolean addInvasionListener(InvasionListener i) {
		if(i == null || m_invasionListeners.contains(i)) { return false; }
		
		m_invasionListeners.add(i);
		
		return true;
	}
	
	public boolean removeInvasionListener(int index) {
		if(index < 0 || index >= m_invasionListeners.size()) { return false; }
		m_invasionListeners.remove(index);
		return true;
	}
	
	public boolean removeInvasionListener(InvasionListener i) {
		if(i == null) { return false; }
		return m_invasionListeners.remove(i);
	}
	
	public void clearInvasionListeners() {
		m_invasionListeners.clear();
	}
	
	public void handleNewInvasion(Invasion i) {
		if(i == null) { return; }
		
		for(int j=0;j<m_invasionListeners.size();j++) {
			m_invasionListeners.elementAt(j).notifyNewInvasion(i);
		}
		
		if(AlertNotifier.filters.filterInvasion(i)) {
			addFilteredInvasion(i);
			
			for(int j=0;j<m_invasionListeners.size();j++) {
				m_invasionListeners.elementAt(j).notifyFilteredInvasion(i);
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
		String guid = null;
		String title = null;
		String type = null;
		String description = null;
		String faction = null;
		String pubTime = null;
		String expTime = null;
		XMLEvent event = null;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		Vector<Outbreak> activeOutbreaks = new Vector<Outbreak>();
		Vector<Invasion> activeInvasions = new Vector<Invasion>();

		while(eventReader.hasNext()) {
			event = eventReader.nextEvent();
			
			if(event.isStartElement()) {
				String localPart = event.asStartElement().getName().getLocalPart();
				
				if(localPart.equalsIgnoreCase(ITEM)) {
					isHeader = false;
				}
				
				if(!isHeader) {
					if(localPart.equalsIgnoreCase(GUID)) {
						guid = getCharacterData(eventReader);
					}
					else if(localPart.equalsIgnoreCase(TITLE)) {
						title = getCharacterData(eventReader);
					}
					else if(localPart.equalsIgnoreCase(AUTHOR)) {
						type = getCharacterData(eventReader);
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
					if(type == null) { return false; }
					
					if(type.equalsIgnoreCase(ALERT)) {
						Alert newAlert = null;
						try { newAlert = new Alert(guid, title, description, faction, pubTime, expTime); }
						catch(IllegalArgumentException e) { throw new MissingDataException("alert is missing information"); }
						
						addAlert(newAlert);
					}
					else if(type.equalsIgnoreCase(OUTBREAK)) {
						Outbreak newOutbreak = null;
						try { newOutbreak = new Outbreak(guid, title, pubTime); }
						catch(IllegalArgumentException e) { throw new MissingDataException("outbreak is missing information"); }
						
						addOutbreak(newOutbreak);
						
						if(newOutbreak != null) {
							activeOutbreaks.add(newOutbreak);
						}
					}
					else if(type.equalsIgnoreCase(INVASION)) {
						Invasion newInvasion = null;
						try { newInvasion = new Invasion(guid, title, pubTime); }
						catch(IllegalArgumentException e) { throw new MissingDataException("invasion is missing information"); }
						catch(InvalidFactionException e) { throw new MissingDataException("invasion has an invalid faction"); }

						addInvasion(newInvasion);
						
						if(newInvasion != null) {
							activeInvasions.add(newInvasion);
						}
					}
					
					guid = null;
					title = null;
					type = null;
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
		
		for(int i=0;i<m_outbreaks.size();i++) {
			if(!activeOutbreaks.contains(m_outbreaks.elementAt(i))) {
				m_outbreaks.elementAt(i).setExpired(true);
			}
		}
		
		for(int i=0;i<m_invasions.size();i++) {
			if(!activeInvasions.contains(m_invasions.elementAt(i))) {
				m_invasions.elementAt(i).setExpired(true);
			}
		}
		
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
