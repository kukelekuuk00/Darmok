package me.botsko.darmok.chatter;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;

import org.bukkit.ChatColor;
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
		
		/**
		 * Apply censors
		 */
		// Caps limits
		if( plugin.getConfig().getBoolean("darmok.censors.caps.enabled") ){
			msg = Darmok.getCensor().filterCaps( msg, plugin.getConfig().getInt("darmok.censors.caps.min-length"), plugin.getConfig().getInt("darmok.censors.caps.min-percentage") );
		}
//		// Fake censor
//		if( plugin.getConfig().getBoolean("darmok.censors.fakecensor.enabled") && Darmok.getCensor().isFakeCensor( msg, plugin.getConfig().getString("darmok.censors.fakecensor.string") ) ){
//			player.sendMessage( Darmok.messenger.playerError("Sorry but we do not allow stars instead of curse words.") );
//			return;
//		}
		// Profanity
		if( plugin.getConfig().getBoolean("darmok.censors.profanity.enabled") ){
			if( Darmok.getCensor().containsSuspectedProfanity( msg ) ){
				
				player.sendMessage( Darmok.messenger.playerError("Profanity or trying to bypass the censor is not allowed. Sorry if this is a false catch.") );
				
//				String alert_msg = player.getName() + "'s message was blocked for profanity.";
//				plugin.alertPlayers(alert_msg);
//				plugin.log( player.getName()+"'s message was blocked for profanity. Original was: " + event.getMessage() );
				
				return;
			} else {
				// scan for words we censor
				msg = Darmok.getCensor().replaceCensoredWords( msg );
			}
		}
		
		// Do they have permission to use colors?
		if( !player.hasPermission("darmok.chatcolor") ){
			msg = ChatColor.stripColor(msg);
		}
		
		// Format the final message
		msg = channel.formatMessage( player, msg );
		
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
				return;
			}

			// All checks are GO for launch
			pl.sendMessage( msg );
			
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