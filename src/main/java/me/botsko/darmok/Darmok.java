package me.botsko.darmok;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
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
import me.botsko.darmok.settings.Settings;
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
	private static Censor censor;
	private FileConfiguration profanity;
	
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
			
			setupDb();
			
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
		Config mc = new Config( this );
		config = mc.getConfig();
		// Load language files
//		language = new Language( mc.getLang() );
		profanity = mc.getProfanityConfig();
		censor = new Censor( (List<String>) profanity.getList("reject-words"), (List<String>) profanity.getList("censor-words") );
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Connection getDb(){
		try {
        	Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:plugins/darmok/darmok.db";
            return DriverManager.getConnection(url);
		} catch (ClassNotFoundException e){
			System.out.print("Error: SQLite database connection was not established. " + e.getMessage());
			e.printStackTrace();
		} catch (SQLException e){
			System.out.print("Error: SQLite database connection was not established. " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 
	 */
	public void setupDb(){
		
		Connection conn = getDb();
		
		try {
			 String query = "CREATE TABLE IF NOT EXISTS `darmok_player_channels` (" +
		        		"id INT PRIMARY KEY," +
		        		"player TEXT," +
		        		"channel TEXT," +
		        		"isDefault INT" +
		        		")";
				Statement st = conn.createStatement();
				st.executeUpdate(query);
				st.executeUpdate("CREATE INDEX IF NOT EXISTS player ON darmok_player_channels (player ASC)");
				
				query = "CREATE TABLE IF NOT EXISTS `darmok_player_channel_perms` (" +
		        		"id INT PRIMARY KEY," +
		        		"player TEXT," +
		        		"channel TEXT," +
		        		"banned INT," +
		        		"muted INT" +
		        		")";
				st.executeUpdate(query);
				st.close();
				conn.close();

		 }
		 catch(SQLException e){
			 log("Database connection error: " + e.getMessage());
		     e.printStackTrace();
		 }
		
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
	 * @return
	 */
	public static Censor getCensor(){
		return censor;
	}
	
	
	/**
	 * 
	 * @param player
	 */
	public static void loadChannelSettingsForPlayer( Player player ){

		PlayerChannels existingChannels = Settings.getPlayerChannels(player);
		if( !existingChannels.getChannels().isEmpty() ){
			getPlayerRegistry().setPlayerChannels( player, existingChannels );
			return;
		}
		
		// If player has no channel settings, load defaults
		HashMap<String,Channel> channels = getChannelRegistry().getChannels();
		for(Entry<String,Channel> entry : channels.entrySet()){
		    if( ChannelPermissions.playerCanAutoJoin( player, entry.getValue() ) ){
		    	Channel channel;
				try {
					channel = entry.getValue().clone();
					if( player.hasPermission("darmok.channel."+entry.getKey()+".default") ){
			    		channel.setDefault( true );
			    	}
			    	getPlayerRegistry().getPlayerChannels(player).joinChannel( channel );
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
