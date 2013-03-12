package me.botsko.darmok;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelRegistry;
import me.botsko.darmok.chatter.Chatter;
import me.botsko.darmok.commands.ChannelCommands;
import me.botsko.darmok.listeners.DarmokPlayerListener;
import me.botsko.darmok.players.PlayerRegistry;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class Darmok extends JavaPlugin {
	
//	/**
//	 * Connection Pool
//	 */
//	private static DataSource pool = new DataSource();

	/**
	 * Protected/private
	 */
	private String plugin_name;
	private String plugin_version;
//	private Language language;
	private Logger log = Logger.getLogger("Minecraft");
	private static ChannelRegistry channelRegistry;
	private static Chatter chatter;
	private static PlayerRegistry playerRegistry;
	
	// Plugins
	private static Essentials essentials = null;
	private static Chat chat = null;
	
	/**
	 * Public
	 */
	public Darmok darmok;
	public static Messenger messenger;
	public static FileConfiguration config;


    /**
     * Enables the plugin and activates our player listeners
     */
	@Override
	public void onEnable(){
		
		plugin_name = this.getDescription().getName();
		plugin_version = this.getDescription().getVersion();

		darmok = this;
		
		this.log("Initializing " + plugin_name + " " + plugin_version + ". By Viveleroi.");
		
//		if(getConfig().getBoolean("darmok.notify-newer-versions")){
//			String notice = UpdateNotification.checkForNewerBuild(plugin_version);
//			if(notice != null){
//				log(notice);
//			}
//		}
		
		// Load configuration, or install if new
		loadConfig();

//		if( getConfig().getBoolean("darmok.allow-metrics") ){
//			try {
//			    Metrics metrics = new Metrics(this);
//			    metrics.start();
//			} catch (IOException e) {
//			    log("MCStats submission failed.");
//			}
//		}

		if(isEnabled()){
			
			channelRegistry = new ChannelRegistry();
			chatter = new Chatter(this);
			playerRegistry = new PlayerRegistry();
			messenger = new Messenger( plugin_name );

			// Plugins we use
			checkPluginDependancies();
			
			// Register channels
			registerChannels();
			
			// Assign event listeners
			getServer().getPluginManager().registerEvents(new DarmokPlayerListener(this), this);
			
			// Add commands
//			getCommand("darmok").setExecutor( (CommandExecutor) new DarmokCommands(this) );
			getCommand("ch").setExecutor( (CommandExecutor) new ChannelCommands(this) );
			
			
			// Load all channels for any online players (on reload)
			for( Player pl : getServer().getOnlinePlayers() ){
				loadChannelSettingsForPlayer( pl );
			}
		}
	}

	
	/**
	 * Load configuration and language files
	 */
	public void loadConfig(){
		Config mc = new Config( this );
		config = mc.getConfig();
		// Load language files
//		language = new Language( mc.getLang() );
	}
	
	
	/**
	 * 
	 */
	private void registerChannels(){
		
		ConfigurationSection channelList = getConfig().getConfigurationSection("darmok.channels");
		
		Set<String> channels = channelList.getKeys(false);
		for(String channelName : channels){

			ConfigurationSection channel = channelList.getConfigurationSection(channelName);
			if(channel == null) continue;
			
			String format = config.getString("darmok.channel.default-format");

			channelRegistry.registerChannel(
					new Channel(
							channelName,
							channel.getString("command"),
							channel.getString("color"),
							( channel.getString("format").isEmpty() ? format : channel.getString("format") ),
							channel.getInt("range")
						) );
			
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static ChannelRegistry getChannelRegistry(){
		return channelRegistry;
	}
	
	
	/**
	 * 
	 */
	public static Chatter getChatter(){
		return chatter;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static PlayerRegistry getPlayerRegistry(){
		return playerRegistry;
	}
	
	
	/**
	 * 
	 * @param player
	 */
	public static void loadChannelSettingsForPlayer( Player player ){
		// @todo load existing channel settings for player
		
		// If player has no channel settings, load defaults
		HashMap<String,Channel> channels = getChannelRegistry().getChannels();
		for(Entry<String,Channel> entry : channels.entrySet()){
		    if( player.hasPermission("darmok.channel."+entry.getKey()+".autojoin") ){
		    	Channel channel;
				try {
					channel = entry.getValue().clone();
					if( player.hasPermission("darmok.channel."+entry.getKey()+".default") ){
			    		channel.setDefault( true );
			    	}
			    	getPlayerRegistry().getPlayerChannels(player).addChannel( channel );
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
		    }
		}
	}
	
	
	/**
	 * 
	 * @param player
	 */
	public static void unloadChannelSettingsForPlayer( Player player ){
		getPlayerRegistry().removePlayer( player );
	}
	

//	/**
//	 * 
//	 * @return
//	 */
//	public Language getLang(){
//		return this.language;
//	}
	
	
	/**
	 * 
	 */
	public void checkPluginDependancies(){
		
		Plugin tmp = getServer().getPluginManager().getPlugin("Essentials");
		if (tmp != null){
			log("Connected with Essentials, using mute settings.");
			essentials = (Essentials) tmp;
		}
	
		// Vault permissions
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
        	log("Connected with Vault.");
            chat = chatProvider.getProvider();
        }
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Essentials getEssentials(){
		return essentials;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Chat getVaultChat(){
		return chat;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String msgMissingArguments(){
		return messenger.playerError("Missing arguments. Check /darmok ? for help.");
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String msgInvalidArguments(){
		return messenger.playerError("Invalid arguments. Check /darmok ? for help.");
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String msgInvalidSubcommand(){
		return messenger.playerError("Darmok doesn't have that command. Check /darmok ? for help.");
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String msgNoPermission(){
		return messenger.playerError("You don't have permission to perform this action.");
	}

	
	/**
	 * 
	 * @param message
	 */
	public void log(String message){
		log.info("["+plugin_name+"]: " + message);
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void logSection(String[] messages){
		if(messages.length > 0){
			log("--------------------- ## Important ## ---------------------");
			for(String msg : messages){
				log(msg);
			}
			log("--------------------- ## ========= ## ---------------------");
		}
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void debug(String message){
		if(config.getBoolean("darmok.debug")){
			log.info("["+plugin_name+"]: " + message);
		}
	}
	
	
	/**
	 * Disable the plugin
	 */
	public void disablePlugin(){
		this.setEnabled(false);
	}
	
	
	/**
	 * Shutdown
	 */
	@Override
	public void onDisable(){
		
		// Unload all channels for any online players
		for( Player pl : getServer().getOnlinePlayers() ){
			unloadChannelSettingsForPlayer( pl );
		}
					
		this.log("Closing plugin.");	
	}
}
