package me.botsko.darmok.channels;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.exceptions.ChannelPermissionException;
import me.botsko.darmok.link.DarmokUser;
import me.botsko.darmok.link.LocalUser;

import org.bukkit.entity.Player;

public class ChannelPermissions {
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanAutoJoin( DarmokUser player, Channel channel ) throws ChannelPermissionException{
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "autojoin" ) && player.hasPermission( permPrefix + "read" ) ){
			return true;
		}
		throw new ChannelPermissionException("Insufficient permission to auto-join this channel.");
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanBan( DarmokUser player, Channel channel ) throws ChannelPermissionException{
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "ban" ) || player.hasPermission( "darmok.mod" ) ){
			return true;
		}
		throw new ChannelPermissionException("Insufficient permission to ban a player from this channel.");
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanDefaultTo( DarmokUser user, Channel channel ) throws ChannelPermissionException{
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !user.hasPermission( permPrefix + "read" ) && !user.hasPermission( permPrefix + "speak" ) ){
			throw new ChannelPermissionException("Insufficient permission to read or speak in this channel.");
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(user, channel) ){
			throw new ChannelPermissionException("Player has been banned from this channel.");
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
		    if( user instanceof LocalUser){
                LocalUser local = (LocalUser) user;
                if( local.getSender() instanceof Player ){
                    Player player = (Player) local.getSender();
                    if( !Darmok.getTownyBridge().playerHasTown( player ) ){
                        throw new ChannelPermissionException("Player does not have a town.");
                    }
                }
            }
		}
		
		return true;
		
	}
	
	
	/**
	 * Can the player force another user to change channels
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanForce( DarmokUser player, Channel channel ) throws ChannelPermissionException {
		if( !player.hasPermission( "darmok.mod" ) ){
			throw new ChannelPermissionException("Insufficient permission to force a player into this channel.");
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanJoin( DarmokUser user, Channel channel ) throws ChannelPermissionException{
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !user.hasPermission( permPrefix + "read" ) && !user.hasPermission( permPrefix + "speak" ) ){
			throw new ChannelPermissionException("Insufficient permission to read or speak in this channel.");
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(user, channel) ){
			throw new ChannelPermissionException("Player has been banned from this channel.");
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
		    if( user instanceof LocalUser){
                LocalUser local = (LocalUser) user;
                if( local.getSender() instanceof Player ){
                    Player player = (Player) local.getSender();
                    if( !Darmok.getTownyBridge().playerHasTown( player ) ){
                        throw new ChannelPermissionException("Player does not have a town.");
                    }
                }
            }
		}
		
		return true;
	}
	
	
	/**
	 * They have to be allowed to join if they have read or speak perms.
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanKick( DarmokUser player, Channel channel ) throws ChannelPermissionException{
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "kick" ) || player.hasPermission( permPrefix + "ban" ) || player.hasPermission( "darmok.mod" ) ){
			return true;
		}
		throw new ChannelPermissionException("Insufficient permission to kick a player from this channel.");
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanLeave( DarmokUser player, Channel channel ) throws ChannelPermissionException{
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "leave" ) ){
			return true;
		}
		throw new ChannelPermissionException("Insufficient permission to leave this channel.");
	}
	
	
	/**
	 * Can't speak without reading, so speak grants read perms.
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanRead( DarmokUser user, Channel channel ) throws ChannelPermissionException{
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !user.hasPermission( permPrefix + "read" ) && !user.hasPermission( permPrefix + "speak" ) ){
			throw new ChannelPermissionException("Insufficient permission to read this channel.");
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(user, channel) ){
			throw new ChannelPermissionException("Player has been banned from this channel.");
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
		    if( user instanceof LocalUser){
		        LocalUser local = (LocalUser) user;
		        if( local.getSender() instanceof Player ){
		            Player player = (Player) local.getSender();
        			if( !Darmok.getTownyBridge().playerHasTown( player ) ){
        				throw new ChannelPermissionException("Player does not have a town.");
        			}
		        }
		    }
		}
		
		return true;
		
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 * @throws ChannelPermissionException 
	 */
	public static boolean playerCanSpeak( DarmokUser user, Channel channel ) throws ChannelPermissionException{
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !user.hasPermission( permPrefix + "speak" ) ){
			throw new ChannelPermissionException("Insufficient permission to speak this channel.");
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(user, channel) ){
			throw new ChannelPermissionException("Player has been banned from this channel.");
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
		    if( user instanceof LocalUser){
                LocalUser local = (LocalUser) user;
                if( local.getSender() instanceof Player ){
                    Player player = (Player) local.getSender();
                    if( !Darmok.getTownyBridge().playerHasTown( player ) ){
                        throw new ChannelPermissionException("Player does not have a town.");
                    }
                }
            }
		}
		
		return true;
	}
}