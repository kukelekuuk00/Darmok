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
	
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(final PlayerJoinEvent event) {
//		Player player = event.getPlayer();
		
		// @todo load the channels they should be in
		// if we've never seen them, use defaults
	
	}

	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		// @todo unload channel settings for the player
	}

	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		Player player = event.getPlayer();

		// Does player have known settings?
		// @todo
		
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
				
				String message = StringUtils.join(messageArgs);
				
				// Chat!
				Darmok.getChatter().send( player, channel, message );
			
			} else {
				// @todo set perma channel mode
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
		// @todo this is fake, needs to actually work
		Channel channel = Darmok.getChannelRegistry().getChannel("g");
		
		Darmok.getChatter().send( player, channel, event.getMessage() );
		
		event.setCancelled(true);
		
	}
}
