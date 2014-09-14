package audio;

import java.util.*;
import java.io.*;
import alert.*;

public class SoundManager {
	
	private Vector<Sound> m_sounds;
	
	final public static Vector<Sound> DEFAULT_SOUNDS = new Vector<Sound>(Arrays.asList(new Sound[] {
		new Sound("Alert", "!.wav")
	}));
	
	public SoundManager() {
		m_sounds = new Vector<Sound>();
		m_sounds.addAll(DEFAULT_SOUNDS);
	}
	
	public int numberOfSounds() {
		return m_sounds.size();
	}
	
	public Sound getSound(int index) {
		if(index < 0 || index >= m_sounds.size()) { return null; }
		return m_sounds.elementAt(index);
	}
	
	public Sound getSound(String name) {
		if(name == null) { return null; }
		String temp = name.trim();
		if(temp.length() == 0) { return null; }
		
		for(int i=0;i<m_sounds.size();i++) {
			if(m_sounds.elementAt(i).getName().equalsIgnoreCase(name)) {
				return m_sounds.elementAt(i);
			}
		}
		
		return null;
	}
	
	public boolean hasSound(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_sounds.size();i++) {
			if(m_sounds.elementAt(i).getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasSound(Sound sound) {
		if(sound == null) { return false; }
		
		for(int i=0;i<m_sounds.size();i++) {
			if(m_sounds.elementAt(i).equals(sound)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int indexOfSound(String name) {
		if(name == null) { return -1; }
		String temp = name.trim();
		if(temp.length() == 0) { return -1; }
		
		for(int i=0;i<m_sounds.size();i++) {
			if(m_sounds.elementAt(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOfSound(Sound sound) {
		if(sound == null) { return -1; }
		
		for(int i=0;i<m_sounds.size();i++) {
			if(m_sounds.elementAt(i).equals(sound)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean addSound(Sound sound) {
		if(sound == null || hasSound(sound)) { return false; }
		
		m_sounds.add(sound);
		
		return true;
	}
	
	public boolean replaceSound(Sound sound) {
		if(sound == null) { return false; }
		
		m_sounds.remove(sound);
		m_sounds.add(sound);
		
		return true;
	}
	
	public boolean removeSound(int index) {
		if(index < 0 || index >= m_sounds.size()) { return false; }
		m_sounds.remove(index);
		return true;
	}

	public boolean removeSound(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_sounds.size();i++) {
			if(m_sounds.elementAt(i).getName().equalsIgnoreCase(name)) {
				m_sounds.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean removeSound(Sound sound) {
		if(sound == null) { return false; }
		return m_sounds.remove(sound);
	}
	
	public void clear() {
		m_sounds.clear();
	}
	
	public void reset() {
		m_sounds.clear();
		m_sounds.addAll(DEFAULT_SOUNDS);
	}
	
	public boolean loadFrom(String fileName) {
		if(fileName == null) { return false; }
		
		BufferedReader in = null;
		String input = null;
		String data = null;
		String soundName = null;
		String soundFileName = null;
		boolean foundHeader = false;
		
		try {
			in = new BufferedReader(new FileReader(fileName));
			
			while((input = in.readLine()) != null) {
				data = input.trim();
				
				if(data.length() == 0) { continue; }
				
				if(!foundHeader) {
					if(data.matches("^\\[.+\\]$")) {
						int startIndex = data.indexOf('[');
						int endIndex = data.indexOf(']');
						if(startIndex >= endIndex) {
							in.close();
							
							return false;
						}
						
						String tempType = data.substring(startIndex + 1, endIndex).trim();
						
						if(tempType.length() == 0) {
							AlertNotifier.console.writeLine("Empty header encountered while parsing sound list file: " + AlertNotifier.settings.soundListFileName);
							
							in.close();
							
							return false;
						}
						
						if(!tempType.equalsIgnoreCase("Sounds")) {
							AlertNotifier.console.writeLine("Invalid header: \"" + tempType + "\" encountered while parsing sound list file: " + AlertNotifier.settings.soundListFileName);
							
							in.close();
							
							return false;
						}
						
						if(foundHeader) {
							AlertNotifier.console.writeLine("Found duplicate \"Sounds\" headers while parsing sound list file: " + AlertNotifier.settings.soundListFileName);
						}
						
						foundHeader = true;
						
						continue;
					}
				}
				else {
					String[] parts = data.replaceAll("([\n\r]+|^[\t ]+|[\t ]+$)", "").split("[\t ]*[:=][\t ]*", 2);
					if(parts.length != 2) {
						in.close();
						
						return false;
					}
					
					if(parts[0].equalsIgnoreCase("Sound Name")) {
						if(soundName != null) {
							AlertNotifier.console.writeLine("Missing sound file name for sound: " + soundName + " in file: " + AlertNotifier.settings.soundListFileName);
						}
						
						soundName = parts[1];
					}
					else if(parts[0].equalsIgnoreCase("Sound File Name")) {
						if(soundFileName != null) {
							AlertNotifier.console.writeLine("Missing sound name for sound: " + soundFileName + " in file: " + AlertNotifier.settings.soundListFileName);
						}
						
						soundFileName = parts[1];
					}
					else {
						AlertNotifier.console.writeLine("Invalid token: \"" + parts[0] + "\" encountered while parsing sound list file: " + AlertNotifier.settings.soundListFileName);
					}
					
					if(soundName != null && soundFileName != null) {
						Sound newSound = new Sound(soundName, soundFileName);
						
						if(!replaceSound(newSound)) {
							AlertNotifier.console.writeLine("Failed to replace sound: \"" + newSound.getName() + "\" in sound manager while parsing sound list file: " + AlertNotifier.settings.soundListFileName);
						}
						
						soundName = null;
						soundFileName = null;
					}
				}
			}
			
			in.close();
		}
		catch(IOException e) {
			AlertNotifier.console.writeLine("Read exception thrown while parsing sound list file: " + AlertNotifier.settings.soundListFileName);
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
		
		out.println("[Sounds]");
		
		for(int i=0;i<m_sounds.size();i++) {
			out.println("Sound Name: " + m_sounds.elementAt(i).getName());
			out.println("Sound File Name: " + m_sounds.elementAt(i).getFileName());
			
			if(i < m_sounds.size() - 1) {
				out.println();
			}
		}
		
		return true;
	}
	
}
