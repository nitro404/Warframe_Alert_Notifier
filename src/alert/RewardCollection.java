package alert;

import java.io.*;
import java.util.*;

public class RewardCollection {
	
	private Vector<Reward> m_rewards;
	
	final public static Vector<Reward> DEFAULT_REWARDS = new Vector<Reward>(Arrays.asList(new Reward[] {
		// item blueprints
		new Reward("Orokin Reactor", RewardType.Blueprint, RewardCategory.ItemBlueprint),
		new Reward("Orokin Catalyst", RewardType.Blueprint, RewardCategory.ItemBlueprint),
		new Reward("Forma", RewardType.Blueprint, RewardCategory.ItemBlueprint),
		
		// items
//		new Reward("Orokin Reactor", RewardType.Item, RewardCategory.Item),
//		new Reward("Orokin Catalyst", RewardType.Item, RewardCategory.Item),
//		new Reward("Forma", RewardType.Item, RewardCategory.Item),
		
		// weapon blueprints
		new Reward("Dagger Axe", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Dagger Axe Skin", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Ceramic Dagger", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Dark Dagger", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Heat Dagger", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Dual Dagger", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Brokk Hammer", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Dark Sword", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Heat Sword", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Jaw Sword", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Pangolin Sword", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Plasma Sword", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Glaive", RewardType.Blueprint, RewardCategory.WeaponBlueprint),
		new Reward("Manticore Axe Skin", RewardType.Blueprint, RewardCategory.WeaponBlueprint),

		// helmet blueprints
		new Reward("Scorpion Ash Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Locust Ash Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Reverb Banshee Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Chorus Banshee Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Phoenix Ember Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Backdraft Ember Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Avalon Excalibur Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Pendragon Excalibur Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Aurora Frost Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Squall Frost Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Triton Hydroid Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Ketos Hydroid Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Essence Loki Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Swindle Loki Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Coil Mag Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Gauss Mag Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Harlequin Mirage Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Raknis Nekros Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Shroud Nekros Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Flux Nova Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Quantum Nova Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Menticide Nyx Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Vespa Nyx Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Oryx Oberon Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Markhor Oberon Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Thrak Rhino Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Vanguard Rhino Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Hemlock Saryn Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Chlora Saryn Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Aura Trinity Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Meridian Trinity Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Bastet Valkyr Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Kara Valkyr Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Esprit Vauban Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Gambit Vauban Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Storm Volt Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Pulse Volt Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Cierzo Zephyr Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		new Reward("Tengu Zephyr Helmet", RewardType.Blueprint, RewardCategory.HelmetBlueprint),
		
		// vauban frame blueprints
		new Reward("Vauban Helmet", RewardType.Blueprint, RewardCategory.VaubanBlueprint),
		new Reward("Vauban Chassis", RewardType.Blueprint, RewardCategory.VaubanBlueprint),
		new Reward("Vauban Systems", RewardType.Blueprint, RewardCategory.VaubanBlueprint),
		
		// auras
		new Reward("Physique", RewardType.Aura, RewardCategory.Aura),
		new Reward("Infested Impedance", RewardType.Aura, RewardCategory.Aura),
		new Reward("Rejuvenation", RewardType.Aura, RewardCategory.Aura),
		new Reward("Energy Siphon", RewardType.Aura, RewardCategory.Aura),
		new Reward("Enemy Radar", RewardType.Aura, RewardCategory.Aura),
		new Reward("Shield Disruption", RewardType.Aura, RewardCategory.Aura),
		new Reward("Corrosive Projection", RewardType.Aura, RewardCategory.Aura),
		new Reward("Rifle Scavenger", RewardType.Aura, RewardCategory.Aura),
		new Reward("Shotgun Scavenger", RewardType.Aura, RewardCategory.Aura),
		new Reward("Sniper Scavenger", RewardType.Aura, RewardCategory.Aura),
		new Reward("Pistol Scavenger", RewardType.Aura, RewardCategory.Aura),
		new Reward("Rifle Amp", RewardType.Aura, RewardCategory.Aura),
		new Reward("Steel Charge", RewardType.Aura, RewardCategory.Aura),
		new Reward("Speed Holster", RewardType.Aura, RewardCategory.Aura),
		new Reward("Dead Eye", RewardType.Aura, RewardCategory.Aura),
		new Reward("Sprint Boost", RewardType.Aura, RewardCategory.Aura),
		new Reward("Loot Detector", RewardType.Aura, RewardCategory.Aura),
		
		// nightmare mods
		new Reward("Constitution", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Fortitude", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Hammer Shot", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Wildfire", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Accelerated Blast", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Blaze", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Ice Storm", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Lethal Torrent", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Shred", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Stunning Speed", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Focus Energy", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Rending Strike", RewardType.Mod, RewardCategory.NightmareMod),
		new Reward("Vigor", RewardType.Mod, RewardCategory.NightmareMod),
		
		// corrupt mods
		new Reward("Blind Rage", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Burdened Magazine", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Corrupt Charge", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Critical Delay", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Fleeting Expertise", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Heavy Caliber", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Hollow Point", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Magnum Force", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Narrow Minded", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Overextended", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Spoiled Strike", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Tainted Clip", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Tainted Mag", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Tainted Shell", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Vicious Spread", RewardType.Mod, RewardCategory.CorruptMod),
		new Reward("Vile Precision", RewardType.Mod, RewardCategory.CorruptMod),
		
		// resources
		new Reward("Alloy Plate", RewardType.Resource, RewardCategory.Resource),
		new Reward("Argon Crystal", RewardType.Resource, RewardCategory.Resource),
		new Reward("Control Module", RewardType.Resource, RewardCategory.Resource),
		new Reward("Circuits", RewardType.Resource, RewardCategory.Resource),
		new Reward("Cryotic", RewardType.Resource, RewardCategory.Resource),
		new Reward("Ferrite", RewardType.Resource, RewardCategory.Resource),
		new Reward("Gallium", RewardType.Resource, RewardCategory.Resource),
		new Reward("Morphics", RewardType.Resource, RewardCategory.Resource),
		new Reward("Nano Spores", RewardType.Resource, RewardCategory.Resource),
		new Reward("Neural Sensor", RewardType.Resource, RewardCategory.Resource),
		new Reward("Neurode", RewardType.Resource, RewardCategory.Resource),
		new Reward("Orokin Cell", RewardType.Resource, RewardCategory.Resource),
		new Reward("Rubedo", RewardType.Resource, RewardCategory.Resource),
		new Reward("Salvage", RewardType.Resource, RewardCategory.Resource),
		new Reward("Oxium Alloy", RewardType.Resource, RewardCategory.Resource),
		new Reward("Plastids", RewardType.Resource, RewardCategory.Resource),
		new Reward("Polymer Bundle", RewardType.Resource, RewardCategory.Resource),
		
		// clan tech
		new Reward("Detonite Injector", RewardType.Item, RewardCategory.ClanTech),
		new Reward("Fieldron", RewardType.Item, RewardCategory.ClanTech),
		new Reward("Mutagen Mass", RewardType.Item, RewardCategory.ClanTech),
		
		// kubrow
		new Reward("Kubrow Egg", RewardType.Item, RewardCategory.Kubrow),
		new Reward("Bite", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Fast Deflection", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Link Armor", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Link Health", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Link Shields", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Loyal Companion", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Maul", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Pack Leader", RewardType.Mod, RewardCategory.Kubrow),
		new Reward("Scavenge", RewardType.Mod, RewardCategory.Kubrow),
		
		// event
		new Reward("Corpus Cipher", RewardType.Event, RewardCategory.Event),
		new Reward("Corpus Datamass", RewardType.Event, RewardCategory.Event),

		// misc
		new Reward("R5 Fusion Core Pack", RewardType.Item, RewardCategory.Item),
	}));
	
	public RewardCollection() {
		m_rewards = new Vector<Reward>();
		
		reset();
	}
	
	public int numberOfRewards() {
		return m_rewards.size();
	}
	
	public Reward getReward(int index) {
		if(index < 0 || index >= m_rewards.size()) { return null; }
		return m_rewards.elementAt(index);
	}
	
	public Reward getReward(String name) {
		if(name == null) { return null; }
		String temp = name.trim();
		if(temp.length() == 0) { return null; }
		
		for(int i=0;i<m_rewards.size();i++) {
			if(m_rewards.elementAt(i).getName().equalsIgnoreCase(name)) {
				return m_rewards.elementAt(i);
			}
		}
		return null;
	}
	
	public boolean hasReward(Reward r) {
		if(r == null) { return false; }
		return m_rewards.contains(r);
	}
	
	public boolean hasReward(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_rewards.size();i++) {
			if(m_rewards.elementAt(i).getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfReward(Reward r) {
		if(r == null) { return -1; }
		return m_rewards.indexOf(r);
	}
	
	public int indexOfReward(String name) {
		if(name == null) { return -1; }
		String temp = name.trim();
		if(temp.length() == 0) { return -1; }
		
		for(int i=0;i<m_rewards.size();i++) {
			if(m_rewards.elementAt(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addReward(Reward r) {
		if(r.getType() == RewardType.None || hasReward(r)) { return false; }
		
		m_rewards.add(r);
		
		return true;
	}
	
	public boolean removeReward(int index) {
		if(index < 0 || index >= m_rewards.size()) { return false; }
		m_rewards.remove(index);
		return true;
	}
	
	public boolean removeReward(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_rewards.size();i++) {
			if(m_rewards.elementAt(i).getName().equalsIgnoreCase(name)) {
				m_rewards.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public boolean removeReward(Reward r) {
		if(r == null) { return false; }
		return m_rewards.remove(r);
	}
	
	public void clear() {
		m_rewards.clear();
	}
	
	public void reset() {
		m_rewards.clear();
		m_rewards.addAll(DEFAULT_REWARDS);
	}
	
	public boolean loadFrom(String fileName) {
		if(fileName == null) { return false; }
		
		BufferedReader in = null;
		String input = null;
		String data = null;
		RewardType rewardType = RewardType.Item;
		
		try {
			in = new BufferedReader(new FileReader(fileName));
			
			while((input = in.readLine()) != null) {
				data = input.trim();
				
				if(data.length() == 0) { continue; }
				
				if(data.matches("^\\[.+\\]$")) {
					int startIndex = data.indexOf('[');
					int endIndex = data.indexOf(']');
					if(startIndex >= endIndex) {
						in.close();
						
						return false;
					}
					
					String tempType = data.substring(startIndex + 1, endIndex).trim();
					if(tempType.length() == 0) {
						AlertNotifier.console.writeLine("Empty header encountered while parsing reward list file: " + AlertNotifier.settings.rewardListFileName);
						
						in.close();
						
						return false;
					}
					
					rewardType = RewardType.parseFrom(tempType);
					
					continue;
				}
				
				addReward(new Reward(data, rewardType));
			}
			
			in.close();
		}
		catch(IOException e) {
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
		
		for(int i=0;i<RewardType.values().length;i++) {
			if(RewardType.values()[i] == RewardType.None) { continue; }
			
			if(i != 0) { out.println(); }
			
			out.println("[" + RewardType.values()[i].getDisplayName() + "]");
			
			for(int j=0;j<m_rewards.size();j++) {
				if(m_rewards.elementAt(j).getType() == RewardType.values()[i]) {
					out.println(m_rewards.elementAt(j).getName());
				}
			}
		}
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof RewardCollection)) { return false; }
		
		RewardCollection r = (RewardCollection) o;
		
		if(m_rewards.size() != r.m_rewards.size()) { return false; }
		
		for(int i=0;i<m_rewards.size();i++) {
			if(!r.hasReward(m_rewards.elementAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
}
