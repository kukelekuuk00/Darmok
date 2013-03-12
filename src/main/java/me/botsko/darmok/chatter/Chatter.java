package me.botsko.darmok.chatter;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;

import org.bukkit.entity.Player;

public class Chatter {
	
	protected Darmok plugin;
	
	
	/**
	 * 
	 * @param plugin
	 */
	public Chatter( Darmok plugin ){
		this.plugin = plugin;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @param msg
	 */
	public void send( Player player, Channel channel, String msg ){
		
		// Not spamming are you?
		if( isPlayerSpamming( player ) ){
			player.sendMessage( Darmok.messenger.playerError("Can the spam man!") );
			return;
		}
		
		// Verify player has permission to SPEAK
		if( ! player.hasPermission( "darmok.channel." + channel.getName() + ".speak" ) ){
			player.sendMessage( Darmok.messenger.playerError("You don't have permission to speak in this channel.") );
			return;
		}
		
		// Muted?
		if( isPlayerMuted( player ) ){
			player.sendMessage( Darmok.messenger.playerError("You've been muted in this channel, sorry.") );
			return;
		}
		
		// @todo is player in channel? we should auto-join them if not

		// @todo find all players meant to receive this message
		// - are they in the channel
		
		for( Player pl : plugin.getServer().getOnlinePlayers() ){
			
			int range = channel.getRange();
			
			// Does range matter?
			if( range > -1 ){
				// if 0, check worlds match
				if( range == 0 && !player.getWorld().equals( pl.getWorld() ) ){
					continue;
				}
				// otherwise, it's a distance
				else if( player.getLocation().distance( pl.getLocation() ) > range ){
					continue;
				}
			}
			
			// Player is in range.
			
			// Ensure they have permission to READ
			if( ! player.hasPermission( "darmok.channel." + channel.getName() + ".read" ) ){
				return;
			}
			
			
			
			// All checks are GO for launch
			pl.sendMessage( channel.formatMessage( player, msg ) );
			
		}
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	private boolean isPlayerSpamming( Player player ){
		// @todo implement me
		return false;
	}
	

	/**
	 * 
	 * @param player
	 * @return
	 */
	private boolean isPlayerMuted( Player player ){
		// @todo implement me
		return false;
		// @todo hook into essentials.getUser(player).isMuted()
	}
}