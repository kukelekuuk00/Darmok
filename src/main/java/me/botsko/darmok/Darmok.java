package me.botsko.darmok;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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
	
	/**
	 * Public
	 */
	public Darmok prism;
	public static Messenger messenger;
	public static FileConfiguration config;


    /**
     * Enables the plugin and activates our player listeners
     */
	@Override
	public void onEnable(){
		
		plugin_name = this.getDescription().getName();
		plugin_version = this.getDescription().getVersion();

		prism = this;
		
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

			// Plugins we use
			checkPluginDependancies();
			
			// Assign event listeners
//			getServer().getPluginManager().registerEvents(new test( this ), this);
			
			// Add commands
//			getCommand("prism").setExecutor( (CommandExecutor) new PrismCommands(this) );
			
			// Init re-used classes
			
			// Init async tasks
			
			// Init scheduled events

			
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
		this.log("Closing plugin.");	
	}
}
