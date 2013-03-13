package me.botsko.darmok.chatter;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;

import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

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
		if( !ChannelPermissions.playerCanSpeak( player, channel ) ){
			player.sendMessage( Darmok.messenger.playerError("You don't have permission to speak in this channel.") );
			return;
		}
		
		// Muted?
		if( isPlayerMuted( player ) ){
			player.sendMessage( Darmok.messenger.playerError("You've been muted in this channel, sorry.") );
			return;
		}
		
		// Only pull players in this channel
		for( Player pl : Darmok.getPlayerRegistry().getPlayersInChannel(channel) ){
			
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
			if( ! ChannelPermissions.playerCanRead( player, channel ) ){
				plugin.debug("PERM: darmok.channel." + channel.getName().toLowerCase() + ".read");
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
		if( Darmok.getEssentials() != null ){
			User user = Darmok.getEssentials().getUser(player);
			if( user != null && user.isMuted() ){
				return true;
			}
		}
		// @todo add-per channel muting
		return false;
	}
}