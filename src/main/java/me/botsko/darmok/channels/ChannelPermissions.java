package me.botsko.darmok.channels;

import me.botsko.darmok.Darmok;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class ChannelPermissions {
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanAutoJoin( Player player, Channel channel ){
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "autojoin" ) && player.hasPermission( permPrefix + "read" ) ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanBan( Player player, Channel channel ){
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "ban" ) || player.hasPermission( "darmok.mod" ) ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanDefaultTo( Player player, Channel channel ){
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) ){
			return false;
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
			return false;
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
			if( !playerHasTown( player ) ){
				return false;
			}
		}
		
		return true;
		
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanJoin( Player player, Channel channel ){
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) ){
			return false;
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
			return false;
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
			if( !playerHasTown( player ) ){
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * They have to be allowed to join if they have read or speak perms.
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanKick( Player player, Channel channel ){
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "kick" ) || player.hasPermission( permPrefix + "ban" ) || player.hasPermission( "darmok.mod" ) ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanLeave( Player player, Channel channel ){
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "leave" ) ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * Can't speak without reading, so speak grants read perms.
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanRead( Player player, Channel channel ){
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) ){
			return false;
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
			return false;
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
			if( !playerHasTown( player ) ){
				return false;
			}
		}
		
		return true;
		
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanSpeak( Player player, Channel channel ){
		
		// Perms?
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( !player.hasPermission( permPrefix + "speak" ) ){
			return false;
		}
		
		// Banned?
		if( Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
			return false;
		}
		
		// If a town channel, make sure they have a town
		if( Darmok.getTowny() != null && channel.getContext() != null && channel.getContext().equals("towny-town") ){
			if( !playerHasTown( player ) ){
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerHasTown( Player player ){
		try {
			Resident resident = TownyUniverse.getDataSource().getResident( player.getName() );
			return resident.hasTown();
		} catch (NotRegisteredException e) {
		}
		return false;
	}
}