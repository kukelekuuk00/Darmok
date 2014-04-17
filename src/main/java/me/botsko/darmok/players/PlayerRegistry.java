package me.botsko.darmok.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.channels.ChannelPermissions;
import me.botsko.darmok.exceptions.LeaveChannelException;
import me.botsko.darmok.exceptions.ChannelPermissionException;
import me.botsko.darmok.link.DarmokUser;

public class PlayerRegistry {
	
	/**
	 * 
	 */
	private HashMap<DarmokUser,PlayerChannels> users = new HashMap<DarmokUser,PlayerChannels>();
	
	/**
	 * 
	 */
	private HashMap<DarmokUser,ArrayList<String>> channelBans = new HashMap<DarmokUser,ArrayList<String>>();
	

	/**
	 * 
	 * @return
	 */
	public HashMap<DarmokUser,PlayerChannels> getPlayers(){
		return users;
	}
	
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public PlayerChannels getPlayerChannels( DarmokUser user ){
		if( ! users.containsKey( user ) ){
			users.put( user, new PlayerChannels( user ) );
		}
		return users.get( user );
	}
	
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public ArrayList<DarmokUser> getPlayersInChannel( Channel channel ){
		ArrayList<DarmokUser> inChannel = new  ArrayList<DarmokUser>();
		for (Entry<DarmokUser,PlayerChannels> entry : users.entrySet()){
		    if( entry.getValue().inChannel(channel) ){
		    	
		    	try {
					ChannelPermissions.playerCanRead( entry.getKey(), channel );
				} catch (ChannelPermissionException e1) {
					try {
						users.get( entry.getKey() ).leaveChannel(channel);
					} catch (LeaveChannelException e) {
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
	 * @param user
	 * @param channel
	 */
	public void banFromChannel( DarmokUser source, DarmokUser user, Channel channel ){
//		if(user instanceof RemoteUser){
//			((RemoteUser) user).banFromChannel(source, channel);
//		}
		try {
			getPlayerChannels( user ).removeChannel(channel);
		} catch (LeaveChannelException e) {
			// not really possible
		}
		setChannelBanForPlayer( user, channel.getCommand() );
	}
	
	
	/**
	 * 
	 * @param user
	 * @param channel
	 */
	public void unbanFromChannel( DarmokUser source, DarmokUser user, Channel channel ){
//		if(user instanceof RemoteUser){
//		    user.unbanFromChannel(source, channel);
//		}
		if( channelBans.containsKey( user ) ){
			ArrayList<String> bannedin = channelBans.get( user );
			bannedin.remove( channel.getCommand() );
			channelBans.put( user, bannedin );
		}
	}
	
	
	/**
	 * 
	 * @param user
	 * @param alias
	 */
	public void setChannelBanForPlayer( DarmokUser user, String alias ){
		ArrayList<String> bannedin;
		if( channelBans.containsKey( user ) ){
			bannedin = channelBans.get( user );
		} else {
			bannedin = new ArrayList<String>();
		}
		bannedin.add( alias );
		channelBans.put( user, bannedin );
	}
	
	
	/**
	 * 
	 * @param user
	 * @param channel
	 * @return
	 */
	public boolean isPlayerBannedFromChannel( DarmokUser user, Channel channel ){
		if( channelBans.containsKey( user ) ){
			ArrayList<String> bannedin = channelBans.get( user );
			if( bannedin.contains( channel.getCommand() ) ){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param user
	 * @param channel
	 * @return
	 */
	public ArrayList<String> getChannelBansForPlayer( DarmokUser user ){
		if( channelBans.containsKey( user ) ){
			return channelBans.get( user );
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param user
	 */
	public void removePlayer( DarmokUser user ){
		if( users.containsKey( user ) ){
			users.remove( user );
		}
	}
}