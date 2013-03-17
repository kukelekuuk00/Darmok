package me.botsko.darmok.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.exceptions.CannotLeaveChannelException;
import me.botsko.darmok.exceptions.ChannelPermissionException;

import org.bukkit.entity.Player;

public class PlayerRegistry {
	
	/**
	 * 
	 */
	private HashMap<Player,PlayerChannels> players = new HashMap<Player,PlayerChannels>();
	
	/**
	 * 
	 */
	private HashMap<Player,ArrayList<String>> channelBans = new HashMap<Player,ArrayList<String>>();
	

	/**
	 * 
	 * @return
	 */
	public HashMap<Player,PlayerChannels> getPlayers(){
		return players;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public PlayerChannels getPlayerChannels( Player player ){
		if( ! players.containsKey( player ) ){
			players.put( player, new PlayerChannels( player ) );
		}
		return players.get( player );
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public ArrayList<Player> getPlayersInChannel( Channel channel ){
		ArrayList<Player> inChannel = new  ArrayList<Player>();
		for (Entry<Player,PlayerChannels> entry : players.entrySet()){
		    if( entry.getValue().inChannel(channel) ){
		    	
		    	try {
					ChannelPermissions.playerCanRead( entry.getKey(), channel );
				} catch (ChannelPermissionException e1) {
					try {
						players.get( entry.getKey() ).leaveChannel(channel);
					} catch (CannotLeaveChannelException e) {
					}
					continue;
				}
		    	
		    	inChannel.add( entry.getKey() );

		    }
		}
		return inChannel;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 */
	public void banFromChannel( Player player, Channel channel ){
		try {
			getPlayerChannels( player ).removeChannel(channel);
		} catch (CannotLeaveChannelException e) {
			// not really possible
		}
		setChannelBanForPlayer( player, channel.getName() );
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 */
	public void unbanFromChannel( Player player, Channel channel ){
		if( channelBans.containsKey( player ) ){
			ArrayList<String> bannedin = channelBans.get( player );
			bannedin.remove( channel.getCommand() );
			channelBans.put( player, bannedin );
		}
	}
	
	
	/**
	 * 
	 * @param player
	 * @param alias
	 */
	public void setChannelBanForPlayer( Player player, String alias ){
		ArrayList<String> bannedin;
		if( channelBans.containsKey( player ) ){
			bannedin = channelBans.get( player );
		} else {
			bannedin = new ArrayList<String>();
		}
		bannedin.add( alias );
		channelBans.put( player, bannedin );
	}
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public boolean isPlayerBannedFromChannel( Player player, Channel channel ){
		if( channelBans.containsKey( player ) ){
			ArrayList<String> bannedin = channelBans.get( player );
			if( bannedin.contains( channel.getCommand() ) ){
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
	public ArrayList<String> getChannelBansForPlayer( Player player ){
		if( channelBans.containsKey( player ) ){
			return channelBans.get( player );
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param player
	 */
	public void removePlayer( Player player ){
		if( players.containsKey( player ) ){
			players.remove( player );
		}
	}
}