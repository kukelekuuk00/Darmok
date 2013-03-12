package me.botsko.darmok.players;

import java.util.HashMap;

import me.botsko.darmok.channels.Channel;

import org.bukkit.entity.Player;

public class PlayerRegistry {
	
	/**
	 * 
	 */
	private HashMap<String,PlayerChannels> players = new HashMap<String,PlayerChannels>();
	
	
	/**
	 * 
	 * @param channel
	 * @param player
	 */
	public void addChannel( Player player, Channel channel ){
		
		String name = player.getName();
		PlayerChannels channels = null;
		
		if( players.containsKey( name ) ){
			channels = players.get( name );
		} else {
			channels = new PlayerChannels();
		}
		if( channels != null ){
			channels.addChannel( channel );
			players.put( name, channels );
		}
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public Channel getDefaultChannel( Player player ){
		
		String name = player.getName();
		
		if( players.containsKey( name ) ){
			PlayerChannels channels = players.get( name );
			if( channels != null ){
				return channels.getDefault();
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param player
	 */
	public void removePlayer( Player player ){
		String name = player.getName();
		if( players.containsKey( name ) ){
			players.remove( name );
		}
	}
}