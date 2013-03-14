package me.botsko.darmok.players;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.settings.Settings;

public class PlayerChannels {
	
	/**
	 * 
	 */
	private Player player;
	
	
	/**
	 * 
	 */
	private HashMap<String,Channel> channels = new HashMap<String,Channel>();
	
	
	/**
	 * 
	 * @param player
	 */
	public PlayerChannels( Player player ){
		this.player = player;
	}
	
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	public Channel getChannel( String command ){
		return channels.get(command);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Channel> getChannels(){
		return channels;
	}

	
	/**
	 * Does not register the channel in the db, use this only when the user
	 * already has joined the channel.
	 * @param c
	 */
	public boolean addChannel( Channel c ){
		channels.put(c.getCommand(), c);
		return true;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Channel getDefault(){
		for (Entry<String,Channel> entry : channels.entrySet()){
		    if( entry.getValue().isDefault() ){
		    	return entry.getValue();
		    }
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public boolean setDefault( Channel channel ){
		boolean channelUpdated = false;
		if( ChannelPermissions.playerCanDefaultTo(player, channel) ){
			for (Entry<String,Channel> entry : channels.entrySet()){
				entry.getValue().setDefault( false );
				if( entry.getValue().getName().equals( channel.getName() ) ){
					entry.getValue().setDefault( true );
					Settings.setDefaultChannelForPlayer( player, entry.getValue() );
					channelUpdated = true;
				}
			}
		}
		return channelUpdated;
	}
	
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public boolean inChannel( Channel channel ){
		return channels.containsKey( channel.getCommand() );
	}
	
	
	/**
	 * 
	 * @param c
	 */
	public boolean joinChannel( Channel c ){
		if( ChannelPermissions.playerCanJoin( player, c ) ){
			addChannel(c);
			Settings.addChannelToPlayer( player, c );
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean leaveChannel( Channel channel ){
		if( channel != null ){
			if( ChannelPermissions.playerCanLeave( player, channel ) ){
				removeChannel( channel );
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean banFromChannel( Channel channel ){
		if( channel != null ){
			removeChannel( channel );
			Settings.banPlayerFromChannel( player, channel );
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean unbanFromChannel( Channel channel ){
		if( channel != null ){
			Settings.unbanPlayerFromChannel( player, channel );
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public void removeChannel( Channel channel ){
		if( channel != null ){
			channels.remove( channel.getCommand() );
			Settings.removeChannelFromPlayer( player, channel );
		}
	}
}