package me.botsko.darmok;

import org.bukkit.configuration.ConfigurationSection;
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
		
		
		// Build channels
		ConfigurationSection channels = config.createSection("darmok.channels");
		
		// Default Channel: ADMIN
		ConfigurationSection admin = channels.createSection("admin");
		admin.addDefault("command", "a");
		admin.addDefault("range", -1);
		admin.addDefault("color", "&e");
		admin.addDefault("format", "&e[%(command)] &f%(player)&e: %(msg)");
		
		// Default Channel: GLOBAL
		ConfigurationSection global = channels.createSection("global");
		global.addDefault("command", "g");
		global.addDefault("range", -1);
		global.addDefault("color", "&6");
		global.addDefault("format", "&6[%(command)] &f%(player)&6: %(msg)");
		
		// Default Channel: HELP
		ConfigurationSection help = channels.createSection("help");
		help.addDefault("command", "h");
		help.addDefault("range", -1);
		help.addDefault("color", "&b");
		help.addDefault("format", "&b[%(command)] &f%(player)&b: %(msg)");
		
		// Default Channel: LOCAL
		ConfigurationSection local = channels.createSection("local");
		local.addDefault("command", "l");
		local.addDefault("range", 100);
		local.addDefault("color", "&f");
		local.addDefault("format", "&f[%(command)] %(player): %(msg)");
		
		// Default Channel: STAFF
		ConfigurationSection staff = channels.createSection("staff");
		staff.addDefault("command", "s");
		staff.addDefault("range", -1);
		staff.addDefault("color", "&a");
		staff.addDefault("format", "&a[%(command)] &f%(player)&a: %(msg)");
		
		// Copy defaults
		config.options().copyDefaults(true);
		
		// save the defaults/config
		plugin.saveConfig();
		
		return config;
		
	}
}