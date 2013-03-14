package me.botsko.darmok.channels;

import me.botsko.darmok.Darmok;

import org.bukkit.entity.Player;

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
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "default" ) || player.hasPermission( permPrefix + "read" ) || player.hasPermission( permPrefix + "speak" ) ){
			if( ! Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public static boolean playerCanJoin( Player player, Channel channel ){
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "read" ) || player.hasPermission( permPrefix + "speak" ) ){
			if( ! Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
				return true;
			}
		}
		return false;
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
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "read" ) || player.hasPermission( permPrefix + "speak" ) ){
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
	public static boolean playerCanSpeak( Player player, Channel channel ){
		String permPrefix = "darmok.channel." + channel.getName().toLowerCase() + ".";
		if( player.hasPermission( permPrefix + "speak" ) ){
			if( ! Darmok.getPlayerRegistry().isPlayerBannedFromChannel(player, channel) ){
				return true;
			}
		}
		return false;
	}
}