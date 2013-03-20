package me.botsko.darmok;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config extends ConfigBase {
	
	
	/**
	 * 
	 * @param plugin
	 */
	public Config(Plugin plugin) {
		super(plugin);
	}

	
	/**
	 * 
	 * @param plugin
	 */
	public FileConfiguration getConfig(){
		
		FileConfiguration config = plugin.getConfig();
		
		config.addDefault("darmok.debug", false);
		config.addDefault("darmok.language", "en-us");
//		config.addDefault("darmok.notify-newer-versions", true);
		
		config.addDefault("darmok.channel.default-format", "%(color)[%(command)] %(prefix) &f%(player) %(suffix)%(color): %(msg)");
		config.addDefault("darmok.channel.default-channel", "g");
		
		config.addDefault("darmok.spam-prevention.enabled", true);
		config.addDefault("darmok.spam-prevention.min-seconds-between-msg", 2);

		config.addDefault("darmok.censors.caps.enabled", false);
		config.addDefault("darmok.censors.caps.min-length", 15);
		config.addDefault("darmok.censors.min-percentage", 30);
		
		config.addDefault("darmok.censors.profanity.enabled", true);
		
//		config.addDefault("darmok.censors.fakecensor.enabled", true);
//		config.addDefault("darmok.censors.fakecensor.string", "***");
		
		// Copy defaults
		config.options().copyDefaults(true);
		
		// save the defaults/config
		plugin.saveConfig();
		
		return config;
		
	}
}