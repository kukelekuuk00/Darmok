package me.botsko.darmok.players;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.exceptions.JoinChannelException;
import me.botsko.darmok.exceptions.LeaveChannelException;
import me.botsko.darmok.exceptions.ChannelPermissionException;

public class PlayerChannels {
	
	/**
	 * 
	 */
	private Player player;
	
	
	/**
	 * 
	 */
	private ArrayList<String> channels = new ArrayList<String>();
	
	
	/**
	 * 
	 */
	private String defaultChannel;
	
	
	/**
	 * 
	 * @param player
	 */
	public PlayerChannels( Player player ){
		this.player = player;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Channel> getChannels(){
		ArrayList<Channel> playerChannels = new ArrayList<Channel>();
		if(!channels.isEmpty()){
			for(String alias : channels){
				Channel c = Darmok.getChannelRegistry().getChannel( alias );
				if( c != null ){
					playerChannels.add(c);
				}
			}
		}
		return playerChannels;
	}

	
	/**
	 * Does not register the channel in the db, use this only when the user
	 * already has joined the channel. Primarily used when re/loading
	 * channel settings for a player.
	 * @param c
	 */
	public boolean addChannel( Channel c ){
		channels.add(c.getCommand());
		return true;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Channel getDefault(){
		if( defaultChannel != null ){
			return Darmok.getChannelRegistry().getChannel( defaultChannel );
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public boolean setDefault( Channel channel ){
		defaultChannel = channel.getCommand();
		return true;
	}
	
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public boolean inChannel( Channel channel ){
		return channels.contains( channel.getCommand() );
	}
	
	
	/**
	 * Subscribes a player to a channel.
	 * @param c
	 * @throws JoinChannelException 
	 */
	public void joinChannel( Channel c ) throws JoinChannelException{
		
		try {
			ChannelPermissions.playerCanJoin( player, c );
		} catch (ChannelPermissionException e){
			throw new JoinChannelException( e.getMessage() );
		}
		
		addChannel(c);
		
	}
	
	
	/**
	 * Removes a channel from the player's subscriptions.
	 * @param player
	 * @return
	 * @throws LeaveChannelException 
	 */
	public boolean leaveChannel( Channel channel ) throws LeaveChannelException{
		if( channel != null ){
			
			try {
				ChannelPermissions.playerCanLeave( player, channel );
			} catch (ChannelPermissionException e){
				throw new LeaveChannelException( e.getMessage() );
			}
			
			if( channel.getCommand().equals(defaultChannel) ){
				if( channels.size() > 1 ){
					// Find the first channel that isn't this one
					for( String alias : channels ){
						if( !alias.equals( channel.getCommand() ) ){
							defaultChannel = alias;
							break;
						}
					}
				} else {
					throw new LeaveChannelException("May not leave only subscribed channel.");
				}
			}
			removeChannel( channel );
			
		}
		return false;
	}
	
	
	/**
	 * Removes a channel from a player without unsubscribing
	 * @param player
	 * @return
	 * @throws LeaveChannelException 
	 */
	public void removeChannel( Channel channel ) throws LeaveChannelException{
		if( channel != null ){
			channels.remove( channel.getCommand() );
			return;
		}
		throw new LeaveChannelException("Removing channel player is not subscribed to.");
	}
}