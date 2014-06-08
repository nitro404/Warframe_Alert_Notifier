package audio;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.LineEvent.*;

import alert.AlertNotifier;

public class Sound {
	
	private String m_name;
	private String m_fileName;
	
	public Sound(String name, String fileName) throws IllegalArgumentException {
		if(name == null) { throw new IllegalArgumentException("sound name cannot be null"); }
		if(fileName == null) { throw new IllegalArgumentException("sound file name cannot be null"); }
		
		m_name = name.trim();
		m_fileName = fileName.trim();
	}
	
	public String getName() {
		return m_name;
	}
	
	public String getFileName() {
		return m_fileName;
	}
	
	public void play() {
		play(0.0f, true);
	}
	
	public void play(float volume) {
		play(volume, true);
	}
	
	public void play(float volume, boolean threaded) {
		if(!threaded) {
			playHelper(volume);
		}
		else {
			// start the sound thread
			final float tempVolume = volume;
			Thread soundThread = new Thread(new Runnable() {
				public void run() {
					playHelper(tempVolume);
				}
			});
			soundThread.start();
		}
	}
	
	private void playHelper(float volume) {
		class AudioListener implements LineListener {
			private boolean done = false;
			
			public synchronized void update(LineEvent e) {
				if(e.getType() == Type.STOP || e.getType() == Type.CLOSE) {
					done = true;
					
					notifyAll();
				}
			}
			
			public synchronized void waitUntilDone() {
				try {
					while(!done) {
						wait();
					}
				}
				catch(InterruptedException e) { }
			}
		}
		
		File audioFile = null;
		AudioInputStream audioInputStream = null;
		AudioListener audioListener = null;
		Clip clip = null;
		FloatControl volumeControl = null;
		
		try {
			audioFile = new File((AlertNotifier.settings.soundDirectoryName.length() == 0 ? "" : AlertNotifier.settings.soundDirectoryName + (AlertNotifier.settings.soundDirectoryName.charAt(AlertNotifier.settings.soundDirectoryName.length() - 1) == '/' || AlertNotifier.settings.soundDirectoryName.charAt(AlertNotifier.settings.soundDirectoryName.length() - 1) == '\\' ? "" : "/")) + m_fileName);
			if(!audioFile.exists() || !audioFile.isFile()) {
				AlertNotifier.console.writeLine("Sound file does not exist: " + audioFile);
				return;
			}
			audioInputStream = AudioSystem.getAudioInputStream(audioFile);
			audioListener = new AudioListener();
			clip = AudioSystem.getClip();
			clip.addLineListener(audioListener);
			clip.open(audioInputStream);
			volumeControl = ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN));
			volumeControl.setValue(volume < volumeControl.getMinimum() ? volumeControl.getMinimum() : (volume > volumeControl.getMaximum() ? volumeControl.getMaximum() : volume));
			clip.start();
			audioListener.waitUntilDone();
		}
		catch(LineUnavailableException e) {
			AlertNotifier.console.writeLine("Failed to obtain a line while attempting to play sound: " + m_fileName);
		}
		catch(UnsupportedAudioFileException e) {
			AlertNotifier.console.writeLine("Audio format not supported for sound : " + m_fileName + ": " + e.getMessage());
		}
		catch(IllegalArgumentException e) {
			AlertNotifier.console.writeLine("Illegal argument encountered when attempting to play sound: " + m_fileName);
		}
		catch(IOException e) {
			AlertNotifier.console.writeLine("Read exception thrown when attempting to play sound: " + m_fileName);
		}
		
		try { if(clip != null) { clip.close(); } } catch(SecurityException e) { }
		try { if(audioInputStream != null) { audioInputStream.close(); } } catch(IOException e) { }
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Sound)) { return false; }
		Sound s = (Sound) o;
		return m_name.equalsIgnoreCase(s.m_name);
	}
	
	public String toString() {
		return m_name;
	}
	
}
