package me.botsko.darmok.chatter;

import java.util.HashMap;
import java.util.List;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.exceptions.ChannelPermissionException;
import me.botsko.darmok.link.DarmokUser;
import me.botsko.darmok.link.LocalUser;
import me.botsko.darmok.link.RemoteUser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

public class Chatter {
	
	/**
	 * 
	 */
	protected Darmok plugin;
	
	/**
	 * 
	 */
	protected HashMap<DarmokUser,Long> messageTimestamps = new HashMap<DarmokUser,Long>();
	
	
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
	public void send( DarmokUser user, Channel channel, String msg ){
		
		// Not spamming are you?
		if( isPlayerSpamming( user ) ){
		    user.sendMessage( Darmok.messenger.playerError("Can the spam man!") );
			return;
		}
		
		try {
			ChannelPermissions.playerCanSpeak( user, channel );
		} catch (ChannelPermissionException e1) {
		    user.sendMessage( Darmok.messenger.playerError( e1.getMessage() ) );
			return;
		}
		
		// Muted?
		if( isPlayerMuted( user ) ){
		    user.sendMessage( Darmok.messenger.playerError("You've been muted in this channel, sorry.") );
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
				
			    user.sendMessage( Darmok.messenger.playerError("Profanity or trying to bypass the censor is not allowed. Sorry if this is a false catch.") );
				
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
		if( !user.hasPermission("darmok.chatcolor") ){
			msg = channel.stripColor( msg );
		}
		
		// Format the final message
		String frm_msg = channel.formatMessage( user, msg );
		
		/**
		 * Build a list of all players we think we should be
		 * messaging.
		 */
		List<DarmokUser> playersToMessage = null;
			
		// Instead, just get all players in channel
		if( playersToMessage == null ){
			playersToMessage = Darmok.getPlayerRegistry().getPlayersInChannel(channel);
		}
		
		
		if( user instanceof RemoteUser ){
            RemoteUser remote = (RemoteUser) user;
            // Use raw message, recipient handles formatting
            remote.writeToChannel(user, channel, msg);
        } else {
            
            LocalUser local = (LocalUser) user;
            
            Player player = null;
            if( local.getSender() instanceof Player ){
                player = (Player) local.getSender();
            }
            
            if( player != null ){
            
                // If towny town context, get online residents of town
                if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
                    List<Player> townyPlayers = Darmok.getTownyBridge().getPlayersInPlayerTown(player);
                    for( Player townyPlayer : townyPlayers ){
                        playersToMessage.add( new LocalUser(townyPlayer) );
                    }
                }
                // If towny nation context, get online residents of town
                if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-nation") ){
                    List<Player> townyPlayers = Darmok.getTownyBridge().getPlayersInPlayerNation(player);
                    for( Player townyPlayer : townyPlayers ){
                        playersToMessage.add( new LocalUser(townyPlayer) );
                    }
                }
    		
        		// Message players if in range
        		for( DarmokUser sendToUser : playersToMessage ){
        		    
        		    if( sendToUser instanceof RemoteUser ) continue;
        		    
        		    Player pl = null;
        		    LocalUser localUser = (LocalUser) sendToUser;
                    if( localUser.getSender() instanceof Player ){
                        pl = (Player) localUser.getSender();
                    }
                    
                    if( pl == null ) continue;
        			
        			int range = channel.getRange();
        
        			// Does range matter?
        			if( range > -1 ){
        				// if 0, check worlds match
        				if( range == 0 && !player.getWorld().equals( pl.getWorld() ) ){
        					continue;
        				}
        				// otherwise, it's a distance
        				else if( !player.getWorld().equals( pl.getWorld() ) || player.getLocation().distance( pl.getLocation() ) > range ){
        					continue;
        				}
        			}
        			
        			// Player is in range.
        			
        			// Ensure they have permission to READ
        			try {
        				ChannelPermissions.playerCanRead( localUser, channel );
        			} catch (ChannelPermissionException e) {
        				return;
        			}
        
        			// All checks are GO for launch
        			pl.sendMessage( frm_msg );
        			
        		}
            }
        }
		
		// log to console
		Bukkit.getServer().getConsoleSender().sendMessage(frm_msg);
		
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	private boolean isPlayerSpamming( DarmokUser user ){
		
		if( !plugin.getConfig().getBoolean("darmok.spam-prevention.enabled") ){
			return false;
		}

		int secondBetween = plugin.getConfig().getInt("darmok.spam-prevention.min-seconds-between-msg");
		long currentTime = System.currentTimeMillis();
		long spam = currentTime;

		if ( messageTimestamps.containsKey(user) ){
			spam = messageTimestamps.get(user);
			messageTimestamps.remove(user);
		} else {
			spam -= ((secondBetween + 1)*1000);
		}

		messageTimestamps.put( user, currentTime );

		if (currentTime - spam < (secondBetween*1000)){
			return true;
		}
		
		return false;
	
	}
	

	/**
	 * 
	 * @param player
	 * @return
	 */
	private boolean isPlayerMuted( DarmokUser user ){
	    if( !(user instanceof LocalUser) ) return false;
	    LocalUser local = (LocalUser) user;
	    if( !(local.getSender() instanceof Player) ) return false;
		if( Darmok.getEssentials() != null ){
		    Player player = (Player) local.getSender();
			User essUser = Darmok.getEssentials().getUser(player);
			if( essUser != null && essUser.isMuted() ){
				return true;
			}
		}
		// @todo add-per channel muting
		return false;
	}
}