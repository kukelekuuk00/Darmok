package me.botsko.darmok.listeners;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.exceptions.JoinChannelException;
import me.botsko.darmok.link.DarmokUser;
import me.botsko.darmok.link.LocalUser;
import me.botsko.darmok.link.RemoteUser;

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
		plugin.loadChannelSettingsForPlayer( new LocalUser(player) );
	}

	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(final PlayerQuitEvent event){
	    LocalUser local = new LocalUser(event.getPlayer());
		plugin.saveChannelSettingsForPlayer( local );
		Darmok.unloadChannelSettingsForPlayer( local );
	}

	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		event.setCancelled(true);
		onUserCommandPreprocess( new LocalUser(event.getPlayer()), event.getMessage());
	}
	
	
	/**
	 * 
	 * @param user
	 * @param msg
	 */
	public void onUserCommandPreprocess(DarmokUser user, String msg){
		
		String cmdArgs[] = msg.split("\\ ");
		String primaryCmd = cmdArgs[0].trim().toLowerCase().replace("/", "");
		
		// Does channel exist?
		Channel channel = Darmok.getChannelRegistry().getChannel( primaryCmd );
		if( channel != null ){
			
			// Are they in the channel?
			if( !(user instanceof RemoteUser) && ! Darmok.getPlayerRegistry().getPlayerChannels( user ).inChannel( channel ) ){
				Darmok.debug("Trying to auto-join player to " + channel.getName());
				
				try {
					Darmok.getPlayerRegistry().getPlayerChannels( user ).joinChannel( channel );
				} catch (JoinChannelException e) {
				    user.sendMessage( Darmok.messenger.playerError( e.getMessage() ) );
					return;
				}

				user.sendMessage( Darmok.messenger.playerSubduedHeaderMsg("Auto-joining channel " + channel.getName() + "..." ) );
			}
			
			// if message actually sent
			if( cmdArgs.length > 1 ){
				
				String[] messageArgs = new String[(cmdArgs.length - 1)];
				for(int i = 1; i < cmdArgs.length; i++ ){
					messageArgs[ (i-1) ] = cmdArgs[i];
				}
				
				String message = StringUtils.join( messageArgs, " ");
				
				// Chat!
				Darmok.getChatter().send( user, channel, message );
			
			} else if(!(user instanceof RemoteUser)) {
				Darmok.debug( "Setting " + user.getName() + "'s default channel to " + channel.getName() );
				if( Darmok.getPlayerRegistry().getPlayerChannels( user ).setDefault( channel ) ){
				    user.sendMessage( Darmok.messenger.playerHeaderMsg("Default channel switched to " + channel.getName() ) );
				} else {
				    user.sendMessage( Darmok.messenger.playerError("Failed setting channel as default. Are you allowed?") );
				}
			}
		}
		// it's not our command, ignore it
	}
	
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		
		LocalUser user = new LocalUser(event.getPlayer());
		
		// Get the current default channel
		Channel channel = Darmok.getPlayerRegistry().getPlayerChannels(user).getDefault();
		
		// Reset their default
		if( channel == null ){
			channel = plugin.resetDefaultChannelForPlayer( user );
		}
		
		if( channel != null ){
			Darmok.debug("Found default channel " + channel.getName() + " for " + user.getName());
			Darmok.getChatter().send( user, channel, event.getMessage() );
			event.setCancelled(true);
		}
	}
}
