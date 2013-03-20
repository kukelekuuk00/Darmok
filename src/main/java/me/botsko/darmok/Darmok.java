package me.botsko.darmok;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.channels.ChannelRegistry;
import me.botsko.darmok.chatter.Censor;
import me.botsko.darmok.chatter.Chatter;
import me.botsko.darmok.commands.ChannelCommands;
import me.botsko.darmok.commands.DarmokCommands;
import me.botsko.darmok.exceptions.JoinChannelException;
import me.botsko.darmok.exceptions.ChannelPermissionException;
import me.botsko.darmok.listeners.DarmokPlayerListener;
import me.botsko.darmok.metrics.Metrics;
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
	private FileConfiguration channels;
	
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

		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    log("MCStats submission failed.");
		}

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
			getCommand("darmok").setExecutor( (CommandExecutor) new DarmokCommands(this) );
			getCommand("ch").setExecutor( (CommandExecutor) new ChannelCommands(this) );
			
			loadChannelsForAllPlayers();
			
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
		channels = configHandler.getChannelConfig();
		censor = new Censor( (List<String>) profanity.getList("reject-words"), (List<String>) profanity.getList("censor-words") );
	}
	
	
	/**
	 * 
	 */
	private void registerChannels(){

		Set<String> channelKeys = channels.getKeys(false);
		for(String channelName : channelKeys){

			ConfigurationSection channel = channels.getConfigurationSection(channelName);
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
			ArrayList<Channel> channels = getChannelRegistry().getChannels();
			for( Channel channel : channels ){
				// Can the player join this?
				
				try {
					ChannelPermissions.playerCanAutoJoin( player, channel );
				} catch (ChannelPermissionException e) {
					continue;
				}
			
				debug("Creating first join in channel " + channel.getCommand());
				
				// Register the channel for this player
		    	try {
					getPlayerRegistry().getPlayerChannels(player).joinChannel( channel );
				} catch (JoinChannelException e) {
					continue;
				}
		    	
		    	// Set as the default channel
				if( getConfig().getString("darmok.channel.default-channel").equals( channel.getCommand() ) ){
					debug("Setting default channel to " + channel.getName());
					getPlayerRegistry().getPlayerChannels(player).setDefault( channel );
		    	}
			}
		} else {

			// Restore channel subscriptions
			@SuppressWarnings("unchecked")
			ArrayList<String> channelAliases = (ArrayList<String>) playerConfig.getList("channels");
			for( String alias : channelAliases ){
				Channel channel = Darmok.getChannelRegistry().getChannel( alias );
				if( channel != null ){
					getPlayerRegistry().getPlayerChannels(player).addChannel( channel );
				}
			}
			
			// Load default
			String defaultAlias = playerConfig.getString("default");
			debug("Setting default channel to " + defaultAlias);
			Channel c = getChannelRegistry().getChannel(defaultAlias);
			if( c != null ){
				getPlayerRegistry().getPlayerChannels(player).setDefault(c);
			}
			// @todo throw an error and fix this
			
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
	 * 
	 * @param player
	 */
	public Channel resetDefaultChannelForPlayer( Player player ){
		ArrayList<Channel> channels = Darmok.getChannelRegistry().getChannels();
		if( !channels.isEmpty() ){
			for ( Channel c : channels ){
				if( config.getBoolean("darmok.channels."+c.getName()+".default") ){
					debug("Resetting "+player.getName()+"'s default channel to " + c.getName());
					getPlayerRegistry().getPlayerChannels(player).setDefault(c);
					return c;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Build a new player channel settings save file based on
	 * current channels.
	 */
	public void saveChannelSettingsForPlayer( Player player ){
		
		FileConfiguration playerConfig = new YamlConfiguration();
		
		// Save current channels
		PlayerChannels playerChannels = getPlayerRegistry().getPlayerChannels(player);
		if( ! playerChannels.getChannels().isEmpty() ){
			ArrayList<String> channelAliases = new ArrayList<String>();
			ArrayList<Channel> channels = playerChannels.getChannels();
			if( !channels.isEmpty() ){
				for( Channel c : channels ){
					channelAliases.add( c.getCommand() );
				}
			}
			playerConfig.set("channels", channelAliases);
		}
		
		playerConfig.set("default", playerChannels.getDefault().getCommand());
		
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
	 * 
	 */
	public void loadChannelsForAllPlayers(){
		// Load all channels for any online players (on reload)
		for( Player pl : getServer().getOnlinePlayers() ){
			loadChannelSettingsForPlayer( pl );
		}
	}
	
	
	/**
	 * 
	 */
	public void unloadChannels(){
		// Save and unload all channels for any online players
		for( Player pl : getServer().getOnlinePlayers() ){
			saveChannelSettingsForPlayer( pl );
			unloadChannelSettingsForPlayer( pl );
		}
	}
	
	
	/**
	 * Shutdown
	 */
	@Override
	public void onDisable(){
		unloadChannels();
		this.log("Closing plugin.");	
	}
}
