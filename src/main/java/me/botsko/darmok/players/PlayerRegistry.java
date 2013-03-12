package me.botsko.darmok.players;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerRegistry {
	
	/**
	 * 
	 */
	private HashMap<String,PlayerChannels> players = new HashMap<String,PlayerChannels>();
	

	/**
	 * 
	 * @return
	 */
	public HashMap<String,PlayerChannels> getPlayers(){
		return players;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public PlayerChannels getPlayerChannels( Player player ){
		String name = player.getName();
		if( ! players.containsKey( name ) ){
			players.put( name, new PlayerChannels() );
		}
		return players.get( name );
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