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
		
		config.addDefault("darmok.debug", true);
		config.addDefault("darmok.language", "en-us");
//		config.addDefault("darmok.notify-newer-versions", true);
		
		
		// Copy defaults
		config.options().copyDefaults(true);
		
		// save the defaults/config
		plugin.saveConfig();
		
		return config;
		
	}
}