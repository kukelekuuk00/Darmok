package me.botsko.darmok;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.channels.ChannelRegistry;
import me.botsko.darmok.chatter.Censor;
import me.botsko.darmok.chatter.Chatter;
import me.botsko.darmok.commands.ChannelCommands;
import me.botsko.darmok.listeners.DarmokPlayerListener;
import me.botsko.darmok.players.PlayerChannels;
import me.botsko.darmok.players.PlayerRegistry;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Darmok extends JavaPlugin {
	

	/**
	 * Protected/private
	 */
	private String plugin_name;
	private String plugin_version;
	private Logger log = Logger.getLogger("Minecraft");
	private static ChannelRegistry channelRegistry;
	private static Chatter chatter;
	private static PlayerRegistry playerRegistry;
	private static Censor censor;
	private FileConfiguration profanity;
	
	// Plugins
	private static Essentials essentials = null;
	private static Chat chat = null;
	private static Towny towny = null;
	
	/**
	 * Public
	 */
	public Darmok darmok;
	public static Messenger messenger;
	public static FileConfiguration config;
	public static Config configHandler;


    /**
     * Enables the plugin and activates our player listeners
     */
	@Override
	public void onEnable(){
		
		plugin_name = this.getDescription().getName();
		plugin_version = this.getDescription().getVersion();

		darmok = this;
		
		this.log("Initializing " + plugin_name + " " + plugin_version + ". By Viveleroi.");
		
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
	@SuppressWarnings("unchecked")
	public void loadConfig(){
		configHandler = new Config( this );
		config = configHandler.getConfig();
		profanity = configHandler.getProfanityConfig();
		censor = new Censor( (List<String>) profanity.getList("reject-words"), (List<String>) profanity.getList("censor-words") );
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
							channel.getInt("range"),
							channel.getString("context")
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
	 * @return
	 */
	public static Censor getCensor(){
		return censor;
	}
	
	
	/**
	 * Load existing yaml channel settings for a player
	 * when they join, or when the server reloads.
	 * 
	 * @param player
	 */
	public void loadChannelSettingsForPlayer( Player player ){

		FileConfiguration playerConfig = configHandler.loadPlayerConfig(player);
		
		// They have no config - set the default channels
		if( playerConfig == null ){

			// Load all channels
			HashMap<String,Channel> channels = getChannelRegistry().getChannels();
			for(Entry<String,Channel> entry : channels.entrySet()){
				// Can the player join this?
			    if( ChannelPermissions.playerCanAutoJoin( player, entry.getValue() ) ){
			    	Channel channel;
					try {
						channel = entry.getValue().clone();
						
						debug("Creating first join in channel " + channel.getName());
						// Set as the default channel
						if( player.hasPermission("darmok.channel."+entry.getKey()+".default") ){
							debug("Setting default channel to " + channel.getName());
				    		channel.setDefault( true );
				    	}
						// Register the channels for this player
				    	getPlayerRegistry().getPlayerChannels(player).joinChannel( channel );
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
			    }
			}
		} else {

			// Restore channel subscriptions
			ConfigurationSection channels = playerConfig.getConfigurationSection("channels");
			for(String channelCommand : channels.getKeys(false)){
				Channel channel = Darmok.getChannelRegistry().getChannel( channelCommand );
				if( channel != null ){
					
					// Load default
					ConfigurationSection channelConfig = channels.getConfigurationSection(channelCommand);
					if( channelConfig.getBoolean("default") ){
						debug("Setting default channel to " + channel.getName());
			    		channel.setDefault( true );
					}
					
					getPlayerRegistry().getPlayerChannels(player).addChannel( channel );
				}
			}
			
			// Restore channel bans
			@SuppressWarnings("unchecked")
			ArrayList<String> bannedIn = (ArrayList<String>) playerConfig.getList("banned-in");
			// If player was banned in this channel, restore that
			if( bannedIn != null && !bannedIn.isEmpty() ){
				for( String channelAlias : bannedIn){
					Darmok.getPlayerRegistry().setChannelBanForPlayer( player, channelAlias );
				}
			}
		}
		
		// re-save just in case server crashes, etc
		saveChannelSettingsForPlayer( player );
		
	}
	
	
	/**
	 * Build a new player channel settings save file based on
	 * current channels.
	 */
	public void saveChannelSettingsForPlayer( Player player ){
		
		FileConfiguration playerConfig = new YamlConfiguration();
		ConfigurationSection configChannels = playerConfig.createSection("channels");
		
		PlayerChannels playerChannels = getPlayerRegistry().getPlayerChannels(player);
		
		if( ! playerChannels.getChannels().isEmpty() ){
			for (Entry<String,Channel> entry : playerChannels.getChannels().entrySet()){
				debug("Saving "+player.getName()+"'s active channel " + entry.getValue().getName() + " isDefault: " + entry.getValue().isDefault());
				ConfigurationSection channelConfig = configChannels.createSection( entry.getKey() );
				channelConfig.set( "default", entry.getValue().isDefault() );
				channelConfig.set( "muted", entry.getValue().isMuted() );
			}
		}
		
		// Set their channel bans
		ArrayList<String> bannedIn = getPlayerRegistry().getChannelBansForPlayer( player );
		if( bannedIn != null && !bannedIn.isEmpty() ){
			debug("Saving ban setting in " + bannedIn);
			playerConfig.set( "banned-in", bannedIn );
		}
		
		try {
			playerConfig.save( this.getDataFolder() +"/players/"+player.getName()+".yml" );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param player
	 */
	public static void unloadChannelSettingsForPlayer( Player player ){
		getPlayerRegistry().removePlayer( player );
	}
	
	
	/**
	 * 
	 */
	public void checkPluginDependancies(){
		
		// Essentials
		Plugin tmp = getServer().getPluginManager().getPlugin("Essentials");
		if (tmp != null){
			log("Connected with Essentials, using mute settings.");
			essentials = (Essentials) tmp;
		}
	
		// Vault chat settings
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
        	log("Connected with Vault.");
            chat = chatProvider.getProvider();
        }
        
        // Towny
        Plugin townyTmp = getServer().getPluginManager().getPlugin("Towny");
		if (townyTmp != null){
			log("Connected with Towny");
			towny = (Towny) townyTmp;
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
	 * @return
	 */
	public static TownyUniverse getTowny(){
		if( towny != null ){
			return towny.getTownyUniverse();
		}
		return null;
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
		
		// Save and unload all channels for any online players
		for( Player pl : getServer().getOnlinePlayers() ){
			saveChannelSettingsForPlayer( pl );
			unloadChannelSettingsForPlayer( pl );
		}
					
		this.log("Closing plugin.");	
	}
}
