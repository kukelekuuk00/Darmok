package me.botsko.darmok.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;

import org.bukkit.entity.Player;

public class PlayerRegistry {
	
	/**
	 * 
	 */
	private HashMap<Player,PlayerChannels> players = new HashMap<Player,PlayerChannels>();
	

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
		    	if( ChannelPermissions.playerCanRead( entry.getKey(), channel ) ){
		    		inChannel.add( entry.getKey() );
		    	} else {
		    		// remove player from channel, permissions must have changed
		    		players.get( entry.getKey() ).leaveChannel(channel);
		    	}
		    }
		}
		return inChannel;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public void setPlayerChannels( Player player, PlayerChannels channels ){
		players.put( player, channels );
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