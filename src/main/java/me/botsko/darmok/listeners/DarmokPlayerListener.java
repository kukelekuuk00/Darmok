package me.botsko.darmok.listeners;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DarmokPlayerListener implements Listener {
	
	protected Darmok plugin;
	
	
	/**
	 * 
	 * @param plugin
	 */
	public DarmokPlayerListener( Darmok plugin ){
		this.plugin = plugin;
	}
	
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(final PlayerJoinEvent event){
		Player player = event.getPlayer();
		Darmok.loadChannelSettingsForPlayer( player );
	}

	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(final PlayerQuitEvent event){
		Darmok.unloadChannelSettingsForPlayer( event.getPlayer() );
	}

	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		
		Player player = event.getPlayer();

		String cmdArgs[] = event.getMessage().split("\\ ");
		String primaryCmd = cmdArgs[0].trim().toLowerCase().replace("/", "");
		
		// Does channel exist?
		Channel channel = Darmok.getChannelRegistry().getChannel( primaryCmd );
		if( channel != null ){
			// if message actually sent
			if( cmdArgs.length > 1 ){
				
				String[] messageArgs = new String[(cmdArgs.length - 1)];
				for(int i = 1; i < cmdArgs.length; i++ ){
					messageArgs[ (i-1) ] = cmdArgs[i];
				}
				
				String message = StringUtils.join( messageArgs, " ");
				
				// Chat!
				Darmok.getChatter().send( player, channel, message );
			
			} else {
				plugin.debug( "Setting " + player.getName() + "'s default channel to " + channel.getName() );
				if( Darmok.getPlayerRegistry().getPlayerChannels( player ).setDefault( channel ) ){
					player.sendMessage( Darmok.messenger.playerHeaderMsg("Default channel switched to " + channel.getName() ) );
				} else {
					player.sendMessage( Darmok.messenger.playerError("Failed setting channel as default. Are you allowed?") );
				}
			}
			
			event.setCancelled(true);
			
		}
		// it's not our command, ignore it
	}
	
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		
		Player player = event.getPlayer();
		
		// Get the current default channel
		Channel channel = Darmok.getPlayerRegistry().getDefaultChannel( player );
		
		if( channel != null ){
			Darmok.getChatter().send( player, channel, event.getMessage() );
			event.setCancelled(true);
		}
	}
}
